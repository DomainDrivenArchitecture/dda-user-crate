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
    [pallet.repl :as pr]
    [dda.cm.operation :as operation]
    [dda.cm.existing :as existing]
    [dda.pallet.crate.dda-user-crate.user.os-user :as os-user]
    [dda.pallet.domain.dda-user-crate :as user]
    [dda.pallet.crate.dda-user-crate.group :as group]
    [dda.pallet.domain.dda-user-crate :as domain]))

(def provisioning-ip
    "192.168.56.104")

(def provisioning-user
  {:login "initial"
   :password "secure1234"})

(def ssh-pub-key
  (os-user/read-ssh-pub-key-to-config))

(def user-config
   {:user-name {:encrypted-password  "xxx"
                :authorized-keys [ssh-pub-key]}})

(def provider
  (existing/provider provisioning-ip "user-node" "dda-user-group"))

(def integrated-group-spec
  (merge
   (group/dda-user-group (domain/crate-stack-configuration user-config))
   (existing/node-spec provisioning-user)))

(defn apply-install []
  (pr/session-summary
    (operation/do-apply-install provider integrated-group-spec)))

(defn apply-config []
  (pr/session-summary
    (operation/do-apply-configure provider integrated-group-spec)))

(defn server-test []
  (pr/session-summary
    (operation/do-server-test provider integrated-group-spec)))
