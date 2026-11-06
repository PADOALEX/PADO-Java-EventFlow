package dev.pado.eventflow;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.UUID;

@RestController @RequestMapping("/api/events") @Validated
public class EventController {
    private final EventService service;
    public EventController(EventService service) { this.service=service; }
    @GetMapping
    public EventService.PageResponse list(@RequestParam(required=false) @Size(max=120) String search,
        @RequestParam(required=false) EventStatus status,
        @RequestParam(defaultValue="date") String sort,@RequestParam(defaultValue="asc") String direction,
        @RequestParam(defaultValue="1") @Min(1) int page,
        @RequestParam(defaultValue="10") @Min(1) @Max(100) int pageSize) {
        return service.list(search,status,sort,direction,page,pageSize);
    }
    @GetMapping("/stats") public EventService.Stats stats() { return service.stats(); }
    @GetMapping("/{id}") public EventResponse get(@PathVariable UUID id) { return service.get(id); }
    @PostMapping public ResponseEntity<EventResponse> create(@Valid @RequestBody EventRequest request) {
        var response=service.create(request);
        return ResponseEntity.created(URI.create("/api/events/"+response.id())).body(response);
    }
    @PutMapping("/{id}") public EventResponse update(@PathVariable UUID id,@Valid @RequestBody EventRequest request) { return service.update(id,request); }
    @PatchMapping("/{id}/complete") public EventResponse complete(@PathVariable UUID id) { return service.complete(id); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable UUID id) { service.delete(id);return ResponseEntity.noContent().build(); }
}
