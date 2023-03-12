(ns patients.components.ui-elements
  (:require [re-frame.core :as rf]
            [patients.components.helpers :as h]
            [patients.components.locale :refer [locale]]))

;;
;; Events
;;

(rf/reg-event-db
 :set-input-value
 (fn [db [_ form-id field-id value]]
   (assoc-in db [form-id field-id] value)))

;;
;; Subs
;;

(rf/reg-sub
 :input-value
 (fn [db [_ form-id field-id]]
   (get-in db [form-id field-id])))

(rf/reg-sub
 :field-error
 (fn [db [_ form-id field-id]]
   (not (nil? (get-in db [:errors form-id field-id])))))

;;
;; Form components
;;

(defn input-field
  [{:keys [label
           form-id
           field-id
           on-change]}]
  (let [field-has-error? @(rf/subscribe [:field-error form-id field-id])]
    ^{:key [form-id field-id]}
    [:label (locale label)
     [:input {:class ["form-control" (when field-has-error? "errors")]
              :id field-id
              :value @(rf/subscribe [:input-value form-id field-id])
              :on-change (if on-change
                           on-change
                           (fn [event] (rf/dispatch [:set-input-value
                                                     form-id
                                                     field-id
                                                     (h/input-value-extractor event)])))}]]))

(defn select-field
  [{:keys [label
           form-id
           field-id
           options
           on-change]}]
  (let [field-has-error? @(rf/subscribe [:field-error form-id field-id])]
    ^{:key [form-id field-id]}
    [:label (locale label)
     [:select {:id field-id
               :class ["form-control" (when field-has-error? "errors")]
               :value (or @(rf/subscribe [:input-value form-id field-id])
                          :none)
               :on-change (if on-change
                            on-change
                            (fn [event] (rf/dispatch [:set-input-value
                                                      form-id
                                                      field-id
                                                      (h/input-value-extractor event)])))}
      (doall
       (for [{:keys [id text params] :or {params {}}} options]
         ^{:key id} [:option (assoc params :value id) (locale (or text id))]))]]))

(defn date-field
  [{:keys [label
           form-id
           field-id
           on-change]}]
  (let [field-has-error? @(rf/subscribe [:field-error form-id field-id])]
    ^{:key [form-id field-id]}
    [:label (locale label)
     [:input {:id field-id
              :type :date
              :class ["form-control" (when field-has-error? "errors")]
              :value @(rf/subscribe [:input-value form-id field-id])
              :on-change (if on-change
                           on-change
                           (fn [event] (rf/dispatch [:set-input-value
                                                     form-id
                                                     field-id
                                                     (h/input-value-extractor event)])))}]]))

(defn button
  [{:keys [label id on-click]}]
  [:button {:id id
            :class "form-button"
            :on-click on-click}
   (locale label)])

;;
;; Visual components

(defn spacer
  []
  [:div {:style {:flex-grow 1}}])

(defn fieldset-row
  [{:keys [title]} & children]
  [:fieldset {:style {:display :flex
                      :gap 10}}
   [:legend (locale title)]
   (doall
    (for [child children]
      child))])

(defn fieldset-column
  [& children]
  (into
   [:div {:style {:display :flex
                  :flex-direction :column
                  :width "100%"}}
    children]))

(defn footer
  []
  [:footer {:class "footer"}])