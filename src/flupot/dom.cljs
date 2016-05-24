(ns flupot.dom
  (:refer-clojure :exclude [map meta time])
  (:require-macros [flupot.dom :as dom])
  (:require cljsjs.react
            [clojure.string :as str]
            [flupot.core :as flupot]))

(def ^:private attr-opts
  (dom/generate-attr-opts))

(defn- push-child! [args child]
  (if (seq? child)
    (doseq [c child]
      (push-child! args c))
    (.push args child)))

(defn- fix-class [v]
  (if (sequential? v)
    (str/join " " (cljs.core/map #(if (keyword? %) (name %) (str %)) v))
    (clj->js v)))

(defn- attrs->react [m]
  (reduce-kv
   (fn [o k v]
     (let [k (name k)]
       (if (= k "class")
         (aset o "className" (fix-class v))
         (aset o (or (aget attr-opts k) k) (clj->js v)))
       o))
   (js-obj)
   m))

(dom/define-dom-fns)
