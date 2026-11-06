# React and .NET API contract comparison

Compared with `PADOALEX/PADO-EventFlow` commit `8ac10b36dd5914fe3b07b627a39d6845595d5ae8`, specifically `frontend/src/api/events.js`, API `Program.cs`, `Contracts/EventContracts.cs` and `Models/EventQuery.cs`.

| Area | Java behavior and comparison |
| --- | --- |
| Paths and methods | Same `/api/events`, `/stats`, UUID detail, POST, PUT, PATCH complete, DELETE; same success status codes |
| List shape | Same camelCase items/count/page/pageSize/totalPages; one-based page and 1-100 size |
| Payload | Same name, description, date, startTime, location and Planned/Completed status; UUID and audit timestamps in responses |
| Search/filter/sort | Same query names and case-sensitive enum values; case-insensitive name/description search; escaped wildcard literals; date/time then ID or name then ID ordering |
| Global stats | Same total/planned/completed shape, independent of list filters |
| Errors | Both expose readable problem detail and optional arrays under `errors`; Java's validation field paths and wording differ |
| Explicit null status | Java normalizes null to Planned, like an omitted status; .NET's Required rejects an explicit null. The React form always sends a valid status |
| Unknown JSON properties | Java rejects them; default .NET deserialization ignores unknown properties. React sends only expected fields |
| Invalid UUID | Java returns structured 400; .NET's GUID route constraint normally gives 404 |
| Trailing slash | Java exposes the canonical no-trailing-slash paths used by the client; trailing-slash aliases are not promised |
| Request limits | Java does not reproduce the original API's 32 KiB JSON cap or 120/minute IP rate limit |
| CORS | Java uses same-origin Vite proxy only; original API also includes an explicit CORS allowlist |
| Database | Separate Java Flyway schema (`event_date`), not compatible with the EF migration table; never connect both implementations to one schema |
| Concurrency | Both are small last-write-wins event demos without per-user authorization |

Native HTTP integration tests cover the API contract. See the verification report for whether the existing React UI was also exercised against the packaged Java backend. No claim of byte-identical error responses or arbitrary client compatibility is made.
