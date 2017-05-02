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
(ns dda.pallet.domain.dda-user-crate
  (:require
   [schema.core :as s]
   [pallet.api :as api]
   [dda.pallet.crate.config :as config-crate]
   [dda.pallet.crate.dda-user-crate :as user-crate]))

(def UserDomainConfig
  user-crate/UserCrateConfig)

(def UserCrateStackConfig
  {:group-specific-config
   {:dda-user-group {:dda-user user-crate/UserCrateConfig}}})

(s/defn ^:always-validate dda-user-crate-stack-configuration :- UserCrateStackConfig
  [convention-config :- UserDomainConfig]
  {:group-specific-config
   {:dda-user-group {:dda-user convention-config}}})

(s/defn ^:always-validate dda-user-group
  [domain-config :- UserDomainConfig]
  (let [config (dda-user-crate-stack-configuration domain-config)]
    (api/group-spec
     "dda-user-group"
     :extends [(config-crate/with-config config)
               user-crate/with-user])))
