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
(ns dda.pallet.dda-user-crate.infra.user.os-user-0-3
  (:require
   [dda.pallet.dda-user-crate.infra.user.ssh-key-0-3 :as ssh-key]))

(defrecord OsUser
  [user-name
   encrypted-password
   authorized-keys
   personal-key])

(defn users-authorized-key-ids
  [username-key global-config]
  (-> global-config :os-user username-key :authorized-keys))

(defn users-personal-key-id
  [username-key global-config]
  (-> global-config :os-user username-key :personal-key))

(defn pallet-user-encrypted-password
  [username-key global-config]
  (-> global-config :os-user username-key :encrypted-password))

(defn new-os-user
  "Creates a operating system user with
* pw: encrypted - can be generated e.g. by mkpasswd test123.
  So password test123 is representet by sqliZ6M65Vfjo.
* authorized-keys: Vector of authorized-key-ids"
  ([user-name authorized-keys]
   (new-os-user user-name nil authorized-keys nil))
  ([user-name encrypted-password authorized-keys]
   (new-os-user user-name encrypted-password authorized-keys nil))
  ([user-name encrypted-password authorized-keys personal-key]
   (OsUser. user-name encrypted-password authorized-keys personal-key)))


(defn new-os-user-from-config
  "creates a new os user from configuration"
  [user-key global-config]
  (let [personal-key (users-personal-key-id user-key global-config)]
    (new-os-user
      (name user-key)
      (pallet-user-encrypted-password user-key global-config)
      (ssh-key/create-keys-from-config
        (users-authorized-key-ids user-key global-config)
        (ssh-key/ssh-key-config global-config))
      (when (some? personal-key)
        (ssh-key/create-key-from-config
          personal-key
          (ssh-key/ssh-key-config global-config))))))


(defn user-home-dir
  "provides the user home path."
  [os-user]
  (let [user-name (:user-name os-user)]
   (if (= user-name "root")
     "/root"
     (str "/home/" user-name))))

(defn user-ssh-dir
  "provides the user .ssh path."
  [os-user]
  (str (user-home-dir os-user) "/.ssh/"))
