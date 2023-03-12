(ns patients.config
  "Namespace for handling configuration data.")

(def PORT 8000)

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
  ^{:doc "Configuraton for Migratus."}
  migratus-config
  {:store :database
   :migration-dir "migrations"
   :db db-spec})