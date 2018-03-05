# dda-user-crate

[![Clojars Project](https://img.shields.io/clojars/v/dda/dda-user-crate.svg)](https://clojars.org/dda/dda-user-crate)
[![Build Status](https://travis-ci.org/DomainDrivenArchitecture/dda-user-crate.svg?branch=master)](https://travis-ci.org/DomainDrivenArchitecture/dda-user-crate)

[![Slack](https://img.shields.io/badge/chat-clojurians-green.svg?style=flat)](https://clojurians.slack.com/messages/#dda-pallet/) | [<img src="https://domaindrivenarchitecture.org/img/meetup.svg" width=50 alt="DevOps Hacking with Clojure Meetup"> DevOps Hacking with Clojure](https://www.meetup.com/de-DE/preview/dda-pallet-DevOps-Hacking-with-Clojure) | [Website & Blog](https://domaindrivenarchitecture.org)

This crate is part of [dda-pallet](https://domaindrivenarchitecture.org/pages/dda-pallet/).

## compatability
dda-pallet is compatible to the following versions
* pallet 0.8.x
* clojure 1.7
* (x)ubunutu 16.04

## Features
This crate manages user & user credential setup.

* create users on target nodes
* set passwords for users (either clear or hashed).
* all credentials / keys can be load from encrypted password-store.
* add users to sudoers
* add a ~/bashrc.d for bash initialization
* adding authorized-keys ssh to target nodes
* transferring ssh-keys to target nodes
* transfering trusted gpg-key to target nodes

## Usage documentation
This crate installs and configures software on your virtual machine. You can provision pre-created virtual machines (see paragraph "Prepare vm" below) or cloud instances.

### Prepare vm
If you want to use this crate, please ensure you meet the preconditions for the remote machine, i.e. xubuntu and openssh-server installed. If not yet installed, you may use the steps below:
1. Install xubuntu16.04.02
2. Login with your initial user and use:
```
sudo apt-get update
sudo apt-get upgrade
sudo apt-get install openssh-server
```
In case you want to install the software on the local machine rather than remote, you wouldn't need openssh-server but only a Java runtime environment. If not yet available, you can install Java by:
```
sudo apt-get install openjdk-7-jre-headless
```
### Usage Summary
1. Download the jar-file from the releases page of this repository (e.g. dda-manage-ide-x.x.x-standalone.jar).
2. Deploy the jar-file on the source machine
3. Create the files `ide.edn` (Domain-Schema for your desktop) and `target.edn` (Schema for Targets to be provisioned) according to the reference and our example configurations. Please create them in the same folder where you've saved the jar-file. For more information about these files refer to the corresponding information below.
4. Start the installation:
```bash
java -jar dda-managed-ide-standalone.jar --targets targets.edn ide.edn
```
If you want to install the ide on your localhost you don't need a target config.
```bash
java -jar dda-managed-ide-0.2.0-standalone.jar ide.edn
```

### Configuration
The configuration consists of two files defining both WHERE to install the software and WHAT to install.
* `targets.edn`: describes on which target system(s) the software will be installed
* `user.edn`: describes which software/packages will be installed

You can download examples of these configuration files from
[https://github.com/DomainDrivenArchitecture/dda-user-crate/blob/master/targets.edn](https://github.com/DomainDrivenArchitecture/dda-user-crate/blob/master/targets.edn) and
[https://github.com/DomainDrivenArchitecture/dda-user-crate/blob/master/user.edn](https://github.com/DomainDrivenArchitecture/dda-user-crate/blob/master/user.edn) respectively.

#### Targets config example
Example content of the file, `targets.edn`:
```clojure
{:existing [{:node-name "target1"                      ; semantic name (keep the default or use a name that suits you)
             :node-ip "192.168.56.104"}]               ; the ip4 address of the machine to be provisioned
             {:node-name "target2"                     ; semantic name (keep the default or use a name that suits you)
                          :node-ip "192.168.56.105"}]  ; the ip4 address of the machine to be provisioned
 :provisioning-user {:login "initial"                  ; user on the target machine, must have sudo rights
                     :password {:plain "secure1234"}}} ; password can be ommited, if a ssh key is authorized
```

#### User config example
Example content of the file, `user.edn`:
```clojure
{:test-user1                                                      ; the user-name, root also works.
    {:clear-password {:plain "xxx"}                               ; the users password
     :ssh-authorized-keys [{:plain "ssh-rsa AAAA...LL comment"}]  ; the authorized ssh keys containig "ssh-rsa" "the key" "a comment"
     :ssh-key {:public-key {:plain "ssh-rsa AAAA...LL comment"}   ; ssh public KEY
               :private-key {:plain "SOME_PRIVATE_SSH_KEY"}}}}    ; ssh privarte key
```

The user config defines the software/packages and user credentials of the newly created user to be installed.

### Watch log for debug reasons
In case of problems you may want to have a look at the log-file:
`less logs/pallet.log`

## Reference
Some details about the architecture: We provide two levels of API. **Domain** is a high-level API with many build in conventions. If this conventions don't fit your needs, you can use our low-level **infra** API and realize your own conventions.

### Domain API

#### Targets
The schema for the targets config is:
```clojure
(def ExistingNode
  "Represents a target node with ip and its name."
  {:node-name s/Str   ; semantic name (keep the default or use a name that suits you)
   :node-ip s/Str})   ; the ip4 address of the machine to be provisioned

(def ExistingNodes
  "A sequence of ExistingNodes."
  {s/Keyword [ExistingNode]})

(def ProvisioningUser
  "User used for provisioning."
  {:login s/Str                                ; user on the target machine, must have sudo rights
   (s/optional-key :password) secret/Secret})  ; password can be ommited, if a ssh key is authorized

(def Targets
  "Targets to be used during provisioning."
  {:existing [ExistingNode]                                ; one ore more target nodes.
   (s/optional-key :provisioning-user) ProvisioningUser})  ; user can be ommited to execute on localhost with current user.
```

The "targets.edn" uses this schema.

#### User config
The schema for the ide configuration is:
```clojure
(def Secret
  (either
    {:plain Str}                    ;   as plain text
    {:password-store-single Str}    ;   as password store key wo linebreaks & whitespaces
    {:password-store-record         ;   as password store entry containing login (record :login)
      {:path Str,                   ;      and password (no field or :password)
       :element (enum :password :login)}}
    {:password-store-multi Str}     ;   as password store key with linebreaks
    {:pallet-secret {:key-id Str,
                    :service-path [Keyword],
                    :record-element (enum :secret :account)}})

(def GpgKey {:public-key secret/Secret                          ; the gpg public key - has to be a valid gpg key.
             (s/optional-key :passphrase) secret/Secret         ; gpg keys passphrase
             (s/optional-key :private-key) secret/Secret})      ; the gpg private key - has to be a valid gpg key.
(def Gpg
  {(s/optional-key :gpg) {:trusted-key GpgKey}})                ; at the moment only a ultimately trusted key can be configured.

(def SshSimpleKeyPair
 {:public-key secret/Secret                                     ; public key in format "ssh-rsa the-key the-comment"
  :private-key secret/Secret})                                  ; ssh private key
(def SshAuthorizedKeys [secret/Secret])                         ; a sequence of authorized keys.
(def Ssh
 {(s/optional-key :ssh-authorized-keys) [secret/Secret]
  (s/optional-key :ssh-key) SshSimpleKeyPair})

(def Settings {(s/optional-key :settings)
               (hash-set (s/enum :sudo :bashrc-d))})            ; sudo means user is part of sudoers group
                                                                ; bashrc-d will create a basrc.d wich contents are executed on bash start.

(def User
  (s/either
    (merge {:hashed-password secret/Secret}                     ; hashed passwords are written directly to /etc/shadow
           Gpg
           Ssh
           Settings)
    (merge {:clear-password secret/Secret}                      ; clear passwords are hashed before writing them to /etc/shadow
           Gpg
           Ssh
           Settings)))
```

### Infra API
The Infra configuration is a configuration on the infrastructure level of a crate. It contains the complete configuration options that are possible with the crate functions.

For installation & configuration with the dda-user-crate the schema is:
```clojure
(def GpgKey {:public-key s/Str
             (s/optional-key :passphrase) s/Str
             (s/optional-key :private-key) s/Str})

(def Gpg
  {(s/optional-key :gpg) {:trusted-key GpgKey}})

(def PublicSshKey
  {:type s/Str
   :public-key s/Str
   :comment s/Str})
(def PrivateSshKey s/Str)
(def SshKeyPair
  {:public-key PublicSshKey
   :private-key PrivateSshKey})
(def Ssh
 {(s/optional-key :ssh-authorized-keys) [ssh-key/PublicSshKey]
  (s/optional-key :ssh-key) ssh-key/SshKeyPair})

(def Settings {(s/optional-key :settings)
               (hash-set (s/enum :sudo :bashrc-d))})

(def User
  (s/either
    (merge {:hashed-password s/Str}
           Gpg Ssh Settings)
    (merge {:clear-password s/Str}
           Gpg Ssh Settings)))

(def UserCrateConfig
  {s/Keyword User})

```

## License
Published under [apache2.0 license](LICENSE.md)
