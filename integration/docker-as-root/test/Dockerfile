FROM dda-user-crate

RUN apt-get -yqq install curl > /dev/null

RUN curl -L -o /app/serverspec.jar https://github.com/DomainDrivenArchitecture/dda-serverspec-crate/releases/download/2.0.0/dda-serverspec-standalone.jar

COPY serverspec.edn /app/serverspec.edn

RUN java -jar /app/serverspec.jar /app/serverspec.edn -v
