(ns client-example.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::user-information
 (fn [db] (:user db)))

(re-frame/reg-sub
 ::login-status
 (fn [db] (:login-status db)))

(re-frame/reg-sub
 ::user-name
 (fn [db] (:user-name db)))

(re-frame/reg-sub
 ::ui
 (fn [db] (:ui db)))
