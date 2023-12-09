(ns moclojer.adapters-test
  (:require [clojure.test :refer [are deftest testing]]
            [moclojer.adapters :as adapters]))

(deftest inputs-config-test
  (testing "inputs->config can read data from all data sources"
    (are [result inputs] (= result inputs)

      {:config-path "config.yaml"
       :mocks-path "mocks.yaml"
       :version true
       :help true}
      (adapters/inputs->config {:args [{:config "config.yaml"
                                        :mocks "mocks.yaml"
                                        :version true
                                        :help true}]
                                :opts {:config "opts-config.yaml"
                                       :mocks "opts-mocks.yaml"}}
                               {:config "default-config.yaml"
                                :mocks "default-mocks.yaml"})

      {:config-path "opts-config.yaml"
       :mocks-path "opts-mocks.yaml"
       :version true
       :help true}
      (adapters/inputs->config {:args []
                                :opts {:config "opts-config.yaml"
                                       :mocks "opts-mocks.yaml"
                                       :version true
                                       :help true}}
                               {})

      {:config-path "env-config.yaml"
       :mocks-path "env-mocks.yaml"
       :version true
       :help true}
      (adapters/inputs->config {:args [{:version true
                                        :help true}]
                                :opts {:config "opts-config.yaml"
                                       :mocks "opts-mocks.yaml"}}
                               {:config "env-config.yaml"
                                :mocks "env-mocks.yaml"})

      {:config-path "env-config.yaml"
       :mocks-path "env-mocks.yaml"
       :version true
       :help true}
      (adapters/inputs->config {:args [{:version true
                                        :help true}]}
                               {:config "env-config.yaml"
                                :mocks "env-mocks.yaml"}))))
