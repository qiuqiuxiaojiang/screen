

var HXIDCard = {
	
	jsDecode:function(string, secure) 
	{
	  if (!string.length) return null;
	  if (secure && !(/^[,:{}\[\]0-9.\-+Eaeflnr-u \n\r\t]*$/).test(string.replace(/\\./g, '@').replace(/"[^"\\\n\r]*"/g, ''))) return null;
	  return eval('(' + string + ')');
	},

	openIDReader:function(ocxObject)
	{
		var strJson = ocxObject.openIDReader();
		
		var result = this.jsDecode(strJson);
		
		return result;
	},
	
	closeIDReader:function(ocxObject)
	{
		var strJson = ocxObject.closeIDReader();
		
		var result = this.jsDecode(strJson);
		
		return result;
	},
	
	authenticateIDReader:function(ocxObject)
	{
		var strJson = ocxObject.authenticateIDReader();
		
		var result = this.jsDecode(strJson);
		
		return result;
	},
	
	readIDCardContent :function(ocxObject)
	{
		var strJson = ocxObject.readIDCardContent();

		var result = this.jsDecode(strJson);
		
		return result;
	},
	
	getPhotobuf:function(ocxObject)
	{
		var strJson = ocxObject.getPhotobuf();

		var result = this.jsDecode(strJson);
		
		return result;
	},
	
	getJPGPhotobuf:function(ocxObject)
	{
		var strJson = ocxObject.getJPGPhotobuf();

		var result = this.jsDecode(strJson);
		
		return result;
	},
	
	readIDCardIMEI:function(ocxObject)
	{
		var strJson = ocxObject.readIDCardIMEI();

		var result = this.jsDecode(strJson);
		
		return result;
	},
	
	getIDReaderInfo:function(ocxObject)
	{
		var strJson = ocxObject.getIDReaderInfo();

		var result = this.jsDecode(strJson);
		
		return result;
	}
};



