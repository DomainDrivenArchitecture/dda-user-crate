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
(ns dda.pallet.dda-user-crate.infra.user
  (:require
   [clojure.string :as string]
   [pallet.actions :as actions]
   [dda.pallet.dda-user-crate.infra.user.os-user :as os-user]
   [dda.pallet.dda-user-crate.infra.user.ssh-key :as ssh-key]))


(defn configure-authorized-keys
  "configure the authorized_keys for a given user, all existing
  authorized_keys will be overwritten."
  [user-name os-user-config]
  (let [ssh-dir (os-user/user-ssh-dir user-name)
        authorized-keys (map ssh-key/format-public-key
                            (:authorized-keys os-user-config))]
    (actions/directory
      ssh-dir
      :owner user-name
      :group user-name
      :mode "755")
    (actions/remote-file
      (str ssh-dir "authorized_keys")
      :overwrite-changes true
      :owner user-name
      :group user-name
      :mode "644"
      :content (string/join
                \newline
                authorized-keys))))

(defn configure-ssh-key
  "configer the users ssh_key."
  [user-name os-user-config]
  (let [ssh-key (:personal-key os-user-config)
        ssh-dir (os-user/user-ssh-dir user-name)]
    (when (some? (:private-key ssh-key))
      (actions/directory
        ssh-dir
        :owner user-name
        :group user-name
        :mode "755")
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

(defn configure-sudo
  "Add user to sudoers without password."
  [user-name]
  (actions/remote-file
   (str "/etc/sudoers.d/" user-name)
   :owner "root"
   :group "root"
   :mode "440"
   :literal true
   :content (str
             user-name "    ALL = NOPASSWD: ALL\n"
             "pallet    ALL=(" user-name ") NOPASSWD: ALL\n")))

(defn create-sudo-user
  "creates a sudo user with pw is encrypted handed over.
  Passwords can be generated e.g. by mkpasswd test123.
  So password test1234 is representet by 3hLlUVSs1Aa1c"
  [user-name user-config]
  (actions/group "sudo" :action :create)
  (actions/user user-name
                :action :create
                :create-home true
                :shell :bash
                :groups ["sudo"]
                :password (:encrypted-password user-config)))