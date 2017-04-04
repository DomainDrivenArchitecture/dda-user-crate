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

(ns dda.pallet.crate.dda-user-crate.user.os-user
  (:require 
   [dda.pallet.crate.dda-user-crate.user.ssh-key :as ssh-key]
   [schema.core :as s]))

(def os-user-config
  {:user-name s/Str
   :encrypted-password s/Str
   (s/optional-key :authorized-keys) [ssh-key/ssh-public-key-config]
   (s/optional-key :personal-key) ssh-key/ssh-key-pair-config})

(defn users-authorized-key-ids
  [username-key global-config]
  (-> global-config :os-user username-key :authorized-keys))

(defn users-personal-key-id
  [username-key global-config]
  (-> global-config :os-user username-key :personal-key))

(defn pallet-user-encrypted-password
  [username-key global-config]
  (-> global-config :os-user username-key :encrypted-password))

(defn user-home-dir
  "provides the user home path."
  [os-user-config]
  (let [user-name (:user-name os-user-config)]
  (if (= user-name "root") 
    "/root" 
    (str "/home/" user-name))))

(defn user-ssh-dir
  "provides the user .ssh path."
  [os-user]
  (str (user-home-dir os-user) "/.ssh/"))

(s/defn ssh-priv-key-from-env :- s/Str
  "function reads ssh private key from environment variable and returns it as a String"
  []
  (let [env-variable "SSH_PRIV_KEY"]
    (System/getenv env-variable)))

(defn read-ssh-keys-to-pair-config
  [& {:keys [ssh-path read-from-env?]}] :- ssh-key/ssh-key-pair-config
  "read ssh-keys from current node to ssh-key-pair-config. If read-from-env? flag is specified, 
   ssh-private-key will be read from enviroment variable SSH_PRIV_KEY"
    (let [ssh-dir (if ssh-path ssh-path (str (System/getenv "HOME") "/.ssh"))]
    {:public-key (ssh-key/string-to-pub-key-config (slurp (str ssh-dir "/id_rsa.pub")))
     :private-key (if read-from-env? (ssh-priv-key-from-env) (slurp (str ssh-dir "/id_rsa")))}
    ))

