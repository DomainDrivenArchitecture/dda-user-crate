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

(ns org.domaindrivenarchitecture.pallet.crate.user.ssh-key)

(defrecord SshKey
  [type public-key comment private-key])

(defn ssh-key-config
  [global-config]
  (-> global-config :ssh-keys))

(defn new-ssh-key
  "Constructor for ssh key"
  ([type public-key comment]
    (SshKey. type public-key comment nil))
  ([type public-key comment private-key]
    (SshKey. type public-key comment private-key)))

(defn public-key-formated
  "returns a authorized_keys formated public key string."
  [ssh-key-record]
  (str 
    (:type ssh-key-record) " " 
    (:public-key ssh-key-record) " " 
    (:comment ssh-key-record)))

(defn create-key-from-config
  "consrtucts a sequence of authorized keys from given key-keys and config."
  [key-id key-config]
  (let [key-map (key-id key-config)]
    (new-ssh-key (:type key-map) 
                 (:public-key key-map)
                 (:comment key-map)
                 (:private-key key-map))))

(defn create-keys-from-config
  "consrtucts a sequence of authorized keys from given key-keys and config."
  [key-ids key-config]
  (map #(create-key-from-config % key-config) key-ids))