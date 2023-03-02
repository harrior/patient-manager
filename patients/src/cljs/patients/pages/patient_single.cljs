(ns patients.pages.patient-single
  (:require [ajax.edn :as edn]
            [clojure.string :as s]
            [day8.re-frame.http-fx]
            [re-frame.core :as rf]
            [patients.components.locale :refer [locale]]
            [patients.components.ui-elements :as ui]))

;;
;; Helpers
;;

(defn remove-nil-keys [m]
  (into {} (for [[k v] m
                 :when (not (nil? v))]
             [k v])))

(defn prepare-data-to-request
  [{:keys [name address insurance-number gender birth-date]}]
  (let [{:keys [family firstname patronymic]} (first name)
        {:keys [postal-code state city line district]} (first address)
        name-text (s/join " " (remove nil? [family firstname patronymic]))
        address-text (s/join ", " (remove nil? [postal-code state city line]))

        new-name (remove-nil-keys
                  {:use "usual"
                   :text name-text
                   :family family
                   :given (remove nil? [firstname patronymic])})
        new-address (remove-nil-keys {:city city
                                      :use "home"
                                      :type "physical"
                                      :state state
                                      :line line
                                      :postalCode postal-code
                                      :country "RU"
                                      :district district
                                      :text address-text})]
    ;; NOTE: refactor it
    (remove-nil-keys {:name [new-name]
                      :address [new-address]
                      :insurance-number insurance-number
                      :gender gender
                      :birth-date birth-date})))

;;
;; Events
;;

(rf/reg-event-db
 :create-new-patient
 (fn [db [_ form-id]]
   (assoc db form-id {:name [{}]
                      :address [{}]})))

(rf/reg-event-db
 :success-post-result
 (fn [db params]
   (println params)))

(rf/reg-event-db
 :bad-http-result
 (fn [db params]
   (println params)
   (println "Something wrong!")))

(rf/reg-event-fx
 :create-patient
 (fn [{:keys [db]} [_ form-id]]
   (let [entered-data (form-id db)
         prepared-data (prepare-data-to-request entered-data)
         request {:method :create-patient
                  :params {:patient-data  prepared-data}}]
     {:http-xhrio {:method          :post
                   :uri             "http://localhost:8000/rpc"
                   :params          request
                   :timeout         5000
                   :format          (edn/edn-request-format)
                   :response-format (edn/edn-response-format)
                   :on-success      [:success-post-result]
                   :on-failure      [:bad-http-result]}})))


;;
;; Subs
;;



;;
;; Patient page
;;

(defn init
  []
  (rf/dispatch [:create-new-patient :patient]))

(defn patient-page
  []
  [:div {:class "container"}
   [:header {:class "header"}
    [:h1 (locale :app/add-patient)]
    [:div {:style {:display :flex}}
     [ui/button {:id :go-main-page-button
                 :label :app/title}]
     [ui/spacer]
     [ui/button {:id :create-patient
                 :label :app/create-patient
                 :on-click #(rf/dispatch [:create-patient :patient])}]]]

   [:div {:style {:display :flex
                  :flex-direction :column
                  :gap 10}}

    [:fieldset {:style {:display :flex
                        :gap 10}}
     [:legend (locale :app/patient-data)]
     [:div {:style {:display :flex
                    :flex-direction :column
                    :width "100%"}}
      [ui/input-field {:form-id :patient
                       :field-id :family
                       :path [:name 0]
                       :label :patient/family}]
      [ui/input-field {:form-id :patient
                       :field-id :firstname
                       :path [:name 0]
                       :label :patient/name}]
      [ui/input-field {:form-id :patient
                       :field-id :patronymic
                       :path [:name 0]
                       :label :patient/patronymic}]]
     [:div {:style {:display :flex
                    :flex-direction :column
                    :width "100%"}}
      [ui/select-field {:form-id :patient
                        :field-id :gender
                        :label :patient/gender
                        :options [{:id :none :text "-" :params {:hidden true}}
                                  {:id :male :text :male}
                                  {:id :female :text :female}
                                  {:id :other :text :other}
                                  {:id :unknown :text :unknown}]}]
      [ui/input-field {:form-id :patient
                       :field-id :insurance-number
                       :label :patient/insurance-number}]
      [ui/date-field {:form-id :patient
                      :field-id :birth-date
                      :label :patient/birthday}]]]

    [:fieldset {:style {:display :flex
                        :gap 10}}
     [:legend (locale :app/patient-data)]
     [:div {:style {:display :flex
                    :flex-direction :column
                    :width "100%"}}
      [ui/input-field {:form-id :patient
                       :field-id :city
                       :path [:address 0]
                       :label :address/city}]
      [ui/input-field {:form-id :patient
                       :field-id :state
                       :path [:address 0]
                       :label :address/state}]
      [ui/input-field {:form-id :patient
                       :field-id :line
                       :path [:address 0]
                       :label :address/line}]]
     [:div {:style {:display :flex
                    :flex-direction :column
                    :width "100%"}}
      [ui/input-field {:form-id :patient
                       :field-id :postal-code
                       :path [:address 0]
                       :label :address/postal-code}]
      [ui/input-field {:form-id :patient
                       :field-id :district
                       :path [:address 0]
                       :label :address/district}]]]]
   [:footer {:class "footer"}]])

(defn main
  []
  (init)
  [patient-page])
