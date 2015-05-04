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

(defmacro div [opts & children]
  (let [dom-sym (dom-symbol 'div)]
    (if (map? opts)
      `(~dom-sym (cljs.core/js-obj ~@(mapcat (fn [[k v]] [(name k) v]) opts)) ~@children)
      `(let [opts# ~opts]
         (if (map? opts#)
           (do (js/console.log "dynamic clj->js")
               (~dom-sym (cljs.core/clj->js opts#) ~@children))
           (~dom-sym nil opts# ~@children))))))
