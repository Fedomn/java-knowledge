### Need to do before run test:

1. generate an RSA private key: `openssl genrsa -out client.pem 2048`
2. generate a self signed certificate: `openssl req -new -x509 -days 3650 -key client.pem -out client.crt -subj "/C=CN/CN=localhost"`
3. package PKCS12: `openssl pkcs12 -export -name client -in client.crt -inkey client.pem -out client.p12`
4. replace `client` in 1.2.3 step command to `server` and execute 1.2.3 step command
5. package client.crt into server-trust.p12: `keytool -import -alias server-trust -file client.crt -keystore server-trust.p12 -storetype PKCS12 -storepass password`
6. package server.crt into client-trust.p12: `keytool -import -alias client-trust -file server.crt -keystore client-trust.p12 -storetype PKCS12 -storepass password`
7. put above eight files into resources/ssl directory
