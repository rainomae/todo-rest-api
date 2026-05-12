# Vearaport — Todo REST API

---

## BUG-001: String-tüüpi ID päringus põhjustab 500 Internal Server Error

**Pealkiri:** Mitte-numbriline väärtus ID kohal tagastab 500 vigase 400 asemel

**Raskusaste:** Keskmine

**Prioriteet:** Kõrge

**Põhjendus:** Kasutajaliides võib saata vale kujuga URL-e (nt navigeerimisviga, kopeerimisviga). Rakendus peaks selliseid sisendeid käsitlema graatsiliselt 400-ga, mitte krahh-taseme 500-ga. 500 vastus võib anda ründajale infot rakenduse sisemistest vigadest.

**Keskkond:**
- Java 26.0.1
- Spring Boot 3.5.14
- H2 in-memory
- OS: Windows 11

**Eeltingimused:**
- Rakendus töötab aadressil `http://localhost:8080`

**Sammud reprodutseerimiseks:**
1. Käivita rakendus: `./gradlew bootRun`
2. Saada päring mitte-numbrilise ID-ga:
   ```
   curl -s http://localhost:8080/api/todos/abc
   ```
3. Vaata vastust

**Oodatud tulemus:**
```json
{
  "timestamp": "...",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid value 'abc' for parameter 'id': must be a number",
  "path": "/api/todos/abc"
}
```

**Tegelik tulemus:**
```json
{
  "timestamp": "2026-05-12T13:56:52.084846400Z",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred",
  "path": "/api/todos/abc"
}
```

**Juurpõhjus:** `GlobalExceptionHandler` ei käsitle `MethodArgumentTypeMismatchException` erindi, mis visatakse kui Spring ei suuda teisendada path variable väärtust `Long` tüüpi. Erind jõuab üldise `Exception` käsitleja kätte, mis tagastab 500.

**Lisamaterjalid:**

curl käsklus:
```bash
curl -v http://localhost:8080/api/todos/abc
```

Täielik response payload:
```
< HTTP/1.1 500
< Content-Type: application/json
{"timestamp":"2026-05-12T13:56:52.084846400Z","status":500,"error":"Internal Server Error","message":"An unexpected error occurred","path":"/api/todos/abc"}
```

**Parandus:** Lisa `GlobalExceptionHandler`-isse järgmine käsitleja:
```java
@ExceptionHandler(MethodArgumentTypeMismatchException.class)
public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                        HttpServletRequest request) {
    String message = String.format("Invalid value '%s' for parameter '%s': must be a number",
            ex.getValue(), ex.getName());
    return build(HttpStatus.BAD_REQUEST, message, request);
}
```

**Staatus:** Tuvastatud, parandus rakendatud (vt `GlobalExceptionHandler.java`)

---

## BUG-002: Negatiivne ID tagastab 404 mitte 400

**Pealkiri:** Negatiivne ID väärtus käsitletakse olematu kirjena, mitte vigase sisendina

**Raskusaste:** Madal

**Prioriteet:** Madal

**Põhjendus:** Negatiivne ID on semantiliselt vigane sisend (auto-genereeritud ID-d algavad alati 1-st), mitte "kirje ei leitud" olukord. Semantiliselt täpsem vastus oleks 400, kuid 404 on ka aktsepteeritav — andmebaasis ei eksisteeri kirjet id=-1, seega pole tegemist kriitilise veaga.

**Keskkond:**
- Java 26.0.1, Spring Boot 3.5.14, Windows 11

**Eeltingimused:**
- Rakendus töötab aadressil `http://localhost:8080`

**Sammud reprodutseerimiseks:**
1. Käivita rakendus: `./gradlew bootRun`
2. Saada päring negatiivse ID-ga:
   ```
   curl -s http://localhost:8080/api/todos/-1
   ```

**Oodatud tulemus:** 400 Bad Request (vigane sisend)

**Tegelik tulemus:**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Todo not found with id: -1",
  "path": "/api/todos/-1"
}
```

**Juurpõhjus:** Rakendus ei valideeri path variable väärtuse vahemikku — negatiivne arv on kehtiv `Long` tüüp, mistõttu jõuab päring service kihti, kus andmebaasist kirjet ei leita ja visatakse `TodoNotFoundException`.

**Staatus:** Tuvastatud, jäetud teadlikult parandamata — 404 käitumine on põhjendatav ja ülesande nõuetes pole path variable vahemiku valideerimist nõutud.
