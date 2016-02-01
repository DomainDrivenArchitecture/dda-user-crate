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

(ns org.domaindrivenarchitecture.pallet.crate.user-test
  (:require
    [clojure.test :refer :all]
    [pallet.build-actions :as build-actions]
    [org.domaindrivenarchitecture.pallet.test-utils :as test-utils]
    [org.domaindrivenarchitecture.pallet.crate.config.node :as node-record]
    [org.domaindrivenarchitecture.pallet.crate.user.ssh-key :as ssh-key-record]
    [org.domaindrivenarchitecture.pallet.crate.user.os-user :as os-user-record]
    [org.domaindrivenarchitecture.pallet.crate.user :as sut]
    ))

(deftest public-key
  (testing 
    "format public key string for ssh authorized keys"
    (is (=  nil
           (sut/ssh-key-config {})
           ))
    ))

(defn ssh-keys []
  {:key1 
   (ssh-key-record/new-ssh-key
     "ssh-rsa" "public-key" "comment" "private-key")})

(deftest test-spec []
  (testing 
    "default-config"
    (is 
      (some?
         (test-utils/find-expected
           "private-key"
           (test-utils/extract-node-values
            (build-actions/build-actions
              build-actions/ubuntu-session         
              (sut/configure-ssh-credentials-to-user 
                :user-name "usr" 
                :key-ids [:key1] 
                :key-config (ssh-keys))))
         ))))
  )
