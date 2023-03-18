(ns patients.components.helpers
  "A collection of helper functions")

(defn input-value-extractor
  "Extracts the value from an input event."
  [event]
  (-> event
      .-target
      .-value))

(defn remove-empty-keys
  "Removes key-value pairs with empty or nil values from a map."
  [m]
  (into {} (for [[k v] m
                 :when (not (or (nil? v)
                                (empty? v)))]
             [k v])))