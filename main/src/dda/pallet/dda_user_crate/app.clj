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
(ns dda.pallet.dda-user-crate.app
  (:require
   [clojure.tools.logging :as logging]
   [schema.core :as s]
   [dda.cm.group :as group]
   [dda.pallet.commons.secret :as secret]
   [dda.pallet.core.app :as core-app]
   [dda.pallet.dda-config-crate.infra :as config-crate]
   [dda.pallet.dda-user-crate.infra :as infra]
   [dda.pallet.dda-user-crate.domain :as domain]))

(def with-user infra/with-user)

(def UserDomainConfig domain/UserDomainConfig)

(def UserDomainConfigResolved domain/UserDomainConfigResolved)

(def InfraResult domain/InfraResult)

(def DdaUserAppConfig
  {:group-specific-config {s/Keyword InfraResult}})

(s/defn ^:always-validate
  app-configuration-resolved :- DdaUserAppConfig
  [config :- UserDomainConfigResolved
   & options]
  (let [{:keys [group-key] :or {group-key infra/facility}} options]
    {:group-specific-config {group-key (domain/infra-configuration config)}}))

(s/defn ^:always-validate
  app-configuration :- DdaUserAppConfig
  [domain-config :- UserDomainConfig
   & options]
  (let [resolved-domain-config (secret/resolve-secrets domain-config UserDomainConfig)]
    (apply app-configuration-resolved resolved-domain-config options)))

(s/defmethod ^:always-validate
  core-app/group-spec infra/facility
  [crate-app
   app-config :- DdaUserAppConfig]
  (group/group-spec
    app-config [(config-crate/with-config app-config)
                with-user]))

(def crate-app (core-app/make-dda-crate-app
                  :facility infra/facility
                  :domain-schema UserDomainConfig
                  :domain-schema-resolved UserDomainConfigResolved
                  :default-domain-file "user.edn"))
