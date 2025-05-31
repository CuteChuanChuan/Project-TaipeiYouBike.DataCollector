FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/scala-2.13/BikeDataCollector-assembly-0.1.0-SNAPSHOT.jar /app/application.jar

ENV JAVA_OPTS="-Xms512m -Xmx1024m"

ENTRYPOINT ["java", "-jar", "/app/application.jar"]