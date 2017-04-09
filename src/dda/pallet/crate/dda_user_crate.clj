(ns dda.pallet.crate.dda-user-crate
  (:require
   [schema.core :as s]
   [org.domaindrivenarchitecture.pallet.core.dda-crate :as dda-crate]
   [dda.pallet.crate.dda-user-crate.user :as user]
   [dda.pallet.crate.dda-user-crate.user.os-user :as os-user]
   [dda.pallet.crate.dda-user-crate.user.ssh-key :as ssh-key]))

(def facility :dda-user)
(def version [0 3 7])

(def UserCrateConfig
  {:os-user-config os-user/os-user-config})

(defn install-user [config]
  (user/create-sudo-user config))

(defn configure-user [config]
  (user/configure-authorized-keys (:os-user-config config))
  (user/configure-ssh-key (:os-user-config config))
  (user/configure-sudo  (:os-user-config config)))

(s/defmethod dda-crate/dda-install facility
  [dda-crate config]
  "user-crate: install routine"
  (install-user config))

(s/defmethod dda-crate/dda-configure facility
  [dda-crate config]
  "user-crate: configure routine"
  (configure-user config))

(def user-crate
  (dda-crate/make-dda-crate
   :facility facility
   :version version))

(def with-user
  (dda-crate/create-server-spec user-crate))
