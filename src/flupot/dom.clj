(ns flupot.dom
  (:refer-clojure :exclude [map meta time]))

(def ^:private tags
  '[a abbr address area article aside audio b base bdi bdo big blockquote body br
    button canvas caption cite code col colgroup data datalist dd del details dfn
    dialog div dl dt em embed fieldset figcaption figure footer form h1 h2 h3 h4 h5
    h6 head header hr html i iframe img input ins kbd keygen label legend li link
    main map mark menu menuitem meta meter nav noscript object ol optgroup option
    output p param picture pre progress q rp rt ruby s samp script section select
    small source span strong style sub summary sup table tbody td textarea tfoot th
    thead time title tr track u ul var video wbr])

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

(defmacro define-dom-fns []
  `(do ~@(clojure.core/map dom-fn tags)))

(defn- attrs->react [m]
  `(cljs.core/js-obj ~@(mapcat (fn [[k v]] [(name (attr-opts k k)) v]) m)))

(defn- dom-macro [tag]
  `(let [dom-sym#  '~(dom-symbol tag)
         opts-sym# (gensym "opts")]
     (defmacro ~tag [opts# & children#]
       (if (map? opts#)
         `(~dom-sym# ~(attrs->react opts#) ~@children#)
         `(let [~opts-sym# ~opts#]
            (if (map? ~opts-sym#)
              (~dom-sym# (attrs->react ~opts-sym#) ~@children#)
              (~dom-sym# nil ~opts-sym# ~@children#)))))))

(defmacro define-dom-macros []
  `(do ~@(clojure.core/map dom-macro tags)))

(define-dom-macros)
