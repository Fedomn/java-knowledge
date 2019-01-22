package com.fedomn.httpclient.ssl;

import static java.lang.String.format;

import java.io.FileInputStream;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import org.apache.http.HttpStatus;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.ssl.SSLContextBuilder;

public class SSLUtil {

  public static final int PORT = 12345;
  public static final String ADDRESS = "localhost";
  public static final String SERVER_KEYSTORE = "./ssl/server.p12";
  public static final String SERVER_TRUSTSTORE = "./ssl/server-trust.p12";
  public static final String CLIENT_KEYSTORE = "./ssl/client.p12";
  public static final String CLIENT_TRUSTSTORE = "./ssl/client-trust.p12";
  public static final char[] PASSWORD = "password".toCharArray();

  public static HttpServer createLocalTestServer(SSLContext sslContext, boolean forceSSLAuth)
      throws UnknownHostException {
    return ServerBootstrap.bootstrap()
        .setListenerPort(PORT)
        .setLocalAddress(Inet4Address.getByName(ADDRESS))
        .setSslContext(sslContext)
        .setSslSetupHandler(socket -> socket.setNeedClientAuth(forceSSLAuth))
        .registerHandler(
            "*", (request, response, context) -> response.setStatusCode(HttpStatus.SC_OK))
        .create();
  }

  public static String getHttpUrl() {
    return format("http://%s:%d", ADDRESS, PORT);
  }

  public static String getHttpsUrl() {
    return format("https://%s:%d", ADDRESS, PORT);
  }

  public static SSLContext createServerSSLContext(KeyStore keyStore, KeyStore trustStore)
      throws KeyManagementException, NoSuchAlgorithmException, UnrecoverableKeyException,
          KeyStoreException {
    KeyManager[] keyManagers = getKeyManagers(keyStore, PASSWORD);
    TrustManager[] trustManagers = getTrustManagers(trustStore);

    SSLContext sslContext = SSLContextBuilder.create().setProtocol("TLS").build();
    sslContext.init(keyManagers, trustManagers, new SecureRandom());
    return sslContext;
  }

  public static KeyStore getServerKeyStore() {
    return getStore(
        SSLUtil.class.getClassLoader().getResource(SERVER_KEYSTORE).getFile(), PASSWORD);
  }

  public static KeyStore getServerTrustStore() {
    return getStore(
        SSLUtil.class.getClassLoader().getResource(SERVER_TRUSTSTORE).getFile(), PASSWORD);
  }

  public static KeyStore getClientKeyStore() {
    return getStore(
        SSLUtil.class.getClassLoader().getResource(CLIENT_KEYSTORE).getFile(), PASSWORD);
  }

  public static KeyStore getClientTrustStore() {
    return getStore(
        SSLUtil.class.getClassLoader().getResource(CLIENT_TRUSTSTORE).getFile(), PASSWORD);
  }

  private static KeyStore getStore(final String storeFilePath, final char[] password) {
    final KeyStore store;
    try {
      store = KeyStore.getInstance("PKCS12");
      try (FileInputStream stream = new FileInputStream(storeFilePath)) {
        store.load(stream, password);
      }
      return store;
    } catch (Exception e) {
      System.err.println("get store err");
      return null;
    }
  }

  private static KeyManager[] getKeyManagers(KeyStore store, final char[] password)
      throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
    KeyManagerFactory keyManagerFactory =
        KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    keyManagerFactory.init(store, password);
    return keyManagerFactory.getKeyManagers();
  }

  private static TrustManager[] getTrustManagers(KeyStore store)
      throws NoSuchAlgorithmException, KeyStoreException {
    TrustManagerFactory trustManagerFactory =
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    trustManagerFactory.init(store);
    return trustManagerFactory.getTrustManagers();
  }
}
