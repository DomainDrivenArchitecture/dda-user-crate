; Copyright (c) meissa GmbH. All rights reserved.
; You must not remove this notice, or any other, from this software.

(ns dda.pallet.crate.dda-user-crate.instantiate-existing
  (:require
   [schema.core :as s]
   [org.domaindrivenarchitecture.cm.operation :as operation]
   [dda.pallet.domain.dda_user_crate :as user]
   [dda.pallet.crate.dda-user-crate.user.os-user :as os-user]))

(def valid-ssh-pub-key-config
  {:type "type"
   :public-key "pub-key"
   :comment "a comment"})

(def valid-ssh-priv-key-config
  "private key")

(def ssh-key-pair-config
  {:public-key valid-ssh-pub-key-config
   :private-key valid-ssh-priv-key-config})

(def provisioning-ip
  "10.0.2.7")

(def provisioning-user
  {:login "jan"
   :password "test1234"})

(def domain-config
  {:user-name "krj"
   :encrypted-password "XfmJhkOyEUsx6"
   :authorized-keys ["auth_key_1" "auth_key_2"]
   :personal-key ssh-key-pair-config})

(defn provider []
  (exisiting/provider provisioning-ip))

(defn integrated-group-spec []
  (merge
   (user/dda-user-group user/UserDomainConfig)
   (exisiting/node-spec provisioning-user)))

(def PalletGroupSpec
  {:phases s/Any
   :default-phases s/Any
   :group-name s/Keyword
   :image {:login-user {:login s/Str
                        :password s/Str}}})

(defn apply-install []
  (operation/do-apply-install (provider) (integrated-group-spec)))

(defn apply-config []
  (operation/do-apply-configure (provider) (integrated-group-spec)))

(defn server-test []
  (operation/do-server-test (provider) (integrated-group-spec)))
