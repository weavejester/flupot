(ns flupot.dom
  (:require cljsjs.react))

(def ^:private attr-opts
  #js {:accept-charset "acceptCharset"
       :accesskey "accessKey"
       :allowfullscreen "allowFullScreen"
       :autocomplete "autoComplete"
       :autofocus "autoFocus"
       :autoplay "autoPlay"
       :class "className"
       :colspan "colSpan"
       :contenteditable "contentEditable"
       :contextmenu "contextMenu"
       :crossorigin "crossOrigin"
       :datetime "dateTime"
       :enctype "encType"
       :formaction "formAction"
       :formenctype "formEncType"
       :formmethod "formMethod"
       :formnovalidate "formNoValidate"
       :formTarget "formtarget"
       :hreflang "hrefLang"
       :for "htmlFor"
       :http-equiv "httpEquiv"
       :maxlength "maxLength"
       :mediagroup "mediaGroup"
       :novalidate "noValidate"
       :rowspan "rowSpan"
       :spellcheck "spellCheck"
       :srcdoc "srcDoc"
       :srcset "srcSet"
       :tabindex "tabIndex"
       :usemap "useMap"})

(defn- attrs->react [m]
  (reduce-kv
   (fn [o k v]
     (let [k (name k)]
       (aset o (or (aget attr-opts k) k) v)
       o))
   (js-obj)
   m))

(defn div [opts & children]
  (let [args (array)]
    (if (map? opts)
      (.push args (attrs->react opts))
      (do (.push args nil)
          (.push args opts)))
    (doseq [child children]
      (.push args child))
    (.apply js/React.DOM.div nil args)))
