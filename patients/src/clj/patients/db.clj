(ns patients.db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql] 
            [patients.db-jsonb]
            [patients.config :as config]))

;; NOTE: needs use logger
(println "Connect to Database")
(def ds (jdbc/get-datasource config/db-spec))
(println "Connected.")

;; NOTE: may be needs move migarion to diffrent file
(println "Make migrations")
(jdbc/execute! ds ["CREATE TABLE IF NOT EXISTS patients (id UUID NOT NULL PRIMARY KEY, patient JSONB NOT NULL DEFAULT '{}')"])
(println "End migrations")
