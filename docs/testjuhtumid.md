# Testjuhtumid — Todo REST API

> **Märkus:** Tegelik tulemus ja staatus on täidetud pärast testide käivitamist.  
> Kõik testid käivitati rakendusega, mis töötas aadressil `http://localhost:8080`.

---

## Positiivsed stsenaariumid

| ID | Kategooria | Pealkiri | Eeltingimused | Testi sammud | Testandmed | Oodatud tulemus | Tegelik tulemus | Staatus | Prioriteet |
|---|---|---|---|---|---|---|---|---|---|
| TC-001 | Positiivne | Kõikide todo-de loendi pärimine | Rakendus töötab pordil 8080 | 1. Ava Postman<br>2. Sea meetodiks GET<br>3. Sisesta URL: `http://localhost:8080/api/todos`<br>4. Klõpsa Send | — | 200 OK, JSON massiiv (ka tühi `[]` on OK) | 200 OK, `[]` | Pass | Kõrge |
| TC-002 | Positiivne | Uue todo loomine minimaalse sisendiga | Rakendus töötab pordil 8080 | 1. Ava Postman<br>2. Sea meetodiks POST<br>3. Sisesta URL: `http://localhost:8080/api/todos`<br>4. Sea Body → raw → JSON<br>5. Klõpsa Send | `{"title":"Test"}` | 201 Created, `Location` päis, JSON vastus `id`-ga | 201 Created, `Location: /api/todos/1` | Pass | Kõrge |
| TC-003 | Positiivne | Uue todo loomine täieliku sisendiga | Rakendus töötab pordil 8080 | 1. Ava Postman<br>2. Sea meetodiks POST<br>3. Sisesta URL: `http://localhost:8080/api/todos`<br>4. Sea Body → raw → JSON<br>5. Klõpsa Send | `{"title":"Test","description":"Desc","completed":true}` | 201 Created, kõik väljad vastuses | 201 Created, `completed: true` | Pass | Kõrge |
| TC-004 | Positiivne | Todo pärimine ID järgi | Rakendus töötab, TC-002 on tehtud (id=1 olemas) | 1. Ava Postman<br>2. Sea meetodiks GET<br>3. Sisesta URL: `http://localhost:8080/api/todos/1`<br>4. Klõpsa Send | id = 1 | 200 OK, JSON objekt õige id-ga | 200 OK, `id: 1` | Pass | Kõrge |
| TC-005 | Positiivne | Todo muutmine | Rakendus töötab, TC-002 on tehtud (id=1 olemas) | 1. Ava Postman<br>2. Sea meetodiks PUT<br>3. Sisesta URL: `http://localhost:8080/api/todos/1`<br>4. Sea Body → raw → JSON<br>5. Klõpsa Send | `{"title":"Updated","completed":true}` | 200 OK, muudetud andmetega vastus | 200 OK, `title: "Updated"` | Pass | Kõrge |
| TC-006 | Positiivne | Todo kustutamine | Rakendus töötab, TC-002 on tehtud (id=1 olemas) | 1. Ava Postman<br>2. Sea meetodiks DELETE<br>3. Sisesta URL: `http://localhost:8080/api/todos/1`<br>4. Klõpsa Send | — | 204 No Content, tühi keha | 204 No Content | Pass | Kõrge |
| TC-007 | Positiivne | Loend pärast loomist sisaldab kirjet | Rakendus töötab, TC-002 on tehtud | 1. Ava Postman<br>2. Sea meetodiks GET<br>3. Sisesta URL: `http://localhost:8080/api/todos`<br>4. Klõpsa Send | — | 200 OK, massiiv 1 elemendiga | 200 OK, `[{"id":1,...}]` | Pass | Keskmine |

---

## Negatiivsed stsenaariumid

| ID | Kategooria | Pealkiri | Eeltingimused | Testi sammud | Testandmed | Oodatud tulemus | Tegelik tulemus | Staatus | Prioriteet |
|---|---|---|---|---|---|---|---|---|---|
| TC-008 | Negatiivne | Tühi pealkiri loomisel | Rakendus töötab pordil 8080 | 1. Ava Postman<br>2. Sea meetodiks POST<br>3. Sisesta URL: `http://localhost:8080/api/todos`<br>4. Sea Body → raw → JSON<br>5. Klõpsa Send | `{"title":""}` | 400 Bad Request, JSON veasõnum | 400 Bad Request, `{"status":400,"message":"Title is required"}` | Pass | Kõrge |
| TC-009 | Negatiivne | Puuduv pealkiri loomisel | Rakendus töötab pordil 8080 | 1. Ava Postman<br>2. Sea meetodiks POST<br>3. Sisesta URL: `http://localhost:8080/api/todos`<br>4. Sea Body → raw → JSON<br>5. Klõpsa Send | `{"description":"test"}` | 400 Bad Request | 400 Bad Request | Pass | Kõrge |
| TC-010 | Negatiivne | Olematu ID pärimisel | Rakendus töötab pordil 8080 | 1. Ava Postman<br>2. Sea meetodiks GET<br>3. Sisesta URL: `http://localhost:8080/api/todos/999999`<br>4. Klõpsa Send | id = 999999 | 404 Not Found, JSON veasõnum | 404 Not Found, `{"status":404}` | Pass | Kõrge |
| TC-011 | Negatiivne | Olematu ID muutmisel | Rakendus töötab pordil 8080 | 1. Ava Postman<br>2. Sea meetodiks PUT<br>3. Sisesta URL: `http://localhost:8080/api/todos/999999`<br>4. Sea Body → raw → JSON<br>5. Klõpsa Send | `{"title":"X"}` | 404 Not Found | 404 Not Found | Pass | Kõrge |
| TC-012 | Negatiivne | Olematu ID kustutamisel | Rakendus töötab pordil 8080 | 1. Ava Postman<br>2. Sea meetodiks DELETE<br>3. Sisesta URL: `http://localhost:8080/api/todos/999999`<br>4. Klõpsa Send | — | 404 Not Found | 404 Not Found | Pass | Kõrge |
| TC-013 | Negatiivne | Vigane JSON keha | Rakendus töötab pordil 8080 | 1. Ava Postman<br>2. Sea meetodiks POST<br>3. Sisesta URL: `http://localhost:8080/api/todos`<br>4. Sea Body → raw → JSON<br>5. Sisesta vigane JSON<br>6. Klõpsa Send | `{title: missing quotes}` | 400 Bad Request | 400 Bad Request, `"Malformed or missing JSON body"` | Pass | Keskmine |
| TC-014 | Negatiivne | Vale HTTP meetod | Rakendus töötab pordil 8080 | 1. Ava Postman<br>2. Sea meetodiks PATCH<br>3. Sisesta URL: `http://localhost:8080/api/todos/1`<br>4. Klõpsa Send | — | 405 Method Not Allowed, JSON veasõnum | 405 Method Not Allowed | Pass | Keskmine |
| TC-015 | Negatiivne | Vale Content-Type | Rakendus töötab pordil 8080 | 1. Ava Postman<br>2. Sea meetodiks POST<br>3. Sisesta URL: `http://localhost:8080/api/todos`<br>4. Sea Headers: Content-Type = text/plain<br>5. Klõpsa Send | `title=test` | 415 Unsupported Media Type | 415 Unsupported Media Type | Pass | Keskmine |

---

## Piirjuhud

| ID | Kategooria | Pealkiri | Eeltingimused | Testi sammud | Testandmed | Oodatud tulemus | Tegelik tulemus | Staatus | Prioriteet |
|---|---|---|---|---|---|---|---|---|---|
| TC-016 | Piirjuht | Täpselt 100-märgine pealkiri | Rakendus töötab pordil 8080 | 1. Ava Postman<br>2. Sea meetodiks POST<br>3. Sisesta URL: `http://localhost:8080/api/todos`<br>4. Sea Body → raw → JSON<br>5. Klõpsa Send | `{"title":"AAAA...A"}` (täpselt 100 tähemärki) | 201 Created | 201 Created | Pass | Keskmine |
| TC-017 | Piirjuht | 101-märgine pealkiri (üle piiri) | Rakendus töötab pordil 8080 | 1. Ava Postman<br>2. Sea meetodiks POST<br>3. Sisesta URL: `http://localhost:8080/api/todos`<br>4. Sea Body → raw → JSON<br>5. Klõpsa Send | `{"title":"AAAA...A"}` (täpselt 101 tähemärki) | 400 Bad Request | 400 Bad Request, `"Title must not exceed 100 characters"` | Pass | Keskmine |
| TC-018 | Piirjuht | Erimärgid pealkirjas (õ, ä, ü, ö) | Rakendus töötab pordil 8080 | 1. Ava Postman<br>2. Sea meetodiks POST<br>3. Sisesta URL: `http://localhost:8080/api/todos`<br>4. Sea Body → raw → JSON<br>5. Klõpsa Send | `{"title":"Ülesanne õppida ära"}` | 201 Created, erimärgid säilivad | 201 Created, title täpselt sama | Pass | Keskmine |
| TC-019 | Piirjuht | Emoji pealkirjas | Rakendus töötab pordil 8080 | 1. Ava Postman<br>2. Sea meetodiks POST<br>3. Sisesta URL: `http://localhost:8080/api/todos`<br>4. Sea Body → raw → JSON<br>5. Klõpsa Send | `{"title":"Test 🎉"}` | 201 Created, emoji säilib | 201 Created, title sisaldab emoji-t | Pass | Madal |
| TC-020 | Piirjuht | Negatiivne ID päringus | Rakendus töötab pordil 8080 | 1. Ava Postman<br>2. Sea meetodiks GET<br>3. Sisesta URL: `http://localhost:8080/api/todos/-1`<br>4. Klõpsa Send | id = -1 | 404 Not Found | 404 Not Found | Pass | Madal |

---

## Turvalisuse stsenaariumid

| ID | Kategooria | Pealkiri | Eeltingimused | Testi sammud | Testandmed | Oodatud tulemus | Tegelik tulemus | Staatus | Prioriteet |
|---|---|---|---|---|---|---|---|---|---|
| TC-021 | Turvalisus | SQL injection pealkirja väljas | Rakendus töötab pordil 8080 | 1. Ava Postman<br>2. Sea meetodiks POST<br>3. Sisesta URL: `http://localhost:8080/api/todos`<br>4. Sea Body → raw → JSON<br>5. Klõpsa Send<br>6. Kontrolli H2 console'ist et tabel `todos` eksisteerib | `{"title":"'; DROP TABLE todos; --"}` | 201 Created, payload käsitletakse tavalise stringina, andmebaas püsib tervena | 201 Created, title salvestatakse muutmata, H2 tabel `todos` eksisteerib edasi | Pass | Kõrge |
| TC-022 | Turvalisus | XSS payload pealkirja väljas | Rakendus töötab pordil 8080 | 1. Ava Postman<br>2. Sea meetodiks POST<br>3. Sisesta URL: `http://localhost:8080/api/todos`<br>4. Sea Body → raw → JSON<br>5. Klõpsa Send<br>6. Kontrolli et vastus on JSON, mitte HTML | `{"title":"<script>alert(1)</script>"}` | 201 Created, payload tagastatakse JSON-is ohutu stringina | 201 Created, JSON vastus sisaldab stringi muutmata kujul ilma HTML renderdamiseta | Pass | Kõrge |
