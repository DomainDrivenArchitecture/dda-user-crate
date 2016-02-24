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

(ns org.domaindrivenarchitecture.pallet.crate.user.os-user-test
  (:require
    [clojure.test :refer :all]
    [org.domaindrivenarchitecture.pallet.crate.user.ssh-key :as ssh-key]
    [org.domaindrivenarchitecture.pallet.crate.user.os-user :as sut]))

(def config
  {:ssh-keys {:k1 {:type "ssh-rsa"
                   :public-key "pub1"
                   :comment "c1"}
              :k2 {:type "ssh-rsa"
                   :public-key "pub2"
                   :comment "c2"
                   :private-key "priv2"}}
   :os-user {:usr1 {:encrypted-password "enc1" 
                    :authorized-keys [:k1 :k2]
                    :personal-key :k2}}
   })
(def config2
  {:ssh-keys {:k1 {:type "ssh-rsa"
                   :public-key "pub1"
                   :comment "c1"}
              :k2 {:type "ssh-rsa"
                   :public-key "pub2"
                   :comment "c2"
                   :private-key "priv2"}}
   :os-user {:usr2 {:encrypted-password "enc1" 
                    :authorized-keys [:k1 :k2]}}
   })
  

(deftest user-from-config
  (testing 
    "create a full blown os-user from config"
    (is (= (sut/new-os-user 
             "usr1" 
             "enc1"
              [(ssh-key/new-ssh-key "ssh-rsa" "pub1" "c1" nil)
               (ssh-key/new-ssh-key "ssh-rsa" "pub2" "c2" "priv2")]
             (ssh-key/new-ssh-key "ssh-rsa" "pub2" "c2" "priv2"))
           (sut/new-os-user-from-config :usr1 config)
           )))
  (testing 
    "create a os-user without private key from config"
    (is (= (sut/new-os-user 
             "usr2" 
             "enc1"
              [(ssh-key/new-ssh-key "ssh-rsa" "pub1" "c1" nil)
               (ssh-key/new-ssh-key "ssh-rsa" "pub2" "c2" "priv2")]
             nil)
           (sut/new-os-user-from-config :usr2 config2)
           ))))


(deftest authorized-keys-for-user
  (testing 
    "generation of authorized keys"
    (is (= ["ssh-rsa pub1 c1" "ssh-rsa pub2 c2"]
           (map ssh-key/public-key-formated 
                (:authorized-keys 
                  (sut/new-os-user-from-config :usr1 config))))))
  )

(deftest user-home
  (testing 
    "user-home path"
    (is (= "/root" 
          (sut/user-home-dir 
            (sut/new-os-user "root" "enc1"))))
    (is (= "/home/other" 
          (sut/user-home-dir 
            (sut/new-os-user "other" "enc1"))))
    )
  (testing 
    "user .ssh path"
    (is (= "/root/.ssh/" 
          (sut/user-ssh-dir 
            (sut/new-os-user "root" "enc1"))))
    ))