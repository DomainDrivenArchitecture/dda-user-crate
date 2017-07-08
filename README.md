# dda-user-crate

This crate is part of [dda-pallet](https://domaindrivenarchitecture.org/pages/dda-pallet/).

## compatability
dda-pallet is compatible to the following versions
 * pallet 0.8.x
 * clojure 1.7
 * (x)ubunutu 16.04

## Features
 * creating os-users on target nodes
 * transferring ssh-keys to target nodes
 * adding authorized-keys to target nodes

## Usage
In order to use this crate you need to define your configuration inside the domain-namespace.

For example:

(def ssh-pub-key
 (os-user/read-ssh-pub-key-to-config))

(def ssh-priv-key (os-user/read-ssh-pub-key-to-config))

(def ssh-key-pair
  {:public-key ssh-pub-key
   :private-key ssh-priv-key})

```
(def domain-config
  :[user-name] { :encrypted-password "$ENCRYPTED_PASSWORD_TO_BE_USED"
                 :authorized-keys [ssh-pub-key]
                 :personal-key ssh-key-pair})
```

You can add more public-keys to the authorized-keys. Please note that adding your
private-key is optional. After defining your configuration you can start the execution
on the node by calling:

dda.pallet.crate.dda-user-crate.instantiate-existing> (apply-install)

Encrypted passwords can be generated e.g. by `mkpasswd test1234`.

## See also
* Howto getting started: https://domaindrivenarchitecture.org/posts/2017-07-07-first-steps-with-dda-pallet/
