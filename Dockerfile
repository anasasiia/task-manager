FROM gradle:7.6.1-jdk17

WORKDIR /src

COPY ./ .

RUN curl -L https://services.gradle.org/distributions/gradle-7.6.1-bin.zip -o gradle-7.6.1-bin.zip

CMD build/install/app/bin/app