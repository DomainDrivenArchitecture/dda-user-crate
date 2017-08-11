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
   [schema.core :as s]
   [dda.pallet.core.dda-crate :as dda-crate]
   [dda.pallet.dda-user-crate.infra.user :as user]
   [dda.config.commons.user-env :as user-env]
   [dda.config.commons.ssh-key :as ssh-key]))

(def facility :dda-user)
(def version [0 3 7])

(def OsUser
 {:encrypted-password s/Str
  (s/optional-key :authorized-keys) [ssh-key/PublicSshKey]
  (s/optional-key :personal-key) ssh-key/SshKeyPair})

(def UserCrateConfig
  {s/Keyword OsUser})

(defn read-ssh-pub-key-to-config
  ( []
   (user-env/read-ssh-pub-key-to-config))
  ( [& {:keys [ssh-dir-path]}]
    (user-env/read-ssh-pub-key-to-config :ssh-dir-path ssh-dir-path)))

(defn read-ssh-priv-key-to-config
  ( []
   (user-env/read-ssh-priv-key-to-config))
  ( [& {:keys [ssh-dir-path read-from-env?]}]
    (user-env/read-ssh-priv-key-to-config
     :ssh-dir-path ssh-dir-path :read-from-env? read-from-env?)))

(defn read-ssh-keys-to-pair-config
  ( []
   (user-env/read-ssh-keys-to-pair-config))
  ( [& {:keys [ssh-dir-path read-from-env?]}]
   (user-env/read-ssh-keys-to-pair-config
    :ssh-dir-path ssh-dir-path
    :read-from-env? read-from-env?)))

(defn install-user [config]
  (doseq [[k v] config]
    (user/create-sudo-user (name k) v)))

(defn configure-user [config]
  (doseq [[k v] config]
    (user/configure-authorized-keys (name k) v)
    (user/configure-ssh-key (name k) v)
    (user/configure-sudo (name k))))

(s/defmethod dda-crate/dda-install facility
  [dda-crate config]
  "user-crate: install routine"
  (install-user config))

(s/defmethod dda-crate/dda-configure facility
  [dda-crate config]
  "user-crate: configure routine"
  (configure-user config))

(def user-crate
  (dda-crate/make-dda-crate
   :facility facility
   :version version))

(def with-user
 (dda-crate/create-server-spec user-crate))
