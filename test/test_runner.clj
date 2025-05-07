(ns test-runner
  (:require [clojure.test :refer :all]
            [auth.user-test]
            [auth.role-test]
            [auth.permission-test]
            [auth.resource-test]))

(defn -main []
  (run-tests 'auth.user-test
             'auth.role-test
             'auth.permission-test
             'auth.resource-test))(ns test-runner)