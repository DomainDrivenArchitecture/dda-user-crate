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


(ns org.domaindrivenarchitecture.pallet.crate.user 
  (:require
    [pallet.actions :as actions]
    [pallet.crate.ssh-key :as ssh-key]
    [org.domaindrivenarchitecture.pallet.crate.config.ssh-key :as ssh-key-record]
    ))

(defn- add-authorized-keys-to-user-wrapped 
  [user-name 
   authorized-key-ids 
   authorized-key-config
   result] 
  (let [authorized-key-id 
        (first authorized-key-ids)]
    (if (empty? authorized-key-ids)
      result
      (recur 
        user-name 
        (pop authorized-key-ids)
        authorized-key-config 
        (let [authorized-key-key
              (keyword (peek authorized-key-ids))
              ssh-key-record 
              (authorized-key-key authorized-key-config)                  
              ]
          (merge
            result
            {authorized-key-key
             (ssh-key/authorize-key
                   user-name
                   (ssh-key-record/format-public-key ssh-key-record))}
            )
          ))
      )
    ))

(defn get-users-authorized-key-ids
  [username-key global-config]
  (-> global-config
    :os-user
    username-key
    :authorized-key-ids)
  )

(defn authorized-key-config
  [global-config]
  (-> global-config
    :ssh-keys)
  )

(defn pallet-user-encrypted-password
  [username-key global-config]
  (-> global-config
    :os-user
    username-key
    :encrypted-password)
  )

(defn add-authorized-keys-to-user 
  [& {:keys [user-name 
            authorized-key-ids 
            authorized-key-config
            result]
      :or {result {} }}] 
  (add-authorized-keys-to-user-wrapped 
    user-name 
    authorized-key-ids
    authorized-key-config
    result)
  )

(defn configure-sudo-for-user
  ""
  [user-name]
  (actions/remote-file 
    (str "/etc/sudoers.d/" user-name) 
    :owner "root" 
    :group "root"
    :mode "440"
    :literal true
    :content (str 
               user-name
               "    ALL = NOPASSWD: ALL\n"
               "pallet"
               "    ALL=(" user-name ") NOPASSWD: ALL\n")
    )
  )

(defn create-sudo-user
  "creates a sudo user with pw is encrypted handed over. 
Passwords can be generated e.g. by mkpasswd test123. 
So password test1234 is representet by 3hLlUVSs1Aa1c"
  [& {:keys [user-name 
             encrypted-password
             authorized-key-ids 
             authorized-key-config]
      :or {authorized-key-ids []
           authorized-key-config {} }}] 
  (actions/group "sudo" :action :create)
  (actions/user user-name 
                :action :create 
                :create-home true 
                :shell :bash
                :groups ["sudo"]
                :password encrypted-password)
  (add-authorized-keys-to-user 
    :user-name user-name 
    :authorized-key-ids authorized-key-ids 
    :authorized-key-config authorized-key-config)
  (configure-sudo-for-user user-name)
  )