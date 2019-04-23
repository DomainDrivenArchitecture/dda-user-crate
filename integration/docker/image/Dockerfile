FROM ubuntu

RUN apt-get update && apt-get upgrade -y && apt-get install openjdk-8-jre -y

RUN apt-get install curl gnupg2 apt-utils sudo -y

COPY target/uberjar/dda-user-standalone.jar /app/dda-user-standalone.jar
COPY integration/docker/image/user.edn /app/user.edn

RUN java -jar /app/dda-user-standalone.jar /app/user.edn
