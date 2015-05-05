(ns flupot.dom
  (:refer-clojure :exclude [map meta time])
  (:require-macros [flupot.dom :as dom])
  (:require cljsjs.react))

(def ^:private attr-opts
  (dom/generate-attr-opts))

(defn- attrs->react [m]
  (reduce-kv
   (fn [o k v]
     (let [k (name k)]
       (aset o (or (aget attr-opts k) k) v)
       o))
   (js-obj)
   m))

(dom/define-dom-fns)
