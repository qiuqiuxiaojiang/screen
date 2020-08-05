<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ include file="../main.jsp"%>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>问卷历史记录</title>
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
		        title:"问卷历史数据列表",
		        url:"${ctx}/question/questionList.json",
		        multiChecked:false,
		        hasRowNumber:true,
		        params:{},
		        columns:[
	                {field:"uniqueId",title:"编号", width:180},
	                {field:"age",title:"年龄", width:200},
		            {field:"gender",title:"性别", width:200},
		            {field:"checkDate",title:"问卷调查日期", width:260,formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val;
		            }},
		            {field:"classifyResult",title:"操作",formatter:function(val,row){
		                var str="<a href='${ctx}/question/editQuestionUI.htm?id="+row.id+"' title='查看问卷信息'>查看问卷信息</a>&nbsp;"; 
		                return str;
		            }},
		        ],
		        pagenation:true
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
			        </div> 
				  </div>
					<table id="mytable" class="table table-bordered"></table>
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
