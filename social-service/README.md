# Social Service
## TODO
- Security: JWT, Verify if user is allowed to perform action, Injection, CORS, CSRF
- Endpoint comments for security
- Logging
- Testing

## About
- The Social service manages users, groups, and chats and their relationships
- It enables the frontend and the other services to not worry about the consistency and business
  logic of most of the domain model.

## Technologies
- Language: Java, Spring Boot, Maven, Eclipse Temurin
- API: OpenAPI, Springdoc
- ORM and Validation: Jakarta, Spring Data JPA
- DB: See the `social-db`

## API
- The API is documented with OpenAPI
- It is auto-generated from the service which has some limitations, especially for the validation,
  as the documentation does not differentiate between update and creation although the service does;
  for these special cases extra documentation can be found at the endpoints
- One can find the JSON file at the `/api-docs` endpoint and the
  YAML version at `/api-docs.yaml`
- A visualization of the API can be accessed at `/swagger-ui`


## Object Model
### Class Diagram
- TODO mention settings not necessarily implemented
- TODO mention relation to db
- TODO

### Validation
1. Syntax, type, and value issues are caught immediately at the controller layer
2. The services further filter out bad request from the business logic perspective
3. The model layer defines the correct syntax and guarantees data consistency
4. The DB constraints act as a last stronghold, even though the application should have filtered out
   all issues by then

## Deployment
### Kubernetes
- TODO

### Local Deployment
1. Change to the Y-Chat root directory
2. Start Docker Compose with the `social-db-compose.yml` and `social-service-compose` files:
```shell
docker-compose -f "social-db-compose.yml" "social-service-compose.yml" up -d
```
3. Verify that everything started correctly:
```shell
docker-compose -f "social-db-compose.yml" "social-service-compose.yml" ps
```
4. Access the endpoints at `localhost:38080`
5. The connection instructions for the DB can be found in the `social-db` readme
