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

(defn quick-start-resolver [type-name field-name]
  (get-in {:Mutation {}
           :Query {}}
          [(keyword type-name) (keyword field-name)]
          (if (not (or (str/starts-with? field-name "__")
                       (str/starts-with? type-name "__")))
            (default-resolver type-name field-name))))

(defn execute
  [context query variables operation-name]
  (executor/execute context validated-schema quick-start-resolver query variables operation-name))