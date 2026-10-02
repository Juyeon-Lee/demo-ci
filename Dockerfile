# CI에서 Maven 으로 만든 jar 를 그대로 사용 (이미지 안에서 재빌드하지 않음)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN mkdir -p /app/data

COPY app.jar app.jar

EXPOSE 8080

VOLUME ["/app/data"]

CMD ["java", "-jar", "app.jar"]
