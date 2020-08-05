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
		
		@media screen and (max-width:940px) {
			#mytable{
				width:200%!important;
				max-width: 200%!important;
			}
		}
		@media screen and (max-width:900px) {
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
		$(document).ready(function(){
			$("#mytable").createTable({
		        title:"客户信息列表",
		        url:"${ctx}/gene/geneListPage.json",
		        multiChecked:false,
		        hasRowNumber:false,
		        params:{},
		        columns:[
					{field:"uniqueId",title:"编号",width:50,formatter:function(val,row){
					    var str="<a href='${ctx}/gene/editGene.htm?id="+row.id+"' title='编号'>"+row.uniqueId+"</a>&nbsp;"; 
					    return str;
					}},
		        	//{field:"uniqueId", title:"编号",width:50},
		            {field:"age",title:"年龄",width:50},
		            {field:"gender",title:"性别",width:50},
		            {field:"birthday",title:"出生日期",width:80},
		            {field:"tangniaobing",title:"糖尿病用药套餐",width:120},
		            {field:"gaoxueya",title:"高血压用药套餐 ",width:120},
		            {field:"gaoxuezhi",title:"他汀类降脂药套餐",width:120},
		            {field:"ctime",title:"添加日期",width:80,sorting:true,formatter:function(val){
		            	var date=new Date(val);
		            	var y = date.getFullYear();
		            	var m = date.getMonth() + 1;
		            	var d = date.getDate();
		            	var h = date.getHours();
		            	var i = date.getMinutes();
		            	var s = date.getSeconds();
		            	var str=y+"-"+m+"-"+d+" "+h+":"+i+":"+s;
		            	return str;
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
