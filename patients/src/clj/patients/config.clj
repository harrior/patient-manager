(ns patients.config)

(def PORT 8000)

;; FIXIT: make loading params from ENV
(def db-spec
  {:dbtype "postgresql"
   :dbname "medicine"
   :host "localhost"
   :user "postgres"
   :password "postgres"})