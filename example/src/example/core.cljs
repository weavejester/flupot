(ns example.core
  (:require [brutha.core :as br]
            [flupot.dom :as dom]))

(enable-console-print!)

(let [app (.getElementById js/document "app")]
  (br/mount
   (dom/div
    {:class "test"}
    (dom/p "Hello " (dom/strong "World"))
    (dom/ul (for [i (range 5)] (dom/li {:key i} i))))
   app))
