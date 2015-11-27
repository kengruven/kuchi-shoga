(defproject kuchi-shoga "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "TBD"
            :url "??"}
  :dependencies [[org.clojure/clojure "1.6.0"]

                 ;; web serving
                 [ring/ring-jetty-adapter "1.4.0"]
                 [compojure "1.4.0"]

                 ;; html templating
                 [hiccup "1.0.5"]

                 ;; database
                 [org.clojure/java.jdbc "0.4.1"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]]

  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler kuchi-shoga.core/app}

  :main ^:skip-aot kuchi-shoga.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
