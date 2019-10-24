Travis 
============
## Travis publish
[Travis publish](https://github.com/scalacenter/sbt-release-early/wiki/How-to-release-in-Travis-(CI)

## Command line Validation
To install the command line client, which requires Ruby 1.9.3 and RubyGems:

gem install travis --no-rdoc --no-ri
Bash
To run the command line lint tool:

## from any directory
travis lint [path to your .travis.yml]


From [reference](https://support.travis-ci.com/hc/en-us/articles/115002904174-Validating-travis-yml-files)


## the travis encrypt for file not working on windows
https://docs.travis-ci.com/user/encrypting-files/
Caveat #
There is a report of this function not working on a local Windows machine. Please use the WSL (Windows Subsystem for Linux) or a Linux or macOS machine.


## command lines
```
gem install travis --no-rdoc --no-ri
travis login
tar cv -C travis -f travis/local.secrets.tar local.pubring.asc local.secring.asc
travis encrypt-file travis/local.secrets.tar -o travis/secrets.tar.enc -p --add
==== the key and iv will added to .travis.yml and encrypted to environment.
encrypting travis/local.secrets.tar for wherby/Hydra
storing result as travis/secrets.tar.enc
storing secure env variables for decryption

key: 0dee7b60bdcdf
iv:  9a6

====
travis encrypt 'PGP_PASS=(***)' --add
travis encrypt 'SONATYPE_USER=(***)' --add
travis encrypt 'SONATYPE_PASSWORD=(***)' --add
travis encrypt 'encrypted_979423a8fb_key=(***)' --add  [Optional]
travis encrypt 'encrypted_979423a8fb_iv=(***)' --add   [Optional]

```
