(ns dda.pallet.domain.dda-user-crate
  (:require
   [schema.core :as s]
   [pallet.api :as api]
   [dda.pallet.crate.config :as config-crate]
   [dda.pallet.crate.dda-user-crate :as user-crate]))

(def UserDomainConfig
  user-crate/UserCrateConfig)

(def UserCrateStackConfig
  {:group-specific-config
   {:dda-user-group {:dda-user user-crate/UserCrateConfig}}})

(s/defn ^:always-validate dda-user-crate-stack-configuration :- UserCrateStackConfig
  [convention-config :- UserDomainConfig]
  {:group-specific-config
   {:dda-user-group {:dda-user convention-config}}})

(s/defn ^:always-validate dda-user-group
  [domain-config :- UserDomainConfig]
  (let [config (dda-user-crate-stack-configuration domain-config)]
    (api/group-spec
     "dda-user-group"
     :extends [(config-crate/with-config config)
               user-crate/with-user])))
