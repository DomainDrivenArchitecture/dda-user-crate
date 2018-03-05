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
(ns dda.pallet.dda-user-crate.app.instantiate-existing
  (:require
    [schema.core :as s]
    [pallet.repl :as pr]
    [dda.pallet.commons.operation :as operation]
    [dda.cm.existing :as existing]
    [dda.config.commons.user-env :as user-env]
    [dda.pallet.dda-user-crate.app :as app]))

(def provisioning-ip
    "192.168.56.104")

(def provisioning-user
  {:login "initial"
   :password "secure1234"})

(def ssh-pub-key
  (user-env/read-ssh-pub-key-to-config))

(def user-config
   {:user-name {:hashed-password  "xxx"
                :ssh-authorized-keys [ssh-pub-key]}})

(def provider
  (existing/provider provisioning-ip "user-node" "dda-user-group"))

(def provisioning-spec
  (merge
   (app/dda-user-group (app/app-configuration user-config))
   (existing/node-spec provisioning-user)))

(defn apply-install
  [& options]
  (let [{:keys [summarize-session]
         :or {summarize-session true}} options]
    (operation/do-apply-install
     (provider)
     (provisioning-spec)
     :summarize-session summarize-session)))

(defn apply-configure
  [& options]
  (let [{:keys [summarize-session]}
        :or {summarize-session true} options]
    (operation/do-apply-configure
     (provider)
     (provisioning-spec)
     :summarize-session summarize-session)))

(defn test
  [& options]
  (let [{:keys [summarize-session]}
        :or {summarize-session true} options]
    (operation/do-server-test
     (provider)
     (provisioning-spec)
     :summarize-session summarize-session)))
