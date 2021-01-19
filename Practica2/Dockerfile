FROM openjdk:15-jdk-alpine
COPY "Peliculas.json" "Peliculas.json"
COPY "./target/Practica2-0.0.1-SNAPSHOT.jar" "app.jar"
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "app.jar"]