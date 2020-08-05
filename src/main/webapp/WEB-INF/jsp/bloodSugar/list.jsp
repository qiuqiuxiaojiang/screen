<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ include file="../main.jsp"%>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>社区健康筛查数据采集与管理系统</title>
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
		.pull-right {
		    margin-top: 2%;
		}
		@media screen and (min-width:1340px) and (max-width: 1680px) {
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
		@media screen and (max-width:1060px) {
			#mytable{
				width:230%!important;
				max-width: 230%!important;
			}
		}
	</style>
	<script  type="text/javascript">
		$(document).ready(function(){
			$("#mytable").createTable({
		        title:"客户信息列表",
		        url:"${ctx}/manage/bs/bsList.json",
		        multiChecked:true,
		        hasRowNumber:true,
		        params:{},
		        columns:[
					{field:"cardId",title:"身份证号",width:200,sorting:true},
		            {field:"bloodSugar",title:"空腹血糖",width:150,sorting:true},
		            {field:"hdl",title:"高密度脂蛋白",width:200,sorting:true},
		            {field:"tg",title:"甘油三酯",width:150,sorting:true},
		            {field:"tc",title:"总胆固醇",width:300,sorting:true},
		            {field:"ldl",title:"低密度脂蛋白",width:190,sorting:true},
		            {field:"testTime",title:"测试时间戳",width:150,sorting:true,formatter:function(val){
		            	var date=new Date(val);
		            	return date.toLocaleString();
		            }},
		            {field:"deviceId",title:"设备编号",width:150,sorting:true},
		            {field:"chipId",title:"芯片编号",width:300,sorting:true},
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
