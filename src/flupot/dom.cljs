(ns flupot.dom
  (:require cljsjs.react))

(defn div [opts & children]
  (let [args (array)]
    (if (map? opts)
      (.push args (clj->js opts))
      (do (.push args nil)
          (.push args opts)))
    (doseq [child children]
      (.push args child))
    (.apply js/React.DOM.div nil args)))
