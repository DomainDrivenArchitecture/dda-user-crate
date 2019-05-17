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
(ns dda.pallet.dda-user-crate.infra.ssh
  (:require
   [clojure.string :as string]
   [clojure.java.io :as io]
   [schema.core :as s]
   [pallet.actions :as actions]
   [dda.config.commons.ssh-key :as ssh-key]))

(def Ssh
 {(s/optional-key :ssh-authorized-keys) [ssh-key/PublicSshKey]
  (s/optional-key :ssh-key) ssh-key/SshKeyPair})

(s/defn configure-ssh-client
  "configure the ssh client."
  [user-name :- s/Str]
  (let [ssh-dir (ssh-key/user-ssh-dir user-name)]
    (actions/directory
     ssh-dir
     :owner user-name
     :group user-name
     :mode "755")
    (actions/remote-file
     (str ssh-dir "config")
     :overwrite-changes true
     :owner user-name
     :group user-name
     :mode "644"
     :content (slurp (io/resource "ssh_config")))))

(s/defn configure-authorized-keys
  "configure the authorized_keys for a given user, all existing
  authorized_keys will be overwritten."
  [user-name :- s/Str
   ssh-config :- Ssh]
  (let [ssh-dir (ssh-key/user-ssh-dir user-name)
        authorized-keys (map ssh-key/format-public-key
                            (:ssh-authorized-keys ssh-config))]
    (actions/remote-file
      (str ssh-dir "authorized_keys")
      :overwrite-changes true
      :owner user-name
      :group user-name
      :mode "644"
      :content (string/join
                \newline
                authorized-keys))))

(s/defn configure-ssh-key
  "configer the users ssh_key."
  [user-name :- s/Str
   ssh-config :- Ssh]
  (let [ssh-dir (ssh-key/user-ssh-dir user-name)
        ssh-key (:ssh-key ssh-config)]
    (when (some? (:private-key ssh-key))
      (actions/remote-file
        (str ssh-dir "id_rsa")
        :overwrite-changes true
        :owner user-name
        :group user-name
        :mode "600"
        :content (:private-key ssh-key))
      (actions/remote-file
        (str ssh-dir "id_rsa.pub")
        :overwrite-changes true
        :owner user-name
        :group user-name
        :mode "644"
        :content (ssh-key/format-public-key (:public-key ssh-key))))))

(s/defn configure-user 
  [user-name :- s/Str
   ssh-config :- Ssh]
  (configure-ssh-client user-name ssh-config)
  (configure-authorized-keys user-name ssh-config)
  (when (contains? ssh-config :ssh-key)
    (configure-ssh-key user-name ssh-config)))
