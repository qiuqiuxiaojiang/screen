var idCardNoUtil = {
provinceAndCitys: {11:"北京",12:"天津",13:"河北",14:"山西",15:"内蒙古",21:"辽宁",22:"吉林",23:"黑龙江",
31:"上海",32:"江苏",33:"浙江",34:"安徽",35:"福建",36:"江西",37:"山东",41:"河南",42:"湖北",43:"湖南",44:"广东",
45:"广西",46:"海南",50:"重庆",51:"四川",52:"贵州",53:"云南",54:"西藏",61:"陕西",62:"甘肃",63:"青海",64:"宁夏",
65:"新疆",71:"台湾",81:"香港",82:"澳门",91:"国外"},


powers: ["7","9","10","5","8","4","2","1","6","3","7","9","10","5","8","4","2"],


parityBit: ["1","0","X","9","8","7","6","5","4","3","2"],


genders: {male:"男",female:"女"},


checkAddressCode: function(addressCode){
var check = /^[1-9]\d{5}$/.test(addressCode);
if(!check) return false;
if(idCardNoUtil.provinceAndCitys[parseInt(addressCode.substring(0,2))]){
return true;
}else{
return false;
}
},


checkBirthDayCode: function(birDayCode){
var check = /^[1-9]\d{3}((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))$/.test(birDayCode);
if(!check) return false;
var yyyy = parseInt(birDayCode.substring(0,4),10);
var mm = parseInt(birDayCode.substring(4,6),10);
var dd = parseInt(birDayCode.substring(6),10);
var xdata = new Date(yyyy,mm-1,dd);
if(xdata > new Date()){
return false;//生日不能大于当前日期
}else if ( ( xdata.getFullYear() == yyyy ) && ( xdata.getMonth () == mm - 1 ) && ( xdata.getDate() == dd ) ){
return true;
}else{
return false;
}
},


getParityBit: function(idCardNo){
var id17 = idCardNo.substring(0,17);

var power = 0;
for(var i=0;i<17;i++){
power += parseInt(id17.charAt(i),10) * parseInt(idCardNoUtil.powers[i]);
}

var mod = power % 11;
return idCardNoUtil.parityBit[mod];
},


checkParityBit: function(idCardNo){
var parityBit = idCardNo.charAt(17).toUpperCase();
if(idCardNoUtil.getParityBit(idCardNo) == parityBit){
return true;
}else{
return false;
}
},


getAge:function (identityCard) {
    var len = (identityCard + "").length;
    if (len == 0) {
        return 0;
    } else {
        if ((len != 15) && (len != 18))//身份证号码只能为15位或18位其它不合法
        {
            return 0;
        }
    }
    var strBirthday = "";
    if (len == 18)//处理18位的身份证号码从号码中得到生日和性别代码
    {
        strBirthday = identityCard.substr(6, 4) + "/" + identityCard.substr(10, 2) + "/" + identityCard.substr(12, 2);
    }
    if (len == 15) {
        strBirthday = "19" + identityCard.substr(6, 2) + "/" + identityCard.substr(8, 2) + "/" + identityCard.substr(10, 2);
    }
    //时间字符串里，必须是“/”
    var birthDate = new Date(strBirthday);
    var nowDateTime = new Date();
    var age = nowDateTime.getFullYear() - birthDate.getFullYear();
    //再考虑月、天的因素;.getMonth()获取的是从0开始的，这里进行比较，不需要加1
    if (nowDateTime.getMonth() < birthDate.getMonth() || (nowDateTime.getMonth() == birthDate.getMonth() && nowDateTime.getDate() < birthDate.getDate())) {
        age--;
    }
    return age;
},

getSex:function (identityCard) {
	if (parseInt(identityCard.substr(16, 1)) % 2 == 1) {
		$("#man").attr("checked","checked");
	} else { 
		$("#woman").attr("checked","checked");
	} 
},

checkIdCardNo: function(idCardNo){
//15位和18位身份证号码的基本校验
var check = /^\d{15}|(\d{17}(\d|x|X))$/.test(idCardNo);
if(!check) return false;
//判断长度为15位或18位
if(idCardNo.length==15){
	var trueOrFlase = idCardNoUtil.check15IdCardNo(idCardNo);
	if(trueOrFlase){
		var age = idCardNoUtil.getAge(idCardNo)
		idCardNoUtil.getSex(idCardNo)
		$("#age").val(age)
		return trueOrFlase;
	}else{
		return trueOrFlase;
	}
	//return idCardNoUtil.check15IdCardNo(idCardNo);
}else if(idCardNo.length==18){
	var trueOrFlase = idCardNoUtil.check18IdCardNo(idCardNo);
	if(trueOrFlase){
		var age = idCardNoUtil.getAge(idCardNo)
		idCardNoUtil.getSex(idCardNo)
		$("#age").val(age)
		return trueOrFlase;
	}else{
		return trueOrFlase;
	}
	//return idCardNoUtil.check18IdCardNo(idCardNo);
}else{
return false;
}
},

//校验15位的身份证号码
check15IdCardNo: function(idCardNo){
//15位身份证号码的基本校验
var check = /^[1-9]\d{7}((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))\d{3}$/.test(idCardNo);
if(!check) return false;
//校验地址码
var addressCode = idCardNo.substring(0,6);
check = idCardNoUtil.checkAddressCode(addressCode);
if(!check) return false;
var birDayCode = '19' + idCardNo.substring(6,12);
//校验日期码
return idCardNoUtil.checkBirthDayCode(birDayCode);
},

//校验18位的身份证号码
check18IdCardNo: function(idCardNo){
//18位身份证号码的基本格式校验
var check = /^[1-9]\d{5}[1-9]\d{3}((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))\d{3}(\d|x|X)$/.test(idCardNo);
if(!check) return false;
//校验地址码
var addressCode = idCardNo.substring(0,6);
check = idCardNoUtil.checkAddressCode(addressCode);
if(!check) return false;
//校验日期码
var birDayCode = idCardNo.substring(6,14);
check = idCardNoUtil.checkBirthDayCode(birDayCode);
if(!check) return false;
//验证校检码
return idCardNoUtil.checkParityBit(idCardNo);
},

formateDateCN: function(day){
var yyyy =day.substring(0,4);
var mm = day.substring(4,6);
var dd = day.substring(6);
return yyyy + '-' + mm +'-' + dd;
},

//获取信息
getIdCardInfo: function(idCardNo){
var idCardInfo = {
gender:"", //性别
birthday:"" // 出生日期(yyyy-mm-dd)
};
if(idCardNo.length==15){
var aday = '19' + idCardNo.substring(6,12);
idCardInfo.birthday=idCardNoUtil.formateDateCN(aday);
if(parseInt(idCardNo.charAt(14))%2==0){
idCardInfo.gender=idCardNoUtil.genders.female;
}else{
idCardInfo.gender=idCardNoUtil.genders.male;
}
}else if(idCardNo.length==18){
var aday = idCardNo.substring(6,14);
idCardInfo.birthday=idCardNoUtil.formateDateCN(aday);
if(parseInt(idCardNo.charAt(16))%2==0){
idCardInfo.gender=idCardNoUtil.genders.female;
}else{
idCardInfo.gender=idCardNoUtil.genders.male;
}

}
return idCardInfo;
},


getId15:function(idCardNo){
if(idCardNo.length==15){
return idCardNo;
}else if(idCardNo.length==18){
return idCardNo.substring(0,6) + idCardNo.substring(8,17);
}else{
return null;
}
},


getId18: function(idCardNo){
if(idCardNo.length==15){
var id17 = idCardNo.substring(0,6) + '19' + idCardNo.substring(6);
var parityBit = idCardNoUtil.getParityBit(id17);
return id17 + parityBit;
}else if(idCardNo.length==18){
return idCardNo;
}else{
return null;
}
}
};
//验证护照是否正确
function checknumber(number){
var str=number;
//在JavaScript中，正则表达式只能使用"/"开头和结束，不能使用双引号
var Expression=/(P\d{7})|(G\d{8})/;
var objExp=new RegExp(Expression);
if(objExp.test(str)==true){
   return true;
}else{
   return false;
} 
};