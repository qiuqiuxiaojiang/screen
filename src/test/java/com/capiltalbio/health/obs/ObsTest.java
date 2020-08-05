package com.capiltalbio.health.obs;

import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.model.CopyObjectResult;

public class ObsTest {
	public static void main(String[] args) throws Exception {
		String endPoint = "obs.cn-north-1.myhwclouds.com";
		String accessId = "ASA7PYV7FQEWON0GTCV2";
		String accessKey = "qZsoKPHjp6LwOwADFyG6JO0KPdC8Vp7pCgVuRNCK";
		String bucketName = "obs-kaifa";
		ObsConfiguration conf = new ObsConfiguration();
		conf.setEndPoint(endPoint);
		conf.setMaxConnections(3000);
		conf.setMaxErrorRetry(3);
		ObsClient obsClient = new ObsClient(accessId, accessKey, conf);
		String srcFileId = "eye/c4ffcb0b-e850-41df-9039-166fd8db1436_report.pdf";
		String destFileId = "testeye/c4ffcb0b-e850-41df-9039-166fd8db1436_report.pdf";
		CopyObjectResult result = obsClient.copyObject(bucketName, srcFileId, bucketName, destFileId);
		System.out.println("\t" + result.getEtag());
		obsClient.close();

	}
}
