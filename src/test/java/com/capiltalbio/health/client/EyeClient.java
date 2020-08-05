package com.capiltalbio.health.client;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capitalbio.auth.util.Constant;
import com.capitalbio.auth.util.HttpUtils;
import com.capitalbio.common.util.EncryptUtil;
import com.capitalbio.common.util.JsonUtil;
import com.capitalbio.common.util.MD5Util;
import com.google.common.collect.Maps;

public class EyeClient {
	String host = "https://fxdm.bioeh.com";
//	String host = "http://114.116.132.137/screening";
//	String host = "http://localhost:8080/screen";
//	String host = "http://114.115.233.33:8080/screening";
	private Logger log = LoggerFactory.getLogger(getClass());
	
	public void testDownload() throws Exception {
		String url = host + "/eye/download.htm";
		HttpPost httpPost = new HttpPost(url);
		String str = "<query apikey=\"fcba709d-4ff8-46cf-9d7c-21f384b7228e\">"
				+"<userId>20180113.10.00.53.401</userId>"
//				+ "<startDate>2015-06-26</startDate><endDate>2016-06-28</endDate>"
				+"</query>";
		httpPost.setEntity(new StringEntity(str, "utf-8"));
		httpPost.setHeader(HTTP.CONTENT_TYPE, "application/xml");
		HttpResponse response = new DefaultHttpClient().execute(httpPost);
		int code = response.getStatusLine().getStatusCode();
		String path = "/Users/wdong/Downloads/temp.zip";
		if (code == 200) {
			byte[] result = EntityUtils.toByteArray(response.getEntity());  
            BufferedOutputStream bw = null;  
            try {  
                // 创建文件对象  
                File f = new File(path);  
                // 创建文件路径  
                if (!f.getParentFile().exists())  
                    f.getParentFile().mkdirs();  
                // 写入文件  
                bw = new BufferedOutputStream(new FileOutputStream(path));  
                bw.write(result);  
            } catch (Exception e) {  
                log.error("保存文件错误,path=" + path + ",url=" + url, e);  
            } finally {  
                try {  
                    if (bw != null)  
                        bw.close();  
                } catch (Exception e) {  
                    log.error(  
                            "finally BufferedOutputStream shutdown close",  
                            e);  
                }  
            }  
			
		} else {
			printMessage("HTTP error code:" + code);
		}

	}
	public void testUpload() throws Exception {
		String url = host+"/eye/upload.htm";
		HttpPost httpPost = new HttpPost(url);
		File file = new File("/Users/wdong/Downloads/20190123.17.39.37.704.1_.zip");
		FileBody bin = new FileBody(file);
        MultipartEntity reqEntity = new MultipartEntity();
        StringBody apikey = new StringBody("fcba709d-4ff8-46cf-9d7c-21f384b7228e");
        reqEntity.addPart("apikey", apikey);
        reqEntity.addPart("file", bin);
        httpPost.setEntity(reqEntity);

//        long start = System.currentTimeMillis();
		HttpResponse response = HttpUtils.getClient().execute(httpPost);
		int code = response.getStatusLine().getStatusCode();
		if (code == 200) {
			String result = EntityUtils.toString(response.getEntity());
			printMessage(result);
		} else {
			printMessage("HTTP error code:" + code);
		}
	}
	

	private void printMessage(String msg) {
		System.out.print("*****************");
		System.out.print(msg);
		System.out.println("*****************");
	}
	

	public static void main(String[] args) throws Exception {
		EyeClient ec = new EyeClient();
        long start = System.currentTimeMillis();
		ec.testUpload();
		long end = System.currentTimeMillis();
		ec.printMessage("time:"+(end-start));
	}
}
