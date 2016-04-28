(defproject org.domaindrivenarchitecture/dda-user-crate "0.3.1"
  :description "A crate to handle users"
  :url "https://www.domaindrivenarchitecture.org"
  :license {:name "Apache License, Version 2.0"
             :url "https://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]                 
                 [prismatic/schema "1.1.0"]
                 [com.palletops/pallet "0.8.12"]
                 [com.palletops/stevedore "0.8.0-beta.7"]
                 [org.domaindrivenarchitecture/dda-config-crate "0.3.1"]]
  :repositories [["snapshots" :clojars]
                 ["releases" :clojars]]
  :deploy-repositories [["snapshots" :clojars]
                        ["releases" :clojars]]
  :pallet {:source-paths ["src"]}
  :local-repo-classpath true
  :profiles {:dev
            {:dependencies
             [[org.clojure/test.check "0.9.0"]
              [com.palletops/pallet "0.8.12" :classifier "tests"]
              [org.domaindrivenarchitecture/dda-pallet-commons "0.1.2" :classifier "tests"]]
             :plugins
             [[com.palletops/pallet-lein "0.8.0-alpha.1"]]}
             :leiningen/reply
              {:dependencies [[org.slf4j/jcl-over-slf4j "1.7.21"]]
               :exclusions [commons-logging]}}
  :classifiers {:tests {:source-paths ^:replace ["test"]
                        :resource-paths ^:replace []}}
)