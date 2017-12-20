(defproject quick-start-server "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [ring "1.6.3"]
                 [ring-cors "0.1.8"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-defaults "0.2.1"]
                 [compojure "1.6.0"]
                 [graphql-clj "0.2.6"]
                 [com.taoensso/timbre "4.10.0"]
                 [thinktopic/wharf "0.2.0"]
                 [yogthos/config "0.9"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler quick-start-server.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
