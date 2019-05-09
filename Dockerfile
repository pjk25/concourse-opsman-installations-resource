FROM oracle/graalvm-ce:1.0.0-rc16 AS BASE

RUN curl -O https://download.clojure.org/install/linux-install-1.10.0.442.sh
RUN chmod +x linux-install-1.10.0.442.sh
RUN ./linux-install-1.10.0.442.sh

RUN curl -LO https://github.com/pivotal-cf/om/releases/download/1.0.0/om-linux

ENV GRAALVM_HOME /opt/graalvm-ce-1.0.0-rc16/

ADD scripts scripts
ADD src src
ADD resources resources
ADD test test
ADD deps.edn .

RUN ./scripts/test.sh
RUN ./scripts/compile.sh

FROM alpine
COPY --from=BASE /om-linux /usr/local/bin/om
COPY --from=BASE /concourse-opsman-installations-resource /usr/local/bin

ADD opt-resource /opt/resource

CMD ["/concourse-opsman-installations-resource"]