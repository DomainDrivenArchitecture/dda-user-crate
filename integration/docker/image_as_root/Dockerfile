FROM ubuntu

RUN apt-get update > /dev/null \
    && apt-get -qqy upgrade > /dev/null \
    && apt-get -qqy install openjdk-11-jre > /dev/null

RUN apt-get -qqy install curl apt-utils sudo gnupg > /dev/null

COPY target/uberjar/dda-user-standalone.jar /app/dda-user-standalone.jar
COPY integration/docker/image_as_root/user.edn /app/user.edn

RUN java -jar /app/dda-user-standalone.jar /app/user.edn
