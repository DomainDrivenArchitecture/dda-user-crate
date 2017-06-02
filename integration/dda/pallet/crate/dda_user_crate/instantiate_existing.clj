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
(ns dda.pallet.crate.dda-user-crate.instantiate-existing
  (:require
    [schema.core :as s]
    [dda.cm.operation :as operation]
    [dda.cm.existing :as existing]
    [dda.pallet.crate.dda-user-crate.user.os-user :as os-user]
    [dda.pallet.domain.dda-user-crate :as user]
    [dda.pallet.domain.dda-user-crate :as domain]))

(def ssh-pub-key
  (os-user/read-ssh-pub-key-to-config))

(def ssh-priv-key "$YOUR_PRIVATE_KEY")

(def ssh-key-pair
  {:public-key ssh-pub-key
   :private-key ssh-priv-key})

(def domain-config
  {:test {:encrypted-password  "USER_PASSWORD"
          :authorized-keys [ssh-pub-key]
          :personal-key ssh-key-pair}})

(def provisioning-ip
    "TARGET_IP")

(def provisioning-user
  {:login "EXISTING_USER_LOGIN"
   :password "EXISTING_USER_PASSWORD"})

(def provider
  (existing/provider provisioning-ip "node-id" "dda-user-group"))

(def integrated-group-spec
  (merge
   (domain/dda-user-group (domain/crate-stack-configuration domain-config))
   (existing/node-spec provisioning-user)))

(defn apply-install []
  (operation/do-apply-install (provider) (integrated-group-spec)))

(defn apply-config []
  (operation/do-apply-configure (provider) (integrated-group-spec)))

(defn server-test []
  (operation/do-server-test (provider) (integrated-group-spec)))
