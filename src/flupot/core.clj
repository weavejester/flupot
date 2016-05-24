(ns flupot.core)

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

(defmacro attrs->react [attrs]
  (clj->js attrs))

(defmacro defelement-fn
  ([name elemf]
   `(defelement-fn ~name ~elemf flupot.core/attrs->react))
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
