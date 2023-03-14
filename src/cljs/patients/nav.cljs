(ns patients.nav
  "Updates the active page and page parameters in patients app state."
  (:require [re-frame.core :as rf]))

;;
;; Events
;;

(rf/reg-event-db
 ::set-active-page
 ^{:doc
   "Updates the active page and page parameters in the patients app state. "}
 (fn [db [_ page & params]]
   (let [app-state (:app db)
         new-app-state (merge app-state
                              {:active-page page
                               :page-params params})]
     (assoc db :app new-app-state))))
