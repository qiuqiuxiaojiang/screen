<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ include file="../main.jsp"%>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>个人健康档案</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/component.css"></link>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/pagenation/pagenation.css"></link>
    <script src="${ctx_jquery }/jquery.form.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/component.js"></script>
	<script src="${ctx}/static/pagenation/pagenation.js"></script>
	
	<style type="text/css">
		.delModule,#batchDel{
			cursor:pointer;
		}
		table td{
			text-align:center;
		}
	</style>
	<script  type="text/javascript">
		$(document).ready(function(){
			$("#mytable").createTable({
		        title:"客户信息列表",
		        url:"${ctx}/manage/push/pushList.json",
		        multiChecked:true,
		        hasRowNumber:true,
		        params:{},
		        columns:[
		            {field:"pkId",title:"唯一标识",width:200,sorting:true},
		            {field:"code",title:"返回代码",width:150,sorting:true,formatter:function(val){
		            	if (val == 0) {
		            		val = "0";
		            	}
		            	return val;
		            }},
		            {field:"tradingType",title:"数据类型",width:150,sorting:true,formatter:function(val){
		            	if(val == "1") {
		            		val = "一体机补录";
		            	} else if (val == "2") {
		            		val = "居家检";
		            	} else if (val == "3"){
		            		val = "眼检";
		            	}
		            	return val;
		            }},
		            {field:"success",title:"是否成功",width:100,sorting:true,formatter:function(val){
		            	if (val == false) {
		            		val = "false";
		            	} else if (val == true) {
		            		val = "true";
		            	}
		            	return val; 
		            }},
		            {field:"sysTime",title:"请求时间戳",width:200,sorting:true,formatter:function(val){
		            	var date=new Date(val);
		            	return date.toLocaleString();
		            }},
		            {field:"testTime",title:"测试时间戳",width:150,sorting:true,formatter:function(val){
		            	var date=new Date(val);
		            	return date.toLocaleString();
		            }},
		            {field:"httpCode",title:"返回状态",width:150,sorting:true},
		            {field:"message",title:"返回信息",width:300,sorting:true}/* ,
		            {field:"fileId",title:"操作",width:150,formatter:function(val,row){
						var str='<a href="javascript:;" onclick="delSample(this)" id="'+row.sid+'" title="删除"><span class="glyphicon glyphicon-remove" aria-hidden="true"></a>'; 
		                return str;
		            }} */
		        ],
		        pagenation:true
		    });
		});
		
		function search(){
			var sampleId=$('#sampleId').val();
			var name=$('#name').val();
			$("#mytable").createTable({
				params:{sampleId:sampleId,name:name}
			});
		}
	</script>
</head>
<body>
	<div class="container-fluid">
		<!-- 以下这是页面需要编辑的区域 -->
		<div class="row">
			<div>
				<div>
					<table id="mytable" class="table table-bordered"></table>
					
				</div>
			</div>
		</div>
	</div>


</body>
</html>
