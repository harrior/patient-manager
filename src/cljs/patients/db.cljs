(ns patients.db
  (:require [re-frame.core :as rf]))

(rf/reg-event-db
 ::initialize
 (fn [_ _]
   {:app {:lang :ru}}))