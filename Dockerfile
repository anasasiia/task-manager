FROM gradle:7.6.1-jdk17

WORKDIR /src

COPY ./ .

RUN gradle installDist

CMD build/install/app/bin/app