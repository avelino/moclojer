(ns moclojer.webhook
  (:require [clj-http.client :as client]
            [clojure.core.async :as a]
            [moclojer.log :as log]))

(defn blocking-sleep [ms]
  "blocking sleep using Thread/sleep"
  (Thread/sleep (long ms)))

(defn request-after-delay
  "after a delay call http-request"
  [url method body & {:keys [headers sleep-time]
                      :or {headers {}
                           ; in seconds, 1 minute is 60000 seconds
                           sleep-time 60}}]
  (let [req {:url url
             :method method
             :headers headers
             :body body}]
    (a/go
      (a/thread
        (log/log :info :sleep sleep-time :webhook-start req)
        (blocking-sleep sleep-time)
        (client/request req)
        (log/log :info :sleep sleep-time :webhook-done req)))))