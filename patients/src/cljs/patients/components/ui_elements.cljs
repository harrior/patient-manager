(ns patients.components.ui-elements
  (:require [reagent.core :as r]
            [reagent.dom :as rd]
            [re-frame.core :as rf]
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
 :get-input-value
 (fn [db [_ form-id field-id]]
   (get-in db [form-id field-id])))

(rf/reg-sub
 :get-field-error
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
  (let [field-has-error? @(rf/subscribe [:get-field-error form-id field-id])]
    ^{:key field-id}
    [:label (locale label)
     [:input {:class ["form-control" (when field-has-error? "errors")]
              :id field-id
              :value @(rf/subscribe [:get-input-value form-id field-id])
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
  (let [field-has-error? @(rf/subscribe [:get-field-error form-id field-id])]
    ^{:key field-id}
    [:label (locale label)
     [:select {:id field-id
               :class ["form-control" (when field-has-error? "errors")]
               :value (or @(rf/subscribe [:get-input-value form-id field-id])
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
  (let [field-has-error? @(rf/subscribe [:get-field-error form-id field-id])]
    ^{:key field-id}
    [:label (locale label)
     [:input {:id field-id
              :type :date
              :class ["form-control" (when field-has-error? "errors")]
              :value @(rf/subscribe [:get-input-value form-id field-id])
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

(defn fieldset
  [{:keys [title]} & children]
  [:fieldset {:style {:display :flex
                      :gap 10}}
   [:legend (locale title)]
   (doall
    (for [child children]
      child))])

(defn fieldset-column
  [& children]
  [:div {:style {:display :flex
                 :flex-direction :column
                 :width "100%"}}
   (doall
    (for [child children]
      child))])

(defn footer
  []
  [:footer {:class "footer"}])

;;
;; Popup
;;

(defn popup
  [message color]
  (let [visible? (r/atom true)]
    (r/create-class
      {:component-did-mount
       (fn []
         (js/setTimeout #(reset! visible? false) 3000))
       :reagent-render
       (fn []
         (when @visible?
           [:div {:style {:display :block
                          :position :fixed
                          :bottom 25
                          :right 25
                          :min-width 200
                          :min-height 20
                          :background color
                          :border "1px solid black"
                          :border-radius 15
                          :color :white
                          :font-size 16
                          :padding 20
                          :opacity 0.9
                          :animation "show-and-hide 3s forwards"}} (locale message)]))})))

(defn show-popup
  [message color]
  (let [container (js/document.createElement "div")]
    (js/document.body.appendChild container)
    (rd/render [popup message color] container)))

(defn show-error-popup
  [message]
  (show-popup message :red))

(defn show-success-popup
  [message]
  (show-popup message :green))