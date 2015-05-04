(ns flupot.dom)

(defn- dom-symbol [tag]
  (symbol "js" (str "React.DOM." (name tag))))

(defn- dom-fn [tag]
  `(defn ~tag [opts# & children#]
     (let [args# (cljs.core/array)]
       (if (map? opts#)
         (.push args# (attrs->react opts#))
         (do (.push args# nil)
             (.push args# opts#)))
       (doseq [child# children#]
         (.push args# child#))
       (.apply ~(dom-symbol tag) nil args#))))

(defmacro define-dom-fns [& tags]
  `(do ~@(map dom-fn tags)))

(defmacro div [& children]
  `(~(dom-symbol 'div) nil nil ~@children))
