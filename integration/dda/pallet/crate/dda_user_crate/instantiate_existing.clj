; Copyright (c) meissa GmbH. All rights reserved.
; You must not remove this notice, or any other, from this software.

(ns dda.pallet.crate.dda-user-crate.instantiate-existing
  (:require [dda.pallet.crate.dda-user-crate.existing :as exisiting]
            [dda.pallet.crate.dda-user-crate.user.os-user :as os-user]
            [dda.pallet.domain.dda-user-crate :as user]
            [org.domaindrivenarchitecture.cm.operation :as operation]
            [schema.core :as s]))

(def ssh-pub-key
  (os-user/read-ssh-pub-key-to-config))

(def ssh-priv-key "$YOUR_PRIVATE_KEY")

(def ssh-key-pair
  {:public-key ssh-pub-key
   :private-key ssh-priv-key})

(def domain-config
  {:user-name "USERNAME_TO_BE_CREATED"
   :encrypted-password  "USER_PASSWORD"
   :authorized-keys [ssh-pub-key]
   :personal-key ssh-key-pair})

(def provisioning-ip
    "TARGET_IP")

(def provisioning-user
  {:login "EXISTING_USER_LOGINjan"
   :password "EXISTING_USER_PASSWORD"})

(def provider
  (exisiting/provider provisioning-ip))

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
