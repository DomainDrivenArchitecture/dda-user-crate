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
(ns dda.pallet.dda-user-crate.domain
  (:require
   [schema.core :as s]
   [dda.pallet.commons.secret :as secret]
   [dda.pallet.commons.ssh-key :as ssh-key]
   [dda.pallet.dda-user-crate.infra :as infra]))

(def GpgKey {:public-key secret/Secret
             (s/optional-key :passphrase) secret/Secret
             (s/optional-key :private-key) secret/Secret})

(def Gpg
  {(s/optional-key :gpg) {:trusted-key GpgKey}})

(def Ssh
 {(s/optional-key :authorized-keys) [ssh-key/PublicSshKey]
  (s/optional-key :personal-key) ssh-key/SshKeyPair})

(def Settings {(s/optional-key :settings)
               (hash-set (s/enum :sudo :bashrc-d))})

(def User
  (s/either
    (merge {:hashed-password secret/Secret}
           Gpg Ssh Settings)
    (merge {:clear-password secret/Secret}
           Gpg Ssh Settings)))


(def UserDomainConfig {s/Keyword User})

(def UserDomainConfigResolved (secret/create-resolved-schema UserDomainConfig))

(def InfraResult {infra/facility infra/UserCrateConfig})

(s/defn ^:always-validate
  infra-configuration :- InfraResult
  [domain-config :- UserDomainConfigResolved]
  {infra/facility domain-config})
