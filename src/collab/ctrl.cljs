(ns collab.ctrl
  (:require [collab.util :as util]))

;;;; Member

(defn- make-member [id]
  {id {:name id :me false :coding false}})

(defn join [state [id sender]]
  (update-in state [:members]
    (fn [ms] (merge ms (make-member id)))))

(defn leave [state [id sender]]
  (update-in state [:members]
    (fn [ms] (dissoc ms id))))

(defn members [state [data sender]]
  (update-in state [:members]
    (fn [ms] (merge ms (into {}
      (map (fn [d] {(:id d) d}) data))))))

(defn- change-nick [state data]
  (assoc-in state [:members (:id data) :name] (:name data)))

(defn- change-nick-in-cursor [state data]
  (update-in state [:cursors]
    (fn [cs] (map (fn [c] (if (= (:id c) (:id data))
      (assoc c :member (:name data)) c)) cs))))

(defn update-member [state [data sender]]
  (-> state
    (change-nick data)
    (change-nick-in-cursor data)))

;;;; Code

(defn make-file [f]
  {:id (:file f) :buffer (:buffer f) :lang (:lang f)})

(defn code [state [data sender]]
  (update-in state [:files] (fn [fs]
    (if (some #(= (:id %) (:file data)) fs)
      (map (fn [f] (if (= (:id f) (:file data))
        (assoc f :buffer (:buffer data))
        f)) fs)
      (concat fs [(make-file data)])
    ))))

;;;; Cursor

(defn make-cursor [id c member]
  {:id id :x (:x c) :y (:y c) :file (:file c) :member member})

(defn cursor [state [data sender]]
  (update-in state [:cursors] (fn [cs]
    (if (some #(= (:id %) sender) cs)
      (map (fn [c] (if (= (:id c) sender)
        (assoc c :x (:x data) :y (:y data) :file (:file data))
        c)) cs)
      (if-let [m (first (filter #(= (:id %) sender) (:members state)))]
        (concat cs [(make-cursor sender data (:name m))])
        (concat cs [(make-cursor sender data sender)]))
    ))))
