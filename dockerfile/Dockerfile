FROM openjdk
COPY listener-0.1-jar-with-dependencies.jar /listener-0.1.jar
COPY config.properties /config.properties
ENTRYPOINT ["java","-jar","/listener-0.1.jar"]
CMD ["PROTOCOL","TYPE","LOWPACKAGE","HIGHPACKAGE","TIME"]
