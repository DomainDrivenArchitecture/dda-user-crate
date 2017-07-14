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
(ns dda.pallet.dda-user-crate.infra.ssh-key-test
  (:require
   [clojure.test :refer :all]
   [schema.core :as s]
   [dda.pallet.dda-user-crate.infra.user.ssh-key :as sut]))

(def valid-ssh-pub-key-config
  {:type "type"
   :public-key "pub-key"
   :comment "a comment"})

(def valid-ssh-priv-key-config
  "private key")

(def ssh-key-pair-config
  {:public-key valid-ssh-pub-key-config
   :private-key valid-ssh-priv-key-config})

(deftest test-configs
  (is (s/validate sut/ssh-public-key-config valid-ssh-pub-key-config))
  (is (s/validate sut/ssh-private-key-config valid-ssh-priv-key-config))
  (is (s/validate sut/ssh-key-pair-config ssh-key-pair-config)))
