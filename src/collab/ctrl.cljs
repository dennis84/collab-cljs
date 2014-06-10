(ns collab.ctrl
  (:require [collab.util :as util]))

;;;; Member

(defn- make-member [m]
  (merge {:id nil :name "" :me false :coding false} m))

(defn- add-members [xs ys]
  (concat xs (map make-member (filter 
    (fn [y] (not-any? #(= (:id y) (:id %)) xs)) ys))))

(defn join [state id]
  (update-in state [:members]
    (fn [ms] (add-members ms [{:id id :name id}]))))

(defn leave [state id]
  (update-in state [:members]
    (fn [ms] (remove #(= id %) ms))))

(defn members [state data]
  (update-in state [:members]
    (fn [ms] (add-members ms data))))

(defn update-member [state data]
  (update-in state [:members]
    (fn [ms] (map (fn [m] (if (= (:id m) (:id data))
      (assoc m :name (:name data)) m)) ms))))

;;;; Code

(defn make-file [f]
  {:id (:file f) :buffer (:buffer f) :lang (:lang f)})

(defn code [state data]
  (update-in state [:files] (fn [fs]
    (if (some #(= (:id %) (:file data)) fs)
      (map (fn [f] (if (= (:id f) (:file data))
        (assoc f :buffer (:buffer data))
        f)) fs)
      (concat fs [(make-file data)])
    ))))
