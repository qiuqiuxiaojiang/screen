package com.capiltalbio.health.utils;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
public class HttpsUtil {

	/**
     * post方式访问
     * @param url 路径
     * @param map 参数
     * @return
     */
    public static HttpResponse httpsPost(HttpPost httpPost) {
        HttpClient httpClient = null;
        try {
            httpClient = new SSLClient();
            //设置参数
            HttpResponse response = httpClient.execute(httpPost);
            return response;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * get方式访问（如果有参数直接 ?xx&yy&zz 的方式即可）
     * @param url
     * @return
     */
    public static HttpResponse httpsGet(HttpGet httpGet) {
        HttpClient httpClient = null;
        try {
            httpClient = new SSLClient();
            HttpResponse response = httpClient.execute(httpGet);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
}

class SSLClient extends DefaultHttpClient {
    //用于进行Https请求的HttpClient
    public SSLClient() throws Exception {
        super();
        SSLContext ctx = SSLContext.getInstance("TLS");
        X509TrustManager tm = new X509TrustManager() {
            
            public void checkClientTrusted(X509Certificate[] chain,String authType) throws CertificateException { }
            
            public void checkServerTrusted(X509Certificate[] chain,String authType) throws CertificateException { }
            
            public X509Certificate[] getAcceptedIssuers() {return null; }
        };
        ctx.init(null, new TrustManager[]{tm}, null);
        SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        ClientConnectionManager ccm = this.getConnectionManager();
        SchemeRegistry sr = ccm.getSchemeRegistry();
        sr.register(new Scheme("https", 443, ssf));
    }
}
