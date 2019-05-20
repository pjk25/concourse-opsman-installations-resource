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
        "/api/v0/installations" (slurp "resources/fixtures/installations.json")
        "/api/v0/installations/1/logs" (slurp "resources/fixtures/logs.json")
        (throw (Exception. (slurp "resources/fixtures/curl_not_found.html")))))))

(deftest in
  (stest/instrument `in/in)

  (testing "with a valid version"
    (let [temp-dir (Files/createTempDirectory "concourse-opsman-installations-resource-" (into-array FileAttribute []))
          destination (.toString temp-dir)]
      (is (= (in/in {:destination destination} fake-om {:version {:finished_at "2018-04-26T03:14:45.528Z"}})
             {:version {:finished_at "2018-04-26T03:14:45.528Z"}
              :metadata [{:name "status" :value "succeeded"}]}))
      (is (= (:id (json/read-str (slurp (io/file destination "installation.json")) :key-fn keyword))
             1))
      (is (not (.exists (io/file destination "installation_logs.json"))))))

  (testing "when the version does not exist"
    (is (thrown? Exception (in/in {:destination ""}
                                  fake-om
                                  {:version {:finished_at "2000-04-26T03:14:45.528Z"}}))))

  (testing "when logs are requested"
    (let [temp-dir (Files/createTempDirectory "concourse-opsman-installations-resource-" (into-array FileAttribute []))
          destination (.toString temp-dir)]
      (is (= (in/in {:destination destination} fake-om {:version {:finished_at "2018-04-26T03:14:45.528Z"}
                                                        :params {:fetch_logs true}})
             {:version {:finished_at "2018-04-26T03:14:45.528Z"}
              :metadata [{:name "status" :value "succeeded"}]}))
      (is (= (:id (json/read-str (slurp (io/file destination "installation.json")) :key-fn keyword))
             1))
      (is (json/read-str (slurp (io/file destination "installation_logs.json")))))))

