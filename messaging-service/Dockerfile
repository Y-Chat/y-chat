FROM gradle:latest AS build
WORKDIR /build
COPY . .


RUN ./gradlew build -x test

FROM openjdk:17-oracle
# openjdk:17-alpine does not exist for M1 macs!
WORKDIR /app
VOLUME /tmp
COPY --from=build build/build/libs/test*SNAPSHOT.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]