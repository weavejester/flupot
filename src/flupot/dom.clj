(ns flupot.dom
  (:refer-clojure :exclude [map meta time])
  (:require [clojure.core :as core]))

(def tags
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
   :onabort :onAbort
   :onblur :onBlur
   :oncancel :onCancel
   :oncanplay :onCanPlay
   :oncanplaythrough :onCanPlayThrough
   :onchange :onChange
   :onclick :onClick
   :oncontextmenu :onContextMenu
   :oncompositionend :onCompositionEnd
   :oncompositionstart :onCompositionStart
   :oncompositionupdate :onCompositionUpdate
   :oncopy :onCopy
   :oncut :onCut
   :ondblclick :onDoubleClick
   :ondrag :onDrag
   :ondragend :onDragEnd
   :ondragenter :onDragEnter
   :ondragexit :onDragExit
   :ondragleave :onDragLeave
   :ondragover :onDragOver
   :ondragstart :onDragStart
   :ondrop :onDrop
   :ondurationchange :onDurationChange
   :onemptied :onEmptied
   :onencrypted :onEncrypted
   :onended :onEnded
   :onerror :onError
   :onfocus :onFocus
   :oninput :onInput
   :onkeydown :onKeyDown
   :onkeypress :onKeyPress
   :onkeyup :onKeyUp
   :onload :onLoad
   :onloadeddata :onLoadedData
   :onloadedmetadata :onLoadedMetadata
   :onloadstart :onLoadStart
   :onmousedown :onMouseDown
   :onmouseenter :onMouseEnter
   :onmouseleave :onMouseLeave
   :onmousemove :onMouseMove
   :onmouseout :onMouseOut
   :onmouseover :onMouseOver
   :onmouseup :onMouseUp
   :onpaste :onPaste
   :onpause :onPause
   :onplay :onPlay
   :onplaying :onPlaying
   :onprogress :onProgress
   :onratechange :onRateChange
   :onscroll :onScroll
   :onseeked :onSeeked
   :onseeking :onSeeking
   :onselect :onSelect
   :onstalled :onStalled
   :onsubmit :onSubmit
   :onsuspend :onSuspend
   :ontimeupdate :onTimeUpdate
   :ontouchcancel :onTouchCancel
   :ontouchend :onTouchEnd
   :ontouchmove :onTouchMove
   :ontouchstart :onTouchStart
   :onvolumechange :onVolumeChange
   :onwaiting :onWaiting
   :onwheel :onWheel
   :rowspan :rowSpan
   :spellcheck :spellCheck
   :srcdoc :srcDoc
   :srcset :srcSet
   :tabindex :tabIndex
   :usemap :useMap})

(defn- mapm [fk fv m]
  (reduce-kv (fn [m k v] (assoc m (fk k) (fv v))) {} m))

(defn- clj->js [x]
  (cond
    (map? x)
    `(cljs.core/js-obj ~@(apply concat (mapm clj->js clj->js x)))
    (or (vector? x) (set? x))
    `(cljs.core/array ~@(core/map clj->js x))
    (or (string? x) (number? x))
    x
    (keyword? x)
    (name x)
    :else
    `(cljs.core/clj->js ~x)))

(defmacro generate-attr-opts []
  (clj->js (mapm name name attr-opts)))

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
         (push-child! args# child#))
       (.apply ~(dom-symbol tag) nil args#))))

(defmacro define-dom-fns []
  `(do ~@(core/map dom-fn tags)))

(defn- attrs->react [m]
  (clj->js (mapm #(name (attr-opts % %)) identity m)))

(defn- literal? [x]
  (not (or (symbol? x) (list? x))))

(defn- flat-dom-form [sym opts children]
  (cond
    (map? opts)
    `(~sym ~(attrs->react opts) ~@children)
    (literal? opts)
    `(~sym nil ~opts ~@children)
    :else
    `(let [opts# ~opts]
       (if (map? opts#)
         (~sym (flupot.dom/attrs->react opts#) ~@children)
         (~sym nil opts# ~@children)))))

(defn- nested-dom-form [sym opts children]
  (let [child-syms (core/map (fn [c] [(if-not (literal? c) (gensym)) c]) children)
        arguments  (core/map (fn [[s c]] (or s c)) child-syms)
        bindings   (filter first child-syms)
        args-sym   (gensym "args")]
    `(let [~@(mapcat identity bindings)]
       (if (or ~@(core/map (fn [[sym _]] `(seq? ~sym)) bindings))
         (let [~args-sym (cljs.core/array)]
           ~@(for [[s c] child-syms]
               (if s `(push-child! ~args-sym ~s) `(.push ~args-sym ~c)))
           ~(cond
              (map? opts)
              `(.apply ~sym ~(attrs->react opts) ~args-sym)
              (literal? opts)
              `(.apply ~sym nil ~args-sym)
              :else
              `(let [opts# ~opts]
                 (if (map? opts#)
                   (.apply ~sym (flupot.dom/attrs->react opts#) ~args-sym)
                   (.apply ~sym nil opts# ~args-sym)))))
         ~(flat-dom-form sym opts arguments)))))

(defn- compile-dom-form [sym opts children]
  (if (every? literal? children)
    (flat-dom-form sym opts children)
    (nested-dom-form sym opts children)))

(defn- dom-macro [tag]
  `(let [dom-sym# '~(dom-symbol tag)]
     (defmacro ~tag [opts# & children#]
       (compile-dom-form dom-sym# opts# children#))))

(defmacro define-dom-macros []
  `(do ~@(core/map dom-macro tags)))

(define-dom-macros)
