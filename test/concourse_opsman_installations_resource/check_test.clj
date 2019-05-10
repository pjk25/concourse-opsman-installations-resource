(ns concourse-opsman-installations-resource.check-test
  (:require [clojure.test :refer [deftest is testing run-tests]]
            [clojure.spec.test.alpha :as stest]
            [clojure.data.json :as json]
            [concourse-opsman-installations-resource.om-cli :as om-cli]
            [concourse-opsman-installations-resource.check :as check]))

(def fake-om
  (reify om-cli/Om
    (curl [this path]
      (condp = path
        "/api/v0/installations" (slurp "resources/fixtures/installations.json")
        (throw (Exception. (slurp "resources/fixtures/curl_not_found.html")))))))

(deftest check
  (stest/instrument `check/check)
  (testing "with no current last version"
    (is (= (check/check {} fake-om {})
           [{:finished_at "2018-04-26T03:14:45.528Z"}
            {:finished_at "2018-04-26T03:27:54.035Z"}])))
  (testing "when there are new versions"
    (is (= (check/check {} fake-om {:version {:finished_at "2018-04-26T03:14:45.528Z"}})
           [{:finished_at "2018-04-26T03:14:45.528Z"}
            {:finished_at "2018-04-26T03:27:54.035Z"}])))
  (testing "when there are no new versions"
    (is (= (check/check {} fake-om {:version {:finished_at "2018-04-26T03:27:54.035Z"}})
           [{:finished_at "2018-04-26T03:27:54.035Z"}])))
  (testing "when the last version no longer exists"
    (is (= (check/check {} fake-om {:version {:finished_at "2010-06-14T02:15:36.495Z"}})
           [{:finished_at "2018-04-26T03:14:45.528Z"}
            {:finished_at "2018-04-26T03:27:54.035Z"}]))))

(deftest is-gte-version?
  (stest/instrument `check/is-gte-version?)
  (is (not (check/is-gte-version? {:finished_at "2018-04-26T03:14:45.528Z"} {:finished_at "2018-04-26T02:35:43.175Z"})))
  (is (check/is-gte-version? {:finished_at "2018-04-26T03:14:45.528Z"} {:finished_at "2018-04-26T03:14:45.528Z"}))
  (is (check/is-gte-version? {:finished_at "2018-04-26T03:14:45.528Z"} {:finished_at "2018-04-26T03:27:54.035Z"})))
