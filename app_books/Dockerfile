# Generar la iamgen docker: docker build -t app-authors .


FROM eclipse-temurin:17.0.10_7-jre-alpine

RUN mkdir /app
WORKDIR /app

COPY  build/quarkus-app/app ./app
COPY  build/quarkus-app/lib ./lib
COPY  build/quarkus-app/quarkus ./quarkus
COPY  build/quarkus-app/quarkus-run.jar app.jar

CMD ["java", "-jar", "app.jar"]