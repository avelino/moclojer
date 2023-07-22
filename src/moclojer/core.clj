(ns moclojer.core
  (:gen-class)
  (:require [babashka.cli :as cli]
            [clojure.string :as string]
            [moclojer.adapters :as adapters]
            [moclojer.config :as config]
            [moclojer.log :as log]
            [moclojer.server :as server])
  (:import (java.util.logging Filter Formatter Handler LogRecord Logger)))


(defn -main
  "software entry point"
  {:org.babashka/cli {:collect {:args []}}}
  [& args]
  (log/setup :info :auto)
  (let [args-opts (cli/parse-args args {:spec config/spec})
        envs {:config (or (System/getenv "CONFIG")
                          (config/with-xdg "moclojer.yml"))
              :mocks (System/getenv "MOCKS")}
        config (adapters/inputs->config args-opts envs config/version)]

    (when (:version config)
      (log/log :error :version-not-found "moclojer" config/version)
      (System/exit 0))

    (when (:help config)
      (log/log :error :empty-args :empty-config config/empty-args)
      (System/exit 0))

    (server/start config)))
