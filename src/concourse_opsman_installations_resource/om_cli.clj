(ns concourse-opsman-installations-resource.om-cli
  (:require [clojure.data.json :as json]
            [clojure.spec.alpha :as s]
            [clojure.java.shell :as shell]))

(s/def ::url string?)

(s/def ::username string?)

(s/def ::password string?)

(s/def ::opsmgr (s/keys :req-un [::url ::username ::password]))

(defprotocol Om
  (curl [this path]))

(s/def ::om #(satisfies? Om %))

(deftype OmCli [opsmgr]
  Om
  (curl [this path]
    (let [{:keys [url username password]} opsmgr
          {:keys [exit out err]} (shell/sh "om"
                                           "--target" url
                                           "--username" username
                                           "--password" password
                                           "curl"
                                           "--silent"
                                           "--path" path)]
      (if (= 0 exit)
        out
        (throw (ex-info err {:path path}))))))
