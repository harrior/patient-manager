(ns patients.db
  "Namespace for connecting to the patients database and performing migrations."
  (:require [clojure.tools.logging :as log]
            [migratus.core :as migratus]
            [next.jdbc :as jdbc]
            [patients.config :as config]
            [patients.db-jsonb]))

(log/info "Connect to Database")
(def db-conn (jdbc/get-datasource config/db-spec))
(log/info "Connected to Database.")

(migratus/init config/migratus-config)
(migratus/migrate config/migratus-config)
