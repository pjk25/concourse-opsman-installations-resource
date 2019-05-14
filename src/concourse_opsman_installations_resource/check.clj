(ns concourse-opsman-installations-resource.check
  (:require [clojure.data.json :as json]
            [clojure.spec.alpha :as s]
            [concourse-opsman-installations-resource.om-cli :as om-cli]))

(s/def ::finished_at string?)

(s/def ::version (s/keys :req-un [::finished_at]))

(defn is-gte-version?
  [version1 version2]
  (<= (compare (:finished_at version1) (:finished_at version2)) 0))

(s/fdef is-gte-version?
        :args (s/cat :version1 ::version :version2 ::version)
        :ret boolean?)

(defn check
  [cli-options om payload]
  (let [{previous-version :version} payload]
    (if (:debug cli-options)
      (binding [*out* *err*]
        (println "Checking for installations since" previous-version)))
    (let [versions (->> (om-cli/curl om "/api/v0/installations")
                        (#(json/read-str % :key-fn keyword))
                        (:installations)
                        (map #(select-keys % [:finished_at]))
                        (reverse))]
      (if previous-version
        (filterv #(is-gte-version? previous-version %) versions)
        [(first versions)]))))

(s/fdef check
        :args (s/cat :cli-options map?
                     :om ::om-cli/om
                     :payload map?)
        :ret ::version)

