(ns kuchi-shoga.core
  (:use [compojure.core]
        [hiccup.core]
        [hiccup.page])
  (:require (compojure
             [route :as route]
             [handler :as handler])))

(defn root [params]
  (format "hello, world!  %s" (keys params)))

(defn matsuri [params]
  (html
   "<!DOCTYPE html>"
   [:html
    [:head
     [:script {:src "bower_components/jquery/dist/jquery.min.js"}]
     [:script {:src "kuchi-shoga.js"}]
     [:link {:href "kuchi-shoga.css" :rel "stylesheet"}]]
    [:body
     [:h2 "Matsuri Taiko"]
     [:div {:id "matsuri"}]
     [:script {:type "text/javascript"}
      "$(function() {
         renderPiece($('#matsuri'), [
                {hits:'*--- *--- *-xx x-x-'},
                {hits:'*-*- --*- *-xx x-x-'},
                {hits:'--*- --*- *-xx x-x-'},
                {hits:'*-x- x-*- *-xx x-x-'},
                {hits:'**xx *-*- *-xx x-x-'}
         ]);
       });"]]]))

(defroutes main-routes
  (GET "/" [] root)
  (GET "/matsuri" [] matsuri)  ;; TEMPORARY -- static path
  (route/files "/" {:root "public"})
  (route/files "/bower_components" {:root "bower_components"})  ;; FIXME: consolidate these...
  (route/not-found "Page not found"))  ;; FUTURE: make this fancier

(def app
  (handler/site main-routes))
