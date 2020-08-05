package com.capitalbio.healthcheck.uploaddata;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TableMgr {

	/**
	 * 创建consumer推送日志表sql
	 * @return
	 */
	public String createConsumer() {
		StringBuffer sql = new StringBuffer();
		sql.append("CREATE TABLE dbo.T_Consumer_Push(");
		sql.append(" id int IDENTITY(1,1) NOT NULL,");
		sql.append(" ConsumerId varchar(20) NOT NULL,");
		sql.append(" ConsumerName varchar(10) NULL,");
		sql.append(" ConsumerCardNo varchar(20) NULL,");
		sql.append(" ConsumerBirthday varchar(20) NULL,");
		sql.append(" ConsumerNianLing int NULL,");
		sql.append(" ConsumerSex varchar(4) NULL,");
		sql.append(" ConsumerHuF varchar(4) NULL,");
		sql.append(" ConsumerNation varchar(20) NULL,");
		sql.append(" ConsumerPhone varchar(14) NULL,");
		sql.append(" ConsumerDianH varchar(50) NULL,");
		sql.append(" ConsumerPhoto image NULL,");
		sql.append(" ConsumerWork varchar(50) NULL,");
		sql.append(" ConsumerAddress varchar(150) NULL,");
		sql.append(" QianFaJiGuan varchar(200) NULL,");
		sql.append(" YouXiaoRiQi varchar(100) NULL,");
		sql.append(" ArchivesData datetime NULL,");
		sql.append(" state varchar(2) NULL,");
		sql.append(" remark varchar(1024) NULL)");
		
		return sql.toString();
	}
	
	/**
	 * 创建HealthCheck推送日志表sql
	 * @return
	 */
	public String createHealthCheck() {
		StringBuffer sql = new StringBuffer();
		sql.append(" CREATE TABLE dbo.T_HealthCheck_Push(");
		sql.append(" ConsumerID varchar(20) NOT NULL,");
		sql.append(" CheckTime datetime NOT NULL,");
		sql.append(" yaowei numeric(18, 1) NULL,");
		sql.append(" XueTang numeric(18, 1) NULL,");
		sql.append(" tiwen numeric(18, 1) NULL,");
		sql.append(" shengao numeric(18, 1) NULL,");
		sql.append(" tizhong numeric(18, 1) NULL,");
		sql.append(" BMI numeric(18, 1) NULL,");
		sql.append(" shousuoya int NULL,");
		sql.append(" suzhangya int NULL,");
		sql.append(" maibo int NULL,");
		sql.append(" mailv int NULL,");
		sql.append(" xueyang int NULL,");
		sql.append(" FatContent numeric(18, 1) NULL,");
		sql.append(" cq_yxjc varchar(200) NULL,");
		sql.append(" cq_tnbjzs varchar(200) NULL,");
		sql.append(" cq_yhjb varchar(200) NULL,");
		sql.append(" cq_tzbsjg varchar(200) NULL,");
		sql.append(" cq_scdd varchar(200) NULL,");
		sql.append(" cq_scz varchar(200) NULL,");
		sql.append(" state varchar(2) NULL,");
		
		sql.append(" remark varchar(1024) NULL)");
		return sql.toString();
	}
	
	public void mgrConsumer(Connection ct, ResultSet rs, String state, StringBuffer log) {
		PreparedStatement selectPs = null;
		ResultSet selectrs = null;
		
		try {
			String selectSql = " select * from T_Consumer_Push where ConsumerId = '" + trim(rs.getString("ConsumerID")) + "'";
			selectPs = ct.prepareStatement(selectSql);
			selectrs = selectPs.executeQuery();
			//若该用户已经存在，则修改
			if (selectrs.next()) {
				this.updateConsumer(ct, rs, state, log);
			} else {
				this.insertConsumer(ct, rs, state, log);
			}
		} catch (Exception e) {
			
		} finally {
			try {
				if (selectPs != null)
					selectPs.close();
				if (selectrs != null)
					selectrs.close();
			} catch (Exception e) {
				e.printStackTrace();
				
				StringWriter sw = new StringWriter();  
	            e.printStackTrace(new PrintWriter(sw, true));  
	            String str = sw.toString();  
	  
	            log.append("插入T_Consumer_Push数据错误："+str).append("\r\n");
			}
		}
	}
	
	/**
	 * 插入客户传送信息
	 * @param rs
	 */
	public void insertConsumer(Connection ct, ResultSet rs, String state, StringBuffer log) {
		Statement ps = null;
		
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" INSERT INTO T_Consumer_Push");
			sql.append(" (ConsumerId ,ConsumerName ,ConsumerCardNo,ConsumerBirthday,ConsumerNianLing,ConsumerSex");
			sql.append(" ,ConsumerHuF,ConsumerNation,ConsumerPhone,ConsumerDianH,ConsumerPhoto,ConsumerWork,ConsumerAddress");
			sql.append(" ,QianFaJiGuan,YouXiaoRiQi ,ArchivesData,state, remark) VALUES (");
			
			sql.append("'").append(trim(rs.getString("ConsumerID"))).append("',");
			sql.append("'").append(trim(rs.getString("ConsumerName"))).append("',");
			sql.append("'").append(trim(rs.getString("ConsumerCardNo"))).append("',");
			sql.append("'").append(trim(rs.getString("ConsumerBirthday"))).append("',");
			sql.append("'").append(trim(rs.getString("ConsumerNianLing"))).append("',");
			sql.append("'").append(trim(rs.getString("ConsumerSex"))).append("',");
			sql.append("'").append(trim(rs.getString("ConsumerHuF"))).append("',");
			sql.append("'").append(trim(rs.getString("ConsumerNation"))).append("',");
			sql.append("'").append(trim(rs.getString("ConsumerPhone"))).append("',");
			sql.append("'").append(trim(rs.getString("ConsumerDianH"))).append("',");
			sql.append("'").append(trim(rs.getString("ConsumerPhoto"))).append("',");
			sql.append("'").append(trim(rs.getString("ConsumerWork"))).append("',");
			sql.append("'").append(trim(rs.getString("ConsumerAddress"))).append("',");
			sql.append("'").append(trim(rs.getString("QianFaJiGuan"))).append("',");
			sql.append("'").append(trim(rs.getString("YouXiaoRiQi"))).append("',");
			sql.append("'").append(trim(rs.getString("ArchivesData"))).append("',");
			sql.append("'").append(state).append("',");

			sql.append(addRemark(state));
			
			sql.append(")");
			
			ps = ct.createStatement();
			ps.executeUpdate(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				e.printStackTrace();
				
				StringWriter sw = new StringWriter();  
	            e.printStackTrace(new PrintWriter(sw, true));  
	            String str = sw.toString();  
	  
	            log.append("插入T_Consumer_Push数据错误："+str).append("\r\n");
			}
		}
	}
	
	/**
	 * 更新客户数据
	 * @param ct
	 * @param rs
	 * @param state
	 * @param log
	 */
	public void updateConsumer(Connection ct, ResultSet rs, String state, StringBuffer log) {
		StringBuffer sql = new StringBuffer();
		try {
			sql.append(" update T_Consumer_Push set ");
			sql.append(" ConsumerID = '").append(trim(rs.getString("ConsumerID"))).append("',");
			sql.append(" ConsumerName = '").append(trim(rs.getString("ConsumerName"))).append("',");
			sql.append(" ConsumerCardNo = '").append(trim(rs.getString("ConsumerCardNo"))).append("',");
			sql.append(" ConsumerBirthday = '").append(trim(rs.getString("ConsumerBirthday"))).append("',");
			sql.append(" ConsumerNianLing = '").append(trim(rs.getString("ConsumerNianLing"))).append("',");
			sql.append(" ConsumerSex = '").append(trim(rs.getString("ConsumerSex"))).append("',");
			sql.append(" ConsumerHuF = '").append(trim(rs.getString("ConsumerHuF"))).append("',");
			sql.append(" ConsumerNation = '").append(trim(rs.getString("ConsumerNation"))).append("',");
			sql.append(" ConsumerPhone = '").append(trim(rs.getString("ConsumerPhone"))).append("',");
			sql.append(" ConsumerDianH = '").append(trim(rs.getString("ConsumerDianH"))).append("',");
			sql.append(" ConsumerPhoto = '").append(trim(rs.getString("ConsumerPhoto"))).append("',");
			sql.append(" ConsumerWork = '").append(trim(rs.getString("ConsumerWork"))).append("',");
			sql.append(" ConsumerAddress = '").append(trim(rs.getString("ConsumerAddress"))).append("',");
			sql.append(" QianFaJiGuan = '").append(trim(rs.getString("QianFaJiGuan"))).append("',");
			sql.append(" YouXiaoRiQi = '").append(trim(rs.getString("YouXiaoRiQi"))).append("',");
			sql.append(" ArchivesData = '").append(trim(rs.getString("ArchivesData"))).append("',");
			sql.append(" state = '").append(state).append("',");
			sql.append(updateRemark(state));
			sql.append(" where ConsumerID = '").append(rs.getString("ConsumerID")).append("'");
			Statement ps = null;
			
			ps = ct.createStatement();
			ps.executeUpdate(sql.toString());
		} catch (Exception e) {
			StringWriter sw = new StringWriter();  
            e.printStackTrace(new PrintWriter(sw, true));  
            String str = sw.toString();  
  
            log.append("更新T_Consumer_Push数据错误："+str).append("\r\n");
			e.printStackTrace();
		}
	}
	
	/**
	 * 只更新T_Consumer_Push表中数据状态及备注
	 * @param ct
	 * @param rs
	 * @param state
	 * @param log
	 */
	public void updateConsumeState(Connection ct, ResultSet rs, String state, StringBuffer log) {
		StringBuffer sql = new StringBuffer();
		try {
			sql.append(" update T_Consumer_Push set state = '").append(state).append("',");
			sql.append(updateRemark(state));
			sql.append(" where ConsumerID = '").append(rs.getString("ConsumerID")).append("'");
			
			Statement ps = null;
			
			ps = ct.createStatement();
			ps.executeUpdate(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter sw = new StringWriter();  
            e.printStackTrace(new PrintWriter(sw, true));  
            String str = sw.toString();  
  
            log.append("更新T_Consumer_Push数据错误："+str).append("\r\n");
		}
	}
	
	/**
	 * 更新数据
	 * 若数据库中不存在该客户当天体检数据，则添加，否则插入
	 * @param ct
	 * @param rs
	 * @param state
	 * @param log
	 */
	public void mgrHealthCheck(Connection ct, ResultSet rs, String state, StringBuffer log) {
		PreparedStatement selectPs = null;
		ResultSet selectrs = null;
		
		try {
			StringBuffer selectSql = new StringBuffer();
			selectSql.append(" select * from T_HealthCheck_Push where ConsumerId = '").append(trim(rs.getString("ConsumerID"))).append("'");
			selectSql.append(" and  CONVERT(varchar(100), CheckTime, 23) = '").append(trim(convertDate(rs.getString("CheckTime")))).append("'");
			selectPs = ct.prepareStatement(selectSql.toString());
			selectrs = selectPs.executeQuery();
			//若该用户已经存在，则修改
			if (selectrs.next()) {
				this.updateHealthCheck(ct, rs, state, log);
			} else {
				this.insertHealthCheck(ct, rs, state, log);
			}
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter sw = new StringWriter();  
            e.printStackTrace(new PrintWriter(sw, true));  
            String str = sw.toString();  
  
            log.append("插入T_Consumer_Push数据错误："+str).append("\r\n");
		} finally {
			try {
				if (selectPs != null)
					selectPs.close();
				if (selectrs != null)
					selectrs.close();
			} catch (Exception e) {
				e.printStackTrace();
				
				StringWriter sw = new StringWriter();  
	            e.printStackTrace(new PrintWriter(sw, true));  
	            String str = sw.toString();  
	  
	            log.append("插入T_Consumer_Push数据错误："+str).append("\r\n");
			}
		}
		
	}
	
	/**
	 * 插入客户体检传送信息
	 * @param rs
	 */
	public void insertHealthCheck(Connection ct, ResultSet rs, String state, StringBuffer log) {
		Statement ps = null;
		
		StringBuffer sql = new StringBuffer();
		sql.append(" INSERT INTO T_HealthCheck_Push");
		sql.append(" (ConsumerID,CheckTime,yaowei,XueTang,tiwen,shengao,tizhong,BMI,shousuoya");
		sql.append(" ,suzhangya,maibo,mailv,xueyang,FatContent,state, remark, ");
		sql.append(" cq_yxjc, cq_tnbjzs, cq_yhjb, cq_tzbsjg, cq_scdd, cq_scz");
		sql.append(") VALUES");
		
		try {
			sql.append("('").append(trim(rs.getString("ConsumerID"))).append("',");
			sql.append("'").append(trim(convertDate(rs.getString("CheckTime")))).append("',");
			sql.append("'").append(strToDouble(rs.getString("yaowei"))).append("',");
			sql.append("'").append(strToDouble(rs.getString("XueTang"))).append("',");
			sql.append("'").append(strToDouble(rs.getString("tiwen"))).append("',");
			sql.append("'").append(strToDouble(rs.getString("shengao"))).append("',");
			sql.append("'").append(strToDouble(rs.getString("tizhong"))).append("',");
			sql.append("'").append(strToDouble(rs.getString("BMI"))).append("',");
			sql.append("'").append(strToInteger(rs.getString("shousuoya"))).append("',");
			sql.append("'").append(strToInteger(rs.getString("suzhangya"))).append("',");
			sql.append("'").append(strToInteger(rs.getString("maibo"))).append("',");
			sql.append("'").append(strToInteger(rs.getString("mailv"))).append("',");
			sql.append("'").append(strToInteger(rs.getString("xueyang"))).append("',");
			sql.append("'").append(strToDouble(rs.getString("FatContent"))).append("',");
			sql.append("'").append(state).append("',");
			
			//添加推送备注
			sql.append(addRemark(state)).append(",");
			sql.append("'").append(trim(rs.getString("cq_yxjc"))).append("',");
			sql.append("'").append(trim(rs.getString("cq_tnbjzs"))).append("',");
			sql.append("'").append(trim(rs.getString("cq_yhjb"))).append("',");
			sql.append("'").append(trim(rs.getString("cq_tzbsjg"))).append("',");
			sql.append("'").append(trim(rs.getString("cq_scdd"))).append("',");
			sql.append("'").append(trim(rs.getString("cq_scz"))).append("'");
			
			
			sql.append(")");
			
			ps = ct.createStatement();
			ps.executeUpdate(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				e.printStackTrace();
				
				StringWriter sw = new StringWriter();  
	            e.printStackTrace(new PrintWriter(sw, true));  
	            String str = sw.toString();  
	  
	            log.append("错误："+str).append("\r\n");
			}
		}
	}
	
	/**
	 * 更新客户体检传送信息
	 * @param rs
	 */
	public void updateHealthCheck(Connection ct, ResultSet rs, String state, StringBuffer log) {
		Statement ps = null;
		
		StringBuffer sql = new StringBuffer();
		sql.append(" update T_HealthCheck_Push set ");
		
		try {
			sql.append("yaowei = ").append(strToDouble(rs.getString("yaowei"))).append(",");
			sql.append("XueTang = ").append(strToDouble(rs.getString("XueTang"))).append(",");
			sql.append("tiwen = ").append(strToDouble(rs.getString("tiwen"))).append(",");
			sql.append("shengao = ").append(strToDouble(rs.getString("shengao"))).append(",");
			sql.append("tizhong = ").append(strToDouble(rs.getString("tizhong"))).append(",");
			sql.append("BMI = ").append(strToDouble(rs.getString("BMI"))).append(",");
			sql.append("shousuoya = ").append(strToInteger(rs.getString("shousuoya"))).append(",");
			sql.append("suzhangya = ").append(strToInteger(rs.getString("suzhangya"))).append(",");
			sql.append("maibo = ").append(strToInteger(rs.getString("maibo"))).append(",");
			sql.append("mailv = ").append(strToInteger(rs.getString("mailv"))).append(",");
			sql.append("xueyang = ").append(strToInteger(rs.getString("xueyang"))).append(",");
			sql.append("FatContent = ").append(strToDouble(rs.getString("FatContent"))).append(",");
			sql.append("state = '").append(state).append("',");
			
			sql.append("cq_yxjc = '").append(trim(rs.getString("cq_yxjc"))).append("',");
			sql.append("cq_tnbjzs = '").append(trim(rs.getString("cq_tnbjzs"))).append("',");
			sql.append("cq_yhjb = '").append(trim(rs.getString("cq_yhjb"))).append("',");
			sql.append("cq_tzbsjg = '").append(trim(rs.getString("cq_tzbsjg"))).append("',");
			sql.append("cq_scdd = '").append(trim(rs.getString("cq_scdd"))).append("',");
			sql.append("cq_scz = '").append(trim(rs.getString("cq_scz"))).append("',");
			
			sql.append(updateRemark(state));
			sql.append(" where ConsumerID = '").append(trim(rs.getString("ConsumerID"))).append("' ");
			sql.append(" and  CONVERT(varchar(100), CheckTime, 23) = '").append(trim(convertDate(rs.getString("CheckTime")))).append("'");
			
			ps = ct.createStatement();
			ps.executeUpdate(sql.toString());
		} catch (Exception e) {
			StringWriter sw = new StringWriter();  
            e.printStackTrace(new PrintWriter(sw, true));  
            String str = sw.toString();  
  
            log.append("更新T_HealthCheck_Push数据错误："+str).append("\r\n");
            
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				StringWriter sw = new StringWriter();  
	            e.printStackTrace(new PrintWriter(sw, true));  
	            String str = sw.toString();  
	  
	            log.append("更新T_HealthCheck_Push数据错误："+str).append("\r\n");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 只更新T_HealthCheck_Push表中数据状态及备注
	 * @param ct
	 * @param rs
	 * @param state
	 * @param log
	 */
	public void updateHealthCheckState(Connection ct, ResultSet rs, String state, StringBuffer log) {
		StringBuffer sql = new StringBuffer();
		try {
			sql.append(" update T_HealthCheck_Push set state = '").append(state).append("',");
			
			sql.append(updateRemark(state));
			
			sql.append(" where ConsumerID = '").append(trim(rs.getString("ConsumerID"))).append("' ");
			sql.append(" and  CONVERT(varchar(100), CheckTime, 23) = ").append(trim(convertDate(rs.getString("CheckTime")))).append("',");
			
			Statement ps = null;
			
			ps = ct.createStatement();
			ps.executeUpdate(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter sw = new StringWriter();  
            e.printStackTrace(new PrintWriter(sw, true));  
            String str = sw.toString();  
  
            log.append("更新T_HealthCheck_Push数据错误："+str).append("\r\n");
		}
	}
	
	public String addRemark(String state) {
		StringBuffer sql = new StringBuffer();
		String time = dateToString(new Date());
		if (state.equals(SqlServerJdbc.DATA_STATE_SUCCESS)) {
			sql.append("'").append(time).append("推送成功\n'");
		} else if (state.equals(SqlServerJdbc.DATA_STATE_ERROR)) {
			sql.append("'").append(time).append("推送失败\n'");
		}
		
		return sql.toString();
	}
	
	public String updateRemark(String state) {
		StringBuffer sql = new StringBuffer();
		String time = dateToString(new Date());
		if (state.equals(SqlServerJdbc.DATA_STATE_SUCCESS)) {
			sql.append("remark = remark+'").append(time).append("推送成功\n'");
		} else if (state.equals(SqlServerJdbc.DATA_STATE_ERROR)) {
			sql.append("remark = remark+'").append(time).append("推送失败\n'");
		}
		
		return sql.toString();
	}
	
	public static String trim(String value) {
		if (value == null || value.length() <= 0) {
			return "";
		}

		return value;
	}
	
	public static double strToDouble(String value) {
		if (value == null || value.length() <= 0) {
			return 0;
		}

		return Double.parseDouble(value);
	}
	
	public static int strToInteger(String value) {
		if (value == null || value.length() <= 0) {
			return 0;
		}

		return Integer.parseInt(value);
	}
	/**
	 * date转String
	 * 
	 * @param date
	 * @return
	 */
	public static String dateToString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		String str=sdf.format(date);  
		return str;
	}
	
	/**
	 * 将String日期转换为Date
	 * 
	 * @param time
	 * @return
	 * @throws Exception
	 */
	public static String convertDate(String time) throws Exception {
		if (time == null || time.length() <= 0) {
			return "";
		}
		
		String[] arrTime = time.split(" ");
		return arrTime[0];
	}
	
	public void tableExist(StringBuffer log) {
		Connection ct = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ct = getCon(log);
			//若用户表不存在，则创建表
			String consumeSql = "select * from sysobjects where name='T_Consumer_Push'";
			ps = ct.prepareStatement(consumeSql);
			rs = ps.executeQuery();
			if (!rs.next()) {
				String cSql = createConsumer();
				ps = ct.prepareStatement(cSql);
				ps.execute();
			}
			
			//若客户体检表不存在，则创建表
			String hcsql = "select * from sysobjects where name='T_HealthCheck_Push'";
			ps = ct.prepareStatement(hcsql);
			rs = ps.executeQuery();
			if (!rs.next()) {
				String hcSql = createHealthCheck();
				ps = ct.prepareStatement(hcSql);
				ps.execute();
			} else {
				//判断表里面是否有这些字段，没有的话则添加
				String addHcSql = "SELECT 1 FROM SYSOBJECTS T1 INNER JOIN SYSCOLUMNS T2 ON T1.ID=T2.ID WHERE T1.NAME='T_HealthCheck_Push' AND T2.NAME='cq_yxjc'";
				ps = ct.prepareStatement(addHcSql);
				rs = ps.executeQuery();
				if (!rs.next()) {
					String addSql = alertSql();
					ps = ct.prepareStatement(addSql);
					ps.execute();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
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
	}
	
	private String alertSql() {
		StringBuffer addSql = new StringBuffer();
		addSql.append("ALTER TABLE [T_HealthCheck_Push]");
		addSql.append(" ADD [cq_yxjc] varchar(200) NULL,");
		addSql.append(" [cq_tnbjzs] varchar(200) NULL,");
		addSql.append(" [cq_yhjb] varchar(200) NULL ,");
		addSql.append(" [cq_tzbsjg] varchar(200) NULL,");
		addSql.append(" [cq_scdd] varchar(200) NULL,");
		addSql.append(" [cq_scz] varchar(200) NULL");
		
		return addSql.toString();
	}
	/**
	 * 获得数据库连接
	 * 
	 * @return
	 */
	public static Connection getCon(StringBuffer log) {
		Connection ct = null;

		try {
			// 加载驱动
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String fileName = FileMgr.getPath() + "\\config.properties";
			Util propertiesUtil = Util.getInstance(fileName);
			String host = propertiesUtil.getValue("dataBaseUrl");
			if (host == null || host.length() <= 0) {
				String error = "获取数据库url失败，请确认配置文件中是否正确配置。";
				System.out.println(error);
				log.append(error);
				return null;
			}
			String userName = propertiesUtil.getValue("userName");
			if (userName == null || userName.length() <= 0) {
				String error = "获取数据库访问用户名失败，请确认配置文件中是否正确配置。";
				System.out.println(error);
				log.append(error);
				return null;
			}
			String password = propertiesUtil.getValue("password");
			ct = DriverManager.getConnection(host, userName, password);
		} catch (Exception e) {
			e.printStackTrace();
			
            StringWriter sw = new StringWriter();  
            e.printStackTrace(new PrintWriter(sw, true));  
            String str = sw.toString();  
  
            log.append("连接数据库出错："+str).append("\r\n");
		}

		return ct;
	}
}
