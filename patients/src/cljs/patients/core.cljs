(ns patients.core
  (:require [reagent.dom :as rdom]
            [re-frame.core :as rf]
            [patients.db :as db]
            [patients.pages.patient-single :refer [patient-page]]
            [patients.pages.patient-grid :refer [patient-grid-page]]))
(enable-console-print!)



(rf/dispatch-sync [::db/initialize])
(rdom/render [patient-grid-page] (js/document.getElementById "app"))
