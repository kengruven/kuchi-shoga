(ns kuchi-shoga.core
  (:use [compojure.core]
        [ring.middleware.defaults]
        [hiccup.core]
        [hiccup.page]
        [clojure.java.jdbc :as sql])
  (:require (compojure [route :as route])
            [clojure.string :as str]
            [ring.middleware.reload :refer [wrap-reload]]))


(def LOCAL-DB {:subprotocol "postgresql"
               :subname "//127.0.0.1:5432/kuchi_shoga"
               :user "kuchi_shoga_app"
               :password "password123"})

(defn render-line [hits]
  (for [[i hit] (map-indexed vector hits)]
    (case hit
      \x [:td.ka "ka"]
      \* [:td.don "don"]
      \. [:td.tsu "tsu"]
      \- [:td.su "su"])))

(defn render-measure [measure]
  "Given a string like '*.**', make an SVG block."
  [:table.measure
   [:tr (render-line measure)]])

(defn render-piece [lines]
  [:table.block
   (for [line lines]
     [:tr.line
      [:td.label (line :label)]
      [:td.hits (for [measure (str/split (line :hits) #" ")]
                  (render-measure measure))]
      [:td.repeat (if (line :repeat) (format "×%d" (line :repeat)))]
      [:td.notes (line :notes)]])])


(defn matsuri [params]
  (html
   (doctype :html5)
   [:html
    [:head
     (include-js "bower_components/jquery/dist/jquery.min.js")
     (include-js "kuchi-shoga.js")
     (include-css "kuchi-shoga.css")

     ;; material design lite (incl icons)
     (include-css "/bower_components/material-design-lite/material.min.css")
     (include-js "/bower_components/material-design-lite/material.min.js")
     (include-css "/bower_components/material-design-icons-iconfont/dist/material-design-icons.css")]

    [:body
     [:h2 "Matsuri Taiko"]
     ;; [:p (format "TESTING: n=%d" (sql/query LOCAL-DB
     ;;                                        ["select count(*) from information_schema"]))]

     ;; TODO: try with symbols...
     [:div
      (render-piece [{:hits "*--- *--- *-xx x-x-"}
                     {:hits "*-*- --*- *-xx x-x-"}
                     {:hits "--*- --*- *-xx x-x-"}
                     {:hits "*-x- x-*- *-xx x-x-"}
                     {:hits "**xx *-*- *-xx x-x-"}])]]]))


(defroutes main-routes
  (GET "/matsuri" [] matsuri)  ;; TEMPORARY -- static path
  (route/files "/" {:root "public"})
  (route/files "/bower_components" {:root "bower_components"})  ;; FIXME: consolidate these...
  (route/not-found "Page not found"))  ;; FUTURE: make this fancier

(def app
  (wrap-reload
   (wrap-defaults main-routes site-defaults)))
