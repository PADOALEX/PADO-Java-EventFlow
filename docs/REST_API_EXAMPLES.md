# HTTP examples

Run these against a disposable local demo. `curl.exe` on Windows avoids PowerShell's historical curl alias; POSIX shells can use `curl`. Examples below use POSIX JSON quoting. In PowerShell, `Invoke-RestMethod -Body ($payload | ConvertTo-Json) -ContentType application/json` avoids native argument-quoting differences.

```sh
curl -i http://127.0.0.1:5080/health
curl -i -H 'Content-Type: application/json' -d '{"name":"Java Meetup","date":"2026-12-05","location":"Lviv","status":"Planned"}' http://127.0.0.1:5080/api/events
# Replace EVENT_UUID with the actual response id.
curl 'http://127.0.0.1:5080/api/events?search=Java&status=Planned&sort=date&direction=asc&page=1&pageSize=10'
curl http://127.0.0.1:5080/api/events/EVENT_UUID
curl -i -X PUT -H 'Content-Type: application/json' -d '{"name":"Updated Meetup","date":"2026-12-06","location":"Online","status":"Planned"}' http://127.0.0.1:5080/api/events/EVENT_UUID
curl -i -X PATCH http://127.0.0.1:5080/api/events/EVENT_UUID/complete
curl http://127.0.0.1:5080/api/events/stats
curl -i -X DELETE http://127.0.0.1:5080/api/events/EVENT_UUID
curl -i 'http://127.0.0.1:5080/api/events?page=0'
```

Expected sequence: 201 + Location, 200 list/detail/update/completion, 200 stats, 204 delete, 400 invalid page. GET of a deleted UUID returns 404. Use the same UUID returned by POST. Unknown statuses, malformed dates and blank names return 400. Error JSON has `status`, `title`, `detail` and validation `errors` where applicable.
