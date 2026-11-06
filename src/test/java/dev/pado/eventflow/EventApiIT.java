package dev.pado.eventflow;

import tools.jackson.databind.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import java.net.URI;
import java.net.http.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/** Real HTTP + Flyway + Hibernate + PostgreSQL. Runs only with -Pintegration. */
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
class EventApiIT {
    @LocalServerPort int port;
    @Autowired ObjectMapper mapper;
    @Autowired EventRepository repository;
    @Autowired JdbcTemplate jdbc;
    private final HttpClient client=HttpClient.newHttpClient();

    @DynamicPropertySource static void database(DynamicPropertyRegistry registry) {
        String url=System.getenv("EVENTFLOW_TEST_DB_URL");
        if (url==null || !url.matches("jdbc:postgresql://(?:localhost|127\\.0\\.0\\.1):[0-9]+/java_eventflow_test"))
            throw new IllegalStateException("Set EVENTFLOW_TEST_DB_URL to a dedicated loopback database named java_eventflow_test; tests delete its events");
        String user=Objects.requireNonNull(System.getenv("EVENTFLOW_TEST_DB_USER"),"EVENTFLOW_TEST_DB_USER required");
        String password=Objects.requireNonNull(System.getenv("EVENTFLOW_TEST_DB_PASSWORD"),"EVENTFLOW_TEST_DB_PASSWORD required");
        registry.add("spring.datasource.url",()->url);registry.add("spring.datasource.username",()->user);registry.add("spring.datasource.password",()->password);
    }
    @BeforeEach void resetDedicatedTestDatabase() { repository.deleteAll(); }
    private record Response(int status,JsonNode body,HttpHeaders headers) { }
    private Response send(String method,String path,String body) throws Exception {
        var builder=HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+port+path))
            .header("Content-Type","application/json").header("Accept","application/json");
        builder.method(method,body==null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body));
        var response=client.send(builder.build(),HttpResponse.BodyHandlers.ofString());
        return new Response(response.statusCode(),response.body().isBlank()?null:mapper.readTree(response.body()),response.headers());
    }
    private String payload(String name,String status) throws Exception {
        return mapper.writeValueAsString(Map.of("name",name,"date","2026-12-05","startTime","18:30:00","location","Lviv","status",status,"description","Learning Java"));
    }
    private Response create(String name,String status) throws Exception {
        var r=send("POST","/api/events",payload(name,status));assertEquals(201,r.status());return r;
    }
    @Test void fullCrudCompletionAndStatisticsRoundTrip() throws Exception {
        var created=create("  Java meetup  ","Planned");String id=created.body().get("id").asText();String path="/api/events/"+id;
        assertEquals(path,created.headers().firstValue("Location").orElseThrow());
        assertEquals("Java meetup",created.body().get("name").asText());
        assertEquals(200,send("GET",path,null).status());
        assertEquals(1,repository.count()); // Not an in-memory controller mock.
        var changed=send("PUT",path,payload("Updated","Planned"));assertEquals(200,changed.status());
        assertEquals("Updated",changed.body().get("name").asText());
        var completed=send("PATCH",path+"/complete",null);assertEquals(200,completed.status());
        assertEquals("Completed",completed.body().get("status").asText());
        var stats=send("GET","/api/events/stats",null).body();assertEquals(1,stats.get("total").asInt());
        assertEquals(0,stats.get("planned").asInt());assertEquals(1,stats.get("completed").asInt());
        assertEquals(204,send("DELETE",path,null).status());assertEquals(0,repository.count());
        assertEquals(404,send("GET",path,null).status());assertEquals(404,send("DELETE",path,null).status());
    }
    @Test void searchEscapesWildcardsAndMatchesCaseInsensitively() throws Exception {
        create("100%_Java","Planned");create("100XXJava","Completed");
        var r=send("GET","/api/events?search=100%25_",null);assertEquals(200,r.status());
        assertEquals(1,r.body().get("totalCount").asInt());
        assertEquals("100%_Java",r.body().get("items").get(0).get("name").asText());
        assertEquals(2,send("GET","/api/events?search=JAVA",null).body().get("totalCount").asInt());
        assertEquals(1,send("GET","/api/events?status=Completed",null).body().get("totalCount").asInt());
    }
    @Test void sortingPaginationAndGlobalStats() throws Exception {
        create("Zeta","Planned");create("Alpha","Completed");create("Beta","Planned");
        var page=send("GET","/api/events?sort=name&pageSize=1&page=2",null).body();
        assertEquals("Beta",page.get("items").get(0).get("name").asText());assertEquals(3,page.get("totalCount").asInt());
        assertEquals(3,page.get("totalPages").asInt());assertEquals(2,page.get("page").asInt());assertEquals(1,page.get("pageSize").asInt());
        assertEquals("Zeta",send("GET","/api/events?sort=name&direction=desc&pageSize=1",null).body().get("items").get(0).get("name").asText());
        assertEquals(0,send("GET","/api/events?page=100",null).body().get("items").size());
        assertEquals(3,send("GET","/api/events/stats",null).body().get("total").asInt());
    }
    @Test void validationProducesStructured400WithoutWrites() throws Exception {
        var r=send("POST","/api/events","{\"name\":\" \",\"location\":\" \",\"date\":null}");
        assertEquals(400,r.status());assertEquals(400,r.body().get("status").asInt());assertTrue(r.body().has("errors"));
        assertFalse(r.body().has("stackTrace"));assertEquals(0,repository.count());
    }
    @Test void invalidQueriesAndMalformedJsonReturn400() throws Exception {
        for (String query:List.of("page=0","pageSize=101","pageSize=0","status=All","sort=unknown","direction=up","page=abc","search="+"x".repeat(121)))
            assertEquals(400,send("GET","/api/events?"+query,null).status(),query);
        assertEquals(400,send("GET","/api/events/not-a-guid",null).status());
        assertEquals(400,send("POST","/api/events","{broken").status());
        assertEquals(400,send("POST","/api/events",payload("x","Planned").replace("2026-12-05","not-a-date")).status());
    }
    @Test void missingUpdateAndCompletionReturn404() throws Exception {
        String path="/api/events/"+UUID.randomUUID();
        assertEquals(404,send("PUT",path,payload("Missing","Planned")).status());
        assertEquals(404,send("PATCH",path+"/complete",null).status());
    }
    @Test void schemaConstraintsProtectDirectWrites() {
        assertThrows(DataIntegrityViolationException.class,()->jdbc.update(
            "insert into events(id,name,event_date,location,status,created_at_utc,updated_at_utc) values(?, ' ', DATE '2026-12-05', 'Lviv', 'Planned', now(), now())",UUID.randomUUID()));
        assertEquals(0,repository.count());
    }
    @Test void healthAndEmptyStatistics() throws Exception {
        assertEquals(200,send("GET","/health",null).status());
        var stats=send("GET","/api/events/stats",null).body();
        assertEquals(0,stats.get("total").asInt());assertEquals(0,stats.get("planned").asInt());assertEquals(0,stats.get("completed").asInt());
    }
    @Test void unknownRoutesAndUnsupportedMethodsKeepTheirHttpStatus() throws Exception {
        assertEquals(404,send("GET","/does-not-exist",null).status());
        assertEquals(405,send("POST","/health","{}").status());
    }
}
