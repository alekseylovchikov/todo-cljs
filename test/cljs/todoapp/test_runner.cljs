(ns todoapp.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [todoapp.core-test]
   [todoapp.common-test]))

(enable-console-print!)

(doo-tests 'todoapp.core-test
           'todoapp.common-test)
