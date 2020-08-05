package com.capiltalbio.health.mock;

public class MockDataClient {

	public static void main(String[] args) throws Exception{
		String[] argsTest = {"http://114.116.74.14:8080/screening", "bloodSugar", "shenchuang", "123456", 
				"411122199209068279", "2019-05-23 01:01:01",
				"tc", "tg", "hdl", "ldl", "bloodGlucose", "bloodGlucose2h", "bloodGlucoseRandom"};
		
//		String[] argsTest = {"http://localhost:8080/com.bioeh.sp.hm.customer.mserverWeixin", "tizhi", 
//				"411122199209068279", "1,2,1,3,1,3,2,3,3,3,2,2,2,2,2,3,3,3,3,3,3,3,4,4,4,4,3,3,3,3,3,3,2,2,2,2,2,3,3,3,3,3,2,2,2,2,2,2,3,3,3,3,3,3,2,2,2,2,2,3",
//				"平和质"};
		
		args = argsTest;
		
		
		if (args == null || args.length < 2) {
			System.out.println("命令格式：java -jar MockDataClient host function otherArgs...\r\nhost示例：https://fxdm.bioeh.com/\r\nfunction必须为bodyData,bloodSugar,tizhi之一");
			return;
		}
		String host = args[0];
		String func = args[1];
		if (func.equals("bodyData")) {
			BodyDataClient bdc = new BodyDataClient();
			String returnMsg = bdc.mockBodyData(host, args);
			System.out.println(returnMsg);
		} else if (func.equals("bloodSugar")) {
			BloodSugarClient bsc = new BloodSugarClient();
			String msgReturn = bsc.mockBloodSugar(host, args);
			System.out.println(msgReturn);
		} else if (func.equals("tizhi")) {
			TiZhiClient tzc = new TiZhiClient();
			String msgReturn = tzc.mockTiZhi(host, args);
			System.out.println(msgReturn);
		} else {
			System.out.println("错误：function必须为bodyData,bloodSugar,tizhi之一");
		}
	}

}
