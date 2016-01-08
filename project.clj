(defproject org.domaindrivenarchitecture/dda-user-crate "0.1.0"
  :description "A crate to handle configuration, their dependencies, documentation and validation"
  :url "http://example.com/FIXME"
  :license {:name "Apache License, Version 2.0"
             :url "https://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.palletops/pallet "0.8.0-RC.11"]
                 [com.palletops/stevedore "0.8.0-beta.7"]
                 [ch.qos.logback/logback-classic "1.0.9"]]
  :repositories [["snapshots" :clojars]
                 ["releases" :clojars]]
  :deploy-repositories [["snapshots" :clojars]
                        ["releases" :clojars]]
  :pallet {:source-paths ["src"]}
  :local-repo-classpath true
  :classifiers {:tests {:source-paths ^:replace ["test"]
                        :resource-paths ^:replace []}})