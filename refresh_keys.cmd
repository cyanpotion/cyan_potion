gpg --list-keys
gpg --list-keys --keyid-format 0xSHORT
gpg --keyserver hkp://pool.sks-keyservers.net --send-keys 4E8A82BDED6064C5
gpg --keyserver hkp://pool.sks-keyservers.net --recv-keys 4E8A82BDED6064C5
gpg --export-secret-keys >C:/Users/xenoa/.gnupg/secring.gpg
pause