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
(ns dda.pallet.dda-user-crate.infra.gpg
  (:require
   [schema.core :as s]
   [clojure.java.io :as io]
   [pallet.actions :as actions]
   [pallet.action :as action]
   [dda.config.commons.ssh-key :as ssh-common]))

(def GpgKey {:public-key s/Str
             :public-key-id s/Str
             (s/optional-key :passphrase) s/Str
             (s/optional-key :private-key) s/Str})

(def GpgConfig
  {:trusted-key GpgKey})

(def Gpg
  {(s/optional-key :gpg) GpgConfig})

(s/defn install
  [user-name :- s/Str
   config :- GpgConfig]
  (actions/package "gnupg2")
  (actions/remote-file
    "/usr/lib/gpg-trust-all.sh"
    :mode "755"
    :content (slurp (io/resource "gpg-trust-all.sh"))
    :owner user-name
    :group user-name
    :action :create))

(s/defn configure-user
  [user-name :- s/Str
   config :- GpgConfig]
  (let [{:keys [trusted-key]} config
        {:keys [public-key public-key-id private-key passphrase]} trusted-key
        user-home (ssh-common/user-home-dir user-name)]
    (actions/remote-file
      (str user-home "/pub.key")
      :content public-key
      :mode "600"
      :owner user-name
      :group user-name
      :action :create)
    (actions/remote-file
      (str user-home "/priv.key")
      :content private-key
      :mode "600"
      :owner user-name
      :group user-name
      :action :create)
    (actions/directory
      (str user-home "/.gnupg")
      :mode "700"
      :owner user-name
      :group user-name
      :action :create)
    (actions/remote-file
      (str user-home "/.gnupg/gpg-agent.conf")
      :mode "600"
      :content (slurp (io/resource "gpg-agent.conf"))
      :owner user-name
      :group user-name
      :action :create)
    (action/with-action-options
     {:sudo-user user-name
      :script-dir user-home
      :script-env {:HOME user-home}}
     (actions/exec-checked-script
      "import & trust gpg key"
      ("echo" "$(whoami)" ">" "log.txt")
      ("gpgconf" "--kill" "gpg-agent")
      ("gpgconf" "--launch" "gpg-agent")
      ("gpg" "--import" ~(str user-home "/pub.key"))
      ("echo" ~passphrase "|" "/usr/lib/gnupg/gpg-preset-passphrase" "--preset" ~public-key-id)
      ("gpg" "--batch" "--import" ~(str user-home "/priv.key"))
      ("/usr/lib/gpg-trust-all.sh")))))
