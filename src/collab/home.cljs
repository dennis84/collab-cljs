(ns collab.home
  (:require [quiescent :as q :include-macros true]
            [quiescent.dom :as d]))

(q/defcomponent Home []
  (d/div {:className "page"}
    (d/section {:className "promo container center-me"}
      (d/div {:className "row"}
        (d/div {:className "col-md-6 col-md-offset-3"}
          (d/h1 {} "Collab")
          (d/p {:className "lead"}
            "Collab is a screen sharing tool that allows you to share source 
             code in real time to the web.")
          (d/button {:className "btn btn-primary btn-lg"
                     :data-toggle "modal"
                     :data-target "#video"} "See how Collab works")
          (d/p {} "Collab is an open source project, go and check it out at Github.")
          (d/div {}
            (d/a {:href "https://github.com/dennis84/collab"} "Backend") " | "
            (d/a {:href "https://github.com/dennis84/vim-collab"} "VIM Plugin") " | "
            (d/a {:href "https://github.com/dennis84/collab-sublime"} "Sublime Text 2 Plugin") " | "
            (d/a {:href "https://github.com/dennis84/collab-web"} "Web App"))
          (d/a {:href "#footer" :className "btn btn-circle"}
            (d/span {:className "glyphicon glyphicon-chevron-down"})))))
    (d/section {:id "footer" :className "footer"}
      (d/div {:className "container center-me"}
        (d/div {:className "row"}
          (d/div {:className "col-md-6 col-md-offset-3"}
            (d/h2 {} "Let's make it together")
            (d/p {:className "lead"}
              "If you have any idea to improve Collab,
               feel free to open an issue or send me an "
              (d/a {:href "mailto:d.dietrich84@gmail.com"} "e-mail"))))))
    (d/div {:id "video" :className "modal fade"}
      (d/div {:className "modal-dialog modal-lg"}
        (d/div {:className "modal-content"}
          (d/div {:className "video-container"}
            (d/iframe {:src "//www.youtube.com/embed/u73O79Ztvts"
                       :width 900
                       :height 466
                       :frameBorder 0
                       :allowFullScreen true})))))))

(defn request-render [app]
  (q/render (Home) (:dom-element app)))
