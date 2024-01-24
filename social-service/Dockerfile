FROM eclipse-temurin:21-jdk-jammy as builder
WORKDIR /build
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw clean install -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre-jammy
WORKDIR /social-service
EXPOSE 80
COPY --from=builder "/build/target/social-service-*.jar" "/social-service/social-service.jar"
ENTRYPOINT ["java", "-jar", "social-service.jar"]