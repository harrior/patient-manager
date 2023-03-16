(ns patients.db
  "Initializes patients database with default values."
  (:require [re-frame.core :as rf]))

(rf/reg-event-db
 ::initialize
 (fn [_ _]
   {:app {:lang :ru
          :active-page :patients}}))