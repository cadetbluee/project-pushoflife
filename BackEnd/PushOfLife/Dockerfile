FROM openjdk:17-jdk
COPY build/libs/PushOfLife-0.0.1-SNAPSHOT.jar /pol.jar
COPY aed_data.csv .
ENTRYPOINT ["java", "-jar", "/pol.jar"]



