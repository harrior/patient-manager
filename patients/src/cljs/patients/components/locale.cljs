(ns patients.components.locale)

(def strings
  {:ru {:app/title "База данных пациентов"
        :app/add-patient "Добавить пациента"
        :app/edit-patient "Редактировать пациента"
        :app/search-placeholder "Поиск"
        :app/create-patient "Создать"
        :app/save "Сохранить"
        :app/patient-data "Данные пациента"
        :app/patient-address "Адрес"
        :app/patients-list "Список пациентов"

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
        :app/save "Save"
        :app/patient-data "Patient data"
        :app/patient-address "Address"
        :app/patients-list "Patients list"

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
  ([key]
   (locale :ru key))
  ([locale key]
   (or (get-in strings [locale key])
       (name key))))