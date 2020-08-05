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
		@media screen and (min-width:1340px) and (max-width: 1680px) {
			#mytable{
				width:120%!important;
				max-width: 120%!important;
			}
		}
		
		@media screen and (max-width:900px) {
			#mytable{
				width:200%!important;
				max-width: 200%!important;
			}
		}
	/* 	@media screen and (max-width:900px) {
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
		        title:"数据管理",
		        url:"${ctx}/manage/hc/dataListByPage.json",
		        multiChecked:false,
		        hasRowNumber:false,
		        params:{},
		        columns:[
		        	{field:"uniqueId", title:"编号",width:50},
		        	{field:"gender",title:"性别",width:50},
		            {field:"checkDate",title:"检查日期",width:100},
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
		            {field:"birthday",title:"出生日期",width:100},
		            {field:"tizhi",title:"体质",width:100},
		            {field:"familyHistory",title:"家族病史",width:70,formatter:function(val,row){
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
		            {field:"haveDisease",title:"既往病史",width:70,formatter:function(val,row){
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
			var item = $("#item").val();
			var district=$('#' + item + 'district').val();
			var startTime=$('#startTime').val();
			var endTime=$('#endTime').val();
			$("#mytable").createTable({
				params:{
					district:district,
					startTime:startTime,
					endTime:endTime
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
				<input type="hidden" id="item" value="${item}">
				  <div class="table-assist" for="mytable">
			         <div class="search-panel" style="text-align: right">
			          	<label>筛查地点：</label>
			          	<c:if test="${item == 'fuxin' }">
				          	<select id="fuxindistrict">
				          		<option value="">阜新市</option>
				          		<option value="海州区">阜新市海州区</option>
				          		<option value="阜蒙县">阜新市阜蒙县</option>
				          		<option value="彰武县">阜新市彰武县</option>
				          	</select>
			          	</c:if>
			          	
			          	<c:if test="${item == 'kunming' }">
				          	<select id="kunmingdistrict">
				          		<option value="盘龙区">昆明市盘龙区</option>
				          	</select>
			          	</c:if>
			          	
			          	<label>筛查时间：</label>
			          	<input class="Wdate" id="startTime" name="startTime" readonly type="text" value="${startTime}"
											onClick="WdatePicker({el:this,dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d'})">—
						<input class="Wdate" id="endTime" name="endTime" readonly type="text" value="${endTime}"
											onClick="WdatePicker({el:this,dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d'})">
			          	
			            <input type="button" class="btn btn-danger btn-sm" value="查询" onclick="javascript:search();" />
			            <input type="button" class="btn btn-danger btn-sm" value="下载检索数据" id="downloadData"/>
			        </div> 
				  </div>
					<table id="mytable" class="table table-bordered"></table>
				</div>
			</div>
		</div>
	</div>
	
</body>
<script language="javascript" type="text/javascript" src="${ctx_static}/My97DatePicker/WdatePicker.js"></script>

<script type="text/javascript">
	$("#downloadData").click(function(){
		$("#downloadData").prop("value", "下载中");
		$("#downloadData").prop("disabled", true);
		
		 var item = $("#item").val();
		 var startTime = $("#startTime").val();
		 var endTime = $("#endTime").val();
		 var district = $("#" + item + "district").val();
	     $.ajax({
	      	url:"${ctx}/manage/hc/downloadHealthCheck.json",
	      	type:"POST",
	      	data:{startTime:startTime, endTime:endTime, district:district},
	      	success:function(msg){
	      		$("#downloadData").prop("value", "下载检索数据");
	    		$("#downloadData").prop("disabled", false);
	      		if (msg.data == 'error') {
	      			alert("暂无相应数据");
	      		} else {
	      			window.open(msg.data);
	      		}
	      	}
	    }); 
	});

</script>
</html>
