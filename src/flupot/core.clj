(ns flupot.core
  (:require [flupot.core.parsing :as p]))

(defn clj->js [x]
  (cond
    (map? x)
    `(cljs.core/js-obj ~@(mapcat (partial map clj->js) x))
    (or (vector? x) (set? x))
    `(cljs.core/array ~@(map clj->js x))
    (or (string? x) (number? x))
    x
    (keyword? x)
    (name x)
    :else
    `(cljs.core/clj->js ~x)))

(defmacro defelement-fn
  ([name elemf]
   `(defelement-fn ~name ~elemf cljs.core/clj->js))
  ([name elemf attrf]
   `(defn ~name [opts# & children#]
      (let [args# (cljs.core/array)]
        (if (map? opts#)
          (.push args# (~attrf opts#))
          (do (.push args# nil)
              (.push args# opts#)))
        (doseq [child# children#]
          (flupot.core/push-child! args# child#))
        (.apply ~elemf nil args#)))))

(defn- flat-dom-form [elemf attrf attrm opts children]
  (cond
    (map? opts)
    `(~elemf ~(attrm opts) ~@children)
    (p/literal? opts)
    `(~elemf nil ~opts ~@children)
    :else
    `(let [opts# ~opts]
       (if (map? opts#)
         (~elemf (~attrf opts#) ~@children)
         (~elemf nil opts# ~@children)))))

(defn- nested-dom-form [elemf attrf attrm opts children]
  (let [child-syms (map (fn [c] [(if-not (p/literal? c) (gensym)) c]) children)
        arguments  (map (fn [[s c]] (or s c)) child-syms)
        bindings   (filter first child-syms)
        args-sym   (gensym "args__")]
    `(let [~@(mapcat identity bindings)]
       (if (or ~@(map (fn [[sym _]] `(seq? ~sym)) bindings))
         (let [~args-sym (cljs.core/array)]
           ~(cond
              (map? opts)
              `(.push ~args-sym ~(attrm opts))
              (p/literal? opts)
              `(do (.push ~args-sym nil)
                   (.push ~args-sym ~opts))
              :else
              `(let [opts# ~opts]
                 (if (map? opts#)
                   (.push ~args-sym (~attrf opts#))
                   (do (.push ~args-sym nil)
                       (.push ~args-sym opts#)))))
           ~@(for [[s c] child-syms]
               (if s `(flupot.core/push-child! ~args-sym ~s) `(.push ~args-sym ~c)))
           (.apply ~elemf nil ~args-sym))
         ~(flat-dom-form elemf attrf attrm opts arguments)))))

(defn compile-dom-form [elemf attrf attrm opts children]
  (if (every? p/literal? children)
    (flat-dom-form elemf attrf attrm opts children)
    (nested-dom-form elemf attrf attrm opts children)))

(defmacro defelement-macro
  ([name elemf]
   `(defelement-macro ~name ~elemf cljs.core/clj->js flupot.core/clj->js))
  ([name elemf attrf attrm]
   `(defmacro ~name [opts# & children#]
      (compile-dom-form '~elemf '~attrf ~attrm opts# children#))))
