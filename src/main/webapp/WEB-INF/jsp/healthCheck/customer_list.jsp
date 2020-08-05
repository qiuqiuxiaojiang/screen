<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ include file="../main.jsp"%>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>筛查用户列表</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/component.css"></link>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/pagenation/pagenation.css"></link>
    <script src="${ctx_jquery }/jquery.form.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/component.js"></script>
	<script src="${ctx}/static/pagenation/pagenation.js"></script>
	<script src="${ctx_jquery }/card.js"></script>
	
	<style type="text/css">
		.delModule,#batchDel{
			cursor:pointer;
		}
		table td{
			text-align:center;
		}
		.pull-right {
		    margin-top: 2%;
		}
		/* @media screen and (min-width:1340px) and (max-width: 1680px) {
			#mytable{
				width:120%!important;
				max-width: 120%!important;
			}
		}
		
		@media screen and (max-width:1340px) {
			#mytable{
				width:200%!important;
				max-width: 200%!important;
			}
		}
		@media screen and (max-width:2100px) {
			#mytable{
				width:240%!important;
				max-width: 240%!important;
			}
		} */
		.modal-dialog {
		    width: 600px;
		    margin: 15% auto;
		}
		.modal-body {
		    width:100%;
		    min-height:8em;
		}
		#uploadFile{
			width:20%;
		}
		.modal-footer {
		    text-align: center;
		}
		.btn-primary {
		    color: #fff;
		    background-color: #42a0d2;
		    border: none!important;
		}
	</style>
	<script  type="text/javascript">
		$(document).ready(function(){
			$("#mytable").createTable({
		        title:"客户信息列表",
		        url:"${ctx}/manage/hc/customerList.json",
		        multiChecked:false,
		        hasRowNumber:false,
		        params:{},
		        columns:[
		        	{field:"uniqueId", title:"编号",width:100},
		            {field:"age",title:"年龄",width:50},
		            {field:"gender",title:"性别",width:50},
		            /* {field:"birthday",title:"出生日期",width:100}, */
		            {field:"recordDate",title:"建档日期",width:100},
		        	/* {field:"", title:"初筛信息", width:50, formatter:function(val,row){
		        		var healthCheckId=row.healthCheckId;
		        		if (healthCheckId!=null && healthCheckId!='') {
			                var str="<a href='${ctx}/manage/hc/editHealthCheck.htm?id="+healthCheckId+"' title='初筛信息'>初筛信息</a>&nbsp;"; 
			                return str;
		        		}
		        		return '';
		        	}},
		        	{field:"", title:"精筛信息", width:100, formatter:function(val,row){
		        		var healthCheckDetailId=row.healthCheckDetailId;
		        		if (healthCheckDetailId!=null && healthCheckDetailId!='') {
			                var str="<a href='${ctx}/manage/hc/editHealthCheckDetail.htm?id="+healthCheckDetailId+"' title='精筛信息'>精筛信息</a>&nbsp;"; 
			                return str;
		        		}
		        		return '';
		        	}},
		        	{field:"", title:"其他信息", width:100, formatter:function(val,row){
		        		var otherInfoId=row.otherInfoId;
		        		if (otherInfoId!=null && otherInfoId!='') {
			                var str="<a href='${ctx}/otherInfo/editOtherInfo.htm?id="+otherInfoId+"' title='其他信息'>其他信息</a>&nbsp;"; 
			                return str;
		        		}
		        		return '';
		        	}},
		            {field:"checkDate",title:"检查日期",width:100},
		            {field:"haveDisease",title:"疾病史",formatter:function(val,row){
		            	if (val==null) {
		            		return "";
		            	}
		            	if(val=='是') {
		            		var disease = row.disease;
		            		return disease;
		            	}else if (val=='否'){
			            	return '否';
		            	} else {
		            		return '';
		            	}
		            }},
		            {field:"familyHistory",title:"家族史",width:70,formatter:function(val,row){
		            	if (val==null) {
		            		return "";
		            	}
		            	if(val=='是') {
		            		var familyDisease = row.familyDisease;
		            		return familyDisease;
		            	}else if (val=='否'){
			            	return '否';
		            	} else {
		            		return '';
		            	}
		            }},
		            {field:"height",title:"身高cm",width:80,formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val;
		            }},
		            {field:"weight",title:"体重kg",width:80,formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(2);
		            }},
		            {field:"BMI",title:"体质指数（BMI）",width:100,formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(2);
		            }},
		            {field:"waistline",title:"腰围cm",width:80,formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val;
		            }},
		            {field:"highPressure",title:"收缩压mmHg",width:60,formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val;
		            }},
		            {field:"lowPressure",title:"舒张压mmHg",width:100,formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val;
		            }},
		            {field:"pulse",title:"脉率（次/分钟）",width:100,formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val;
		            }},
		            {field:"temperature",title:"体温℃",width:70,formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(2);
		            }},
		            {field:"fatContent",title:"体脂率%",width:80,formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(2);
		            }},
		            {field:"bloodGlucose",title:"空腹血糖（mmo/L）",width:100,formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(2);
		            }},
		            {field:"bloodGlucose2h",title:"餐后2h血糖（mmo/L）",width:100,formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(2);
		            }},
		            {field:"tcStr",title:"总胆固醇",width:85},
		            {field:"tgStr",title:"甘油三酯",width:85},
		            {field:"hdlStr",title:"高密度脂蛋白",width:120},
		            {field:"ldlStr",title:"低密度脂蛋白",width:120},
		            {field:"tizhi",title:"中医体质辨识结果",width:145},
		            {field:"eyeCheck",title:"中医眼象辨识",width:120,formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val;
		            }},
		            {field:"riskScore",title:"糖尿病危险因素评估分数",width:120,formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val;
		            }},
		            {field:"bloodSugarCondition",title:"血糖情况",width:100},
		            {field:"bloodLipidCondition",title:"血脂情况",width:100},
		            {field:"bloodPressureCondition",title:"血压情况",width:100},
		            {field:"classifyResult",title:"初筛人群分类结果",width:100},
		            {field:"nationality",title:"民族",width:50},
		            {field:"householdRegistrationType",title:"常驻类型",width:100},
		            {field:"district",title:"筛查区域",width:100},
		            {field:"checkPlace",title:"筛查地点",width:100},
		            {field:"checkGroup",title:"筛查组",width:100} */
		        	{field:"", title:"操作", width:400, formatter:function(val,row){
		        		var uniqueId = row.uniqueId;
		        		var url = "<a href='${ctx}/screenTral/screenTralList.htm?uniqueId="+uniqueId+"' title='查看筛查轨迹'>查看筛查轨迹</a>&nbsp;&nbsp;";
		        		url += "<a href='${ctx}/manage/hc/showDocument.htm?uniqueId="+uniqueId+"' title='查看建档信息'>查看建档信息</a>&nbsp;&nbsp;";
		        		var hcId = row.healthCheckId;
		        		if (hcId!=null && hcId!='') {
		        			url += "<a href='${ctx}/manage/hc/showHealthCheckUI.htm?id="+hcId+"' title='查看初筛信息'>查看初筛信息</a>&nbsp;&nbsp;";
		        		} else {
		        			url += "<a href='javascript:noShow(\"您目前还未进行初筛信息检测！\")' title='查看初筛信息'>查看初筛信息</a>&nbsp;&nbsp;";
		        		}
		        		var healthCheckDetailId=row.healthcheckDetailId;
		        		if (healthCheckDetailId!=null && healthCheckDetailId!='') {
			                url += "<a href='${ctx}/manage/hc/showHealthCheckDetailUI.htm?id="+healthCheckDetailId+"' title='查看精筛信息'>查看精筛信息</a>&nbsp;&nbsp;";
		        		} else {
		        			url += "<a href='javascript:noShow(\"您目前还未进行精筛信息检测！\")' title='查看精筛信息'>查看精筛信息</a>&nbsp;&nbsp;";
		        		}
		        		
		        		//基因检测
		        		//url+="<a href='javascript:checkData(\"gene\","+uniqueId+",\"${ctx}/gene/editGene.htm?uniqueId="+uniqueId+"\")' title='查看基因检测信息'>查看基因检测信息</a>&nbsp;&nbsp;";
		        		
		        		var eyeCheck = row.eyeCheck;
		        		if (eyeCheck!=null && eyeCheck!='' && eyeCheck == '已检测') {
		        			url+="<a href='${ctx}/eyeRecord/viewRecord/"+uniqueId+".htm' title='查看目诊信息'>查看目诊信息</a>&nbsp;&nbsp;";
		        		} else {
		        			url += "<a href='javascript:noShow(\"您目前还未进行眼像检测！\")' title='查看目诊信息'>查看目诊信息</a>&nbsp;&nbsp;";
		        		}
		        		
		        		var gene = row.gene;
		        		if (gene!=null && gene!='' && gene == 'true') {
		        			url+="<a href='${ctx}/gene/showGene.htm?uniqueId="+uniqueId+"' title='查看基因检测信息'>查看基因检测信息</a>&nbsp;&nbsp;";
		        		} else {
		        			url += "<a href='javascript:noShow(\"您目前还未进行基因检测！\")' title='查看基因检测信息'>查看基因检测信息</a>&nbsp;&nbsp;";
		        		}
		        		//url+="<a href='javascript:checkData(\"visit\","+uniqueId+",\"${ctx}/manage/hc/showHealthCheckUI.htm?uniqueId="+uniqueId+"\")' title='查看随访信息'>查看随访信息</a>&nbsp;&nbsp;";
		        		return url;
		        	}}
		        
		        ],
		        pagenation:true
		    });
			
			
			$("#uploadFile").click(function () {
				$("#uploadFile").hide();
	  			$("#uploadFile2").show();
			    var options = {
				 	url : "${ctx }/healthcheck/uploadFile.json",
					success : function(o) {
						if (o.code == 0) {
							$("#uploadFile").val("上传");
							alert("上传成功");
							window.location.href="${ctx}/manage/hc/list.htm";
						} else {
							$("#uploadFile").hide();
				  			$("#uploadFile2").show();
							alert("上传失败");
						} 
					 }
			  	};
			  	$("#uploadForm").ajaxForm(options);				
			}); 
		});
		
		function search(){
			var customerId=$('#customerId').val();
			var customerName=$('#customerName').val();
			$("#mytable").createTable({
				params:{
					customerId:customerId,
					customerName:customerName
				}
			});
		}
		
		function checkData(param, uniqueId, url) {
			$.ajax({
				type : "post",
				url : "${ctx}/manage/hc/checkCustomerData.json",
				data : {
					param:param,
					uniqueId:uniqueId
				},
				success:function(data){
					if(data.code==0){
						window.location.href=url;
					} else {
						alert(data.message);
					}
				}
			});
		}
		
		function noShow(msg) {
			alert(msg);
		}
	</script>
</head>
<body>
<div style="display:none">
<object id="MyObject" classid="clsid:AD6DB163-16C6-425F-97DA-CC28F30F249A" codebase="HXIDCard.CAB#version=1,0,0,1" width=0 height=0></object>
</div>
	<div class="container-fluid">
		<!-- 以下这是页面需要编辑的区域 -->
		<div class="row">
			<div>
				<div>
				  <div class="table-assist" for="mytable">
			         <div class="search-panel" style="text-align: right">
		         		<label>身份证：</label><input type="text" name="customerId" id="customerId" />
			            <input type="button" class="btn btn-primary" value="读取身份证" onclick="javascript:readCard()">
			            <label>姓名：</label><input type="text" name="customerName" id="customerName" />
			            <input type="button" class="btn btn-danger btn-sm" value="查询" onclick="javascript:search();" />
<!-- 		            	<p style="float: right;display:inline-block;">
		            		<input id="uploadBnt" type="button" class="btn btn-sm btn-success" data-toggle="modal" data-target="#myModal" value="上传文件"  onclilk='upFile(event);'/>
		            		提示：请下载<a href="${ctx_static }/初筛模板.xlsx">模板</a>，填写有效信息
		            	</p> -->
			        </div> 
				  </div>
					<table id="mytable" class="table table-bordered"></table>
				</div>
			</div>
		</div>
	</div>
	
	
	<!-- 模态框（Modal） -->
	<div class="modal fade" id="myModal" data-backdrop="static"  tabindex="-1"  role="dialog" aria-labelledby="myModalLabel"  aria-hidden="true">
	
	   <div class="modal-dialog">
	      <div class="modal-content">
	         <div class="modal-header">
	            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
	                  &times;
	            </button>
	            <h4 class="modal-title" id="myModalLabel">
	             	  <b>文件上传</b>
	            </h4>
	         </div>
	         <div class="modal-body" id="uploadInner">
	         	<form id="uploadForm" action="" method="post" enctype="multipart/form-data" style="width:100%;text-align:left;float:left;">
						    <div class="btn-panel">
					            <label style="display:block">上传人群文件：</label>
					            <input type="file" id="file" name="file" style="width:45%;display:inline-block;margin-left:15%"/>
					            <input type="submit" id="uploadFile" class="btn btn-primary"  value="上传">
					            <input class="btn btn-primary" type="button" id="uploadFile2" value="上传中" disabled="disabled" style="display: none;width: 20%;">
					            
					        </div>
			        	</form>
	         
	         </div>
	         <div class="modal-footer">
	            <button type="button" class="btn btn-primary" data-dismiss="modal" style="width:20%">
	            	关闭
	            </button>
	         </div>
	      </div>
		</div>
   	</div>
</body>
<script type="text/javascript" src="${ctx_static}/device/HXIDCard.js"></script>
<script type="text/javascript">
function readCard() {
    var result = HXIDCard.readIDCardContent(MyObject);
    if ("1" == result.code) {
    	var idCard = result.content.cardNo;
    	$('#customerId').val(idCard);
    }
}
</script>
</html>
