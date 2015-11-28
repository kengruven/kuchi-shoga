(ns kuchi-shoga.core
  (:use [clojure.string :as str]
        [compojure.core]
        [hiccup.core]
        [hiccup.page])
  (:require (compojure
             [route :as route]
             [handler :as handler])))


(def KA-LENGTH 6)

(defn render-hit [x y strength]
  [:circle {:cx x :cy y :r strength}])

(defn render-line [x y hits]
  [:g
   [:line {:x1 (- x 20)
           :y1 y
           :x2 (+ 10 (* 20 (count hits)))
           :y2 y}]
   (for [[i hit] (map-indexed vector hits)]
     (if (= hit \x)
       [:path {:d (format "M%d,%d l%d,%d m0,%d l%d,%d"
                          (+ x (* 20 i) (- KA-LENGTH)) (- y KA-LENGTH)
                          (* 2 KA-LENGTH) (* 2 KA-LENGTH)
                          (* -2 KA-LENGTH)
                          (* -2 KA-LENGTH) (* 2 KA-LENGTH))}]
       (render-hit (+ x (* 20 i)) y
                   ({\* 8 \. 3 #" " 0} hit))))])

(defn render-measure [measure]
  "Given a string like '*.**', make an SVG block."
  [:svg {:width (* 20 (inc (count measure)))
         :height 25}
   [:g {:class "toplevel"} (render-line 15 15 measure)]])

(defn render-piece [lines]
  [:table
   (for [line lines]
     [:tr
      [:td {:class "label"} (line :label)]
      [:td (for [measure (str/split (line :hits) #" ")]
             (render-measure measure))]
      [:td {:class "repeat"} (if (line :repeat) (format "×%d" (line :repeat)))]
      [:td {:class "notes"} (line :notes)]])])


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
  (handler/site main-routes))
