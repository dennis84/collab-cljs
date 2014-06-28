(ns collab.change-nick
  (:require [clojure.string :as s]
            [quiescent :as q :include-macros true]
            [quiescent.dom :as d]
            [collab.util :as util]))

(q/defcomponent ChangeNick [conn]
  (q/on-mount
    (d/div {:id "change-nick" :className "modal fade bs-modal-sm"}
      (d/div {:className "modal-dialog modal-sm"}
        (d/div {:className "modal-content"}
          (d/form {:onSubmit (fn [e]
                               (.preventDefault e)
                               (let [input (.getElementById js/document "nickname")
                                     value (.-value input)]
                                  (when (not (s/blank? value))
                                    (.send conn (util/clj->json {:t "update-nick" :d value})))
                                  (.modal (js/$ "#change-nick") "hide")))}
            (d/div {:className "modal-body"}
              (d/label {} "Nickname")
              (d/input {:id "nickname" :className "form-control input-lg"}))
            (d/div {:className "modal-footer"}
              (d/button {:className "btn btn-default" :data-dismiss "modal"} "Close")
              (d/button {:className "btn btn-primary" :type "submit"} "Apply"))
            ))))
    (fn [node] (let [$elem (js/$ node)]
      (.on $elem "shown.bs.modal" #(.focus (.querySelector node "input")))))))
