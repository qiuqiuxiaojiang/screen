<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@include file="../main.jsp" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>目诊记录</title>
	<link rel="stylesheet" type="text/css" href="${ctx}/static/css/component.css"></link>
	<link rel="stylesheet" type="text/css" href="${ctx_bootstrap}/css/bootstrap-datetimepicker.min.css"></link>
	<script src="${ctx_jquery }/jquery.form.js"></script>
	<script type="text/javascript" src="${ctx}/static/js/component.js"></script>
	<script type="text/javascript" src="${ctx_bootstrap}/js/bootstrap-datetimepicker.min.js"></script>
	<script src="${ctx_jquery }/jquery.validate.js"></script>
	<style type="text/css">
		#addEyeRecord{
			margin-top: 5%
		}
		.eyerecord-block,.modal-body{
			font-size:12px;
			color:#515151;
		}
		.eyerecord-block .panel-heading,.eyerecord-block .panel-footer{
			background:#FCFCFC;
		}
		.eyerecord-block a.display{
			color:#3684C4;
			font-weight:bold;
			margin-left:5px;
		}
		.eyerecord-block .panel-title{
			font-size:13px;
			color:#649C09;
		}
		.eyerecord-block .table tr td{
			border:0px;
		}
		.eyerecord-block .table tr td>label{
			margin:0px 5px;
			font-weight:normal;
		}
		#pointer{
			cursor: pointer;
		}
		label.error{
	    	color:Red; 
			font-size:13px; 
			/* margin-left:5px; 
			padding-left:5px;  
			background:url("error.png") left no-repeat; */
	    }
	    
	</style>
</head>
<body>
	<div class="container-fluid">
		<div class="panel-body"><button id="backlist" class="btn btn-primary btn-md">返回列表</button></div>
		<div class="panel-body table-responsive">
			<table class="table table-bordered">
				<tr><td><label>姓名：</label><label>${basicInfo.name }</label></td></tr>
				<tr><td><label>编号：</label><label>${basicInfo.userId }</label></td></tr>
			</table>
		</div>
		<!-- 以下这是页面需要编辑的区域 -->
		<section>
			<c:forEach var="eyeRecord" items="${recordList}">
				<div class="eyerecord-block row">
					<div class="panel panel-default">
						  <div class="panel-heading">
						  	<div class="row">
						  		<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
							  		<h2 class="panel-title">
							    	<span class="glyphicon glyphicon-time"></span>
							    	<label>${eyeRecord.visitId }</label>
							    	</h2>
						  		</div>
						  		<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
						  			<a href="javascript:void(0);" title="删除" id="delEyeRecord_${eyeRecord.id }" class="pull-right"><span class="glyphicon glyphicon-remove text-danger"></span></a>
						  		</div>
						  	</div>
						    
						  </div>
						  <c:if test="${!empty eyeRecord.comprehensiveList}">
						  <div class="panel-body table-responsive">
						  		<c:forEach var="str" items="${eyeRecord.comprehensiveList }">
						  		<p>${str }</p>
						  		</c:forEach>
						  		<table class="table">
						  		<c:forEach var="result" items="${eyeRecord.resultList }">
						  			<tr><td>
						  			<p>疾病：${result.Disease }</p>
						  			<p>特征：${result.Feature }</p>
						  			<p>症状：${result.Symptom }</p>
						  			</td>
						  			<td><p>${result.Path1.name }</p>
						  			<p><img src="${ctx}/files/img/${result.Path1.fileId }.htm" class="img-rounded" style="height: 100px;" /></p>
						  			</td>
						  			<td><p>${result.Path2.name }</p>
						  			<p><img src="${ctx}/files/img/${result.Path2.fileId }.htm" class="img-rounded" style="height: 100px;" /></p>
						  			</td>
						  			</tr>
						  		</c:forEach>
						  		</table>
						  </div>
						  </c:if>
						  <c:if test="${!empty eyeRecord.healthControl }">
						  <div class="panel-body table-responsive">
							  		<h3 class="panel-title">
							    	<label>眼象拍摄结果</label>
							    	</h3>
							    <table class="table">
							    <tr>
							    	<td>&nbsp;</td>
							    	<td><img src="${ctx }/files/img/${eyeRecord.eyeImage.LUp}.htm" class="img-rounded" style="height: 100px;"/></td>
							    	<td>&nbsp;</td>
							    	<td>&nbsp;</td>
							    	<td><img src="${ctx }/files/img/${eyeRecord.eyeImage.RUp}.htm" class="img-rounded" style="height: 100px;"/></td>
							    	<td>&nbsp;</td>
							    </tr>
							    <tr>
							    	<td><img src="${ctx }/files/img/${eyeRecord.eyeImage.LLeft}.htm" class="img-rounded" style="height: 100px;"/></td>
							    	<td><img src="${ctx }/files/img/${eyeRecord.eyeImage.LN}.htm" class="img-rounded" style="height: 100px;"/></td>
							    	<td><img src="${ctx }/files/img/${eyeRecord.eyeImage.LRight}.htm" class="img-rounded" style="height: 100px;"/></td>
							    	<td><img src="${ctx }/files/img/${eyeRecord.eyeImage.RLeft}.htm" class="img-rounded" style="height: 100px;"/></td>
							    	<td><img src="${ctx }/files/img/${eyeRecord.eyeImage.RN}.htm" class="img-rounded" style="height: 100px;"/></td>
							    	<td><img src="${ctx }/files/img/${eyeRecord.eyeImage.RRight}.htm" class="img-rounded" style="height: 100px;"/></td>
							    </tr>
							    <tr>
							    	<td>&nbsp;</td>
							    	<td><img src="${ctx }/files/img/${eyeRecord.eyeImage.LDown}.htm" class="img-rounded" style="height: 100px;"/></td>
							    	<td>&nbsp;</td>
							    	<td>&nbsp;</td>
							    	<td><img src="${ctx }/files/img/${eyeRecord.eyeImage.RDown}.htm" class="img-rounded" style="height: 100px;"/></td>
							    	<td>&nbsp;</td>
							    </tr>
							    </table>
							  		<h3 class="panel-title">
							    	<label>特征提取结果</label>
							    	</h3>
							   <table class="table">
							    <tr><td>特征</td><td>位置</td><td>数值</td><td>健康提示</td></tr>
						  		<c:forEach var="feature" items="${eyeRecord.featureList }">
							    <tr><td>${feature.featureName }</td>
							    	<td>${feature.featurePos }</td>
							    	<td>${feature.featureCount }</td>
							    	<td>${feature.featureNote }</td>
							    </tr>
							    </c:forEach>
							   </table>
							  		<h3 class="panel-title">
							    	<label>综合分析结果</label>
							    	</h3>
							    <c:if test="${empty eyeRecord.sw || eyeRecord.sw.corporeity=='1' }">
							   <p><b>体质倾向</b></p>
							   <p>${eyeRecord.analysisResult.corporeity }</p>
							   </c:if>
							    <c:if test="${empty eyeRecord.sw || eyeRecord.sw.syndrome=='1' }">
							   <p><b>中医证候</b></p>
							   <p>${eyeRecord.analysisResult.syndrome }</p>
							   </c:if>
							    <c:if test="${empty eyeRecord.sw || eyeRecord.sw.disease=='1' }">
							   <p><b>易发病症</b></p>
							   <p>${eyeRecord.analysisResult.disease }</p>
							   </c:if>
							    <c:if test="${empty eyeRecord.sw || eyeRecord.sw.analysis=='1' }">
							   <p><b>基本解读</b></p>
							   <p>${eyeRecord.analysisResult.analysis }</p>
							   </c:if>
							  		<h3 class="panel-title">
							    	<label>健康管理方案</label>
							    	</h3>
							    <c:if test="${empty eyeRecord.sw || eyeRecord.sw.diet=='1' }">
							   <p><b>饮食方案</b></p>
							   <p>${eyeRecord.healthControl.diet }</p>
							   </c:if>
							    <c:if test="${empty eyeRecord.sw || eyeRecord.sw.dailylife=='1' }">
							   <p><b>起居方案</b></p>
							   <p>${eyeRecord.healthControl.dailylife }</p>
							   </c:if>
							    <c:if test="${empty eyeRecord.sw || eyeRecord.sw.sport=='1' }">
							   <p><b>导引方案</b></p>
							   <p>${eyeRecord.healthControl.sport }</p>
							   </c:if>
							    <c:if test="${empty eyeRecord.sw || eyeRecord.sw.emotion=='1' }">
							   <p><b>情志方案</b></p>
							   <p>${eyeRecord.healthControl.emotion }</p>
							   </c:if>
							    <c:if test="${empty eyeRecord.sw || eyeRecord.sw.channelandpoint=='1' }">
							   <p><b>经穴方案</b></p>
							   <p>${eyeRecord.healthControl.channelandpoint }</p>
							   </c:if>
							    <c:if test="${empty eyeRecord.sw || eyeRecord.sw.medicatedbath=='1' }">
							   <p><b>药浴方案</b></p>
							   <p>${eyeRecord.healthControl.medicatedbath }</p>
							   </c:if>
							    <c:if test="${empty eyeRecord.sw || eyeRecord.sw.drug=='1' }">
							   <p><b>药物方案</b></p>
							   <p>${eyeRecord.healthControl.drug }</p>
							   </c:if>
							    <c:if test="${empty eyeRecord.sw || eyeRecord.sw.physicalexamination=='1' }">
							   <p><b>体检方案</b></p>
							   <p>${eyeRecord.healthControl.physicalexamination }</p>
							   </c:if>
							   
						  </div>
						  </c:if>
							  
					</div>
				</div>
			</c:forEach>
		</section>
    <!-- 以上这是页面需要编辑的区域 -->
    </div>
    
	
	<script type="text/javascript">
	$(document).ready(function(){
		
		$("a[id^='delEyeRecord_']").click(function(){
			var eyeRecordId = $(this).attr("id").split("_")[1];
			var c = window.confirm("确定要删除吗？");
			if(c){
				$.ajax({
					url:"${ctx}/eyeRecord/deleteRecord/"+eyeRecordId+".json",
					success:function(o){
						if(o.message = 'success'){
							alert("删除成功！");
							window.location.href="${ctx}/eyeRecord/listRecord/${basicInfo.userId}.htm";
						}else{
							alert("删除失败！");
						}
					}
				});
			}
		});
		
		  $('#backlist').click(function(){
			  <c:if test="${returnPage=='eyeRecord'}">
        	  window.location.href="${ctx }/eyeRecord/list.htm";
        	  </c:if>
        	  <c:if test="${returnPage=='customer'}">
        	  window.location.href="${ctx }/manage/hc/list.htm";
        	  </c:if>
          })

	});
	
	</script>
</body>
</html>