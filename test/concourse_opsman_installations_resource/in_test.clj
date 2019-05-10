(ns concourse-opsman-installations-resource.in-test
  (:require [clojure.test :refer [deftest is testing run-tests]]
            [clojure.spec.test.alpha :as stest]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [concourse-opsman-installations-resource.om-cli :as om-cli]
            [concourse-opsman-installations-resource.in :as in])
  (:import [java.nio.file Files]
           [java.nio.file.attribute FileAttribute]))

(def fake-om
  (reify om-cli/Om
    (curl [this path]
      (condp = path
        "/api/v0/installations" (json/read-str (slurp "resources/fixtures/installations.json") :key-fn keyword)
        (throw (Exception. (slurp "resources/fixtures/curl_not_found.html")))))))

(deftest in
  (stest/instrument `in/in)

  (testing "with a valid version"
    (let [temp-dir (Files/createTempDirectory "concourse-opsman-installations-resource-" (into-array FileAttribute []))
          destination (.toString temp-dir)]
      (is (= (in/in {:destination destination} fake-om {:version {:finished_at "2018-04-26T03:14:45.528Z"}})
           {:version {:finished_at "2018-04-26T03:14:45.528Z"}
            :metadata [{:name "status" :value "succeeded"}]}))
;       (in/in {:destination destination} fake-om {:version {:finished_at "2018-04-26T03:14:45.528Z"}})
      (is (json/read-str (slurp (io/file destination "installation.json"))))))

  (testing "when the version does not exist"
    (is (thrown? Exception (in/in {:destination ""}
                                  fake-om
                                  {:version {:finished_at "2000-04-26T03:14:45.528Z"}})))))

