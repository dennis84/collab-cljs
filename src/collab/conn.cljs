(ns collab.conn
  (:require [clojure.string :as s]
            [cljs.core.async :refer (>!)]
            [collab.util :as util])
  (:require-macros [cljs.core.async.macros :refer (go)]))

(defn on-message [channels msg]
  (let [data (util/json->clj (.-data msg))
        channel (case (:t data)
          "join"          (:join channels)
          "leave"         (:leave channels)
          "members"       (:members channels)
          "update-member" (:update-member channels)
          "code"          (:code channels)
          "cursor"        (:cursor channels))]
    (go (>! channel [(:d data) (:s data)]))))

(defn on-open [channels ws]
  (do (.send ws (util/clj->json {:t "members"}))
      (go (>! (:open channels) []))))

(defn on-close [channels]
  (go (>! (:close channels) [])))

(defn init-websocket-receiver [app]
  (let [ws (:connection app)]
    (set! (.-onopen ws) (fn [] (on-open (:channels app) ws)))
    (set! (.-onclose ws) (fn [] (on-close (:channels app))))
    (set! (.-onmessage ws) (fn [m] (on-message (:channels app) m)))))

(defn make [room]
  (new js/WebSocket (s/join ["ws://localhost:9000/" room])))
