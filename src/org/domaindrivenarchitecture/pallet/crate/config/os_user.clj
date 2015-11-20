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

(ns org.domaindrivenarchitecture.pallet.crate.config.os-user
 )

(defrecord OsUser
  [encrypted-password authorized-key-ids])

(defn new-os-user
  "Creates a operating system user with 
* pw: encrypted - can be generated e.g. by mkpasswd test123. 
  So password test123 is representet by sqliZ6M65Vfjo.
* authorized-keys: Vector of authorized-key-ids"
  ([authorized-keys]
    (new-os-user nil authorized-keys))
  ([encrypted-password authorized-keys]
    (OsUser. encrypted-password authorized-keys))
  )