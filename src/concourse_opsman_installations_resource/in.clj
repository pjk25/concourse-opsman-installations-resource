(ns concourse-opsman-installations-resource.in
  (:require [clojure.java.io :as io]
            [clojure.data.json :as json]
            [clojure.spec.alpha :as s]
            [concourse-opsman-installations-resource.util :as util]
            [concourse-opsman-installations-resource.om-cli :as om-cli]))

(s/def ::finished_at string?)

(s/def ::version (s/keys :req-un [::finished_at]))

(s/def ::destination string?)

(defn- same-installation?
  [version installation]
  (= version (select-keys installation [:finished_at])))

(defn in
  [cli-options om payload]
  (let [{:keys [version]} payload]
    (if (:debug cli-options)
      (binding [*out* *err*]
        (println "Retrieving version" version)))
    (let [installation (->> (om-cli/curl om "/api/v0/installations")
                            (:installations)
                            (filter #(same-installation? version %))
                            (first))]
      (if (nil? installation)
        (throw (Exception. (str "Version " version "not found."))))
      
      (let [json-file (io/file (:destination cli-options) "installation.json")]
        (if (:debug cli-options)
          (binding [*out* *err*]
            (println "Writing installation data to" (.toString json-file))))
        (with-open [w (io/writer json-file)]
          (json/write installation w)))
      {:version (select-keys installation [:finished_at])
       :metadata [{:name "status" :value (:status installation)}]})))

(s/fdef in
        :args (s/cat :cli-options (s/keys :req-un [::destination])
                     :om ::om-cli/om
                     :payload (s/keys :req-un [::version]))
        :ret ::version)

