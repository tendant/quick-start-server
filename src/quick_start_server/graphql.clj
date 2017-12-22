(ns quick-start-server.graphql
  (:require [graphql-clj.schema-validator :as sv]
            [graphql-clj.resolver :as resolver]
            [graphql-clj.executor :as executor]
            [clojure.java.io :as io]
            [taoensso.timbre :as log]
            [wharf.core :as kebab]
            [clojure.string :as str]))

(defn- uuid
  []
  (java.util.UUID/randomUUID))

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

(def cameras (->> ["GH5", "A7RIII", "7DII", "D850", "A6300", "A6500", "X100f", "XT2", "6DII", "200D", "M100", "D7500", "A9", "RX10IV", "RX0", "GFX 50S", "X-E3", "Pentax K-P", "Leica M10", "Leica CL", "G9", "GF9", "O-MD e-m10 III", "DJI X7"]
                  (map (fn [c]
                         {:id (uuid)
                          :name c}))))

(defn cameras-fn [context parent args]
  (let [{:strs [cursor limit]} args]
    (if (not (nil? limit))
      []
      cameras)))


(defn quick-start-resolver [type-name field-name]
  (get-in {:Mutation {:onClick on-click-fn}
           :Query {:hello hello-fn
                   :clicks clicks-fn
                   :name name-fn
                   :cameras cameras-fn}}
          [(keyword type-name) (keyword field-name)]
          (if (not (or (str/starts-with? field-name "__")
                       (str/starts-with? type-name "__")))
            (default-resolver type-name field-name))))

(defn execute
  [context query variables operation-name]
  (executor/execute context validated-schema quick-start-resolver query variables operation-name))
