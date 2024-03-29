(ns user
  (:require [clojure.test :refer [run-tests run-all-tests]]))

(require '[clojure.tools.namespace.repl :refer [refresh set-refresh-dirs]])

(set-refresh-dirs "src" "test")

(defn run-my-tests
  []
  (refresh)
  (run-all-tests #"concourse-opsman-installations-resource.*-test"))
