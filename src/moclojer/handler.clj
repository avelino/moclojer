(ns moclojer.handler
  (:require [clojure.string :as string]
            [io.pedestal.http :as http]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.jetty]
            [clojure.data.json :as json]
            [io.pedestal.http.route :as route]
            [selmer.parser :as selmer]
            [slugify.core :refer [slugify]]))

(defn home-handler
  "home handler /"
  [_]
  {:status 200
   :body   "{\"body\": \"(-> moclojer server)\"}"})

(defn handler
  "prepare function to receive http request (handler)"
  [r]
  [(body-params/body-params)
   http/json-body
   (fn [req]
     {:status       (get-in r [:endpoint :response :status] 200)
      :content-type (get-in r [:endpoint :response :headers :content-type]
                            "application/json")
      :body         (selmer/render (get-in r [:endpoint :response :body] "{}")
                                   {:path-params  (:path-params req)
                                    :query-params (:query-params req)
                                    :json-params  (:json-params req)})})])

(defn generate-pedestal-route
  "Generate a Pedestal route from a Moclojer route"
  [config]
  (concat
   (route/expand-routes `#{["/" :get home-handler :route-name :home]})
   (sequence
    (mapcat
     (fn [{:keys [endpoint]
           :as   r}]
       (route/expand-routes
        #{[(:path endpoint)
           (keyword (string/lower-case (:method endpoint "get")))
           (handler r)
           :route-name (keyword (slugify (:path endpoint)))]})))
    config)))

(defn get-endpoints-edn [config]
  (let [endpoints (:endpoints config)]
    endpoints))

(defn generate-pedestal-route-from-edn
  [config]
  (sequence (mapcat (fn [r]
                      (let [route-name (first r)
                            endpoint (second r)
                            body (json/write-str (-> endpoint :response :body))]
                        (route/expand-routes
                          #{[(:path endpoint)
                             (:method endpoint :get)
                             (handler {:endpoint (-> endpoint (assoc-in [:response :body] body))})
                             :route-name route-name]})))
                    (get-endpoints-edn config))))
