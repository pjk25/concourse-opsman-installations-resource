(ns concourse-opsman-installations-resource.util-test
  (:require [clojure.test :refer [deftest is testing run-tests]]
            [clojure.data.json :as json]
            [concourse-opsman-installations-resource.util :as util]))

(deftest keywordize
  (testing "a simple map"
    (is (= (util/keywordize {"a" 1})
           {:a 1})))
  (testing "a nested map"
    (is (= (util/keywordize {"a" 1
                             "b" [{"c" 2}]})
           {:a 1
            :b [{:c 2}]}))))
