(ns client-example.core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [client-example.core :as core]))

(deftest fake-test
  (testing "fake description"
    (is (= 1 2))))
