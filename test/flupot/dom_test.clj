(ns flupot.dom-test
  (:require [clojure.test :refer :all]
            [flupot.dom :as dom]))

(deftest test-inline-macros
  (testing "literal option map"
    (is (= (macroexpand-1 '(flupot.dom/div {:class "foo"} "bar"))
           '(js/React.DOM.div (cljs.core/js-obj "className" "foo") "bar"))))

  (testing "literal arguments with no option map"
    (is (= (macroexpand-1 '(flupot.dom/div "foo" "bar"))
           '(js/React.DOM.div nil "foo" "bar"))))

  (testing "ambiguous option map"
    (let [sexp (macroexpand-1 '(flupot.dom/span foo bar baz))
          opts (-> sexp second first)]
      (is (= sexp
             `(let [~opts ~'foo]
                (if (cljs.core/map? ~opts)
                  (js/React.DOM.span (flupot.dom/attrs->react ~opts) ~'bar ~'baz)
                  (js/React.DOM.span nil ~opts ~'bar ~'baz))))))))
