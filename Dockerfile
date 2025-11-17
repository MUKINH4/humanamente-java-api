FROM gradle:8.11-alpine AS build
WORKDIR /app
COPY . .
RUN ./gradlew build -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=build /app/build/libs/*.jar app.jar

RUN chown -R appuser:appgroup /app
USER appuser

ENTRYPOINT ["java", "-jar", "app.jar"]