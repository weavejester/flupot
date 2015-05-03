(ns flupot.dom
  (:require cljsjs.react))

(defn div [& args]
  (.apply js/React.DOM.div nil (into-array (cons nil args))))
