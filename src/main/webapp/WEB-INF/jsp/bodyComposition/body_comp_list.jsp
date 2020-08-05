<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ include file="../main.jsp"%>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>体成分仪数据列表</title>
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
		.pull-right {
		    margin-top: 2%;
		}
		@media screen and (min-width:1340px) and (max-width: 1680px) {
			#mytable{
				width:150%!important;
				max-width: 150%!important;
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
		        title:"体成分仪数据列表",
		        url:"${ctx}/body/dataList.json",
		        multiChecked:false,
		        hasRowNumber:true,
		        params:{},
		        columns:[
		            {field:"customerId",title:"身份证号"},
		            {field:"name",title:"姓名"},
		            {field:"sex",title:"性别"},
		            {field:"age",title:"年龄"},
		            {field:"checkDate",title:"检查日期"},
		            {field:"height",title:"身高",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"weight",title:"体重",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"BMI",title:"BMI",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"WHR",title:"腰臀比",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"fat",title:"脂肪",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"bone",title:"骨质",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"protein",title:"蛋白质",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"water",title:"水分",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"muscle",title:"肌肉",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"SMM",title:"骨骼肌",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"PBF",title:"体脂百分比",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"BMR",title:"基础代谢",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"edema",title:"水肿系数",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(2);
		            }},
		            {field:"VFI",title:"内脏脂肪指数",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"score",title:"健康评分",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"bodyType",title:"体型",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val;
		            }},
		            {field:"LBM",title:"瘦体重",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"ICW",title:"细胞内液",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"ECW",title:"细胞外液",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"standardWeight",title:"目标体重",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"weightControl",title:"体重控制",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"fatControl",title:"脂肪控制",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"muscleControl",title:"肌肉控制",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"liverRisk",title:"脂肪肝风险系数",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"trFat",title:"躯干脂肪",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"laFat",title:"左上肢脂肪",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"raFat",title:"右上肢脂肪",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"llFat",title:"左下肢脂肪",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"rlFat",title:"右上肢脂肪",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"trWater",title:"躯干水分量",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"laWater",title:"左上肢水分",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"raWater",title:"右上肢水分",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"llWater",title:"左下肢水分",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"rlWater",title:"右下肢水分",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"trMuscle",title:"躯干肌肉量",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"laMuscle",title:"左上肢肌肉",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"raMuscle",title:"右上肢肌肉",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"llMuscle",title:"左下肢肌肉",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }},
		            {field:"rlMuscle",title:"右下肢肌肉",formatter:function(val){
		            	if (val==null) {
		            		return "";
		            	}
		            	return val.toFixed(1);
		            }}
		        ],
		        pagenation:true
		    });
	 		$("#uploadFile").click(function () {
				$("#uploadFile").hide();
	  			$("#uploadFile2").show();
			    var options = {
			 	url : "${ctx }/body/uploadFile.json",
				success : function(o) {
					if (o.code == 0) {
						$("#uploadFile").val("上传");
						alert("上传成功");
						window.location.href="${ctx}/body/list.htm";
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
	</script>
	<script type="text/javascript">
	function search(){
		var customerId=$('#customerId').val();
		var name=$('#name').val();
		$("#mytable").createTable({
			params:{customerId:customerId,name:name}
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
				            <label>身份证：</label><input type="text" name="customerId" id="customerId" />
				            <label>姓名：</label><input type="text" name="name" id="name" />
				            <input type="button" class="btn btn-danger btn-sm" value="查询" onclick="javascript:search();" />
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
					            <label style="display:block">上传人群文件：</label>
					            <input type="file" id="file" name="file" style="width:45%;display:inline-block;margin-left:15%"/>
					            <input type="submit" id="uploadFile" class="btn btn-primary"  value="上传">
					            <input class="btn btn-primary" type="button" id="uploadFile2" value="上传中" disabled="disabled" style="display: none;width: 20%;">
					            
					        </div>
			        	</form>
	         
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
<script type="text/javascript" src="${ctx_static}/lightbox/lightbox-2.6.min.js"></script>
<script type="text/javascript" src="${ctx_static}/zyUpload/uploadinfo.js"></script>
<script type="text/javascript" src="${ctx_static}/zyUpload/zyFile.js"></script>
<script type="text/javascript" src="${ctx_static}/zyUpload/zyUpload.js"></script>
<script type="text/javascript" src="${ctx_static}/js/component.js"></script>
<script type="text/javascript" src="${ctx_static}/js/zyUpload-assist.js"></script>
<script type="text/javascript" src="${ctx_static}/pagenation/pagenation.js"></script>
</html>
