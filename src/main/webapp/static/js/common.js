function StringBuffer() {
    this.__strings__ = new Array;
}

StringBuffer.prototype.append = function (str) {
    this.__strings__.push(str);
    return this.__strings__;
};

StringBuffer.prototype.toString = function () {
    return this.__strings__.join("");
};

function trim(str,is_global){ 
	 var result; 
	 result = str.replace(/(^\s+)|(\s+$)/g,""); 
	 if(is_global.toLowerCase()=="g") 
	 result = result.replace(/\s/g,""); 
	 return result; 
}	

function arrToStr(arr,separator){
	if(arr==null || arr=="null" || arr.length==0){
		return "";
	}else{
		var str="";
		for(var i in arr){
			str+=arr[i]+separator;
		}
		str=str.substring(0,str.length-1);
		return str;
	}
	
}

function strToArr(str,separator){
	if(str==null || str=="null" || str=="" || str.length==0){
		return "";
	}else{
		var arr = new Array();
		arr=str.split(separator);
		return arr;
	}
	
}
