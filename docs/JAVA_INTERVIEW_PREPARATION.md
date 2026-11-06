# Підготовка до Java співбесіди

1. **Core Java.** `==` проти `equals/hashCode`; primitive/boxed types; immutable `String`; `final`; class/interface/record; access modifiers. Поясни, чому `UUID` зручно використовувати як id, а `LocalDate` не є instant.
2. **Generics.** Type erasure, `List<Integer>`, `Comparator<? super T>`, неможливість `new T[]`. Наведи приклад із AlgorithmLab. Чому `ArrayList` зазвичай зручніший за linked list для indexed reads?
3. **Collections.** List/Set/Map, ArrayList/HashMap, порядок, дублікати, hash collisions; ітератор та ConcurrentModificationException. Fail-fast не означає thread safety.
4. **Exceptions.** Checked/unchecked, `try-with-resources`, propagation, відмінність validation failure від database outage. Не перетворюй усі помилки на 200.
5. **OOP і DI.** Інкапсуляція, композиція, поліморфізм, constructor injection. Що створює Spring container? Чому service bean може бути singleton без зберігання mutable request data?
6. **Spring Boot.** Auto-configuration, component scan, profiles, configuration precedence, annotations маршрутизації та validation. Чому `.env` автоматично працює у Compose, але не у `java -jar`?
7. **JPA.** Entity lifecycle, persistence context, dirty checking, transaction proxy, read-only transaction, lazy loading та N+1. Поясни `open-in-view: false` і repository specification.
8. **REST і HTTP.** GET/POST/PUT/PATCH/DELETE, idempotent semantics, 201/Location, 204, 400/404/409/500. PUT повторює бажаний ресурс; це не гарантує ідентичних audit timestamps. Чому повторний DELETE може дати 404 і лишатися idempotent за кінцевим станом?
9. **SQL.** PK, constraints, indexes, migration history, transaction isolation. Для `%substring%` звичайний index не є універсальним прискоренням. Чому тестуємо PostgreSQL, а не підмінюємо locking/SQL behavior H2?
10. **Межі проєкту.** Немає auth, rate limiter, revision token чи multi-user isolation. Поясни, які зміни потрібні перед публічним hosting, не називаючи поточний demo production-ready.

Чесна відповідь про Java: «Мій основний практичний бекенд — C#/.NET. Я знаю Java core fundamentals і використовую окремі Java AlgorithmLab та Spring EventFlow як навчальні проєкти. Код підготовлено з AI-допомогою; можу продемонструвати конкретні частини, які сам запустив, змінив і можу пояснити». Останню частину кажи лише після власної практики. Не видавай виконання Codex за свою самостійну реалізацію.
