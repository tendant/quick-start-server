(ns quick-start-server.graphql
  (:require [graphql-clj.schema-validator :as sv]
            [graphql-clj.resolver :as resolver]
            [graphql-clj.executor :as executor]
            [clojure.java.io :as io]
            [taoensso.timbre :as log]
            [wharf.core :as kebab]
            [clojure.string :as str]))

(def validated-schema (-> (io/resource "quick-start-schema.graphql")
                          slurp
                          sv/validate-schema))

(def graphql-field->keyword
  (comp keyword str/lower-case kebab/camel->hyphen))

(defn default-resolver
  [type-name field-name]
  (fn [context parent args]
    (if (and field-name
             (not (str/starts-with? field-name "__")))
      (do
        ;; (log/debugf "default-resolver: type-name:%s, field-name:%s." type-name field-name)
        (get parent (graphql-field->keyword field-name))))))

(defn hello-fn [context parent args]
  "world")


(def clicks (atom 0))

(defn on-click-fn [context parent args]
  ;; increase count by 1
  (swap! clicks inc)
  @clicks)

(defn clicks-fn [context parent args]
  @clicks)

(defn name-fn [context parent args]
  (str "This is a test name" (rand-int 1000)))

(defn quick-start-resolver [type-name field-name]
  (get-in {:Mutation {:onClick on-click-fn}
           :Query {:hello hello-fn
                   :clicks clicks-fn
                   :name name-fn}}
          [(keyword type-name) (keyword field-name)]
          (if (not (or (str/starts-with? field-name "__")
                       (str/starts-with? type-name "__")))
            (default-resolver type-name field-name))))

(defn execute
  [context query variables operation-name]
  (executor/execute context validated-schema quick-start-resolver query variables operation-name))