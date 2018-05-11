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
(ns dda.pallet.dda-user-crate.infra
  (:require
   [clojure.tools.logging :as logging]
   [schema.core :as s]
   [pallet.actions :as actions]
   [dda.pallet.core.infra :as core-infra]
   [dda.pallet.dda-user-crate.infra.schema :as schema]
   [dda.pallet.dda-user-crate.infra.user :as user]
   [dda.pallet.dda-user-crate.infra.ssh :as ssh]
   [dda.pallet.dda-user-crate.infra.bash :as bash]
   [dda.pallet.dda-user-crate.infra.gpg :as gpg]))

(def facility :dda-user)

(def GpgKey schema/GpgKey)

(def Ssh schema/Ssh)

(def User schema/User)

(def UserCrateConfig schema/UserCrateConfig)

(s/defn filter-ssh :- Ssh
  [config :- User]
  (select-keys config [:ssh-authorized-keys :ssh-key]))

(defn install-user [config]
  (user/create-sudo-group)
  (doseq [[k v] config]
    (user/create-user (name k) v)
    (when (contains? v :gpg)
      (gpg/install (name k) (:gpg v)))))

(defn configure-user [config]
  (doseq [[k v] config]
    (let [{:keys [settings]
           :or {settings #{:sudo :bashrc-d}}} v]
      (ssh/configure-authorized-keys (name k) (filter-ssh v))
      (when (contains? v :ssh-key)
        (ssh/configure-ssh-key (name k) (filter-ssh v)))
      (when (contains? settings :sudo)
        (user/configure-user-sudo (name k)))
      (when (contains? settings :bashrc-d)
        (bash/configure-bashrc-d (name k) v))
      (when (contains? v :gpg)
        (gpg/configure (name k) (:gpg v))))))

(s/defmethod core-infra/dda-install facility
  [dda-crate config]
  "user-crate: install routine"
  (install-user config))

(s/defmethod core-infra/dda-configure facility
  [dda-crate config]
  "user-crate: configure routine"
  (configure-user config))

(def user-crate
  (core-infra/make-dda-crate-infra
   :facility facility))

(def with-user
 (core-infra/create-infra-plan user-crate))
