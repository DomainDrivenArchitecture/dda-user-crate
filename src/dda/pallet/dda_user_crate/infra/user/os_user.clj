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
(ns dda.pallet.dda-user-crate.infra.user.os-user
  (:require
   [dda.pallet.dda-user-crate.infra.user.ssh-key :as ssh-key]
   [schema.core :as s]))

(def os-user-config
  {:encrypted-password s/Str
   (s/optional-key :authorized-keys) [ssh-key/ssh-public-key-config]
   (s/optional-key :personal-key) ssh-key/ssh-key-pair-config})

(defn user-home-dir
  "provides the user home path."
  [user-name]
  (if (= user-name "root")
    "/root"
    (str "/home/" user-name)))

(defn user-ssh-dir
  "provides the user .ssh path."
  [user-name]
  (str (user-home-dir user-name) "/.ssh/"))

(s/defn ssh-priv-key-from-env-to-config :- s/Str
  "function reads ssh private key from environment variable and returns it as a String"
  []
  (let [env-variable "SSH_PRIV_KEY"]
    (System/getenv env-variable)))

(defn read-ssh-pub-key-to-config
  "read the ssh-public-key to a config"
  [& {:keys [ssh-dir-path]}]
  (let [ssh-dir (or ssh-dir-path (str (System/getenv "HOME") "/.ssh"))]
    (ssh-key/string-to-pub-key-config (slurp (str ssh-dir "/id_rsa.pub")))))

(defn read-ssh-priv-key-to-config
  "read the ssh-private-key to a config"
  [& {:keys [ssh-dir-path read-from-env?]}]
  (let [ssh-dir (or ssh-dir-path (str (System/getenv "HOME") "/.ssh"))]
    (if read-from-env? (ssh-priv-key-from-env-to-config) (slurp (str ssh-dir "/id_rsa")))))

(defn read-ssh-keys-to-pair-config
  [& {:keys [ssh-dir-path read-from-env?]}]
  "read ssh-keys from current node to ssh-key-pair-config. If read-from-env? flag is specified,
   ssh-private-key will be read from enviroment variable SSH_PRIV_KEY"
    {:public-key (read-ssh-pub-key-to-config :ssh-dir-path ssh-dir-path)
     :private-key (read-ssh-priv-key-to-config :ssh-dir-path ssh-dir-path
                                               :read-from-env? read-from-env?)})
