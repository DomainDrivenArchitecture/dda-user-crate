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

(ns org.domaindrivenarchitecture.pallet.crate.user.ssh-key-test
  (:require
    [clojure.test :refer :all]
    [org.domaindrivenarchitecture.pallet.crate.user.ssh-key :as sut]
    ))

(def config
  {:k1 {:type "ssh-rsa"
        :public-key "pub1"
        :comment "c1"}
   :k2 {:type "ssh-rsa"
        :public-key "pub2"
        :comment "c2"
        :private-key "priv2"}})

(deftest key-to-string
  (testing 
    "format public key string for ssh authorized keys"
    (is (= "ssh-rsa public-key comment"
           (sut/public-key-formated
             (sut/new-ssh-key
                  "ssh-rsa"
                  "public-key"
                  "comment"))
           ))))
  
(deftest key-from-config
  (testing 
    "create key from config"
    (is (= (sut/new-ssh-key "ssh-rsa" "pub1" "c1" nil)
           (sut/create-key-from-config
             :k1 config)
           )))
  (testing 
    "create seq of keys from config"
    (is (= (list (sut/new-ssh-key "ssh-rsa" "pub2" "c2" "priv2")
             (sut/new-ssh-key "ssh-rsa" "pub1" "c1" nil))
           (sut/create-keys-from-config
             (list :k2 :k1) config)
           ))))
  
