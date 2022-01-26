#!/bin/bash

# generating a UUID as password for the generated keystore
#
UUID=$(cat /proc/sys/kernel/random/uuid)

# Create private key
#
keytool -genkey -alias private -keystore /usr/local/tomcat/ssl/private.p12 -storetype PKCS12 -keyalg RSA -storepass ${UUID} -keypass ${UUID} -validity 90 -keysize 2048 -dname "CN=localhost" -ext san=dns:${HOSTNAME}

# set the private keys secret in server.xml
#
sed -i "s/@@sslpwd@@"/${UUID}/g /usr/local/tomcat/conf/server.xml

# overwrite the variables since they are not needed anywhere anymore
#
export UUID=

# run the original tomcat entry point as specified in tomcat's Dockerfile
#
sh /usr/local/tomcat/bin/catalina.sh run