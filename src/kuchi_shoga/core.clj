(ns kuchi-shoga.core
  (:use [compojure.core])
  (:require (compojure
              [route :as route]
              [handler :as handler])))

(defn root [params]
  (format "hello, world!  %s" (keys params)))

(defroutes main-routes
  (GET "/" [] root)
  (route/files "/" {:root "public"})
  (route/not-found "Page not found"))  ;; FUTURE: make this fancier

(def app
  (handler/site main-routes))
