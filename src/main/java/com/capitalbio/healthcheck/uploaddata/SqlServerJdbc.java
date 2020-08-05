package com.capitalbio.healthcheck.uploaddata;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import com.capitalbio.auth.util.Constant;
import com.capitalbio.auth.util.JwtUtil;
import com.capitalbio.common.util.DateUtil;
import com.capitalbio.common.util.EncryptUtil;
import com.capitalbio.common.util.JsonUtil;
import com.capitalbio.common.util.MD5Util;

public class SqlServerJdbc {
	public static String DATA_INSERT = "insert";
	public static String DATA_UPDATE = "update";
	
	public static String DATA_STATE_SUCCESS = "1";
	public static String DATA_STATE_ERROR = "0";
	
	public static void main(String[] args) {
		// 执行导入脚本程序
		new SqlServerJdbc();
	}
	
	String customerUrl;
	String hcUrl;
	public SqlServerJdbc() {
		Timer timer = new Timer();
		String rootPath = FileMgr.getPath();
		String fileName = rootPath + "\\config.properties";
		Util propertiesUtil = Util.getInstance(fileName);
		
		customerUrl = propertiesUtil.getValue("customerUrl");
		hcUrl = propertiesUtil.getValue("healthcheckUrl");
		
		if (customerUrl == null || customerUrl.length() <= 0) {
			String error = "获取接收客户数据到服务器的url失败，请确认配置文件中是否正确配置。";
			System.out.println(error);
			FileMgr.writeLog(error);
			return;
		}
		if (hcUrl == null || hcUrl.length() <= 0) {
			String error = "获取接收客户体检数据服务器的url失败，请确认配置文件中是否正确配置。";
			System.out.println(error);
			FileMgr.writeLog(error);
			return;
		}
		
		String intervalTime = propertiesUtil.getValue("intervalTime");
		
		long lintervalTime = 0;
		if (intervalTime != null && intervalTime.length() > 0) {
			lintervalTime = Long.parseLong(intervalTime);
		}
		
		if (lintervalTime <= 0) {
			String error = "获取任务执行时间间隔出错，请确认配置文件中是否正确配置。";
			System.out.println(error);
			FileMgr.writeLog(error);
			return;
		}
		
		timer.schedule(new RemindTask(), 0, lintervalTime * 1000);
	}

	/**
	 * 定时扫描数据库任务
	 * @author bio-hxh
	 *
	 */
	class RemindTask extends TimerTask {
		public void run() {
			long startTime = System.currentTimeMillis();
			FileMgr filePath = new FileMgr();

			// 获取上次扫描时间
			String date = filePath.readLastLine();

			System.out.println("上次扫描数据库时间:" + date);
			System.out.println("正在扫描传送数据，请稍后...");
			
			//日志
			StringBuffer log = new StringBuffer();
			log.append("开始传送数据：").append("\r\n");
			
			TableMgr tableMgr = new TableMgr();
			tableMgr.tableExist(log);
			
			// 扫描客户数据
			String scustomer = importCustomer(date, customerUrl, log);
			// 扫描客户体检数据
			String scheck = importHealthCheck(date, hcUrl, log);

			/**
			 * 导入客户和客户体检数据都成功时才记录上次扫描时间
			 */
			if (scustomer != null && scustomer.length() > 0 && scheck != null && scheck.length() > 0) {
				filePath.downLastImportDate();
			}

			long endTime = System.currentTimeMillis();

			String zhs = "总耗时：" + (endTime - startTime) / 1000;
			System.out.println(zhs);
			
			log.append(zhs).append("\r\n");
			FileMgr.writeLog(log.toString());
		}
	}

	/**
	 * 导入T_customer数据
	 */
	public String importCustomer(String lastImportTime, String url, StringBuffer log) {
		Connection ct = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;

		try {
			ct = TableMgr.getCon(log);
			if (ct == null) return "";
			
			StringBuffer commSql = new StringBuffer();
			commSql.append(
					"SELECT ConsumerId,ConsumerName,ConsumerCardNo,ConsumerBirthday,ConsumerNianLing,ConsumerSex,ConsumerHuF,ConsumerNation,");
			commSql.append(
					"ConsumerPhone,ConsumerDianH,ConsumerPhoto,ConsumerWork,ConsumerAddress,QianFaJiGuan,YouXiaoRiQi,ArchivesData ");
			
			//读取T_Consumer表中数据并上传
			StringBuffer sql = new StringBuffer();
			sql.append(commSql.toString()).append(" FROM T_Consumer ");
			if (lastImportTime != null && lastImportTime.length() > 0) {
				sql.append(" where updateTime >= '");
				sql.append(lastImportTime).append("'");
			}

			ps = ct.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			int readn = excConsumeRs(ct, rs, url, log, DATA_INSERT);
			
			//读取T_Consumer_Push表中未上传成功的数据并上传
			StringBuffer sqlPush = new StringBuffer();
			sqlPush.append(commSql.toString()).append(" FROM T_Consumer_Push where state = '").append(DATA_STATE_ERROR).append("'");
			ps1 = ct.prepareStatement(sqlPush.toString());
			rs1 = ps1.executeQuery();

			int readnpush = excConsumeRs(ct, rs1, url, log, DATA_UPDATE);

			String readStr = "该批次传送客户数据数量:" + (readn + readnpush);
			System.out.println(readStr);
			
			//写入日志
			log.append(readStr).append("\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (rs1 != null)
					rs1.close();
				if (ps != null)
					ps.close();
				if (ps1 != null)
					ps1.close();
				if (ct != null)
					ct.close();
			} catch (Exception e) {
				e.printStackTrace();
				
				StringWriter sw = new StringWriter();  
	            e.printStackTrace(new PrintWriter(sw, true));  
	            String str = sw.toString();  
	  
	            log.append("错误："+str).append("\r\n");
			}
		}

		return "success";
	}

	public int excConsumeRs(Connection ct, ResultSet rs, String url, StringBuffer log, String dataMgr) {
		int readn = 0;
		try {
			while (rs.next()) {
				readn++;
				TreeMap<String, Object> data = new TreeMap<String, Object>();
				String customerId = trim(rs.getString("ConsumerID"));
				data.put("customerId", customerId);
				data.put("name", trim(rs.getString("ConsumerName")));
				data.put("cardNo", trim(rs.getString("ConsumerCardNo")));
				data.put("birthday", trim(rs.getString("ConsumerBirthday")));
				data.put("age", rs.getInt("ConsumerNianLing"));
				data.put("sex", trim(rs.getString("ConsumerSex")));
				data.put("married", trim(rs.getString("ConsumerHuF")));
				data.put("nation", trim(rs.getString("ConsumerNation")));
				data.put("phone", trim(rs.getString("ConsumerPhone")));
				data.put("fixLinePhone", trim(rs.getString("ConsumerDianH")));
				data.put("photo", trim(rs.getString("ConsumerPhoto")));
				data.put("vocation", trim(rs.getString("ConsumerWork")));
				data.put("address", trim(rs.getString("ConsumerAddress")));
				data.put("IssuingAuthority", trim(rs.getString("QianFaJiGuan")));
				data.put("idValiddate", trim(rs.getString("YouXiaoRiQi")));

				Date archivesDate = rs.getDate("ArchivesData");
				data.put("archivesDate", archivesDate.getTime());

				String token = JwtUtil.createJWT(Constant.JWT_ID, customerId, Constant.JWT_TTL);
				data.put("token", token);
				
				String json = JsonUtil.mapToJson(data);

				// 发送数据
				String state = uploadData(url, json, log);
				
				//若为T_Consumer表中读取的数据，直接更新表中所有字段
				//若为T_Consumer_Push表中读取数据，只更新状态数据
				TableMgr table = new TableMgr();
				if (dataMgr.equals(DATA_INSERT)) {
					table.mgrConsumer(ct, rs, state, log);
				} else if (dataMgr.equals(DATA_UPDATE)) {
					table.updateConsumeState(ct, rs, state, log);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter sw = new StringWriter();  
            e.printStackTrace(new PrintWriter(sw, true));  
            String str = sw.toString();  
  
            log.append("错误："+str).append("\r\n");
		}
		
		return readn;
	}
	
	/**
	 * 导入T_HealthCheck数据
	 */
	public String importHealthCheck(String lastImportTime, String url, StringBuffer log) {
		Connection ct = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;

		try {
			ct = TableMgr.getCon(log);
			if (ct == null) return "";
			StringBuffer commSql = new StringBuffer();
			commSql.append("select t.ConsumerID,CheckTime,tiwen,shengao,tizhong,BMI,shousuoya,suzhangya,maibo,xueyang,FatContent,mailv,yaoWei,XueTang, ");
			commSql.append(" cq_yxjc, cq_tnbjzs, cq_yhjb, cq_tzbsjg, cq_scdd, cq_scz, ");
			commSql.append(" c.ConsumerName, ConsumerNianLing, ConsumerSex,consumerPhone from t_consumer c ");
			
			//先导入T_HealthCheck表中的数据
			StringBuffer sql = new StringBuffer();
			sql.append(commSql).append(", T_HealthCheck t");
			sql.append(" where t.ConsumerID = c.ConsumerID");
			if (lastImportTime != null && lastImportTime.length() > 0) {
				sql.append(" and CheckTime >= ").append("'").append(lastImportTime).append("'");
			}

			ps = ct.prepareStatement(sql.toString());
			rs = ps.executeQuery();

			int readn =  this.excHealthCheckRs(ct, rs, url, log, DATA_INSERT);

			//将T_HealthCheck_Push表中的未导入成功的数据再导入一遍
			StringBuffer sqlPush = new StringBuffer();
			sqlPush.append(commSql).append(", T_HealthCheck_Push t where state = '").append(DATA_STATE_ERROR).append("'");
			sqlPush.append(" and t.ConsumerID = c.ConsumerID");
			ps1 = ct.prepareStatement(sqlPush.toString());
			rs1 = ps1.executeQuery();
			int readnn =  this.excHealthCheckRs(ct, rs1, url, log, DATA_UPDATE);
			
			String readStr = "该批次传送客户体检数据数量:" + (readn + readnn);
			System.out.println(readStr);
			//写入日志
			log.append(readStr).append("\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (rs1 != null)
					rs1.close();
				if (ps != null)
					ps.close();
				if (ps1 != null)
					ps1.close();
				if (ct != null)
					ct.close();
			} catch (Exception e) {
				e.printStackTrace();
				
				StringWriter sw = new StringWriter();  
	            e.printStackTrace(new PrintWriter(sw, true));  
	            String str = sw.toString();  
	  
	            log.append("错误："+str).append("\r\n");
			}
		}

		return "success";
	}

	public int excHealthCheckRs(Connection ct, ResultSet rs, String url, StringBuffer log, String dataMgr) {
		int readn = 0;
		try {
			while (rs.next()) {
				readn++;
				TreeMap<String, Object> data = new TreeMap<String, Object>();
				String customerId = trim(rs.getString("ConsumerID"));
				String encCustomerId = EncryptUtil.encryptByKey(customerId);
				data.put("customerId", encCustomerId);
				data.put("name", rs.getString("ConsumerName"));
				data.put("sex", rs.getString("ConsumerSex"));
				data.put("age", rs.getInt("ConsumerNianLing"));
				data.put("contact", rs.getString("consumerPhone"));
				
				data.put("temperature", rs.getDouble("tiwen"));
				data.put("height", rs.getDouble("shengao"));
				data.put("weight", rs.getDouble("tizhong"));
				data.put("BMI", rs.getDouble("BMI"));
				data.put("highPressure", rs.getInt("shousuoya"));
				data.put("lowPressure", rs.getInt("suzhangya"));
				data.put("pulse", rs.getInt("maibo"));
				data.put("oxygen", rs.getInt("xueyang"));
				data.put("fatContent", rs.getDouble("FatContent"));
				//data.put("mailv", rs.getInt("mailv"));
				data.put("waistline", rs.getDouble("yaowei"));
				data.put("bloodGlucose", rs.getDouble("XueTang"));//空腹血糖

				Date archivesDate = rs.getDate("CheckTime");
				data.put("checkTime", DateUtil.datetimeToString(archivesDate));

				//新增的6个字段
				data.put("eyeCheck", rs.getString("cq_yxjc"));
				data.put("familyHistory", rs.getString("cq_tnbjzs"));
				data.put("disease", rs.getString("cq_yhjb"));
				data.put("tizhi", rs.getString("cq_tzbsjg"));
				data.put("checkPlace", rs.getString("cq_scdd"));
				data.put("checkGroup", rs.getString("cq_scz"));
				
				Long sysTime = System.currentTimeMillis();
				data.put("sysTime", sysTime);
				String token = MD5Util.MD5Encode(encCustomerId+sysTime+Constant.SECRET_KEY);
				data.put("token", token);
				
				data.put("src", "aio");
				
				String json = JsonUtil.mapToJson(data);
				
				String state = uploadData(url, json.toString(), log);
				
				TableMgr table = new TableMgr();
				if (dataMgr.equals(DATA_INSERT)) {
					table.mgrHealthCheck(ct, rs, state, log);
				} else if (dataMgr.equals(DATA_UPDATE)) {
					table.updateHealthCheck(ct, rs, state, log);
				}
				
				log.append("HealthCheck info："+json).append("\r\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return readn;
	}
	public static String trim(String value) {
		if (value == null || value.length() <= 0) {
			return "";
		}

		return value;
	}

	/**
	 * 上传数据
	 * 0 代表传送失败    1代表传送成功
	 * @param urlStr
	 * @param json
	 */
	public String uploadData(String url, String json, StringBuffer log) {
		try {
			log.append("传送数据url："+url).append("\r\n");
			
			if (url == null || url.length() <= 0 || json == null || json.length() <= 0) {
				return DATA_STATE_ERROR;
			}

			// 确定请求方式
			HttpPost httpPost = new HttpPost(url);
			StringEntity se = new StringEntity(json, "UTF-8");
			httpPost.setEntity(se);
			httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
			se.setContentType("text/json");
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			HttpResponse response = new DefaultHttpClient().execute(httpPost);
			int code = response.getStatusLine().getStatusCode();
			log.append("Code:").append(code).append("\r\n");
			HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null){
                InputStream instreams = httpEntity.getContent(); 
                byte b[] = new byte[1024];  
                int len = 0;  
                int temp=0;          //所有读取的内容都使用temp接收  
                while((temp=instreams.read())!=-1){    //当没有读取完时，继续读取  
                    b[len]=(byte)temp;  
                    len++;  
                }  
                instreams.close();  
                String msg = new String(b, 0, len, "UTF-8");  
                
                log.append("response content:").append(msg).append("\r\n");
            }
			
			if (code != 200) {
				String errorCode = "ERROR Code:" + code;
				System.out.println(errorCode);
				
				return DATA_STATE_ERROR;
			}
			
			return DATA_STATE_SUCCESS;
		} catch (Exception e) {
			String lw = "连接传送数据服务器接口失败，请确定设备正确联网。";
			System.out.println(lw);
			e.printStackTrace();
			StringWriter sw = new StringWriter();  
            e.printStackTrace(new PrintWriter(sw, true));  
            String str = sw.toString();  
  
            log.append("连接传送数据服务器接口失败，请确定设备正确联网。").append("\r\n");
            log.append("传送数据错误："+str).append("\r\n");
		}
		
		return DATA_STATE_ERROR;
	}
}
