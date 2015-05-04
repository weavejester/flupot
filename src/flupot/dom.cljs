(ns flupot.dom
  (:refer-clojure :exclude [map meta time])
  (:require-macros [flupot.dom :as dom])
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

(dom/define-dom-fns
  a abbr address area article aside audio b base bdi bdo big blockquote body br
  button canvas caption cite code col colgroup data datalist dd del details dfn
  dialog div dl dt em embed fieldset figcaption figure footer form h1 h2 h3 h4 h5
  h6 head header hr html i iframe img input ins kbd keygen label legend li link
  main map mark menu menuitem meta meter nav noscript object ol optgroup option
  output p param picture pre progress q rp rt ruby s samp script section select
  small source span strong style sub summary sup table tbody td textarea tfoot th
  thead time title tr track u ul var video wbr)
