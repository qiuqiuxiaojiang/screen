<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ include file="../main.jsp"%>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>登录信息</title>
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
		        url:"${ctx}/loginlog/logList.json",
		        multiChecked:true,
		        hasRowNumber:true,
		        params:{},
		        columns:[
		            {field:"username",title:"用户名",width:300,sorting:true},
		            {field:"logintime",title:"登录时间",width:300,sorting:true},
		            {field:"loginState",title:"登录状态",width:300,sorting:true,formatter:function(val){
		            	if(val == "success") {
		            		val = "成功";
		            	} else if (val == "fail") {
		            		val = "失败";
		            	}
		            	return val;
		            }},
		            {field:"loginip",title:"IP",width:300,sorting:true},
		            {field:"sessionId",title:"sessionId",width:300,sorting:true}
		        ],
		        pagenation:true
		    });
		});
		
		function search(){
			var username=$('#username').val();
			$("#mytable").createTable({
				params:{username:username}
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
					<div class="table-assist" for="mytable"  style="text-align: right">
				        <div class="search-panel">
				            <label>用户名：</label><input type="text" name="username" id="username" />
				            <input type="button" class="btn btn-danger btn-sm" value="查询" onclick="javascript:search();" />
				        </div>
				    </div> 
					<table id="mytable" class="table table-bordered"></table>
				</div>
			</div>
		</div>
	</div>


</body>
</html>
