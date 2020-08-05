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
	<style type="text/css">
		.delModule,#batchDel{
			cursor:pointer;
		}
		table td{
			text-align:center;
		}
	</style>
	<script  type="text/javascript">
	var flag=true;
		$(document).ready(function(){
			$("#mytable").createTable({
		        title:"上传文件列表",
		        url:"${ctx}/fileUploadRecord/fileList.json",
		        multiChecked:false,
		        hasRowNumber:true,
		        params:{},
		        columns:[
		            {field:"fileName",title:"文件名", formatter:function(val,row){
		        		var fileUrl=row.fileUrl;
		        		var fileName=row.fileName;
		        		if (fileName!=null && fileName!='') {
			               // var str="<a href='javascript:void(0);' title='初筛数据'  onclick='downFile(\""+fileUrl+"\",\""+fileId+"\")'>"+fileName+"</a>&nbsp;"; 
			                var str="<a href='" + fileUrl + "'>"+fileName+"</a>&nbsp;"; 
			                return str;
		        		}
		        		return '';
		        	}},
		            {field:"dataType",title:"数据类型"},
		            {field:"state",title:"状态"},
		            {field:"startTime",title:"开始时间"},
		            {field:"endTime",title:"结束时间"},
		            {field:"fileName",title:"错误输出文件", formatter:function(val,row){
		        		var errFileId=row.errFileId;
		        		var errFileUrl=row.errFileUrl;
		        		if (errFileId!=null && errFileId!='') {
			                var str="<a href='" + errFileUrl + "'>"+errFileId+"</a>&nbsp;"; 
			                return str;
		        		}
		        		return '';
		        	}}
		        ],
		        pagenation:true
		    });
		});
		
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
<script type="text/javascript" src="${ctx_static}/pagenation/pagenation.js"></script>


</html>
