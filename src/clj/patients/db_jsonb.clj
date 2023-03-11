(ns patients.db-jsonb
  "This namespace provides functions for working with JSON/JSONB PostgreSQL field types.
   It includes functions for converting Clojure data to and from PGobject objects containing JSON,
   as well as protocols for use with the `next.jdbc` library to allow Clojure data to be used with
   PostgreSQL's `json` and `jsonb` types.
   Details: https://github.com/seancorfield/next-jdbc/blob/develop/doc/tips-and-tricks.md#working-with-json-and-jsonb"
  (:require [next.jdbc.prepare :as prepare]
            [next.jdbc.result-set :as rs]
            [jsonista.core :as json])
  (:import (org.postgresql.util PGobject)
           [java.sql PreparedStatement]))

(set! *warn-on-reflection* true)

(def mapper (json/object-mapper {:decode-key-fn keyword}))
(def ->json json/write-value-as-string)
(def <-json #(json/read-value % mapper))

(defn ->pgobject
  "Transforms Clojure data to a PGobject that contains the data as
  JSON. PGObject type defaults to `jsonb` but can be changed via
  metadata key `:pgtype`"
  [x]
  (let [pgtype (or (:pgtype (meta x)) "jsonb")]
    (doto (PGobject.)
      (.setType pgtype)
      (.setValue (->json x)))))

(defn <-pgobject
  "Transform PGobject containing `json` or `jsonb` value to Clojure
  data."
  [^org.postgresql.util.PGobject v]
  (let [type  (.getType v)
        value (.getValue v)]
    (if (#{"jsonb" "json"} type)
      (when value
        (with-meta (<-json value) {:pgtype type}))
      value)))

(extend-protocol prepare/SettableParameter
  clojure.lang.IPersistentMap
  (set-parameter [m ^PreparedStatement s i]
    (.setObject s i (->pgobject m)))

  clojure.lang.IPersistentVector
  (set-parameter [v ^PreparedStatement s i]
    (.setObject s i (->pgobject v))))

(extend-protocol rs/ReadableColumn
  org.postgresql.util.PGobject
  (read-column-by-label [^org.postgresql.util.PGobject v _]
    (<-pgobject v))
  (read-column-by-index [^org.postgresql.util.PGobject v _2 _3]
    (<-pgobject v)))