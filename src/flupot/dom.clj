(ns flupot.dom)

(defn- dom-fn [tag]
  `(defn ~tag [opts# & children#]
     (let [args# (cljs.core/array)]
       (if (map? opts#)
         (.push args# (attrs->react opts#))
         (do (.push args# nil)
             (.push args# opts#)))
       (doseq [child# children#]
         (.push args# child#))
       (.apply ~(symbol "js" (str "React.DOM." tag)) nil args#))))

(defmacro define-dom-fns [& tags]
  `(do ~@(map dom-fn tags)))
