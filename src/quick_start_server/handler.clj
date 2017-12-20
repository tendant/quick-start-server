(ns quick-start-server.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :as response :refer [redirect]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.json :refer [wrap-json-params]]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.reload :refer [wrap-reload]]
            [cheshire.core :as json]
            [taoensso.timbre :as log]
            [quick-start-server.graphql :as graphql]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (POST "/graphql" [schema query variables operationName :as request]
        (try
          (let [context nil
                result (graphql/execute context query variables operationName)]
            (log/debug "result:" result)
            (response/response result))
          (catch Throwable e
            (let [ed (ex-data e)]
              (log/error e)
              {:status 500
               :headers {}
               :body "Internal Server Error"}))))
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      wrap-json-response
      (wrap-cors :access-control-allow-origin [#"http://localhost:8080" #"http://.*"]
                 :access-control-allow-methods [:get :put :post :delete])
      (wrap-defaults api-defaults)
      wrap-json-params
      wrap-reload))
