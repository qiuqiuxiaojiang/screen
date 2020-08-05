	jQuery.validator.addMethod("alnum", 
		function(value, element) {
            return this.optional(element) || /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[^]{8,16}$/.test(value);
    },"需输入大写字母、小写字母和数字");
	
	jQuery.validator.addMethod("alnum1", 
			function(value, element) {
	            return this.optional(element) || /^[0-9a-zA-Z\_]{6,20}$/.test(value);
	    },"请输入字母、数字、下划线，且长度不得少于六位数");


	jQuery.validator.addMethod("isMobile", function(value, element) {  
	    var length = value.length;  
	    var regPhone = /^1([3578]\d|4[57])\d{8}$/;  
	    return this.optional(element) || ( length == 11 && regPhone.test( value ) );    
	}, "请正确填写手机号");   


	jQuery.validator.addMethod("<span style='color:#FF0000;'><strong>isChar</strong></span>", function(value, element) {  
	    var length = value.length;  
	    var regName = /[^\u4e00-\u9fa5]/g;  
	    return this.optional(element) || !regName.test( value );    
	}, "请正确格式的姓名(暂支持汉字)");  
	
	
	jQuery.validator.addMethod("<strong>isZipCode</strong>", function(value, element) {    
	    var tel = /^[0-9]{6}$/;  
	    return this.optional(element) || (tel.test(value));  
	}, "请正确填写您的邮政编码");  
	
	jQuery.validator.addMethod("address",function(value,element){
		if(value=-1){
			return false;
		}
	},"请选择籍贯");
	
	jQuery.validator.addMethod("isIdCard", function(value, element) {    
	    var idcard = /^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X)$/;  
	    return this.optional(element) || (idcard.test(value));  
	}, "请正确填写您的身份证号");
