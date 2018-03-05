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
(ns dda.pallet.dda-user-crate.domain-test
  (:require
   [clojure.test :refer :all]
   [schema.core :as s]
   [dda.pallet.dda-user-crate.domain :as sut]))

(deftest valid-configurations
  (is (= {:dda-user {:user1 {:hashed-password "hashed"}}}
         (sut/infra-configuration {:user1 {:hashed-password "hashed"}})))
  (is (= {:dda-user {:user1 {:hashed-password "hashed"}
                     :user2 {:clear-password "clear"}}}
         (sut/infra-configuration {:user1 {:hashed-password "hashed"}
                                   :user2 {:clear-password "clear"}})))
  (is (= {:dda-user {:user1 {:hashed-password "hashed"
                             :gpg {:trusted-key {:public-key "public gpg"}}}}}
         (sut/infra-configuration {:user1 {:hashed-password "hashed"
                                           :gpg {:trusted-key {:public-key "public gpg"}}}})))
  (is (= {:dda-user {:user1 {:hashed-password "hashed"
                             :ssh-authorized-keys [{:type "a", :public-key "b", :comment "c"}
                                                   {:type "a", :public-key "b", :comment "c"}]}}}
         (sut/infra-configuration {:user1 {:hashed-password "hashed"
                                           :ssh-authorized-keys ["a b c" "a b c"]}}))))
