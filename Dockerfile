FROM clojure:tools-deps

RUN curl -L https://github.com/pivotal-cf/om/releases/download/1.0.0/om-linux -o /usr/local/bin/om
RUN chmod +x /usr/local/bin/om

ADD scripts scripts
ADD src src
ADD resources resources
ADD test test
ADD deps.edn .

RUN ./scripts/test.sh

RUN rm -dR resources test scripts

ADD opt-resource /opt/resource

CMD ["./scripts/run.sh"]