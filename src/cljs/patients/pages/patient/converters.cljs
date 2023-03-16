(ns patients.pages.patient.converters
  "Namespace for patient page converters."
  (:require [clojure.string :as s]
            [patients.components.helpers :as h]))

;;
;; Data converters
;;

(defn prepare-patient-data-to-request
  "Prepares the patient data for a request by joining,
   removing empty values, and creating a map of patient information."
  [patient]
  (let [{:keys [patient-address
                patient-data
                patient-name]} patient

        {:keys [family firstname patronymic]} patient-name
        {:keys [postal-code state city line district]} patient-address
        {:keys [gender insurance-number birth-date]} patient-data

        patronymic (if (empty? firstname)
                     nil
                     patronymic)

        name-text (s/join " " (remove nil? [family firstname patronymic]))
        address-text (s/join ", " (remove nil? [postal-code state city line]))

        new-name (h/remove-empty-keys
                  {:use "usual"
                   :text name-text
                   :family family
                   :given (remove empty? [firstname patronymic])})
        new-address (h/remove-empty-keys {:use "home"
                                          :type "physical"
                                          :country "RU"
                                          :city city
                                          :state state
                                          :line line
                                          :postalCode postal-code
                                          :district district
                                          :text address-text})]
    (h/remove-empty-keys {:name [new-name]
                          :address [new-address]
                          :insurance-number insurance-number
                          :gender gender
                          :birth-date birth-date})))

(defn convert-response-to-patient-data
  "Converts a patient response to a patient data.
   It removes empty values and creates a map of patient information."
  [{:keys [gender insurance-number birth-date]
    [{:keys [city state line postalCode district]}] :address
    [{:keys [family] [firstname patronymic] :given}] :name}]

  {:patient-data
   (h/remove-empty-keys {:gender gender
                         :insurance-number insurance-number
                         :birth-date birth-date})
   :patient-address
   (h/remove-empty-keys {:city city
                         :state state
                         :line line
                         :postal-code postalCode
                         :district district})
   :patient-name
   (h/remove-empty-keys {:family family
                         :firstname firstname
                         :patronymic patronymic})})

(defn transform-error-path
  "Takes a collection of error paths and transforms them into
   a map representing the locations of errors in the patient form."
  [error-paths]
  (reduce (fn [acc path]
            (let [form (first path)
                  key (last path)]
              (case form
                :name (case key
                        :given (assoc-in acc [:patient-name :firstname] true)
                        (assoc-in acc [:patient-name key] true))
                :address (assoc-in acc [:patient-address key] true)
                (assoc-in acc [:patient-data key] true))))
          {}
          error-paths))