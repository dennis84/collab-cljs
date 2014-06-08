(ns collab.util)

(defn json->clj [s]
  (js->clj (.parse js/JSON s) :keywordize-keys true))

(defn clj->json [o]
  (.stringify js/JSON (clj->js o)))
