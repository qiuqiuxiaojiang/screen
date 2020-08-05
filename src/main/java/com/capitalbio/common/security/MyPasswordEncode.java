package com.capitalbio.common.security;

import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;

public class MyPasswordEncode extends MessageDigestPasswordEncoder{

	public MyPasswordEncode(String algorithm) {
		super(algorithm);
		// TODO Auto-generated constructor stub
	}

	// 如果返回true，则验证通过。
	/*public boolean isPasswordValid(String savePass, String submitPass, Object salt) {
		return savePass.equalsIgnoreCase(Util.MD5WithSalt(submitPass,
				salt.toString()));
	}*/

}
