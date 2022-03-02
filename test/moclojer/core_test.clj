(ns moclojer.core-test
  (:require [cheshire.core :as json]
            [clojure.test :refer [deftest is]]
            [io.pedestal.test :refer [response-for]]
            [yaml.core :as yaml]
            [moclojer.aux.service :refer [service-fn]]))

(deftest hello-world
  (let [config (yaml/parse-string (slurp "moclojer.yml"))
        service-fn (service-fn config)]
    (is (= {:hello "Hello, World!"}
           (-> service-fn
               (response-for :get "/hello-world")
               :body
               (json/parse-string true))))))

(deftest dyanamic-endpoint
  (let [config (yaml/parse-string (slurp "moclojer.yml"))
        service-fn (service-fn config)]
    (is (= {:hello "moclojer!"}
           (-> service-fn
               (response-for :get "/hello/moclojer")
               :body
               (json/parse-string true))))))

(deftest with-params
  (let [config (yaml/parse-string (slurp "moclojer.yml"))
        service-fn (service-fn config)]
    (is (= {:path-params "moclojer" :query-params "moclojer"}
           (-> service-fn
               (response-for :get "/with-params/moclojer?param1=moclojer")
               :body
               (json/parse-string true))))))

(deftest first-post-route
  (let [config (yaml/parse-string (slurp "moclojer.yml"))
        service-fn (service-fn config)]
    (is (= {:project "moclojer"}
           (-> service-fn
               (response-for :post "/first-post-route"
                             :headers {"Content-Type" "application/json"}
                             :body (json/encode {:project "moclojer"}))
               :body
               (json/parse-string true))))))
