(ns patients.components.ui-elements
  (:require [patients.components.locale :refer [locale]]))

(defn input-field
  [{:keys [label id path]}]
  ^{:key id}
  [:label (locale label)
   [:input {:class "form-control"
            :id id}]])

(defn select-field
  [{:keys [label id options path]}]
  ^{:key id}
  [:label (locale label)
   [:select {:class "form-control"
             :id id}
    (for [{:keys [id text]} options]
      ^{:key id} [:option {:value id} text])]])

(defn date-field
  [{:keys [label id path]}]
  ^{:key id}
  [:label (locale label)
   [:input {:type :date
            :class "form-control"
            :id id}]])

(defn button
  [{:keys [label id on-click]}]
  [:button {:id id
            :class "form-button"
            :on-click on-click}
   (locale label)])

(defn spacer
  []
  [:div {:style {:flex-grow 1}}])