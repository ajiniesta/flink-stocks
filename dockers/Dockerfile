FROM ubuntu:14.04.4

USER root

RUN apt-get update

RUN apt-get -y install curl tar ca-certificates

RUN mkdir -p /usr/java/default && \
    curl -Ls 'http://download.oracle.com/otn-pub/java/jdk/8u74-b02/jre-8u74-linux-x64.tar.gz' -H 'Cookie: oraclelicense=accept-securebackup-cookie' | \
    tar --strip-components=1 -xz -C /usr/java/default/

ENV JAVA_HOME /usr/java/default/
ENV PATH $PATH:$JAVA_HOME/bin

RUN mkdir -p /opt/kafka && \
  curl -Ls 'http://apache.rediris.es/kafka/0.9.0.1/kafka_2.10-0.9.0.1.tgz' |  \
  tar --strip-components=1 -xz -C /opt/kafka

ENV KAFKA_HOME /opt/kafka
ENV PATH $PATH:$KAFKA_HOME/bin

ADD startup.sh /usr/bin/startup.sh

RUN sed -i -e 's/\r$//' /usr/bin/startup.sh

EXPOSE 2181
EXPOSE 9092

CMD ["startup.sh"]
