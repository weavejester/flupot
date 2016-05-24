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

  (testing "classes as literal vectors"
    (is (= (macroexpand-1 '(flupot.dom/p {:class ["foo" :bar 'baz]} "foo"))
           '(js/React.DOM.p (cljs.core/js-obj "className" "foo bar baz") "foo"))))

  (testing "classes as literal sets"
    (is (= (macroexpand-1 '(flupot.dom/p {:class #{"foo" :bar 'baz}} "foo"))
           '(js/React.DOM.p (cljs.core/js-obj "className" "foo bar baz") "foo"))))

  (testing "classes as symbols"
    (is (= (macroexpand-1 '(flupot.dom/p {:class c} "foo"))
           '(js/React.DOM.p
             (cljs.core/js-obj "className" (cljs.core/clj->js (flupot.dom/fix-class c)))
             "foo"))))

  (testing "literal arguments with no option map"
    (is (= (macroexpand-1 '(flupot.dom/div "foo" "bar"))
           '(js/React.DOM.div nil "foo" "bar"))))

  (testing "ambiguous option map"
    (is (= (normalize-gensyms (macroexpand-1 '(flupot.dom/span foo "bar" "baz")))
           (normalize-gensyms
            `(let [opts# ~'foo]
               (if (map? opts#)
                 (js/React.DOM.span (flupot.dom/attrs->react opts#) "bar" "baz")
                 (js/React.DOM.span nil opts# "bar" "baz")))))))

  (testing "ambiguous first child"
    (is (= (normalize-gensyms (macroexpand-1 '(flupot.dom/span {} bar "baz")))
           (normalize-gensyms
            `(let [bar# ~'bar]
               (if (or (seq? bar#))
                 (let [args# (cljs.core/array)]
                   (.push args# (cljs.core/js-obj))
                   (flupot.core/push-child! args# bar#)
                   (.push args# "baz")
                   (.apply js/React.DOM.span nil args#))
                 (js/React.DOM.span (cljs.core/js-obj) bar# "baz")))))))

  (testing "ambiguous last child"
    (is (= (normalize-gensyms (macroexpand-1 '(flupot.dom/span "bar" baz)))
           (normalize-gensyms
            `(let [baz# ~'baz]
               (if (or (seq? baz#))
                 (let [args# (cljs.core/array)]
                   (do (.push args# nil)
                       (.push args# "bar"))
                   (flupot.core/push-child! args# baz#)
                   (.apply js/React.DOM.span nil args#))
                 (js/React.DOM.span nil "bar" baz#)))))))

  (testing "ambiguous options and children"
    (is (= (normalize-gensyms (macroexpand-1 '(flupot.dom/span foo bar baz)))
           (normalize-gensyms
            `(let [bar# ~'bar, baz# ~'baz]
               (if (or (seq? bar#) (seq? baz#))
                 (let [args# (cljs.core/array)]
                   (let [opts# ~'foo]
                     (if (map? opts#)
                       (.push args# (flupot.dom/attrs->react opts#))
                       (do (.push args# nil)
                           (.push args# opts#))))
                   (flupot.core/push-child! args# bar#)
                   (flupot.core/push-child! args# baz#)
                   (.apply js/React.DOM.span nil args#))
                 (let [opts2# ~'foo]
                   (if (map? opts2#)
                     (js/React.DOM.span (flupot.dom/attrs->react opts2#) bar# baz#)
                     (js/React.DOM.span nil opts2# bar# baz#))))))))))
