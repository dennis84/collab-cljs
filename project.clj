(defproject collab "0.1.0"
  :plugins [[lein-cljsbuild "1.0.3"]]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2227"]
                 [org.clojure/core.async "0.1.303.0-886421-alpha"]
                 [quiescent/quiescent "0.1.4"]
                 [com.facebook/react "0.10.0"]]
  :cljsbuild {
    :builds [{
      :source-paths ["src"]
      :compiler {
        :output-to "dist/js/collab.js"
        :preamble ["react/react.min.js"]
        :externs ["react/externs/react.js"]}}]})
