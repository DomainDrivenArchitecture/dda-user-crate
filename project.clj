(defproject dda/dda-user-crate "0.4.1-SNAPSHOT"
  :description "A crate to handle users"
  :url "https://www.domaindrivenarchitecture.org"
  :license {:name "Apache License, Version 2.0"
             :url "https://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [prismatic/schema "1.1.6"]
                 [com.palletops/pallet "0.8.12" :exclusions [org.clojure/tools.cli]]
                 [dda/dda-pallet "0.4.0"]]
  :profiles {:dev
             {:source-paths ["integration"]
              :resource-paths ["dev-resources"]
              :dependencies
              [[org.domaindrivenarchitecture/pallet-aws "0.2.8.2"]
               [com.palletops/pallet "0.8.12" :classifier "tests"]
               [org.domaindrivenarchitecture/dda-pallet-commons "0.3.2" :classifier "tests"]
               [ch.qos.logback/logback-classic "1.2.3"]
               [org.slf4j/jcl-over-slf4j "1.7.22"]]
              :plugins
              [[lein-sub "0.3.0"]]}
             :leiningen/reply
               {:dependencies [[org.slf4j/jcl-over-slf4j "1.7.22"]]
                :exclusions [commons-logging]}}
  :local-repo-classpath true
  :repositories [["snapshots" :clojars]
                 ["releases" :clojars]]
  :deploy-repositories [["snapshots" :clojars]
                        ["releases" :clojars]]
  :classifiers {:tests {:source-paths ^:replace ["test" "integration"]
                         :resource-paths ^:replace ["dev-resources"]}})
