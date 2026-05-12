# Testjuhtumid — Todo REST API

> **Märkus:** Tegelik tulemus ja staatus on täidetud pärast testide käivitamist.  
> Kõik testid käivitati rakendusega, mis töötas aadressil `http://localhost:8080`.

---

## Positiivsed stsenaariumid

| ID | Kategooria | Pealkiri | Eeltingimused | Sammud | Sisend | Oodatud tulemus | Tegelik tulemus | Staatus | Prioriteet |
|---|---|---|---|---|---|---|---|---|---|
| TC-001 | Positiivne | Kõikide todo-de loendi pärimine | Rakendus töötab | `GET /api/todos` | — | 200 OK, JSON massiiv (ka tühi `[]` on OK) | 200 OK, `[]` | Pass | Kõrge |
| TC-002 | Positiivne | Uue todo loomine minimaalse sisendiga | Rakendus töötab | `POST /api/todos` kehaga `{"title":"Test"}` | `{"title":"Test"}` | 201 Created, `Location` päis, JSON vastus `id`-ga | 201 Created, `Location: /api/todos/1` | Pass | Kõrge |
| TC-003 | Positiivne | Uue todo loomine täieliku sisendiga | Rakendus töötab | `POST /api/todos` kõikide väljadega | `{"title":"Test","description":"Desc","completed":true}` | 201 Created, kõik väljad vastuses | 201 Created, `completed: true` | Pass | Kõrge |
| TC-004 | Positiivne | Todo pärimine ID järgi | TC-002 on tehtud | `GET /api/todos/{id}` | id = loodud todo id | 200 OK, JSON objekti vastus õige id-ga | 200 OK, `id: 1` | Pass | Kõrge |
| TC-005 | Positiivne | Todo muutmine | TC-002 on tehtud | `PUT /api/todos/{id}` uue kehaga | `{"title":"Updated","completed":true}` | 200 OK, muudetud andmetega vastus | 200 OK, `title: "Updated"` | Pass | Kõrge |
| TC-006 | Positiivne | Todo kustutamine | TC-002 on tehtud | `DELETE /api/todos/{id}` | — | 204 No Content, tühi keha | 204 No Content | Pass | Kõrge |
| TC-007 | Positiivne | Loend pärast loomist sisaldab kirjet | TC-002 on tehtud | `GET /api/todos` | — | 200 OK, massiiv 1 elemendiga | 200 OK, `[{"id":1,...}]` | Pass | Keskmine |

---

## Negatiivsed stsenaariumid

| ID | Kategooria | Pealkiri | Eeltingimused | Sammud | Sisend | Oodatud tulemus | Tegelik tulemus | Staatus | Prioriteet |
|---|---|---|---|---|---|---|---|---|---|
| TC-008 | Negatiivne | Tühi pealkiri loomisel | Rakendus töötab | `POST /api/todos` tühja title-ga | `{"title":""}` | 400 Bad Request, JSON veasõnum | 400 Bad Request, `{"status":400,"message":"Title is required"}` | Pass | Kõrge |
| TC-009 | Negatiivne | Null pealkiri loomisel | Rakendus töötab | `POST /api/todos` title puudub | `{"description":"test"}` | 400 Bad Request | 400 Bad Request | Pass | Kõrge |
| TC-010 | Negatiivne | Olematu ID pärimisel | Rakendus töötab | `GET /api/todos/999999` | id = 999999 | 404 Not Found, JSON veasõnum | 404 Not Found, `{"status":404}` | Pass | Kõrge |
| TC-011 | Negatiivne | Olematu ID muutmisel | Rakendus töötab | `PUT /api/todos/999999` kehaga | `{"title":"X"}` | 404 Not Found | 404 Not Found | Pass | Kõrge |
| TC-012 | Negatiivne | Olematu ID kustutamisel | Rakendus töötab | `DELETE /api/todos/999999` | — | 404 Not Found | 404 Not Found | Pass | Kõrge |
| TC-013 | Negatiivne | Vigane JSON keha | Rakendus töötab | `POST /api/todos` kehaga vigane JSON | `{title: missing quotes}` | 400 Bad Request | 400 Bad Request, `"Malformed or missing JSON body"` | Pass | Keskmine |
| TC-014 | Negatiivne | Vale HTTP meetod | Rakendus töötab | `PATCH /api/todos/1` | — | 405 Method Not Allowed, JSON veasõnum | 405 Method Not Allowed | Pass | Keskmine |
| TC-015 | Negatiivne | Vale Content-Type | Rakendus töötab | `POST /api/todos` Content-Type: text/plain | `title=test` | 415 Unsupported Media Type | 415 Unsupported Media Type | Pass | Keskmine |

---

## Piirjuhud

| ID | Kategooria | Pealkiri | Eeltingimused | Sammud | Sisend | Oodatud tulemus | Tegelik tulemus | Staatus | Prioriteet |
|---|---|---|---|---|---|---|---|---|---|
| TC-016 | Piirjuht | Täpselt 255-märgine pealkiri | Rakendus töötab | `POST /api/todos` 255-märgilise title-ga | `{"title":"A".repeat(255)}` | 201 Created | 201 Created | Pass | Keskmine |
| TC-017 | Piirjuht | 256-märgine pealkiri (üle piiri) | Rakendus töötab | `POST /api/todos` 256-märgilise title-ga | `{"title":"A".repeat(256)}` | 400 Bad Request | 400 Bad Request, `"Title must not exceed 255 characters"` | Pass | Keskmine |
| TC-018 | Piirjuht | Erimärgid pealkirjas (õ, ä, ü, ö) | Rakendus töötab | `POST /api/todos` erimärkidega | `{"title":"Ülesanne õppida ära"}` | 201 Created, erimärgid säilivad | 201 Created, title täpselt sama | Pass | Keskmine |
| TC-019 | Piirjuht | Emoji pealkirjas | Rakendus töötab | `POST /api/todos` emoji-ga | `{"title":"Test 🎉"}` | 201 Created, emoji säilib | 201 Created, title sisaldab emoji-t | Pass | Madal |
| TC-020 | Piirjuht | Negatiivne ID päringus | Rakendus töötab | `GET /api/todos/-1` | id = -1 | 404 Not Found | 404 Not Found | Pass | Madal |

---

## Turvalisuse stsenaariumid

| ID | Kategooria | Pealkiri | Eeltingimused | Sammud | Sisend | Oodatud tulemus | Tegelik tulemus | Staatus | Prioriteet |
|---|---|---|---|---|---|---|---|---|---|
| TC-021 | Turvalisus | SQL injection pealkirja väljas | Rakendus töötab | `POST /api/todos` SQL injection payload-iga | `{"title":"'; DROP TABLE todos; --"}` | 201 Created (payload käsitletakse tavalise stringina), andmebaas püsib tervena | 201 Created, title salvestatakse muutmata, H2 tabel `todos` eksisteerib edasi | Pass | Kõrge |
| TC-022 | Turvalisus | XSS payload pealkirja väljas | Rakendus töötab | `POST /api/todos` XSS payload-iga | `{"title":"<script>alert(1)</script>"}` | 201 Created, payload tagastatakse JSON-is ohutu stringina (mitte HTML-ina) | 201 Created, JSON vastus sisaldab stringi muutmata kujul ilma HTML renderdamiseta | Pass | Kõrge |
