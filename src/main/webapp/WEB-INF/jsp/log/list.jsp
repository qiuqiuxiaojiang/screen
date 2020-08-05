<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ include file="../main.jsp"%>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>日志记录</title>
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
		        title:"日志信息列表",
		        url:"${ctx}/manage/log/logList.json",
		        multiChecked:true,
		        hasRowNumber:true,
		        params:{},
		        columns:[
		            {field:"username", title:"操作用户",sorting:true},
		            {field:"ip",title:"IP",width:200,sorting:true},
		            {field:"uri",title:"请求地址",width:150,sorting:true},
		            {field:"ctime",title:"时间",width:250,formatter:function(val){
		            	var date=new Date(val);
		            	return date.Format("yyyy-MM-dd hh:mm:ss");
		            }}
		        ],
		        pagenation:true
		    });
		});
		
		function search(){
			var method = $('#method').val();
			var ip = $('#ip').val();
			var uri = $('#uri').val();
			$("#mytable").createTable({
				params:{method:method,ip:ip,uri:uri}
			});
		}
		Date.prototype.Format = function (fmt) { //author: meizz 
		    var o = {
		        "M+": this.getMonth() + 1, //月份 
		        "d+": this.getDate(), //日 
		        "h+": this.getHours(), //小时 
		        "m+": this.getMinutes(), //分 
		        "s+": this.getSeconds(), //秒 
		        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
		        "S": this.getMilliseconds() //毫秒 
		    };
		    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
		    for (var k in o)
		    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
		    return fmt;
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
				        	<label>ip：</label><input type="text" name="ip" id="ip" />
				            <label>方法：</label><input type="text" name="method" id="method" style="width:25%;"/>
				            <label>请求地址：</label><input type="text" name="uri" id="uri" style="width:25%;"/>
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
