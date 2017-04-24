; Copyright (c) meissa GmbH. All rights reserved.
; You must not remove this notice, or any other, from this software.

(ns dda.pallet.crate.dda-user-crate.instantiate-existing
  (:require [dda.pallet.crate.dda-user-crate.existing :as exisiting]
            [dda.pallet.domain.dda_user_crate :as user]
            [org.domaindrivenarchitecture.cm.operation :as operation]
            [schema.core :as s]))

(def ssh-pub-key
  {:type "type"
  :public-key "pub-key"
  :comment "this is a comment"})

(def ssh-priv-key "priv-key")

(def ssh-key-pair
  {:public-key ssh-pub-key
   :private-key ssh-priv-key})

(def domain-config
  {:user-name "krj"
   :encrypted-password "secret-pw"
   :authorized-keys [ssh-pub-key]
   :personal-key ssh-key-pair})

(def provisioning-ip
    "10.0.2.7")

(def provisioning-user
  {:login "jan"
   :password "test1234"})

(def provider
  (exisiting/remote-node provisioning-ip))

(def integrated-group-spec
  (merge
   (user/dda-user-group domain-config)
   (exisiting/node-spec provisioning-user)))

(def PalletGroupSpec
  {:phases s/Any
   :default-phases s/Any
   :group-name s/Keyword
   :image {:login-user {:login s/Str
                        :password s/Str}}})

(defn apply-install []
  (operation/do-apply-install provider integrated-group-spec))

(defn apply-config []
  (operation/do-apply-configure provider integrated-group-spec))

(defn server-test []
  (operation/do-server-test provider integrated-group-spec))
