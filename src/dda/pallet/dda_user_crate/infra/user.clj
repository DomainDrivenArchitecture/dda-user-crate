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
   [pallet.actions :as actions]))

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
