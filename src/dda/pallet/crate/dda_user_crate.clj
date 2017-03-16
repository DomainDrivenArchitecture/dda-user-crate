(ns dda.pallet.crate.dda-user-crate
  (:require
    [schema.core :as s]
    [org.domaindrivenarchitecture.pallet.core.dda-crate :as dda-crate]
    [dda.pallet.crate.dda-user-crate.user :as user]))

(def facility :dda-user)
(def version [0 3 7])

(def OsUserConfig
  {:user-name s/Str
  :encrypted-password s/Str
  :authorized-keys [s/Str]
  :personal-key s/Str})

(def SshKeyConfig
  {:type s/Str
   :public-ley s/Str
   :comment s/Str
   (s/optional-key :private-key) s/Str})

(def UserCrateConfig
  {:os-user-config OsUserConfig
  :ssh-key-config SshKeyConfig}
  )

(defn install-user [config]
  (user/create-sudo-user config))

;maybe replace juxt with something that doesnt return a vector?
(defn configure-user [config]
  ((juxt 
    user/configure-authorized-keys user/configure-ssh-key user/configure-sudo)
    (:os-user-config config))
  nil) 

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