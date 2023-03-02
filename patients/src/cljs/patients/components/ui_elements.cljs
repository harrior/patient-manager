(ns patients.components.ui-elements
  (:require [re-frame.core :as rf]
            [patients.components.helpers :as h]
            [patients.components.locale :refer [locale]]))

;;
;; Events
;;

(rf/reg-event-db
 :set-input-value
 (fn [db [_ form-id field-id path value]]
   (let [full-path (flatten [form-id path field-id])]
     (assoc-in db full-path value))))

;;
;; Subs
;;

(rf/reg-sub
 :get-input-value
 (fn [db [_ form-id field-id path]]
   (let [full-path (flatten [form-id path field-id])]
     (get-in db full-path))))


;;
;; Components
;;

(defn input-field
  [{:keys [label
           form-id
           field-id
           path
           on-change]
    :or {path []}}]
  ^{:key field-id}
  [:label (locale label)
   [:input {:class "form-control"
            :id field-id
            :value @(rf/subscribe [:get-input-value form-id field-id path])
            :on-change (if on-change
                         on-change
                         (fn [event] (rf/dispatch [:set-input-value
                                                   form-id
                                                   field-id
                                                   path
                                                   (h/input-value-extractor event)])))}]])

(defn select-field
  [{:keys [label
           form-id
           field-id
           options
           path
           on-change]
    :or {path []}}]
  ^{:key field-id}
  [:label (locale label)
   [:select {:class "form-control"
             :id field-id
             :on-change (if on-change
                          on-change
                          (fn [event] (rf/dispatch [:set-input-value
                                                    form-id
                                                    field-id
                                                    path
                                                    (h/input-value-extractor event)])))}
    (for [{:keys [id text params] :or {params {}}} options]
      ^{:key id} [:option (assoc params :value id) (locale (or text id))])]])

(defn date-field
  [{:keys [label
           form-id
           field-id
           path
           on-change]
    :or {path []}}]
  ^{:key field-id}
  [:label (locale label)
   [:input {:type :date
            :class "form-control"
            :id field-id
            :on-change (if on-change
                         on-change
                         (fn [event] (rf/dispatch [:set-input-value
                                                   form-id
                                                   field-id
                                                   path
                                                   (h/input-value-extractor event)])))}]])

(defn button
  [{:keys [label id on-click]}]
  [:button {:id id
            :class "form-button"
            :on-click on-click}
   (locale label)])

(defn spacer
  []
  [:div {:style {:flex-grow 1}}])