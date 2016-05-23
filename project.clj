(defproject flupot "0.3.0"
  :description "ClojureScript functions for creating React elements"
  :url "https://github.com/weavejester/flupot"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.228" :scope "provided"]
                 [cljsjs/react-dom "15.1.0-0"]]
  :plugins [[lein-cljsbuild "1.1.3"]]
  :cljsbuild
  {:builds
   {:main
    {:source-paths ["src"]
     :compiler     {:output-to "target/main.js"}}}})
