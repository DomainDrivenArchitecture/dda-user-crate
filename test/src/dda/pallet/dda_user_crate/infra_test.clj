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
(ns dda.pallet.dda-user-crate.infra-test
  (:require
   [clojure.test :refer :all]
   [schema.core :as s]
   [dda.pallet.dda-user-crate.infra :as sut]))

(def ssh-pub-key
  {:type "type"
   :public-key "pub-key"
   :comment "this is a comment"})

(def ssh-priv-key "priv-key")

(def ssh-key-pair
  {:public-key ssh-pub-key
   :private-key ssh-priv-key})

(def os-user-valid-config
  {:hashed-password "secret-pw"})

(def os-user-valid-complete-config
  (merge os-user-valid-config
         {:authorized-keys [ssh-pub-key]
          :personal-key ssh-key-pair}))

(deftest valid-configurations
  (is (s/validate sut/User os-user-valid-config))
  (is (s/validate sut/User os-user-valid-complete-config)))
