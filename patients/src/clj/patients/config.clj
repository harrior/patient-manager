(ns patients.config
  "Namespace for handling configuration data.")

(def PORT 8000)

;; FIXIT: make loading params from ENV
(def
  ^{:doc "Configuration for Database."}
  db-spec
  "A map containing the database connection details"
  {:dbtype "postgresql"
   :dbname "medicine"
   :host "localhost"
   :user "postgres"
   :password "postgres"})

(def
  ^{:doc "Configuration for Database (test)."}
  db-spec-test
  "A map containing the database connection details"
  {:dbtype "postgresql"
   :dbname "medicine_test"
   :host "localhost"
   :user "postgres"
   :password "postgres"})


(def
  ^{:doc "Configuraton for Migratus."}
  migratus-config
  {:store :database
   :migration-dir "migrations"
   :db db-spec})

(def
  ^{:doc "Configuraton for Migratus (test)."}
  migratus-config-test
  {:store :database
   :migration-dir "migrations"
   :db db-spec-test})