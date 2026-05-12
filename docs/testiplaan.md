# Testiplaan — Todo REST API

## 1. Eesmärk ja ulatus

### Eesmärk
Tagada, et Todo REST API vastab funktsionaalsetele nõuetele, käsitleb veavolukordi korrektselt ning on robustne piirjuhtude ja pahatahtlike sisendite suhtes.

### Ulatus — mida testitakse
- Kõik viis CRUD endpointi: `GET /api/todos`, `GET /api/todos/{id}`, `POST /api/todos`, `PUT /api/todos/{id}`, `DELETE /api/todos/{id}`
- Sisendi valideerimine: kohustuslikud väljad, pikkuspiirangud, tühjad väärtused
- Veakäsitlus: õiged HTTP staatuskoodid ja struktureeritud JSON veasõnumid
- Piirväärtused: maksimaalsed väljade pikkused, erimärgid
- Turvalisus: SQL injection ja XSS payload'id pealkirja väljas

### Ulatus — mida ei testita
- Jõudlus- ja koormustestimine (pole nõutud)
- Autentimine ja autoriseerimine (rakenduses pole)
- UI testimine (puudub)
- Andmete püsivus taaskäivituse üle (H2 in-memory kaob taaskäivitamisel — see on teadlik arhitektuuriotsus)

---

## 2. Testimise tüübid

| Tüüp | Kirjeldus |
|---|---|
| **Funktsionaalne** | Iga endpoint töötab vastavalt spetsifikatsioonile (õiged staatuskoodid, vastuse struktuur) |
| **Negatiivne** | Vigane sisend, olematu ID, vale HTTP meetod, puuduv keha |
| **Piirväärtuste** | Täpselt max-pikkusega sisend, max+1 pikkusega sisend, tühi string vs null |
| **Turvalisus** | SQL injection katsed, XSS payload'id — kontrollime, et rakendus ei krahhita ega leki infot |
| **Regressioon** | JUnit testid käivitatakse igal buildi'l, tagades et muudatused ei riku olemasolevat funktsionaalsust |

---

## 3. Testimise keskkond

| Komponent | Versioon |
|---|---|
| Java | 26.0.1 |
| Spring Boot | 3.5.14 |
| Gradle | 9.5.0 |
| Andmebaas | H2 in-memory |
| Testiraamistik (unit/integratsiooni) | JUnit 5 + Mockito + MockMvc |
| API testimise tööriist | Postman + Newman |
| Operatsioonisüsteem | Windows 11 |

---

## 4. Riskid ja eeldused

| Risk / Eeldus | Mõju | Leevendus |
|---|---|---|
| H2 in-memory andmebaas kaob taaskäivitamisel | Testandmed tuleb iga test run'iga uuesti luua | Newman collection kasutab eelmise päringu `id`-d muutujana — testid on järjestikused |
| Java 26 on uus versioon, mõned teegid võivad olla ebastabiilsed | Build võib ebaõnnestuda | Kasutame Spring Boot 3.5.14 LTS seeriat, mis on Java 26-ga testitud |
| Postman/Newman testid eeldavad töötavat serverit pordil 8080 | Newman testid ebaõnnestuvad kui server ei tööta | README sisaldab serveri käivitamise juhised enne Newman käivitamist |
| Testid on iseseisvad JUnit tasemel (`@Transactional` rollback) | JUnit testid ei sõltu üksteisest | Iga JUnit test saab puhta andmebaasi |

---

## 5. Edukuse kriteeriumid

- Kõik JUnit testjuhtumid staatusega **PASS**
- Ükski kriitiline ega kõrge prioriteediga bug pole lahendamata
- JaCoCo raport näitab service kihil vähemalt **90% line coverage**
- Kõik controller endpointid on MockMvc testidega kaetud (kõik staatuskoodid)
- Newman käivitab kogu collection'i läbi **0 veaga**
- Rakendus käivitub `./gradlew bootRun` käsuga vigadeta
- H2 console on kättesaadav aadressil `http://localhost:8080/h2-console`
