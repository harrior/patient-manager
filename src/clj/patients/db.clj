(ns patients.db
  "Connects to the patients database and performs migrations."
  (:require [migratus.core :as migratus]
            [next.jdbc :as jdbc]
            [patients.config :as config]
            [patients.db-jsonb]))

(defn db-conn
  "The database connection"
  []
  (jdbc/get-datasource config/db-spec))

(defn init-db
  "Create a database and grant all privileges to a user if it does not already exist.

  db-spec - a map containing configuration for Database"
  [db-spec]
  (let [{:keys [user dbname]} db-spec
        db (next.jdbc/get-datasource (dissoc db-spec :dbname))
        database-exists? (:exists
                          (next.jdbc/execute-one!
                           db
                           ["SELECT EXISTS(SELECT datname FROM pg_catalog.pg_database WHERE datname = ?);" dbname]))]
    (when-not database-exists?
      (next.jdbc/execute-one! db [(format "CREATE DATABASE %s" dbname)]))
    (next.jdbc/execute-one! db [(format "GRANT ALL PRIVILEGES ON DATABASE %s TO %s" dbname user)])))

(defn make-migrations
  "Initializes  Migratus library and runs database migrations"
  [migratus-config]
  (migratus/init migratus-config)
  (migratus/migrate migratus-config))
