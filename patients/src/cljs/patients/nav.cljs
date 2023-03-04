(ns patients.nav
  (:require [re-frame.core :as rf]))

;;
;; Events
;;

(rf/reg-event-db
 ::set-active-page
 (fn [db [_ page & params]]
   (println page params)
   (let [app-state (:app db)
         new-app-state (merge app-state
                              {:active-page page
                               :page-params params})]
     (assoc db :app new-app-state))))
