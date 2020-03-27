# CSI5354 - Assignments 02

## API for Person

- Get all persons: `GET /app/persons`
- Get a person: `GET /app/persons/1`
- Get cars for a person: `GET /app/persons/1/cars`
- Create a person: `POST /app/persons`
- Update a person: `PUT /app/persons/1`
- Remove a person: `DELETE /app/persons/1`

## API for Car

- Get all cars: `GET /app/cars`
- Get a car: `GET /app/cars/1`
- Create a car: `POST /app/cars`
- Update a car: `PUT /app/cars/1`
- Remove a car: `DELETE /app/cars/1`

## Run tests

```
$ mvn clean test
```

- JUnit tests: `CarModelTest` and `PersonModelTest`
- Arquillian Integration tests: `CarResourceTest` and `PersonResourceTest`
- Consumer Pact tests: `CarConsumerTest` and `PersonConsumerTest`

## Run application

```
$ mvn thorntail:run
```
