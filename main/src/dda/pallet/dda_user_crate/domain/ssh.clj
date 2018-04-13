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
(ns dda.pallet.dda-user-crate.domain.ssh
  (:require
   [schema.core :as s]
   [dda.config.commons.ssh-key :as ssh-commons]
   [dda.pallet.commons.secret :as secret]))

(def SshSimpleKeyPair
 {:public-key secret/Secret
  :private-key secret/Secret})

(def SshAuthorizedKeys [secret/Secret])

(def Ssh
 {(s/optional-key :ssh-authorized-keys) [secret/Secret]
  (s/optional-key :ssh-key) SshSimpleKeyPair})

(def SshSimpleKeyPairResolved (secret/create-resolved-schema SshSimpleKeyPair))

(def SshAuthorizedKeysResolved (secret/create-resolved-schema SshAuthorizedKeys))

(s/defn  ^:always-validate
  key-pair-infra-configuration :- ssh-commons/SshKeyPair
  [key-pair-domain-config :- SshSimpleKeyPairResolved]
  {:public-key (ssh-commons/string-to-pub-key-config (:public-key key-pair-domain-config))
   :private-key (:private-key key-pair-domain-config)})

(s/defn  ^:always-validate
  authorized-keys-infra-configuration :- [ssh-commons/PublicSshKey]
  [authorized-keys-domain-config :- SshAuthorizedKeysResolved]
  (into []
        (map ssh-commons/string-to-pub-key-config authorized-keys-domain-config)))
