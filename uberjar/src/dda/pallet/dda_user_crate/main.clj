; Licensed to the Apache Software Foundation (ASF) under one
; or more contributor license agreements. See the NOTICE file
; distributed with this work for additional information
; regarding copyright ownership. The ASF licenses this file
; to you under the Apache License, Version 2.0 (the
; "License"); you may not use this file except in compliance
; with the License. You may obtain a copy of the License at
;
; http://www.apache.org/licenses/LICENSE-2.0
;
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.

(ns dda.pallet.dda-user-crate.main
  (:gen-class)
  (:require
   [clojure.string :as str]
   [clojure.tools.cli :as cli]
   [dda.pallet.core.main-helper :as mh]
   [dda.pallet.core.app :as core-app]
   [dda.pallet.dda-user-crate.app :as app]))

(defn usage [options-summary]
  (str/join
   \newline
   ["dda-user-crate provision over ssh or locally users to an existing- or cloud-targets. One"
    "shoot provisioning is supported but also continuous configuration application is possible."
    ""
    "Usage: java -jar dda-user-crate-[version]-standalone.jar [options] user.edn"
    ""
    "Options:"
    options-summary
    ""
    "user.edn"
    "  - follows the edn format."
    "  - has to be a valid UserConventionConfig (see: https://github.com/DomainDrivenArchitecture/dda-user-crate)"
    ""]))

(defn -main [& args]
  (let [{:keys [options arguments errors summary help]} (cli/parse-opts args mh/cli-options)
        verbose (if (contains? options :verbose) 1 0)]
    (cond
      help (mh/exit 0 (usage summary))
      errors (mh/exit 1 (mh/error-msg errors))
      (not= (count arguments) 1) (mh/exit 1 (usage summary))
      (:serverspec options) (if (core-app/existing-serverspec
                                  app/crate-app
                                  {:convention (first arguments)
                                   :targets (:targets options)
                                   :verbosity verbose})
                                (mh/exit-test-passed)
                                (mh/exit-test-failed))
      (:configure options) (if (core-app/existing-configure
                                  app/crate-app
                                  {:convention (first arguments)
                                   :targets (:targets options)})
                             (mh/exit-default-success)
                             (mh/exit-default-error))
      :default (let [result (core-app/existing-install
                                app/crate-app
                                {:convention (first arguments)
                                 :targets (:targets options)})]
                  (if result
                      (mh/exit-default-success)
                      (mh/exit-default-error))))))
