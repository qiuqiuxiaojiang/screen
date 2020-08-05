<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ include file="../main.jsp"%>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>精筛数据历史记录</title>
    <link rel="stylesheet" type="text/css" href="${ctx_static}/css/component.css"></link>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/pagenation/pagenation.css"></link>
	<script  type="text/javascript">
	var flag=true;
		$(document).ready(function(){
			$("#mytable").createTable({
		        title:"健康检测精筛数据列表",
		        url:"${ctx}/manage/hc/healthDetailList.json",
		        multiChecked:false,
		        hasRowNumber:true,
		        params:{},
		        columns:[
	                 {field:"uniqueId",title:"编号",formatter:function(val,row){
			                var str="<a href='${ctx}/manage/hc/editHealthCheckDetail.htm?id="+row.id+"' title='编号'>"+row.uniqueId+"</a>&nbsp;"; 
			                return str;
			            }},
		            {field:"age",title:"年龄"},
		            {field:"gender",title:"性别"},
		            {field:"checkDate2",title:"第一次复诊日期"},
		            {field:"highPressure2",title:"第一次复诊收缩压",formatter:function(val){
		            	if (val==null || val=="") {
		            		return "";
		            	}
		            	return val;
		            }},
		            {field:"lowPressure2",title:"第一次复诊舒张压",formatter:function(val){
		            	if (val==null || val=="") {
		            		return "";
		            	}
		            	return val;
		            }},
		            {field:"checkDate3",title:"第二次复诊日期"},
		            {field:"highPressure3",title:"第二次复诊收缩压",formatter:function(val){
		            	if (val==null || val=="") {
		            		return "";
		            	}
		            	return val;
		            }},
		            {field:"lowPressure3",title:"第二次复诊舒张压",formatter:function(val){
		            	if (val==null || val=="") {
		            		return "";
		            	}
		            	return val;
		            }},
		            {field:"checkDate4",title:"第三次复诊日期"},
		            {field:"highPressure4",title:"第三次复诊收缩压",formatter:function(val){
		            	if (val==null || val=="") {
		            		return "";
		            	}
		            	return val;
		            }},
		            {field:"lowPressure4",title:"第三次复诊舒张压",formatter:function(val){
		            	if (val==null || val=="") {
		            		return "";
		            	}
		            	return val;
		            }},
		            {field:"diagnoseHtn",title:"医生确诊高血压"},
			          
			        {field:"ogttDate",title:"OGTT检测时间"},
		            {field:"ogtt",title:"OGTT",formatter:function(val){
		            	if (val==null || val=="") {
		            		return "";
		            	}
		            	return val.toFixed(2);
		            }},
		            {field:"ogtt2h",title:"OGTT2H",formatter:function(val){
		            	if (val==null || val=="") {
		            		return "";
		            	}
		            	return val.toFixed(2);
		            }},
		            {field:"diagnoseDm",title:"医生确诊糖尿病"},
		            {field:"tgDetail",title:"甘油三酯",formatter:function(val){
		            	if (val==null || val=="") {
		            		return "";
		            	}
		            	return val.toFixed(2);
		            }},
		            {field:"tcDetail",title:"总胆固醇",formatter:function(val){
		            	if (val==null || val=="") {
		            		return "";
		            	}
		            	return val.toFixed(2);
		            }},
		            {field:"hdlDetail",title:"高密度脂蛋白胆固醇",formatter:function(val){
		            	if (val==null || val=="") {
		            		return "";
		            	}
		            	return val.toFixed(2);
		            }},
		            {field:"ldlDetail",title:"低密度脂蛋白胆固醇",formatter:function(val){
		            	if (val==null || val=="") {
		            		return "";
		            	}
		            	return val.toFixed(2);
		            }},
		            {field:"diagnoseHpl",title:"医生确诊血脂异常"},
		            {field:"malb",title:"尿微量白蛋白",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(2);
		            }},
		            {field:"ucr",title:"尿肌酐",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(2);
		            }},
		            {field:"districtDetail",title:"筛查地点"}
		        ],
		        pagenation:true
		    });
			
			$("#uploadFileButton").click(function() {
				$("#uploadFile").show();
	  			$("#uploadFile2").hide();
				$("#file").val("");
				$("#msgTable").html("");
			});
			
			
	 		$("#uploadFile").click(function () {
	 			console.log($("#file")[0].files.length);
	 			var length = $("#file")[0].files.length;
	 			if (length == 1) {
	 				$("#uploadFile").hide();
		  			$("#uploadFile2").show();
	 			}
				
			    var options = {
			 	url : "${ctx }/importData/healthCheckDetail.json",
				success : function(o) {
					if (o.code == 0) {
						var dataMap = o.dataMap;
						if (dataMap == null || dataMap.msgList == null || dataMap.msgList.length == 0) {
							alert("上传成功");
							$("#uploadFile").val("上传");
							window.location.href="${ctx}/manage/hc/healthDetailListUI.htm";
						} else {
							$("#uploadFile").show();
				  			$("#uploadFile2").hide();
			        		var qcHtmlTR = "";
							for (var i = 0; i < dataMap.msgList.length; i++) {
								var item = dataMap.msgList[i];
								qcHtmlTR += '<tr> ' +
										  '<td>' + item.customerId + '</td>' + 
										  '<td>' + item.rowNo + '</td>' +
										  '<td>' + item.colNo + '</td>' +
										  '<td>' + item.errorMsg + '</td>' +
										  '</tr>';
							}
							if(qcHtmlTR.length>0){
								var qcHtmlTable = $("#msgTable");
				        		qcHtmlTable.html("");
				        		qcHtmlTable.append('<tr> ' +
										  '<td >身份证号</td>' + 
										  '<td>行</td>' +
										  '<td>列</td>' +
										  '<td>消息</td>' +
										  '</tr>');
				        		qcHtmlTable.append(qcHtmlTR);
				        		qcHtmlTable.append('<tr> ' +
										  '<td colspan="5" style="color: red;">以上字段内容不符合规范，请认真核对，其他数据已经导入。</td>' + 
										  '</tr>');
							}

						}
					} else if (o.message == "请上传文件"){
						$("#uploadFile").show();
			  			$("#uploadFile2").hide();
						alert(o.message);
					} else {
						$("#uploadFile").hide();
			  			$("#uploadFile2").show();
						alert(o.message);
					} 
				}
			  };
			  $("#uploadForm").ajaxForm(options);				
			}); 

		});
	</script>
	<script type="text/javascript">
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
	<style>
	
@media screen and (min-width:1340px) and (max-width: 1680px) {
			.table,#mytable{
				width:120%!important;
				max-width: 120%!important;
			}
}
@media screen and (max-width:940px) {
			.table,#mytable{
				width:200%!important;
				max-width: 200%!important;
			}
}
@media screen and (max-width:900px) {
			.table,#mytable{
				width:240%!important;
				max-width: 240%!important;
			}
}
@media screen and (max-width: 2100px){
		#mytable {
	    width: 240%!important;
	    max-width: 240%!important;
	    }
}
	</style>
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
				         <div class="search-panel">
				            <label>身份证：</label><input type="text" name="customerId" id="customerId" />
				            <input type="button" class="btn btn-primary" value="读取身份证" onclick="javascript:readCard()">
				            <label>姓名：</label><input type="text" name="customerName" id="customerName" />
				            <input type="button" class="btn btn-danger btn-sm" value="查询" onclick="javascript:search();" />
				            <input type="button" id="uploadFileButton" class="btn btn-prmary" value="上传文件"  data-toggle="modal" data-target="#myModal" />
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
					            <label style="display:block">上传精筛数据：</label>
					            <input type="file" id="file" name="file" style="width:45%;display:inline-block;margin-left:15%"/>
					            <input type="submit" id="uploadFile" class="btn btn-primary"  value="上传">
					            <input class="btn btn-primary" type="button" id="uploadFile2" value="上传中" disabled="disabled" style="display: none;width: 20%;">
					            
					        </div>
			        	</form>
	        	<table class="table table-bordered" id="msgTable">
				
				</table>
	         
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
<script type="text/javascript" src="${ctx_jquery}/jquery.form.js"></script>
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
