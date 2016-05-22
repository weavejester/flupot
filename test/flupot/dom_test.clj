(ns flupot.dom-test
  (:require [clojure.test :refer :all]
            [clojure.walk :as walk]
            [flupot.dom :as dom]))

(def ^:private gensym-regex #"(_|[a-zA-Z0-9\-\'\*]+)#?_+(\d+_*#?)+(auto__)?$")

(defn- gensym? [s]
  (and (symbol? s) (re-find gensym-regex (name s))))

(defn- normalize-gensyms [expr]
  (let [counter   (atom 0)
        re-gensym (memoize (fn [_] (symbol (str "__norm__" (swap! counter inc)))))]
    (walk/postwalk #(if (gensym? %) (re-gensym %) %) expr)))

(deftest test-inline-macros
  (testing "literal option map"
    (is (= (macroexpand-1 '(flupot.dom/div {:class "foo"} "bar"))
           '(js/React.DOM.div (cljs.core/js-obj "className" "foo") "bar"))))

  (testing "event listeners"
    (is (= (macroexpand-1 '(flupot.dom/div {:onclick f} "foo"))
           '(js/React.DOM.div (cljs.core/js-obj "onClick" (cljs.core/clj->js f)) "foo"))))

  (testing "literal style attribute"
    (is (= (macroexpand-1 '(flupot.dom/div {:style {:background-color "red"}} "foo"))
           '(js/React.DOM.div
             (cljs.core/js-obj "style" (cljs.core/js-obj "background-color" "red"))
             "foo"))))

  (testing "symbols in style attribute"
    (is (= (macroexpand-1 '(flupot.dom/p {:style {:color x}} "foo"))
           '(js/React.DOM.p
             (cljs.core/js-obj "style" (cljs.core/js-obj "color" (cljs.core/clj->js x)))
             "foo"))))

  (testing "literal arguments with no option map"
    (is (= (macroexpand-1 '(flupot.dom/div "foo" "bar"))
           '(js/React.DOM.div nil "foo" "bar"))))

  (testing "ambiguous option map"
    (let [sexp (macroexpand-1 '(flupot.dom/span foo bar baz))]
      (is (= (normalize-gensyms sexp)
             (normalize-gensyms
              `(let [bar# ~'bar, baz# ~'baz]
                 (if (or (seq? bar#) (seq? baz#))
                   (let [args# (cljs.core/array)]
                     (flupot.dom/push-child! args# bar#)
                     (flupot.dom/push-child! args# baz#)
                     (let [opts# ~'foo]
                       (if (map? opts#)
                         (.apply js/React.DOM.span (flupot.dom/attrs->react opts#) args#)
                         (.apply js/React.DOM.span nil opts# args#))))
                   (let [opts2# ~'foo]
                     (if (map? opts2#)
                       (js/React.DOM.span (flupot.dom/attrs->react opts2#) bar# baz#)
                       (js/React.DOM.span nil opts2# bar# baz#)))))))))))
