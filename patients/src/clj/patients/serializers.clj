(ns patients.serializers)

(defn patient-serialiser
  "Serialises patient data.
   Accepts a `patient` record, and returns a serialised flat map of patient data"
  [patient]
  (let [patient* (:patients/patient patient)
        insurance-number (:insurance-number patient*)
        fullname (-> patient*
                     :name
                     first
                     :text)
        address (-> patient*
                    :address
                    first
                    :text)
        gender (:gender patient*)
        birth-date (:birth-date patient*)
        identifier (:patients/id patient)]
    {:insurance-number insurance-number
     :fullname fullname
     :address address
     :gender gender
     :birth-date birth-date
     :identifier identifier}))