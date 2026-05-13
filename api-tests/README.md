# API automaattestid — Newman

## Eeldused

- [Node.js](https://nodejs.org/) paigaldatud
- Newman paigaldatud globaalselt:
  ```bash
  npm install -g newman
  ```
- Rakendus töötab aadressil `http://localhost:8080`

## Käivitamine

**1. Käivita rakendus (eraldi terminalis, projekti juurkaustast):**

Linux / macOS:
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

Windows (cmd):
```cmd
gradlew.bat bootRun --args="--spring.profiles.active=dev"
```

> Newman ise ei vaja H2 console'i, aga `dev` profiili kasutamine hoiab käivituskäsu kogu projektis ühtsena.

**2. Käivita Newman collection (projekti juurkaustast):**

Linux / macOS:
```bash
newman run api-tests/TodoAPI.postman_collection.json \
  -e api-tests/TodoAPI.postman_environment.json \
  --reporters cli
```

Windows (cmd, üks rida):
```cmd
newman run api-tests\TodoAPI.postman_collection.json -e api-tests\TodoAPI.postman_environment.json --reporters cli
```

## Mida collection katab

Collection sisaldab 15 päringut järgmises järjekorras:

| # | Päring | Eesmärk |
|---|---|---|
| 1 | GET /api/todos | Tühi loend alguses |
| 2 | POST /api/todos | Loomine — salvestab `todoId` muutujasse |
| 3 | POST /api/todos (tühi title) | 400 valideerimisviga |
| 4 | POST /api/todos (puuduv keha) | 400 vigane JSON |
| 5 | POST /api/todos (vale Content-Type) | 415 |
| 6 | GET /api/todos | Loend sisaldab 1 kirjet |
| 7 | GET /api/todos/{todoId} | Pärimine ID järgi |
| 8 | GET /api/todos/999999 | 404 olematu ID |
| 9 | GET /api/todos/abc | 400 vale ID tüüp |
| 10 | PUT /api/todos/{todoId} | Uuendamine |
| 11 | PUT /api/todos/999999 | 404 olematu ID |
| 12 | PUT /api/todos/{todoId} (tühi title) | 400 valideerimisviga |
| 13 | PATCH /api/todos/{todoId} | 405 meetod pole lubatud |
| 14 | DELETE /api/todos/999999 | 404 olematu ID |
| 15 | DELETE /api/todos/{todoId} | Kustutamine — 204 |
| 16 | GET /api/todos/{todoId} | 404 pärast kustutamist |

## Märkus collection muutujate kohta

`POST /api/todos` päring salvestab loodud todo `id` automaatselt collection muutujasse `todoId`:
```javascript
pm.collectionVariables.set("todoId", json.id);
```
Kõik järgnevad päringud kasutavad seda muutujat (`{{todoId}}`), nii et testid töötavad järjestikuselt ilma käsitsi väärtusi sisestamata.
