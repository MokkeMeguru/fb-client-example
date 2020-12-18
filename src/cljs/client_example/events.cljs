(ns client-example.events
  (:require
   [re-frame.core :as re-frame]
   [client-example.db :as db]
   [ajax.core :as ajax]
   [day8.re-frame.tracing :refer-macros [fn-traced]]

   [client-example.interfaces.firebase.core :refer [firebase-init]]
   [client-example.interfaces.firebase.ui :refer [firebaseui-init]]
   ;; ["firebase/app" :default firebase]
   ))

;; default db


(re-frame/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
            db/default-db))

;; firebase events

(re-frame/reg-event-db
 ::store-firebase-instance
 (fn [db [_ instance]]
   (assoc db :firebase-instance instance)))

(re-frame/reg-fx
 ::init-firebase
 (fn [config]
   (re-frame/dispatch [::store-firebase-instance (firebase-init config)])))

(re-frame/reg-event-fx
 ::initialize-firebase
 (fn [_ [_ firebase-config]]
   {::init-firebase firebase-config}))

(re-frame/reg-event-db
 ::initialize-firebase-ui
 (fn [db _]
   (let [ui (firebaseui-init)]
     (print ui)
     (assoc db :ui ui))))
 ;; (fn [db [_ firebase-ui-id firebase-ui-config]]
 ;;   (let [_ui (js/firebaseui.auth.AuthUI. (js/firebase.auth))
 ;;         _ (print _ui)
 ;;         ui (.start _ui
 ;;                    "#firebaseui-auth-container"
 ;;                    (clj->js {:signInSuccessUrl "#/"
 ;;                              :signInOptions ["google.com"]}))]
 ;;     (print "initialize firebase ui" _ui)
 ;;     (assoc db :firebase-ui _ui)))

;; username
(re-frame/reg-event-db
 ::save-user-information
 (fn [db [_ user]]
   (assoc db :user user)))

(re-frame/reg-event-db
 ::signout
 (fn [db _]
   (-> (.. js/firebase auth signOut)
       (.then (print "signout is successfull!")))
   (-> db
       (dissoc :user)
       (dissoc :user-name)
       (assoc :login-status false))))

(re-frame/reg-cofx
 ::firebase-auth-user-display-name
 (fn [coeffects _]
   (assoc coeffects :display-name (.. js/firebase auth -currentUser -displayName))))

(re-frame/reg-event-fx
 ::login-success
 [(re-frame/inject-cofx ::firebase-auth-user-display-name)]
 (fn [{:keys [db display-name]} [_ body]]
   (print "login success" body)
   (print "user" display-name)
   {:db (-> db
            (assoc :login-status :success)
            (assoc :user-name display-name))}))

(re-frame/reg-event-db
 ::login-failure
 (fn [db [_ err]]
   (print "login failure" err)
   (assoc db :login-status :failure)))

(re-frame/reg-event-fx
 ::login
 (fn [{:keys [db]} [_ id-token]]
   (print "login")
   {:http-xhrio {:method :post
                 :headers {"Authorization" id-token}
                 :uri "http://localhost:3000/api/signin"
                 :timeout 8000
                 :format (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [::login-success]
                 :on-failure [::login-failure]}}))

(re-frame/reg-event-fx
 ::login-err
 (fn [cofx [_ err]]
   (print cofx)
   (print err)))

(re-frame/reg-fx
 ::firebase-auth-getIdToken
 (fn [{:keys [on-success on-failure]}]
   (-> (.getIdToken (.. js/firebase auth -currentUser))
       (.then (fn [id-token] (re-frame/dispatch (vec (concat on-success [id-token])))))
       (.catch (fn [err] (re-frame/dispatch (vec (concat on-failure [err]))))))))

(re-frame/reg-event-fx
 ::firebase-auth-signInSuccesWithAuthResult
 (fn [_ _]
   {::firebase-auth-getIdToken
    {:on-success [::login]
     :on-failure [::login-error]}}
   ;; (-> (.getIdToken (.. firebase auth -currentUser))
   ;;     (.then (fn [id-token] (print id-token)))
   ;;     (.catch (fn [err] (print err))))
   ))

;; (vec (concat [1 2] [3]))
