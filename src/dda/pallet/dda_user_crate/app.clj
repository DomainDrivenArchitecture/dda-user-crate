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
   [schema.core :as s]
   [pallet.api :as api]
   [dda.pallet.dda-config-crate.infra :as config-crate]
   [dda.pallet.dda-user-crate.infra :as infra]
   [dda.pallet.dda-user-crate.domain :as domain]))

(def UserAppConfig
  {:group-specific-config
   {s/Keyword {:dda-user infra/UserCrateConfig}}})

(s/defn ^:allways-validate create-app-configuration :- UserAppConfig
 [config :- infra/UserCrateConfig
  group-key :- s/Keyword]
 {:group-specific-config
    {group-key config}})

(def with-user infra/with-user)

(defn app-configuration
  [domain-config
   & {:keys [group-key] :or {group-key :dda-user-group}}]
 (s/validate domain/UserDomainConfig domain-config)
 (create-app-configuration
  (domain/infra-configuration domain-config) group-key))

(s/defn ^:always-validate dda-user-group
  [config :- UserAppConfig]
  (let [group-name (name (key (first (:group-specific-config config))))]
    (api/group-spec
      group-name
      :extends [(config-crate/with-config config)
                with-user])))
