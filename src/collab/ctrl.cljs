(ns collab.ctrl)

(defn join [state]
  (update-in state [:members] concat ["baz"]))
