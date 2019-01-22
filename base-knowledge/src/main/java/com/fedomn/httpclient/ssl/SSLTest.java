package com.fedomn.httpclient.ssl;

import static com.fedomn.httpclient.ssl.SSLUtil.PASSWORD;
import static com.fedomn.httpclient.ssl.SSLUtil.createLocalTestServer;
import static com.fedomn.httpclient.ssl.SSLUtil.createServerSSLContext;
import static com.fedomn.httpclient.ssl.SSLUtil.getClientKeyStore;
import static com.fedomn.httpclient.ssl.SSLUtil.getClientTrustStore;
import static com.fedomn.httpclient.ssl.SSLUtil.getHttpUrl;
import static com.fedomn.httpclient.ssl.SSLUtil.getHttpsUrl;
import static com.fedomn.httpclient.ssl.SSLUtil.getServerKeyStore;
import static com.fedomn.httpclient.ssl.SSLUtil.getServerTrustStore;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.IOException;
import java.security.KeyStore;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.junit.Before;
import org.junit.Test;

public class SSLTest {

  private static final SSLContext NO_SSL_CONTEXT = null;
  private static final KeyStore NO_TRUST_STORE = null;
  private static final TrustStrategy NO_TRUST_STRATEGY = null;

  private static final boolean ONE_WAY_SSL = false; // no client certificates
  private static final boolean TWO_WAY_SSL = true; // client certificates mandatory

  private CloseableHttpClient httpClient;

  @Before
  public void setUp() {
    httpClient = HttpClients.createDefault();
  }

  private HttpResponse helper(HttpServer server, HttpClient client, HttpRequestBase req)
      throws IOException {
    server.start();
    HttpResponse rsp;
    try {
      rsp = client.execute(req);
    } finally {
      server.stop();
    }
    return rsp;
  }

  // one-way-ssl
  @Test
  public void httpReq_to_noSSLCtxServer_Rsp200() throws IOException {
    HttpServer server = createLocalTestServer(NO_SSL_CONTEXT, ONE_WAY_SSL);
    HttpResponse rsp = helper(server, httpClient, new HttpGet(getHttpUrl()));
    assertThat(rsp.getStatusLine().getStatusCode()).isEqualTo(SC_OK);
  }

  @Test
  public void httpsNoSSLCtxReq_to_noSSLCtxServer_ThrowSSLException() throws IOException {
    HttpServer server = createLocalTestServer(NO_SSL_CONTEXT, ONE_WAY_SSL);
    assertThatExceptionOfType(SSLException.class)
        .isThrownBy(() -> helper(server, httpClient, new HttpGet(getHttpsUrl())))
        .withMessageContaining("Unrecognized SSL message, plaintext connection");
  }

  @Test
  public void httpsNoSSLCtxReq_to_SSLCtxServer_ThrowSSLException() throws Exception {
    SSLContext sslContext = createServerSSLContext(getServerKeyStore(), NO_TRUST_STORE);
    HttpServer server = createLocalTestServer(sslContext, ONE_WAY_SSL);

    /*
      The server's cert does not exist in the default trust store (jre/lib/security/cacerts).
      When httpClient connecting to the server that presents a certificate for validation during the SSL handshake,
      our client cannot validate it and throws an SSLHandshakeException.
    */
    assertThatExceptionOfType(SSLHandshakeException.class)
        .isThrownBy(() -> helper(server, httpClient, new HttpGet(getHttpsUrl())))
        .withMessageContaining("unable to find valid certification path to requested target");
  }

  @Test
  public void httpsSSLCtxTrustAllCertReq_to_SSLCtxServer_Rsp200() throws Exception {
    SSLContext sslContext = createServerSSLContext(getServerKeyStore(), NO_TRUST_STORE);
    HttpServer server = createLocalTestServer(sslContext, ONE_WAY_SSL);

    /*
      HttpClient trust all certificates that presented to it. so certificate validation is bypassed.
      Because of server certificate is self signed cert, so TrustSelfSignedStrategy will also work.
    */
    SSLContext clientTrustSSLCtx =
        new SSLContextBuilder().loadTrustMaterial(NO_TRUST_STORE, new TrustAllStrategy()).build();

    httpClient = HttpClients.custom().setSSLContext(clientTrustSSLCtx).build();
    helper(server, httpClient, new HttpGet(getHttpsUrl()));
  }

  @Test
  public void httpsSSLCtxTrustServerCertReq_to_SSLCtxServer_Rsp200() throws Exception {
    SSLContext sslContext = createServerSSLContext(getServerKeyStore(), NO_TRUST_STORE);
    HttpServer server = createLocalTestServer(sslContext, ONE_WAY_SSL);

    /*
      Server certificate was imported into the client's trustStore.
      Attention: loadTrustMaterial can receive trustStore and trustStrategy two parameter,
      the two parameter will be combined into one configuration, so TrustAllStrategy and TrustSelfSignedStrategy
      will override trustStore configuration. if you don't want this behavior, set strategy null.
    */
    SSLContext clientTrustSSLCtx =
        new SSLContextBuilder().loadTrustMaterial(getClientTrustStore(), NO_TRUST_STRATEGY).build();

    httpClient = HttpClients.custom().setSSLContext(clientTrustSSLCtx).build();
    helper(server, httpClient, new HttpGet(getHttpsUrl()));
  }

  // two-way-ssl
  @Test
  public void httpsSSLCtxReq_to_SSLCtxNoTrustStoreServer_ThrowSSLHandshakeException()
      throws Exception {
    /*
      Server's trustStore is null, so get server trustManagers is default in jre/lib/security/cacerts
      that dose not contains client certificate. The configuration meaning the server will not be able
      to validate the client's certificate. The SSL handshake will fail.
    */
    SSLContext serverSSLContext = createServerSSLContext(getServerKeyStore(), NO_TRUST_STORE);
    HttpServer server = createLocalTestServer(serverSSLContext, TWO_WAY_SSL);

    SSLContext clientSSLContext =
        new SSLContextBuilder()
            .loadKeyMaterial(getClientKeyStore(), PASSWORD)
            .loadTrustMaterial(getClientTrustStore(), NO_TRUST_STRATEGY)
            .build();
    httpClient = HttpClients.custom().setSSLContext(clientSSLContext).build();

    assertThatExceptionOfType(SSLHandshakeException.class)
        .isThrownBy(() -> helper(server, httpClient, new HttpGet(getHttpsUrl())))
        .withMessageContaining("Received fatal alert: bad_certificate");
  }

  @Test
  public void httpsSSLCtxNoKeyStoreReq_to_SSLCtxServer_ThrowSSLHandshakeException()
      throws Exception {
    SSLContext serverSSLContext =
        createServerSSLContext(getServerKeyStore(), getServerTrustStore());
    HttpServer server = createLocalTestServer(serverSSLContext, TWO_WAY_SSL);

    /*
      Client is not configured with a KeyStone, meaning it will not present certificate to the server.
      So the SSL handshake will fail.
    */
    SSLContext clientSSLContext =
        new SSLContextBuilder().loadTrustMaterial(getClientTrustStore(), NO_TRUST_STRATEGY).build();
    httpClient = HttpClients.custom().setSSLContext(clientSSLContext).build();

    assertThatExceptionOfType(SSLHandshakeException.class)
        .isThrownBy(() -> helper(server, httpClient, new HttpGet(getHttpsUrl())))
        .withMessageContaining("Received fatal alert: bad_certificate");
  }

  @Test
  public void httpsSSLCtxReq_to_SSLCtxServer_Rsp200() throws Exception {
    SSLContext serverSSLContext =
        createServerSSLContext(getServerKeyStore(), getServerTrustStore());
    HttpServer server = createLocalTestServer(serverSSLContext, TWO_WAY_SSL);

    SSLContext clientSSLContext =
        new SSLContextBuilder()
            .loadKeyMaterial(getClientKeyStore(), PASSWORD)
            .loadTrustMaterial(getClientTrustStore(), NO_TRUST_STRATEGY)
            .build();
    httpClient = HttpClients.custom().setSSLContext(clientSSLContext).build();

    HttpResponse rsp = helper(server, httpClient, new HttpGet(getHttpsUrl()));
    assertThat(rsp.getStatusLine().getStatusCode()).isEqualTo(SC_OK);
  }
}
