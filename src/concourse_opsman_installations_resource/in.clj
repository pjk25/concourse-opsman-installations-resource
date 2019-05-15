(ns concourse-opsman-installations-resource.in
  (:require [clojure.java.io :as io]
            [clojure.data.json :as json]
            [clojure.spec.alpha :as s]
            [concourse-opsman-installations-resource.om-cli :as om-cli]))

(s/def ::finished_at string?)

(s/def ::version (s/keys :req-un [::finished_at]))

(s/def ::destination string?)

(defn- same-installation?
  [version installation]
  (= version (select-keys installation [:finished_at])))

(defn- write-as-json
  [cli-options content filename]
  (let [json-file (io/file (:destination cli-options) filename)]
    (if (:debug cli-options)
      (binding [*out* *err*]
        (println "Writing data to" (.toString json-file))))
    (with-open [w (io/writer json-file)]
      (json/write content w))))

(defn in
  [cli-options om payload]
  (let [{:keys [version params]} payload]
    (if (:debug cli-options)
      (binding [*out* *err*]
        (println "Retrieving version" version)))
    (let [installations (->> (om-cli/curl om "/api/v0/installations")
                            (#(json/read-str % :key-fn keyword))
                            (:installations))
          installation (first (filter #(same-installation? version %) installations))]

      (if (nil? installation)
        (throw (ex-info "Version not found" {:version version})))

      (if (:include_history params)
        (write-as-json cli-options installations "installations.json"))

      (write-as-json cli-options installation "installation.json")
      
      (if (:fetch_logs params)
        (let [installation_logs (om-cli/curl om (format "/api/v0/installations/%d/logs" (:id installation)))
              logs-file (io/file (:destination cli-options) "installation_logs.json")]
          (if (:debug cli-options)
            (binding [*out* *err*]
              (println "Writing installation logs to" (.toString logs-file))))
          (spit logs-file installation_logs)))
      {:version (select-keys installation [:finished_at])
       :metadata [{:name "status" :value (:status installation)}]})))

(s/fdef in
        :args (s/cat :cli-options (s/keys :req-un [::destination])
                     :om ::om-cli/om
                     :payload (s/keys :req-un [::version]))
        :ret ::version)

