(ns example.core
  (:require [brutha.core :as br]
            [flupot.dom :as dom]))

(enable-console-print!)

(defn content []
  (let [p dom/p
        a {:style {:color :blue}}
        c ["foo" :bar 'baz]]
    (dom/div
     {:class "test"}
     (dom/p "Hello " (dom/strong {:onclick #(js/alert "World")} "World"))
     (p "Testing functions")
     (p (list "Testing " "functions " "with " "lists"))
     (dom/ul (for [i (range 1 6)] (dom/li {:key i} i)))
     (dom/p {:style {:color :red}} "Testing style")
     (dom/p a "Testing programmatic style")
     (dom/p "Testing " (list "lists " (list "within " "lists")))
     (dom/p {:class ["foo" :bar 'baz]} "Multiple classes")
     (dom/p {:class c} "Multiple classes in symbol"))))

(let [app (.getElementById js/document "app")]
  (br/mount (content) app))
