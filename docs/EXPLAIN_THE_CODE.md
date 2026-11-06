# Як пояснити Java EventFlow

Простеж POST від JSON до committed row. Чому request і response — окремі DTO, а entity не повертається напряму? Request annotations перевіряють вхідні дані, response зберігає стабільний API-контракт, entity описує persistence mapping. SQL CHECK constraints захищають також від прямого некоректного SQL, який оминає controller.

Поясни constructor injection і чому service не зберігає події у власному `List`. PostgreSQL — джерело даних; JPA managed entity живе в межах транзакції. PUT використовує dirty checking. Чому не викликаємо repository з React? Браузер не має доступу до DB password і не повинен визначати SQL.

Поясни one-based `page` і zero-based `PageRequest`. При однакових датах сортування завершується UUID, щоб порядок був визначеним. У substring search `%`, `_` та backslash екрановані як літерали; Criteria передає значення як параметри. Це відрізняється від конкатенації SQL. Звичайний B-tree не прискорює `%term%` пошук.

Тести `EventApiIT` запускають справжній embedded HTTP server, Flyway і PostgreSQL. `repository.deleteAll()` діє лише після перевірки окремої loopback test database. Позитивний CRUD, 400, 404, paging і constraint violation — різні сценарії. `mvn test` не виконує IT; потрібен `-Pintegration verify`. Якщо БД не налаштована, profile має впасти, а не намалювати успіх.

Перед співбесідою власноруч додай нове необов'язкове поле через DTO/entity/migration/response/test; поясни, чому не можна редагувати вже застосовану Flyway V1. Зміни сортування та напиши перевірку; запусти повний integration profile. Поясни last-write-wins і чому цей код не реалізує optimistic locking. Перевір свою здатність відлагодити 400 і прочитати SQL-помилку без AI.
