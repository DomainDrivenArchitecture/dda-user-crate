# Infra-Schema
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

# Domain-Schema
```clojure
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

# Domain-Example
```clojure
{:test-user1                                                      ; the user-name, root also works.
    {:clear-password {:plain "xxx"}                               ; the users password
     :ssh-authorized-keys [{:plain "ssh-rsa AAAA...LL comment"}]  ; the authorized ssh keys containig "ssh-rsa" "the key" "a comment"
     :ssh-key {:public-key {:plain "ssh-rsa AAAA...LL comment"}   ; ssh public KEY
               :private-key {:plain "SOME_PRIVATE_SSH_KEY"}}}}    ; ssh privarte key
```
