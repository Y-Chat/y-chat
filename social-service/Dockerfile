FROM maven:3.9.6-eclipse-temurin-21-jammy as builder
WORKDIR /build
COPY pom.xml ./
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean install -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre-jammy
WORKDIR /social-service
EXPOSE 80
COPY --from=builder "/build/target/social-service-*.jar" "/social-service/social-service.jar"
ENTRYPOINT ["java", "-jar", "social-service.jar"]