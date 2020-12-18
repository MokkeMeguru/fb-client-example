(ns client-example.views
  (:require
   [re-frame.core :as re-frame]
   [client-example.subs :as subs]
   [client-example.events :as events]
   [reagent.core :as r]
   [reagent.dom :as dom]
   [client-example.interfaces.firebase.auth :refer [providers]]))

(def ui-config
  {:signInFlow "redirect"
   :signInSuccessUrl "#/"
   :signInOptions [((:google providers))
                   ((:email providers))]
   :callbacks {:signInSuccessWithAuthResult
               (fn [e] (print "success! " e)
                 (.log js/console (.. js/firebase auth -currentUser -displayName))
                 (->
                  (.getIdToken (.. js/firebase auth -currentUser) true)
                  (.then (fn [id-token] (print id-token)))
                  (.catch (fn [err] (print err))))
                 (re-frame/dispatch [::events/save-user-information (.. js/firebase auth  -currentUser)])
                 (re-frame/dispatch [::events/firebase-auth-signInSuccesWithAuthResult]))}})

(defn StyledFirebaseAuth [config]
  (let [ui-config (:ui-config config)
        firebaseUiWidget (re-frame/subscribe [::subs/ui])]
    (r/create-class
     {:display-name "StyledFirebaseAuth"
      :component-did-mount
      (fn [this]
        (.start @firebaseUiWidget "#firebaseui-container" ui-config))
      :reagent-render
      (fn [config]
        [:div#firebaseui-container])})))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])
        user (re-frame/subscribe [::subs/user-information])
        user-name (re-frame/subscribe [::subs/user-name])
        login-status (re-frame/subscribe [::subs/login-status])
        auth (.auth js/firebase)
        _ (.. js/firebase auth useDeviceLanguage)]

    [:div
     [:h1 "Hello from " @name (if @login-status (str " : " @user-name))]
     (if-not @user
       [StyledFirebaseAuth {:ui-config (clj->js ui-config)}]
       [:button {:on-click #(re-frame/dispatch [::events/signout])} "logout"])]))
