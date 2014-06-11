(ns collab.ctrl
  (:require [collab.util :as util]))

;;;; Member

(defn- make-member [m]
  (merge {:id nil :name "" :me false :coding false} m))

(defn- add-members [xs ys]
  (concat xs (map make-member (filter 
    (fn [y] (not-any? #(= (:id y) (:id %)) xs)) ys))))

(defn join [state [id sender]]
  (update-in state [:members]
    (fn [ms] (add-members ms [{:id id :name id}]))))

(defn leave [state [id sender]]
  (update-in state [:members]
    (fn [ms] (remove #(= id %) ms))))

(defn members [state [data sender]]
  (update-in state [:members]
    (fn [ms] (add-members ms data))))

(defn- change-nick [state data]
  (update-in state [:members]
    (fn [ms] (map (fn [m] (if (= (:id m) (:id data))
      (assoc m :name (:name data)) m)) ms))))

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
