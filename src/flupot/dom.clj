(ns flupot.dom)

(def ^:private attr-opts
  {:accept-charset :acceptCharset
   :accesskey :accessKey
   :allowfullscreen :allowFullScreen
   :autocomplete :autoComplete
   :autofocus :autoFocus
   :autoplay :autoPlay
   :class :className
   :colspan :colSpan
   :contenteditable :contentEditable
   :contextmenu :contextMenu
   :crossorigin :crossOrigin
   :datetime :dateTime
   :enctype :encType
   :formaction :formAction
   :formenctype :formEncType
   :formmethod :formMethod
   :formnovalidate :formNoValidate
   :formTarget :formtarget
   :hreflang :hrefLang
   :for :htmlFor
   :http-equiv :httpEquiv
   :maxlength :maxLength
   :mediagroup :mediaGroup
   :novalidate :noValidate
   :rowspan :rowSpan
   :spellcheck :spellCheck
   :srcdoc :srcDoc
   :srcset :srcSet
   :tabindex :tabIndex
   :usemap :useMap})

(defmacro generate-attr-opts []
  `(cljs.core/js-obj ~@(mapcat (fn [[k v]] [(name k) (name v)]) attr-opts)))

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

(defn- attrs->react [m]
  `(cljs.core/js-obj ~@(mapcat (fn [[k v]] [(name (attr-opts k k)) v]) m)))

(defmacro div [opts & children]
  (let [dom-sym (dom-symbol 'div)]
    (if (map? opts)
      `(~dom-sym ~(attrs->react opts) ~@children)
      `(let [opts# ~opts]
         (if (map? opts#)
           (~dom-sym (attrs->react opts#) ~@children)
           (~dom-sym nil opts# ~@children))))))
