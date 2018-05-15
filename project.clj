(defproject dda/dda-user-crate "1.0.5-SNAPSHOT"
  :description "A crate to handle users"
  :url "https://www.domaindrivenarchitecture.org"
  :license {:name "Apache License, Version 2.0"
             :url "https://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [
                 ;[dda/pallet-common "0.4.1-SNAPSHOT"]
                 ;[org.clojure/core.incubator "0.1.4"]
                 ;[org.clojure/tools.logging "0.4.0"]
                 ;[org.clojure/tools.macro "0.1.5"]
                 ;[org.clojure/tools.cli "0.3.7"]
                 ;[org.clojure/algo.monads "0.1.6"]
                 ;[com.palletops/chiba "0.2.0"]
                 ;[clj-ssh "0.5.14"]
                 ;[enlive "1.1.6" :exclusions [org.clojure/clojure]]
                 ;[prismatic/schema "1.1.9"]
                 ;[commons-codec "1.11"]
                 [dda/dda-pallet "2.1.3-SNAPSHOT"]]
  :source-paths ["main/src"]
  :resource-paths ["main/resources"]
  :repositories [["snapshots" :clojars]
                 ["releases" :clojars]]
  :deploy-repositories [["snapshots" :clojars]
                        ["releases" :clojars]]
  :profiles {:dev {:source-paths ["integration/src"
                                  "test/src"
                                  "uberjar/src"]
                   :resource-paths ["integration/resources"
                                    "test/resources"]
                   :dependencies
                   [[org.clojure/test.check "0.10.0-alpha2"]
                    [org.clojure/core.async "0.4.474"]
                    [org.domaindrivenarchitecture/pallet-aws "0.2.8.2"]
                    [ch.qos.logback/logback-classic "1.3.0-alpha4"]
                    [org.slf4j/jcl-over-slf4j "1.8.0-beta2"]]
                   :plugins [[lein-sub "0.3.0"]
                             [lein-pprint "1.1.2"]]
                   :leiningen/reply
                   {:dependencies [[org.slf4j/jcl-over-slf4j "1.8.0-beta0"]]
                    :exclusions [commons-logging]}
                   :repl-options {:init-ns dda.pallet.dda-user-crate.app.instantiate-aws}}
             :test {:test-paths ["test/src"]
                    :resource-paths ["test/resources"]
                    :dependencies []}
             :uberjar {:source-paths ["uberjar/src"]
                       :resource-paths ["uberjar/resources"]
                       :aot :all
                       :main dda.pallet.dda-user-crate.main
                       :dependencies [[org.clojure/tools.cli "0.3.7"]
                                      [ch.qos.logback/logback-classic "1.3.0-alpha4"]
                                      [org.slf4j/jcl-over-slf4j "1.8.0-beta2"]]}}
  :local-repo-classpath true)
