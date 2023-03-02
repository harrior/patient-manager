(ns patients.core
  (:require [reagent.dom :as rdom]
            [re-frame.core :as rf]
            [patients.db :as db]
            [patients.pages.patient-single :as patient-single]
            [patients.pages.patient-grid :as patient-grid]))
(enable-console-print!)



(rf/dispatch-sync [::db/initialize])
(rdom/render [patient-single/main] (js/document.getElementById "app"))
;; (rdom/render [patient-grid/main] (js/document.getElementById "app"))
