(ns client-example.interfaces.firebase.ui)

(defn firebaseui-init []
  (let [auth (.auth js/firebase)
        _ (set! (.-languageCode auth) "ja")
        ui  (try (new (.AuthUI (.-auth js/firebaseui)) auth)
                 (catch js/Error e
                   (print  e)))]
    (.. js/firebaseui -auth -AuthUI getInstance)))

;; :(firebaseui-init)
