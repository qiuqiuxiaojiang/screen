<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ include file="../main.jsp"%>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>随访计划列表</title>
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
		.btn-primary {
		    color: #fff;
		    background-color: #42a0d2;
		    border: none!important;
		}
	</style>
	<script  type="text/javascript">
		$(document).ready(function(){
			$("#mytable").createTable({
		        title:"随访计划列表",
		        url:"${ctx}/visit/visitPlanList.json",
		        multiChecked:false,
		        hasRowNumber:false,
		        params:{},
		        columns:[
		        	{field:"uniqueId", title:"编号",width:100},
		            {field:"age",title:"年龄",width:50},
		            {field:"gender",title:"性别",width:50},
		            {field:"visitTimes", title:"随访次数", formatter:function(val, row){
		            	if (val==null) {
		            		return "0";
		            	}
		            	var str="<a href='${ctx}/visit/showTrack.htm?uniqueId="+row.uniqueId+"' title='随访次数'>"+val+"</a>&nbsp;";
		            	return str;
		            }},
		            {field:"planDate",title:"计划随访时间", formatter:function(val, row){
		            	if (val==null) {
		            		return '';
		            	}
		            	var str = dateToString(val);
		            	return str;
		            }},
		            {field:"remindDate",title:"计划提醒时间", formatter:function(val, row){
		            	if (val==null) {
		            		return '';
		            	}
		            	var str = dateToString(val);
		            	return str;
		            }},
		            {field:"expireDate",title:"计划过期时间", formatter:function(val, row){
		            	if (val==null) {
		            		return '';
		            	}
		            	var str = dateToString(val);
		            	return str;
		            }},
		            {field:"visitStatus",title:"随访状态"}
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
				  <div class="table-assist" for="mytable">
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
<script type="text/javascript" src="${ctx_static}/device/HXIDCard.js"></script>
<script type="text/javascript">
function readCard() {
    var result = HXIDCard.readIDCardContent(MyObject);
    if ("1" == result.code) {
    	var idCard = result.content.cardNo;
    	$('#customerId').val(idCard);
    }
}
function dateToString(datestr){ 
	var mill = parseInt(datestr);
	var date = new Date(mill);
	  var year = date.getFullYear(); 
	  var month =(date.getMonth() + 1).toString(); 
	  var day = (date.getDate()).toString();  
	  if (month.length == 1) { 
	      month = "0" + month; 
	  } 
	  if (day.length == 1) { 
	      day = "0" + day; 
	  }
	  var dateTime = year + "-" + month + "-" + day;
	  return dateTime; 
	}
</script>
</html>
