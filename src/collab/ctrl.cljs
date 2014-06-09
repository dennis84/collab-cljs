(ns collab.ctrl)

(defn- make-member [m]
  (merge {:id nil :name "" :me false :coding false} m))

(defn- add-members [xs ys]
  (concat xs (map make-member (filter 
    (fn [y] (not-any? #(= (:id y) (:id %)) xs)) ys))))

(defn join [state id]
  (update-in state [:members]
    (fn [ms] (add-members ms [{:id id}]))))

(defn leave [state id]
  (update-in state [:members]
    (fn [ms] (remove #(= id %) ms))))

(defn members [state data]
  (update-in state [:members]
    (fn [ms] (add-members ms data))))
