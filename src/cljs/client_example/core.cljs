(ns client-example.core
  (:require
   [reagent.dom :as rdom]
   [re-frame.core :as re-frame]
   [day8.re-frame.http-fx]

   [client-example.events :as events]
   [client-example.views :as views]
   [client-example.config :as config]))

(def firebase-config
  {:apiKey  "AIzaSyDZVXGv8u0ivt-I_QrmERDVrnJp6Grqq_M"
   :authDomain "auth-example-b4be9.firebaseapp.com"
   :projectId  "auth-example-b4be9"
   :storageBucket "auth-example-b4be9.appspot.com"
   :messagingSenderId "261119699722"
   :appId "1:261119699722:web:6b84d31a77da20ad2028f3"})

(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-panel] root-el)))

(defn init []
  (re-frame/dispatch-sync [::events/initialize-db])
;;  (re-frame/dispatch-sync [::events/initialize-firebase firebase-config])
  (re-frame/dispatch-sync [::events/initialize-firebase-ui])
  (dev-setup)
  (mount-root))
