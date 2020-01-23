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
(ns dda.pallet.dda-user-crate.infra.bash
  (:require
   [schema.core :as s]
   [pallet.actions :as actions]
   [pallet.action :as action]
   [dda.config.commons.user-home :as user-home]))

(s/defn install-bashrc-d
  [user-name :- s/Str]
  (let [user-home (user-home/user-home-dir user-name)]
    (actions/directory
      (str user-home "/.bashrc.d")
      :owner user-name
      :group user-name
      :mode "755")
    (actions/exec-checked-script
      "enable sourcing for bashrc files"
      ("sudo" "-H" "-u" ~user-name "bash" "-c"
       "'printf \"
# source .bashrc.d files
if [ -d ~/.bashrc.d ]; then
  for i in ~/.bashrc.d/*.sh; do
   if [ -r $i ]; then
      . \\$i
    fi
  done
  unset i
fi
\" >>  ~/.bashrc'"))))
