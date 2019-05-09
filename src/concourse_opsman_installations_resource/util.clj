(ns concourse-opsman-installations-resource.util)

(defn keywordize [parsed-json]
  (if (map? parsed-json)
    (zipmap (map keyword (keys parsed-json)) 
            (map keywordize (vals parsed-json)))
    (if (sequential? parsed-json)
      (map keywordize parsed-json)
      parsed-json)))
