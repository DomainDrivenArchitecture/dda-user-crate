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
   [dda.config.commons.ssh-key :as ssh-commons]
   [dda.config.commons.gpg-key :as gpg-commons]
   [dda.config.commons.map-utils :as mu]
   [dda.pallet.commons.secret :as secret]
   [clojure.tools.logging :as logging]
   [dda.pallet.dda-user-crate.domain.ssh :as ssh]
   [dda.pallet.dda-user-crate.infra :as infra]))

(def GpgKey {:public-key secret/Secret
             (s/optional-key :passphrase) secret/Secret
             (s/optional-key :private-key) secret/Secret})
(def Gpg
  {(s/optional-key :gpg) {:trusted-key GpgKey}})

(def Ssh ssh/Ssh)
(def SshResolved ssh/SshResolved)

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

(defn-
  user-infra-configuration
  [user-domain-config]
  (let [{:keys [hashed-password clear-password gpg ssh-authorized-keys ssh-key settings]} user-domain-config]
    (merge
     (when (contains? user-domain-config :hashed-password)
       {:hashed-password hashed-password})
     (when (contains? user-domain-config :clear-password)
       {:clear-password clear-password})
     (when (contains? user-domain-config :gpg)
       {:gpg (mu/deep-merge gpg
                            {:trusted-key {:public-key-id (gpg-commons/hex-id (get-in gpg [:trusted-key :public-key]))}})})
     (when (contains? user-domain-config :ssh-authorized-keys)
       {:ssh-authorized-keys (ssh/authorized-keys-infra-configuration ssh-authorized-keys)})
     (when (contains? user-domain-config :ssh-key)
       {:ssh-key (ssh/key-pair-infra-configuration ssh-key)})
     (when (contains? user-domain-config :settings)
       {:settings settings}))))


(s/defn ^:always-validate
  infra-configuration :- InfraResult
  [domain-config :- UserDomainConfigResolved]
  {infra/facility
    (apply merge (map (fn [[k v]] {k (user-infra-configuration v)}) domain-config))})
