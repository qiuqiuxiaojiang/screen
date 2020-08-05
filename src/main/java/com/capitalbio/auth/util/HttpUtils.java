package com.capitalbio.auth.util;

import java.security.KeyManagementException;
import java.util.Arrays;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;

public class HttpUtils {
	public static CloseableHttpClient getClient() {
		try {
			SSLContext sslcontext = SSLContexts.createDefault();
			sslcontext.init(null, new TrustManager[] {new TrustAnyTrustManager()}, null);
			SSLConnectionSocketFactory ssf = new SSLConnectionSocketFactory(sslcontext, NoopHostnameVerifier.INSTANCE);
			RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(300000).
					setConnectTimeout(3500000).setConnectionRequestTimeout(320000)
					.setCookieSpec(CookieSpecs.STANDARD_STRICT).setExpectContinueEnabled(true)
					.setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).build();
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
					.register("http", PlainConnectionSocketFactory.INSTANCE)
					.register("https", ssf).build();
			PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager)
					.setDefaultRequestConfig(defaultRequestConfig).setMaxConnTotal(1000)
					.setMaxConnPerRoute(200).build();
			return httpClient;
		} catch (KeyManagementException e) {
			throw new IllegalArgumentException("can't create trustManager", e);
		}
	}

}
