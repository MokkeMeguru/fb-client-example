(ns client-example.interfaces.firebase.core
  (:require))

(defn initialize-app [config]
  (.log js/console "firebase enabled")
  (-> js/firebase (.initializeApp (clj->js config))))

(defn firebase-init [firebase-config]
  (let [firebase-instance (initialize-app firebase-config)]
    {:instance firebase-instance}))
