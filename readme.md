# Inventory Service

## Tech
- Kotlin
- Spring Boot
- JPA / Hibernate
- H2 in-memory database

## Build & test application
```
./gradlew clean build
```

## Start application
Via your IDE or:
```
./gradlew bootRun
```
navigate to [swagger-ui](http://localhost:8080/swagger-ui.html)

navigate to [h2-console](http://localhost:8080/h2-console) (default login applies: username=sa, no password)

## Usage via curl
(No guarantees, below is copied from swagger-ui without testing)
1. Create some stock
```
curl -X 'POST' \
  'http://localhost:8080/stock' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "storeName": "GAMMA Utrecht",
  "productCode": 123456789,
  "totalStock": 99
}'
```
2. Update stock amount
```
curl -X 'PATCH' \
  'http://localhost:8080/stock/1' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "totalStock": 100
}'
```
3. Create reservation
```
curl -X 'POST' \
  'http://localhost:8080/reservation' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "storeName": "GAMMA Utrecht",
  "productCode": 123456789,
  "amount": 10
}'
```
4. List stock
```
curl -X 'GET' \
  'http://localhost:8080/stock' \
  -H 'accept: */*'   
```

5. Wait 5 min for reservation expiration and repeat step 4. (Note: Expiry time can be changed via properties file.)


6. Delete stock
```
curl -X 'DELETE' \
  'http://localhost:8080/stock/1' \
  -H 'accept: */*'
```
