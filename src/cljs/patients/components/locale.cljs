(ns patients.components.locale
  "A namespace for handling the localization of the application."
  (:require [re-frame.core :as rf]))

;;
;; Subs
;;

(rf/reg-sub
 :current-lang
 ^{:doc "Returns current language of the application."}
 (fn [db _]
   (or (get-in db [:app :lang])
       :ru)))

;;
;; Constants
;;

(def strings
  {:ru {:app/title "База данных пациентов"
        :app/add-patient "Добавить пациента"
        :app/edit-patient "Редактировать пациента"
        :app/search-placeholder "Поиск"
        :app/create-patient "Создать"
        :app/save-patient "Сохранить"
        :app/delele-patient "Удалить"
        :app/patient-data "Данные пациента"
        :app/patient-address "Адрес"
        :app/patients-list "Список пациентов"
        :app/bad-request "Ошибка сети"
        :app/success-removed "Запись удалена"
        :app/success-updated "Запись сохранена"
        :app/success-created "Запись создана"
        :app/validation-error "Ошибка валидации: одно или несколько полей заполнены неверно"

        :patient/fullname "ФИО"
        :patient/gender "Пол"
        :patient/birthday "Дата рождения"
        :patient/insurance-number "Полис"
        :patient/family "Фамилия"
        :patient/name "Имя"
        :patient/patronymic "Отчество"
        :address/city "Город"
        :address/state "Область\\Край"
        :address/line "Адрес"
        :address/text "Адрес"
        :address/postal-code "Индекс"
        :address/district "Район"}

   :en {:app/title "Patient database"
        :app/add-patient "Add patient"
        :app/edit-patient "Edit Patient"
        :app/search-placeholder "Search"
        :app/create-patient "Create"
        :app/save-patient "Save"
        :app/delele-patient "Delete"
        :app/patient-data "Patient data"
        :app/patient-address "Address"
        :app/patients-list "Patients list"
        :app/bad-request "Network error"
        :app/success-removed "Record created"
        :app/success-updated "Record saved"
        :app/success-created "Record created"
        :app/validation-error "Validation error: one or more fields are filled incorrectly."

        :patient/fullname "Name"
        :patient/gender "Gender"
        :patient/birthday "Birthday"
        :patient/insurance-number "Insurance Number"
        :patient/family "Last Name"
        :patient/name "First Name"
        :patient/patronymic "Patronymic" ;; Middle name?
        :address/city "City"
        :address/state "State"
        :address/line "Address"
        :address/text "Address"
        :address/postal-code "Postal Code"
        :address/district "District"}})

(defn locale
  "Retrieves the localized string for the given key."
  ([key]
   (let [lang @(rf/subscribe [:current-lang])]
     (locale lang key)))
  ([locale key]
   (or (get-in strings [locale key])
       (name key))))