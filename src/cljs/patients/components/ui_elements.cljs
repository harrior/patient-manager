(ns patients.components.ui-elements
  (:require [re-frame.core :as rf]
            [stylefy.core :refer [use-style]]
            [patients.components.locale :refer [locale]]
            [patients.components.helpers :as h]
            [patients.components.styles :as styles]))

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

(defn- form-field
  [{:keys [label
           form-id
           field-id
           field-type
           options
           on-change]}]
  (let [field-has-error? @(rf/subscribe [:field-error form-id field-id])
        value @(rf/subscribe [:input-value form-id field-id])
        field-styles (use-style (styles/form-field-style-with-error field-has-error?))

        on-change-fn (if on-change
                       on-change
                       (fn [event] (rf/dispatch [:set-input-value
                                                 form-id
                                                 field-id
                                                 (h/input-value-extractor event)])))

        common-props (merge field-styles
                            {:id field-id
                             :value value
                             :on-change on-change-fn})]
    ^{:key [form-id field-id]}
    [:label (locale label)
     (case field-type
       :input [:input common-props]
       :select [:select (assoc common-props :value (or value :none))
                (doall
                 (for [{:keys [id text params] :or {params {}}} options]
                   ^{:key id} [:option (assoc params :value id) (locale (or text id))]))]
       :date [:input (assoc common-props
                            :type :date)])]))

(defn input-field
  [props]
  (form-field (assoc props :field-type :input)))

(defn select-field
  [props]
  (form-field (assoc props :field-type :select)))

(defn date-field
  [props]
  (form-field (assoc props :field-type :date)))


(defn button
  [{:keys [label id on-click]}]
  [:button (merge (use-style styles/form-button)
                  {:id id
                   :on-click on-click})
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
  [:footer (use-style styles/footer)])