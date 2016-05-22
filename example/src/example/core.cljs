(ns example.core
  (:require [brutha.core :as br]
            [flupot.dom :as dom]))

(enable-console-print!)

(let [app (.getElementById js/document "app")
      p   dom/p]
  (br/mount
   (dom/div
    {:class "test"}
    (dom/p "Hello " (dom/strong "World"))
    (p "Testing functions")
    (p (list "Testing " "functions " "with " "lists"))
    (dom/ul (for [i (range 1 6)] (dom/li {:key i} i)))
    (dom/p {:style {:color :red}} "Testing style"))
   app))
