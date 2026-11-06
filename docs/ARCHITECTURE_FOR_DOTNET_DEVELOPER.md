# Spring для розробника .NET

| ASP.NET Core / EF Core | Spring / JPA у проєкті |
| --- | --- |
| `Program.cs`, DI container | `@SpringBootApplication`, component scanning, constructor injection |
| Controller або Minimal API | `@RestController`, `@GetMapping`, `@PostMapping` |
| Request DTO + DataAnnotations | Java `record` + Jakarta Bean Validation + `@Valid` |
| Scoped application service | `@Service`; bean зазвичай singleton, тому без mutable стану запиту |
| `DbContext`, `DbSet`, LINQ | `JpaRepository`, `JpaSpecificationExecutor`, Criteria predicates |
| EF change tracking + SaveChanges | JPA persistence context, dirty checking, flush/commit |
| Explicit transaction / unit of work | `@Transactional` на методах service через Spring proxy |
| EF migrations | Flyway versioned SQL + `ddl-auto: validate` |
| `IExceptionHandler`, ProblemDetails | `@RestControllerAdvice`, `ProblemDetail` |
| xUnit + WebApplicationFactory | JUnit 5 + `@SpringBootTest(RANDOM_PORT)` + HTTP client |

Для POST controller перевіряє JSON, викликає service і повертає 201 із Location. Service створює UUID, нормалізує рядки та передає entity в repository. На завершенні транзакції Hibernate робить SQL. PUT завантажує managed entity і змінює її; повторний `save` не потрібний для dirty checking. Якщо сталася runtime exception, Spring відкочує транзакцію.

`@Transactional` працює через proxy: звичайний виклик одного методу того самого об'єкта з іншого не проходить через proxy. Тут controller викликає public service methods, тому межа коректна. Repository interfaces реалізує Spring Data. `Specification` формує SQL-предикати, а не завантажує всю таблицю в пам'ять. Сортування обмежене allowlist, щоб клієнт не вибирав довільні entity properties.

`LocalDate` відповідає даті без часового поясу, приблизно `DateOnly`; `LocalTime` — `TimeOnly`; `Instant` — UTC instant. Не перетворюй день події на midnight UTC: це може змістити календарний день у браузері. Open-in-view вимкнений, щоб HTTP serialization не робила приховані запити до БД.

Це невеликий monolith: auth, CQRS, message bus і microservices не потрібні для цієї навчальної задачі. Відсутність auth обмежує його локальними demo-сценаріями. Оригінальний .NET backend залишається окремим і працездатним.
