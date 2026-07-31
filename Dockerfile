FROM eclipse-temurin:21-jre-alpine

RUN apk upgrade --no-cache

WORKDIR /app

RUN addgroup -S appgroup \
    && adduser -S -G appgroup -H appuser

COPY --chown=appuser:appgroup \
    target/devsecops-lab-0.0.1-SNAPSHOT.jar app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]