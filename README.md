# Todo REST API

Lihtne ülesannete haldamise REST API, mis on ehitatud Spring Boot raamistikuga. Rakendus võimaldab ülesandeid luua, lugeda, muuta ja kustutada (CRUD).

## Tehnoloogiad

| Komponent | Versioon |
|---|---|
| Java | 26.0.1 |
| Spring Boot | 3.5.14 |
| Gradle | 9.5.0 |
| H2 (in-memory andmebaas) | 2.3.232 |
| JUnit 5 + Mockito | Spring Boot Starter Test |
| springdoc-openapi (Swagger) | 2.8.9 |
| Newman (API testid) | 6.2.2 |

## Eeldused

- **Java 26+** paigaldatud — kontroll: `java --version`
  - Allalaadimine: [Adoptium Temurin 26](https://adoptium.net/temurin/releases/?version=26) (tasuta, OpenJDK)
  - Veendu, et `JAVA_HOME` keskkonnamuutuja viitab Java 26 paigaldusele
- **Gradle 9.5+** — tuleb automaatselt Gradle wrapperi kaudu (`./gradlew` või `gradlew.bat`), eraldi paigaldama ei pea
- **Git** paigaldatud — kontroll: `git --version`
- **Node.js 18+ ja npm** (ainult API testide jaoks) — kontroll: `node --version` ja `npm --version`
  - Allalaadimine: [nodejs.org](https://nodejs.org/) (võta LTS versioon)
  - npm tuleb Node.js-iga automaatselt kaasa, eraldi paigaldama ei pea
- **Newman** paigaldatakse globaalselt npm-iga: `npm install -g newman`
  - Kontroll: `newman --version`

## Käivitamine

**Linux / macOS:**
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

**Windows (cmd):**
```cmd
gradlew.bat bootRun --args="--spring.profiles.active=dev"
```

**Windows (PowerShell):**
```powershell
.\gradlew.bat bootRun --args="--spring.profiles.active=dev"
```

> **Märkus:** lipp `--spring.profiles.active=dev` aktiveerib dev profiili, mis lubab H2 konsooli. Ilma selleta käivitub rakendus tootmisprofiiliga ja `/h2-console` ei ole kättesaadav.

Rakendus käivitub aadressil **http://localhost:8080**

- H2 console (ainult dev profiilis): http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:tododb`
  - Username: `sa`, Password: *(tühi)*
- Swagger UI: http://localhost:8080/swagger-ui.html

## API dokumentatsioon

### Endpointid

| Meetod | URL | Kirjeldus | Õnnestumise kood |
|---|---|---|---|
| GET | `/api/todos` | Kõikide ülesannete loend | 200 OK |
| GET | `/api/todos/{id}` | Ühe ülesande pärimine | 200 OK |
| POST | `/api/todos` | Uue ülesande loomine | 201 Created |
| PUT | `/api/todos/{id}` | Ülesande muutmine | 200 OK |
| DELETE | `/api/todos/{id}` | Ülesande kustutamine | 204 No Content |

### Andmemudel

```json
{
  "id": 1,
  "title": "Osta piim",
  "description": "Poest",
  "completed": false,
  "createdAt": "2026-05-12T13:00:00"
}
```

### curl näited

**Kõik ülesanded:**
```bash
curl http://localhost:8080/api/todos
```

**Ülesanne ID järgi:**
```bash
curl http://localhost:8080/api/todos/1
```

**Uue ülesande loomine:**
```bash
curl -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -d '{"title": "Osta piim", "description": "Poest", "completed": false}'
```

**Ülesande muutmine:**
```bash
curl -X PUT http://localhost:8080/api/todos/1 \
  -H "Content-Type: application/json" \
  -d '{"title": "Osta piim ja mune", "completed": true}'
```

**Ülesande kustutamine:**
```bash
curl -X DELETE http://localhost:8080/api/todos/1
```

### Veakäsitlus

Kõik vead tagastatakse JSON kujul:

```json
{
  "timestamp": "2026-05-12T13:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Title is required",
  "path": "/api/todos"
}
```

| Kood | Olukord |
|---|---|
| 400 | Tühi pealkiri, vigane JSON, vale ID tüüp |
| 404 | Olematu ID |
| 405 | Vale HTTP meetod |
| 415 | Vale Content-Type |

## Testide käivitamine

**JUnit unit- ja integratsioonitestid** (Linux/macOS / Windows):
```bash
./gradlew test          # Linux/macOS
gradlew.bat test        # Windows
```

**JaCoCo kaetavuse raport:**
```bash
./gradlew jacocoTestReport      # Linux/macOS
gradlew.bat jacocoTestReport    # Windows
```
Raport: `build/reports/jacoco/test/html/index.html`
Praegune kaetavus: **93.3% (line coverage)**

**Newman API automaattestid** (server peab töötama):

Linux / macOS:
```bash
newman run api-tests/TodoAPI.postman_collection.json \
  -e api-tests/TodoAPI.postman_environment.json
```

Windows (cmd, üks rida):
```cmd
newman run api-tests\TodoAPI.postman_collection.json -e api-tests\TodoAPI.postman_environment.json
```

## Projekti struktuur

```
todo-rest-api/
├── src/
│   ├── main/java/ee/rainer/todo/
│   │   ├── controller/        # REST controller + GlobalExceptionHandler
│   │   ├── service/           # TodoService interface + TodoServiceImpl
│   │   ├── repository/        # Spring Data JPA repository
│   │   ├── model/             # Todo entity
│   │   ├── dto/               # TodoRequest, TodoResponse, ErrorResponse (Java records)
│   │   └── exception/         # TodoNotFoundException
│   ├── main/resources/
│   │   ├── application.properties      # Peamine konfiguratsioon
│   │   └── application-dev.properties  # Dev profiil (H2 console)
│   └── test/java/ee/rainer/todo/
│       ├── service/           # TodoServiceImplTest (unit, Mockito)
│       ├── controller/        # TodoControllerTest (MockMvc, @WebMvcTest)
│       └── TodoEndToEndTest   # (@SpringBootTest, täielik CRUD voog)
├── api-tests/
│   ├── TodoAPI.postman_collection.json
│   ├── TodoAPI.postman_environment.json
│   └── README.md
├── docs/
│   ├── testiplaan.md
│   ├── testjuhtumid.md
│   └── vearaport.md
├── build.gradle
└── README.md
```

## QA dokumentatsioon

Kaustas `docs/` asuvad:
- [testiplaan.md](docs/testiplaan.md) — testimise eesmärk, ulatus, riskid, edukuse kriteeriumid
- [testjuhtumid.md](docs/testjuhtumid.md) — 22 testjuhtumit (positiivsed, negatiivsed, piirjuhud, turvalisus)
- [vearaport.md](docs/vearaport.md) — 2 tuvastatud viga koos juurpõhjuse analüüsi ja parandustega

## Veaotsing

**`java --version` näitab vanemat versiooni kui 26:**
- Paigalda Java 26 ([Adoptium Temurin](https://adoptium.net/temurin/releases/?version=26)).
- Veendu, et `JAVA_HOME` viitab uuele paigaldusele ja `PATH` sisaldab `$JAVA_HOME/bin`.
- Gradle wrapper kasutab automaatselt toolchain'i — kui süsteemis on Java 26 olemas, leiab Gradle selle ise.

**Port 8080 on hõivatud:**
- Leia konflikteeruv protsess: `netstat -ano | findstr :8080` (Windows) või `lsof -i :8080` (Linux/macOS).
- Või muuda porti: lisa `application.properties`-isse rida `server.port=8081`.

**`/h2-console` annab 404:**
- Käivitasid rakenduse **ilma** `--spring.profiles.active=dev` lipuks — H2 console on ainult dev profiilis.
- Käivita uuesti dev-lipu`ga (vt "Käivitamine").

**`./gradlew: command not found` (Windows):**
- Windowsis kasuta `gradlew.bat` (mitte `./gradlew`).

**Newman ei leia faili:**
- Käivita käsk **projekti juurkaustasta** (mitte `api-tests/` seest), et suhteline tee `api-tests/TodoAPI.postman_collection.json` toimiks.
- Veendu, et server töötab aadressil `http://localhost:8080` enne Newmani käivitamist.

## AI tööriistade kasutus

Selle projekti arendamisel kasutasin **Claude Code** (Anthropic) töövahendina järgmiselt:

- **Projekti ülesehitus:** Claude genereeris Gradle projekti struktuuri, `build.gradle` sisu ja `application.properties` algse konfiguratsiooni.
- **Koodigenereerimine:** Entity, DTO record'id, Service, Controller ja GlobalExceptionHandler kirjutati Claude abiga, järgides minu eelnevalt kinnitatud arhitektuuriplaani.
- **Testid:** JUnit unit- ja integratsioonitestid ning Postman collection genereeriti Claude abil, kuid testide loogika ja kaetavus lähtus minu nõuetest.
- **Veaotsimine:** Claude aitas tuvastada kaks päris viga — `MethodArgumentTypeMismatchException` käsitlemata jätmine (BUG-001) ja Newman collection muutuja prioriteediprobleem.
- **QA dokumentatsioon:** Testiplaan, testjuhtumid ja vearaport kirjutati Claude abiga.

**Ise tegin:** Arhitektuuriotsused (kihtide eraldamine, DTO muster, Lombok-ist loobumine: DTO-d on record'id, Entity-l on käsitsi getter'id/setter'id), iga commit'i kinnitamine ja koodi ülevaatus, vigade reprodutseerimine ja paranduse valideerimine käsitsi Newman'iga.
