# CSI5354 - Midterm 01

## API for Person

- Get all persons: `GET /app/persons`
- Get a person: `GET /app/persons/{id}`
- Create a person: `POST /app/persons`
- Update a person: `PUT /app/persons/{id}`
- Remove a person: `DELETE /app/persons/{id}`

## API for Team

- Get all teams: `GET /app/teams`
- Get a team: `GET /app/teams/{id}`
- Create a team: `POST /app/teams`
- Update a team: `PUT /app/teams/{id}`
- Remove a team: `DELETE /app/teams/{id}`
- Add a team member: `GET /app/teams/{teamId}/member/{personId}`
- Add a team leader: `GET /app/teams/{teamId}/leader/{personId}`
- Add a preferred team: `GET /app/teams/{teamId}/preferred/{preferredTeamId}`

## Run tests

```
$ mvn clean test
```

## Run application

```
$ mvn clean thorntail:run -DskipTests
```

## Deploy application in Minishift

```
$ mvn clean fabric8:deploy -Popenshift -DskipTests
```

## Undeploy application

```
$ mvn fabric8:undeploy -Popenshift -DskipTests
```
