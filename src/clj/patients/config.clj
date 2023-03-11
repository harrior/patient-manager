(ns patients.config
  "Namespace for handling configuration data.")

(def PORT 8000)

;; FIXIT: make loading params from ENV
(def
  ^{:doc "Configuration for Database."}
  db-spec
  "A map containing the database connection details"
  {:dbtype "postgresql"
   :dbname (or (System/getenv "POSTGRES_DB")
               "medicine")
   :host (or (System/getenv "POSTGRES_HOST")
             "localhost")
   :user (or (System/getenv "POSTGRES_USER")
             "postgres")
   :password (or (System/getenv "POSTGRES_PASSWORD")
                 "postgres")})

(def
  ^{:doc "Configuration for Database (test)."}
  db-spec-test
  "A map containing the database connection details"
  {:dbtype "postgresql"
   :dbname (or (System/getenv "POSTGRES_TEST_DB")
               "medicine_test")
   :host (or (System/getenv "POSTGRES_HOST")
             "localhost")
   :user (or (System/getenv "POSTGRES_USER")
             "postgres")
   :password (or (System/getenv "POSTGRES_PASSWORD")
                 "postgres")})

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
   :migration-dir "migrationstest"
   :db db-spec-test})