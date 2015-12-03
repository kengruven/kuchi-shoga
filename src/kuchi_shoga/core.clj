(ns kuchi-shoga.core
  (:use [clojure.string :as str]
        [compojure.core]
        [ring.middleware.defaults]
        [hiccup.core]
        [hiccup.page]
        [clojure.java.jdbc :as sql])
  (:require (compojure
             [route :as route])))


(def LOCAL-DB {:subprotocol "postgresql"
               :subname "//127.0.0.1:5432/kuchi_shoga"
               :user "kuchi_shoga_app"
               :password "password123"})

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
  (wrap-defaults main-routes site-defaults))
