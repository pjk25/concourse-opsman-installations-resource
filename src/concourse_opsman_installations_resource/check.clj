(ns concourse-opsman-installations-resource.check
  (:require [clojure.data.json :as json]
            [clojure.spec.alpha :as s]
            [concourse-opsman-installations-resource.util :as util]
            [concourse-opsman-installations-resource.om-cli :as om-cli]))

(s/def ::finished_at string?)

(s/def ::version (s/keys :req-un [::finished_at]))

(defn is-gte-version?
  [version1 version2]
  (comment (binding [*out* *err*]
    (println "is-gte-version?" version1 version2)))
  (<= (compare (:finished_at version1) (:finished_at version2)) 0))

(s/fdef is-gte-version?
        :args (s/cat :version1 ::version :version2 ::version)
        :ret boolean?)

(defn check
  [cli-options om payload]
  (let [{previous-version :version} payload]
    (binding [*out* *err*]
      (println "Checking for installations since" previous-version))
    (let [installations-result (om-cli/curl om "/api/v0/installations")
          {:keys [installations]} (util/keywordize installations-result)
          versions (map #(select-keys % [:finished_at]) installations)]
      (comment (binding [*out* *err*]
        (println "Found versions:")
        (clojure.pprint/pprint versions)))
      (filterv #(or (nil? previous-version)
                    (is-gte-version? previous-version %)) (reverse versions)))))

(s/fdef check
        :args (s/cat :cli-options map?
                     :om ::om-cli/om
                     :payload map?)
        :ret ::version)

