(ns dda.pallet.domain.dda_user_crate
  (:require 
    [schema.core :as s]
    [pallet.api :as api]
    [org.domaindrivenarchitecture.pallet.crate.config :as config-crate]
    [dda.pallet.crate.dda-user-crate :as user-crate]))

(def dda-user-domain-config 
  {})

(def dda-user-crate-stack-config
  {:ssh-keys s/Any
   :os-user s/Any
   :group-specific-config 
   {:dda-user-group 
    {:additional-config {:dda-user user-crate/UserCrateConfig}}}
   }
  )


(s/defn ^:always-validate dda-user-crate-stack-configuration :- dda-user-crate-stack-config
  [convention-config :- dda-user-domain-config]
  {:ssh-keys nil
   :os-user nil
   :group-specific-config 
   {:dda-user-group 
    {:host-name nil
     :domain-name nil
     :additional-config {:dda-user user-crate/UserCrateConfig}}}
   }
  )

(s/defn ^:always-validate dda-user-group
  [domain-config :- dda-user-domain-config]
  (let [target (get-in domain-config [:target])
        config (dda-user-crate-stack-configuration domain-config)]
    (api/group-spec
      "dda-user-group"
      :extends [(config-crate/with-config config)
                user-crate/with-user]
      :node-spec (get-in target [:aws :aws-node-spec])
      :count (get-in target [:aws :count])))
  )