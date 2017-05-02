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
(ns dda.pallet.crate.dda-user-crate.user.ssh-key
  (:require
   [schema.core :as s]
   [clojure.string :as str]))

(def ssh-public-key-config
  {:type s/Str
   :public-key s/Str
   :comment s/Str})

(def ssh-private-key-config s/Str)

(def ssh-key-pair-config
  {:public-key ssh-public-key-config
   :private-key ssh-private-key-config})

(defn format-public-key
  "returns a formatted public-key from an ssh-config"
  [ssh-public-key-config]
  (str
   (:type ssh-public-key-config) " "
   (:public-key ssh-public-key-config) " "
   (:comment ssh-public-key-config)))

(s/defn string-to-pub-key-config [pub-key :- s/Str] :- ssh-public-key-config
  "function takes a public-key as a string and returns it as a ssh-public-key-config"
  (let [col (clojure.string/split pub-key #" ")]
    {:type (first col)
     :public-key (second col)
     :comment (nth col 2)}))

