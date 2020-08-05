<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ include file="../main.jsp"%>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>初筛数据历史记录</title>
    <link rel="stylesheet" type="text/css" href="${ctx_static}/css/component.css"></link>
 	<link rel="stylesheet" type="text/css" href="${ctx_static}/lightbox/lightbox.css"></link>
 	<link rel="stylesheet" type="text/css" href="${ctx_static}/zyUpload/zyUpload.css"></link>
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
		@media screen and (min-width:1340px) and (max-width: 1680px) {
			#mytable{
				width:150%!important;
				max-width: 150%!important;
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
		}
		
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
	var flag=true;
		$(document).ready(function(){
			$("#mytable").createTable({
		        title:"健康检测初筛数据列表",
		        url:"${ctx}/manage/hc/healthList.json",
		        multiChecked:false,
		        hasRowNumber:true,
		        params:{},
		        columns:[
	                 {field:"uniqueId",title:"编号",width:150,formatter:function(val,row){
			                var str="<a href='${ctx}/manage/hc/editHealthCheck.htm?id="+row.id+"' title='编号'>"+row.uniqueId+"</a>&nbsp;"; 
			                return str;
			            }},
		            /* {field:"customerId",title:"身份证号"}, */
		           /*  {field:"name",title:"姓名"}, */
		            {field:"gender",title:"性别"},
		            {field:"age",title:"年龄"},
		           /*  {field:"contact",title:"手机号"}, */
		            {field:"checkDate",title:"检查日期"},
		            {field:"height",title:"身高",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val;
		            }},
		            {field:"weight",title:"体重",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(2);
		            }},
		            {field:"BMI",title:"BMI",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(2);
		            }},
		            {field:"highPressure",title:"收缩压",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val;
		            }},
		            {field:"lowPressure",title:"舒张压",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val;
		            }},
		            {field:"pulse",title:"脉搏",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val;
		            }},
		            {field:"temperature",title:"体温",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(2);
		            }},
		            {field:"waistline",title:"腰围",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val;
		            }},
		            {field:"hipline",title:"臀围",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val;
		            }},
		            {field:"WHR",title:"腰臀比",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(2);
		            }},
		            {field:"oxygen",title:"血氧",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val;
		            }},
		            {field:"fatContent",title:"体脂率",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(2);
		            }},
		            {field:"bloodGlucose",title:"空腹血糖",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(2);
		            }},
		            {field:"bloodGlucose2h",title:"餐后2h血糖",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(2);
		            }},
		            {field:"bloodGlucoseRandom",title:"随机血糖",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(2);
		            }},
		            {field:"riskScore",title:"糖尿病危险因素评估分数",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val;
		            }},
		            {field:"tg",title:"甘油三酯"},
		           /*  {field:"tg",title:"甘油三酯",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(2);
		            }}, */
		            {field:"tc",title:"总胆固醇"},
		          /*   {field:"tc",title:"总胆固醇",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(2);
		            }}, */
		            {field:"hdl",title:"高密度脂蛋白胆固醇"},
		            {field:"ldl",title:"低密度脂蛋白胆固醇"},
		            {field:"eyeCheck",title:"眼象检测",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val;
		            }},
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
		            {field:"familyHistory",title:"家族史",formatter:function(val,row){
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
		            {field:"tizhi",title:"体质"},
		            {field:"district",title:"筛查地点"},
		            {field:"checkGroup",title:"筛查组"},
		            {field:"OGTTTest",title:"OGTT检测"},
		            {field:"bloodLipidTest",title:"血脂检测"},
		            {field:"bloodPressureTest",title:"血压检测"},
		            {field:"geneTest",title:"基因检测"},
		            {field:"bloodSugarCondition",title:"血糖情况"},
		            {field:"bloodLipidCondition",title:"血脂情况"},
		            {field:"bloodPressureCondition",title:"血压情况"},
		            {field:"classifyResult",title:"人群分类结果"}
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
				<div class="table-assist" for="mytable"  style="text-align: right">
				    <div class="search-panel" style="text-align: right">
		         		<label>身份证：</label><input type="text" name="customerId" id="customerId" />
			            <input type="button" class="btn btn-primary" value="读取身份证" onclick="javascript:readCard()">
			            <label>姓名：</label><input type="text" name="customerName" id="customerName" />
			            <input type="button" class="btn btn-danger btn-sm" value="查询" onclick="javascript:search();" />
<!-- 		            	<p style="float: right;display:inline-block;">
		            		<input id="uploadBnt" type="button" class="btn btn-sm btn-success" data-toggle="modal" data-target="#myModal" value="上传文件"  onclilk='upFile(event);'/>
		            		提示：请下载<a href="${ctx_static }/初筛模板.xlsx">模板</a>，填写有效数据
		            	</p>
		            	 -->
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
<script type="text/javascript" src="${ctx_static}/pagenation/pagenation.js"></script>
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
