; Copyright (c) meissa GmbH. All rights reserved.
; You must not remove this notice, or any other, from this software.

(ns dda.pallet.crate.dda-user-crate.instantiate-existing
  (:require
   [clojure.inspector :as inspector]
   [schema.core :as s]
   [pallet.api :as api]
   [pallet.compute :as compute]
   [pallet.compute.node-list :as node-list]
   [org.domaindrivenarchitecture.cm.operation :as operation]
   [dda.pallet.crate.dda-git-crate.existing :as exisiting]
   [dda.pallet.domain.dda_user_crate :as user]))

(def provisioning-ip
  "192.168.56.103")



(def provisioning-user
  {:login "jem"
   :password "test1234"})

(def domain-config
  {:user-name "krj"
   :encrypted-password ""
   :authorized-keys []
   :personal-key ""}
  )

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
