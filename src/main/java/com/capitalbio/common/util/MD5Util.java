package com.capitalbio.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
	public final static String[] strDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
	protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6',
        '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	protected static MessageDigest messageDigest = null;
	static {
	    try {
	        messageDigest = MessageDigest.getInstance("MD5");
	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	}

	/*public String mergePasswordAndSalt(String password, Object salt, boolean strict) {  
        if (password == null) {  
            password = "";  
        }  
  
        if (strict && (salt != null)) {  
            if ((salt.toString().lastIndexOf("{") != -1) || (salt.toString().lastIndexOf("}") != -1)) {  
                throw new IllegalArgumentException("Cannot use { or } in salt.toString()");  
            }  
        }  
  
        if ((salt == null) || "".equals(salt)) {  
            return password;  
        } else {  
            return password + "{" + salt.toString() + "}";  
        }  
	}  */
	
	/*public String mergePasswordAndSalt(String password, Object salt, boolean strict) throws IllegalAccessException {
		if (password == null) {
			password = "";
		}
		
		if (strict && (salt != null)) {
			if ((salt.toString().lastIndexOf("{") != -1) || (salt.toString().lastIndexOf("}") != -1)) {
				throw new IllegalAccessException("Cannot use { or } in salt.toString()");
			}
		}
		
		if ((salt == null) || "".equals(salt)) {
			return password;
		} else {
			return password + "{" + salt.toString() + "}";
		}
	}*/


	public static String MD5Encode(String origin) {
		String resultString = null;

		try {
//			resultString = new String(origin);
//			messageDigest.update(origin.getBytes("UTF-8"));
			resultString = bufferToHex(messageDigest.digest(origin
					.getBytes("UTF-8")));
		} catch (Exception ex) {

		}
		return resultString;
	}
	
    public static String GetMD5Code(String strObj) {
		String resultString = null;
		try {
			resultString = new String(strObj);
			MessageDigest md = MessageDigest.getInstance("MD5");
			// md.digest() 该函数返回值为存放哈希值结果的byte数组
			resultString = byteToString(md.digest(strObj.getBytes()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resultString;
	}

    public static String byteToString(byte[] bByte) {
		StringBuffer sBuffer = new StringBuffer();
		for (int i = 0; i < bByte.length; i++) {
			sBuffer.append(byteToArrayString(bByte[i]));
		}
		return sBuffer.toString();
	}
// 返回形式为数字跟字符串
	public static String byteToArrayString(byte bByte) {
		int iRet = bByte;
		// System.out.println("iRet="+iRet);
		if (iRet < 0) {
			iRet += 256;
		}
		int iD1 = iRet / 16;
		int iD2 = iRet % 16;
		return strDigits[iD1] + strDigits[iD2];
	}
	
	/**
     * 根据文件路径计算文件的MD5
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static String getFileMD5String(String fileName) throws IOException {
        File f = new File(fileName);
        return getFileMD5String(f);
    }
 
    /**
     * 根据文件对象计算文件的MD5
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String getFileMD5String(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        FileChannel ch = in.getChannel();
        MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0,
                file.length());
        messageDigest.update(byteBuffer);
        String str = bufferToHex(messageDigest.digest());
        in.close();
        return str;
    }
    
    /** 
     * 对byte类型的数组进行MD5加密 
     *  
     */ 
    public static String getMD5String(byte[] bytes) { 
    	messageDigest.update(bytes);   
        return bufferToHex(messageDigest.digest());   
    } 

 
    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }
 
    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }
 
    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        char c1 = hexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }
 
    /**
     * 测试方法
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
//        String fileName = "/Users/wdong/Downloads/2013.pptx"; // 更改文件名不会导致文件md5值变化
//        System.out.println(getFileMD5String(fileName));
    	System.out.println(MD5Util.MD5Encode("123456"));
//    	System.out.println(MD5Util.MD5Encode("34960.027.636.9两江新区185101866360.00.00null0李阳594b5894607e380726733d2c0男1521448304853149641920000010000011FYzOa_Ebn3oA4tc3REuShA2fRWiSo5ZbLrm4dEKA3Nk0.00.0bioehEquipmentASEjk170222"));
//    	System.out.println(MD5Util.GetMD5Code("34960.027.636.9两江新区185101866360.00.00null0李阳594b5894607e380726733d2c0男1521448304853149641920000010000011FYzOa_Ebn3oA4tc3REuShA2fRWiSo5ZbLrm4dEKA3Nk0.00.0bioehEquipmentASEjk170222"));
    }

}
