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
   [dda.config.commons.user-env :as user-env]
   [dda.pallet.dda-user-crate.infra.schema :as schema]))

(s/defn install
  [user-name :- s/Str
   config :- schema/User]
  (actions/package "gnupg2")
  (actions/remote-file
    "/usr/lib/gpg-trust-all.sh"
    :mode "755"
    :content (slurp (io/resource "gpg-trust-all.sh"))
    :owner user-name
    :group user-name
    :action :create))

(s/defn configure
  [user-name :- s/Str
   config :- schema/User]
  (let [{:keys [trusted-key]} (:gpg config)
        {:keys [public-key private-key passphrase]} trusted-key
        user-home (user-env/user-home-dir user-name)]
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
      :content "allow-loopback-pinentry"
      :owner user-name
      :group user-name
      :action :create)
    (action/with-action-options
     {:sudo-user user-name
      :script-dir user-home
      :script-env {:HOME user-home}}
     (actions/exec-checked-script
      "import & trust gpg key"
      ("gpgconf" "--reload gpg-agent")
      ("gpg2" "--import" ~(str user-home "/pub.key"))
      ("echo" ~passphrase "|" "gpg2" "--pinentry-mode loopback"
              "--batch --passphrase-fd 0"
              "--import" ~(str user-home "/priv.key"))
      ("/usr/lib/gpg-trust-all.sh")))))
