(ns patients.components.helpers)

(defn input-value-extractor
  [event]
  (-> event
      .-target
      .-value))

(defn remove-empty-keys
  [m]
  (into {} (for [[k v] m
                 :when (not (or (nil? v)
                                (empty? v)))]
             [k v])))