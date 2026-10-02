# ---- build ----
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -B -DskipTests package \
    && find target -name '*.jar' ! -name '*-plain.jar' -exec cp {} /app/app.jar \;

# ---- run ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN mkdir -p /app/data

COPY --from=build /app/app.jar app.jar

EXPOSE 8080

VOLUME ["/app/data"]

ENTRYPOINT ["java", "-jar", "app.jar"]
