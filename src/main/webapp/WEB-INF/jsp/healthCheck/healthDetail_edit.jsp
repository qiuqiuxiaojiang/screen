<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<%@include file="../main.jsp" %>
<head>
<title>添加精筛数据</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/component.css"></link>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/healthcheck/css/addGoods.css"></link>
	<script language="javascript" type="text/javascript" src="${ctx_static}/My97DatePicker/WdatePicker.js"></script>
    <script src="${ctx_jquery }/jquery.form.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/component.js"></script>
	<!-- 表单验证 -->
	<script src="${ctx_jquery }/jquery.validate.js"></script>
	<script src="${ctx_jquery }/card.js"></script>
    <style type="text/css">
	    label.error{
	    	color:Red; 
			font-size:13px; 
	    }
		#images li{
			float:left;    
			list-style: none;
	    	margin: 10px 5px;
		}
		 #visit-error{
		    position: absolute;
		    right: 13%;
		    bottom: 0;
		}
    </style>
	<script type="text/javascript">
	   $(function () {
		   
		   jQuery.validator.addMethod("indicator", function(value, element) {
				var check = /^((<|>)?\d+)(\.\d+)?$/.test(value);
				if (check) {
					var dvalue=parseFloat(value);
					if (dvalue==0) {
						check = false;
					}
				}
				return this.optional(element) || check;     
			}, "请正确输入数值"); 
		   
	        $("#healthcheckDetailInfo").validate({
				rules:{
					name:{
						required: true
					},
					customerId:{
						required: true,
						isIdCardNo:true
					},
					tgDetail:{
						indicator:true
					},
					tcDetail:{
						indicator:true
					},
					hdlDetail:{
						indicator:true
					},
					ldlDetail:{
						indicator:true
					},
					ogtt:{
						indicator:true,
						min:1
					},
					ogtt2h:{
						indicator:true,
						min:1
					},
					highPressure2:{
						digits:true,
						min:1
					},
					lowPressure2:{
						digits:true,
						min:1
					},
					highPressure3:{
						digits:true,
						min:1
					},
					lowPressure3:{
						digits:true,
						min:1
					},
					highPressure4:{
						digits:true,
						min:1
					},
					lowPressure4:{
						digits:true,
						min:1
					},
					malb:{
						indicator:true
					},
					ucr:{
						indicator:true
					},
					visit:{
						required:true
					}
				},  
    	        messages: {  
    	        	name: {
    	        		required:"请输入用户名",
    	        	},
    	        	customerId:{
						required: "请输入身份证号",
						isIdCardNo:"请输入正确的身份证号"
					},
					tgDetail:{
						number:"请输入合法数字"
					},
					tcDetail:{
							number:"请输入合法数字"
					},
					hdlDetail:{
						number:"请输入合法数字"
					},
					ldlDetail:{
						number:"请输入合法数字"
					},
					ogtt:{
						number:"请输入合法数字"
					},
					ogtt2h:{
						number:"请输入合法数字"
					},
					highPressure2:{
						number:"请输入合法数字"
					},
					lowPressure2:{
						number:"请输入合法数字"
					},
					highPressure3:{
						number:"请输入合法数字"
					},
					lowPressure3:{
						number:"请输入合法数字"
					},
					highPressure4:{
						number:"请输入合法数字"
					},
					lowPressure4:{
						number:"请输入合法数字"
					},
					malb:{
						number:"请输入合法数字"
					},
					ucr:{
						number:"请输入合法数字"
					},
					visit:{
						required:"请选择是否参加随访"
					}
					
    	  		},
    	  		highlight : function(element) {  
		             	$(element).closest('.controls').addClass('has-error');  
			    },  
	            success : function(label) {  
	                label.closest('.controls').removeClass('has-error');  
	                label.remove();  
	            }	        	
	        });
	    });
	
	 $(document).ready(function(){

          $("#printBtn").click(function(){
				return submitPSDForm("generatePdf");
			});
			$("#saveBtn").click(function(){
				return submitPSDForm("submit");
			});
		   // 身份证号码验证 
		   jQuery.validator.addMethod("isIdCardNo", function(value, element) { 
		     return this.optional(element) || idCardNoUtil.checkIdCardNo(value);     
		   }, "请正确输入您的身份证号码"); 
		   
		   
		   
			 function submitPSDForm(category){
				 //debugger;
				 var uniqueId=$('#uniqueId').val();
			     if (uniqueId==null||uniqueId=='') {
			    		alert("此用户尚未建档，请建档后再筛查！");
			    		window.location.href="${ctx}/manage/hc/addHealthCheckDetailedUI.htm";
			    		return false;
			     }
			     
			    var geneReports="";
	    		$("input[type=checkbox][name='geneReport']").each(function(){
	    		    if(this.checked){
	    		    	geneReports+=$(this).val()+",";
	    		    }
	    		});
	    		if(geneReports.length>0){
	    			geneReports = geneReports.substring(0, geneReports.length-1);
	    		} 
	    		
	    		var remarkJs = $("#remarkJs").val();
					
			     var imageUrls = $("#imageUrls").val();
			     var url = "${ctx}/manage/hc/saveHealthCheckDetail.json";
			     if (category=="generatePdf") {
			    	 url = "${ctx}/manage/hc/generateHealthCheckDetailPdf.json";
			     }
			     var options = {
						    url:url,
						    data:{
						    	geneReports:geneReports,
						    	remarkJs:remarkJs
						    },
						    success:function(o){
								 if(o.message="success"){
									if (imageUrls == null) {//新增
										var indexArr = new Array( $("#dd div").length);
									    var fromData = new FormData();
										 $("#dd >div").each(function(i){
											 var imgId = $(this).attr("imgid");
											 var t = $("#image").children("input[type='file'][imgid='"+imgId+"']")[0].files[0];
											 fromData.append("file", t);
										 });
										 fromData.append("ids",o.data);
										 fromData.append("imageType",1);
									} else { //修改
										var fromData = new FormData();
										var imageOrder = "";//原有图片的顺序
										var fileOrder = "";//上传的图片的顺序
										 $("#ddd >div").each(function(i){
											 var id = $(this).find("img").attr("id");
											 if(id!=null&&id.indexOf("img")==-1){//表示是原上传图片，则需拼装一下
												// imageOrder +=id+","+i+"@";
												 imageOrder +=id+",";
											 }else{
												 fileOrder +=i+",";
												 var imgId = $(this).attr("imgid");
												 var t = $("#datailGoodsImage").children("input[type='file'][imgid='"+imgId+"']")[0].files[0];
												 fromData.append("file",$("#datailGoodsImage").children("input[type='file'][imgid='"+imgId+"']")[0].files[0]);
											 }
										 });
										 fromData.append("ids",o.data);
										 fromData.append("imageType",1);
										 fromData.append("imageOrder",imageOrder);
										 fromData.append("fileOrder",fileOrder);
									}
									$.ajax({
										url:"${ctx}/manage/hc/saveHealthCheckImage.json",
										contentType: "multipart/form-data",
										type:"POST",
										data:fromData,
										dataType:"text",
										processData: false,
										contentType: false,
										cache: false,
										beforeSend : function(){
										},
										success : function(data){
											
										}
									});
									debugger;
									if (category == "generatePdf") {
										 /** 先预览报告 **/
										 var dataResult = o.dataMap;
										 previewPdf(dataResult.fileName);
// 										 $(function () { $('#myModal').on('hidden.bs.modal', function () {
// 											window.location.href="${ctx}/manage/hc/addHealthCheckDetailedUI.htm";
// 								 			})
// 										 });
										 return;
									 }
									//$("#urlresourceid").val(o.data);
									alert("保存成功!");
									window.location.href="${ctx}/manage/hc/addHealthCheckDetailedUI.htm";
								}else{
									alert("保存失败！");
								}
						    }
			     };
			     $("#healthcheckDetailInfo").ajaxForm(options);
			 }
	}); 
	 

	 
	 /**预览pdf**/
     function previewPdf(fileName){
         $('#printIframe').attr('src',fileName);
         $('#myModal').modal('show');
     }
	 
	 function afterSubmit(o){
		 return true;
	 }
	   
	</script>
</head>
<body>
<div style="display:none">
<object id="MyObject" classid="clsid:AD6DB163-16C6-425F-97DA-CC28F30F249A" codebase="HXIDCard.CAB#version=1,0,0,1" width=0 height=0></object>
</div>
	<div class="modal fade" id="myModal" style="z-index: 10000;"
		tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
		aria-hidden="true">
		<div class="modal-dialog" style="width: 900px;">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title" id="myModalLabel">报告预览</span></h4>
				</div>
				<div class="modal-body" style="width: 100%;">
					<iframe id="printIframe"  width="100%" height="700"></iframe> 
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">
						返回</button>
				</div>
			</div>
		</div>
	</div>
	<div class="container-fluid">
               	 <!-- 以下这是页面需要编辑的区域 -->
                     <div class="row">
                         <div >
                             <div class="panel panel-warning">
                                 <div class="panel-body">
                                 	<form id="healthcheckDetailInfo" role="form" method="post" >
									<input type="hidden" id="dataId" name="id" value="${editEntity.id}" />
									<input type="hidden" name="uniqueId" id="uniqueId" value="${editEntity.uniqueId }">
									<input type="hidden" id="item" value="${item }">
									<input type="hidden" id="classifyResultCs" value="${editEntity.classifyResult }">
									<input type="hidden" id="geneTest" value="${editEntity.geneTest }">
									<input type="hidden" id="dmTag" value="${editEntity.dmTag }">
									
									<h4 class="block-title"><span>基本信息</span></h4>
										<div class="form-group">
											<label for="customerId" class="col-sm-4 control-lavel"><span style="color:red;">*</span>身份证号</label>
											<div class="col-sm-8 controls">
												<input type="text" id="customerId" readonly class="form-control" name="customerId" value="${editEntity.customerId}"
													placeholder="请输入身份证号" style="width: 200px; padding: 7px 10px; display:inline-block;border: 1px solid #ccc; margin-right: 10px;">
												<input type="button" id="inputIdcardButton" class="btn btn-primary " value="手工录入" onclick="javascript:inputId()">
												<input type="button" id="inputQueryButton" class="btn btn-primary " style="display:none" value="查询" 
													onclick="javascript:queryByCustomerId()">
											</div>
										</div>
										<div class="form-group">
											<label for="name" class="col-sm-4 control-lavel"><span style="color:red;">*</span>姓名</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" readonly id="customerName" name="name" value="${editEntity.name}"
													placeholder="请输入用户名">
											</div>
										</div>
										
										<div class="form-group">
											<label for="gender" class="col-sm-4 control-lavel"><span style="color:red;">*</span>性别</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" id="gender" name="gender" value="${editEntity.gender}" readonly>
			
											</div>
										</div>
										
										<div class="form-group">
											<label for="customerId" class="col-sm-4 control-lavel"><span style="color:red;">*</span>出生日期</label>
											<div class="col-sm-8 controls">
												<input type="text" id="birthday" class="form-control" readonly="readonly" name="birthday" value="${editEntity.birthday}">
											</div>
										</div>
										
										<div class="form-group">
											<label for="age" class="col-sm-4 control-lavel"><span style="color:red;">*</span>年龄</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" id="age" readonly="readonly" name="age" value="${editEntity.age}"
													>
											</div>
										</div>
										<div class="form-group">
											<label for="cardId" class="col-sm-4 control-lavel">手机号</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" id="mobile" name="mobile" readonly value="${editEntity.mobile}">
											</div>
										</div>
										<div class="form-group">
											<label for="url" class="col-sm-4 control-lavel"><span style="color:red;">*</span>是否患有疾病</label>
											<div class="col-sm-8 controls">
												<c:if test="${editEntity.haveDisease=='是' }">
												<c:set var="diseaseShow" value="${editEntity.disease }"/>
												</c:if>
												<c:if test="${editEntity.haveDisease=='否' }">
												<c:set var="diseaseShow" value="否"/>
												</c:if>
												
												<input type="text"  class="form-control" readonly id="diseaseShow" value="${diseaseShow }"/>
												<input type="hidden" id="disease" name="disease" value="${editEntity.disease }"/>
												<input type="hidden" id="haveDisease" name="haveDisease" value="${editEntity.haveDisease }"/>
											</div>
										</div>
										
										<div class="form-group">
											<label for="url" class="col-sm-4 control-lavel"><span style="color:red;">*</span>是否有疾病家族史</label>
											<div class="col-sm-8 controls">
												<c:if test="${editEntity.familyHistory=='是' }">
												<c:set var="familyHistoryShow" value="${editEntity.familyDisease }"/>
												</c:if>
												<c:if test="${editEntity.familyHistory=='否' }">
												<c:set var="familyHistoryShow" value="否"/>
												</c:if>
												<input type="text"  class="form-control" readonly id="familyHistoryShow" value="${familyHistoryShow}"/>
												<input type="hidden" id="familyHistory" name="familyHistory" value="${editEntity.familyHistory }"/>
												<input type="hidden" id="familyDisease" name="familyDisease" value="${editEntity.familyDisease }"/>
											</div>
										</div>
										
							          	<h4 class="block-title"><span>生理指标</span></h4>
							          	
										<div class="form-group">
											<label for="checkDate2" class="col-md-4 control-lavel">第一次血压检测时间</label>
											<div class="col-md-8 controls">
											<input class="Wdate" id="checkDate2" name="checkDate2" readonly type="text" value="${editEntity.checkDate2}"
											onClick="WdatePicker({el:this,dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d'})" onblur="calssifyResultVal();">
											</div>
											
										</div>
										<br>
										<div class="form-group">
											<label for="highPressure" class="col-md-4 control-lavel">第一次复诊收缩压（mmHg）</label>
											<div class="col-md-8 controls">
												<input type="text" class="form-control" id="highPressure2" name="highPressure2" value="${editEntity.highPressure2}" onblur="calssifyResultVal();" onkeyup="this.value=this.value.replace(/^ +| +$/g,'')">
											</div>
										</div>
										
										<div class="form-group">
											<label for="lowPressure" class="col-md-4 control-lavel">第一次复诊舒张压（mmHg）</label>
											<div class="col-md-8 controls">
												<input type="text" class="form-control" id="lowPressure2" name="lowPressure2" value="${editEntity.lowPressure2}" onblur="calssifyResultVal();" onkeyup="this.value=this.value.replace(/^ +| +$/g,'')">
											</div>
										</div>
										<div class="form-group">
											<label for="checkDate3" class="col-md-4 control-lavel">第二次血压检测时间</label>
											<div class="col-md-8 controls">
											<input class="Wdate" id="checkDate3" name="checkDate3" readonly type="text" value="${editEntity.checkDate3}"
											onClick="WdatePicker({el:this,dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d'})" onblur="calssifyResultVal();">
											</div>
											
										</div>
										<br>
										
										<div class="form-group">
											<label for="highPressure" class="col-md-4 control-lavel">第二次复诊收缩压（mmHg）</label>
											<div class="col-md-8 controls">
												<input type="text" class="form-control" id="highPressure3" name="highPressure3" value="${editEntity.highPressure3}" onblur="calssifyResultVal();" onkeyup="this.value=this.value.replace(/^ +| +$/g,'')">
											</div>
										</div>
										
										<div class="form-group">
											<label for="lowPressure" class="col-md-4 control-lavel">第二次复诊舒张压（mmHg）</label>
											<div class="col-md-8 controls">
												<input type="text" class="form-control" id="lowPressure3" name="lowPressure3" value="${editEntity.lowPressure3}" onblur="calssifyResultVal();" onkeyup="this.value=this.value.replace(/^ +| +$/g,'')">
											</div>
										</div>
										<div class="form-group">
											<label for="checkDate4" class="col-md-4 control-lavel">第三次血压检测时间</label>
											<div class="col-md-8 controls">
											<input class="Wdate" id="checkDate4" name="checkDate4" readonly type="text" value="${editEntity.checkDate4}"
											onClick="WdatePicker({el:this,dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d'})" onblur="calssifyResultVal();">
											</div>
											
										</div>
										<br>
										
										<div class="form-group">
											<label for="highPressure4" class="col-md-4 control-lavel">第三次复诊收缩压（mmHg）</label>
											<div class="col-md-8 controls">
												<input type="text" class="form-control" id="highPressure4" name="highPressure4" value="${editEntity.highPressure4}" onblur="calssifyResultVal();" onkeyup="this.value=this.value.replace(/^ +| +$/g,'')">
											</div>
										</div>
										
										<div class="form-group">
											<label for="lowPressure4" class="col-md-4 control-lavel">第三次复诊舒张压（mmHg）</label>
											<div class="col-md-8 controls">
												<input type="text" class="form-control" id="lowPressure4" name="lowPressure4" value="${editEntity.lowPressure4}" onblur="calssifyResultVal();" onkeyup="this.value=this.value.replace(/^ +| +$/g,'')">
											</div>
										</div>
										<div class="form-group">
											<label for="diagnoseHtn" class="col-md-4 control-lavel">医生确诊高血压</label>
											<div class="col-md-8 controls checkbox-list">
												<label> 
													<input type="radio" id="diagnoseHtnYes" name="diagnoseHtn" value="是" <c:if test="${(editEntity.diagnoseHtn=='是')}">checked="checked"</c:if> /> 是
												</label>
												<label>
													<input type="radio" id="diagnoseHtnNo" name="diagnoseHtn" value="否" <c:if test="${(editEntity.diagnoseHtn=='否')}">checked="checked"</c:if> /> 否
												</label>
											</div>
										</div>
										<br>

										<h4 class="block-title"><span>OGTT检测结果</span></h4>
							          	
							          	<div class="form-group">
											<label for="ogttDate" class="col-md-4 control-lavel">OGTT检测时间</label>
											<div class="col-md-8 controls checkbox-list">
											<input class="Wdate" id="ogttDate" name="ogttDate" readonly type="text" value="${editEntity.ogttDate}"
											onClick="WdatePicker({el:this,dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d'})">
											</div>
											
										</div>
										<div class="form-group">
											<label for="ogtt" class="col-md-4 control-lavel">OGTT（mmol/L）</label>
											<div class="col-md-8 controls">
												<input type="text" class="form-control" id="ogtt" name="ogtt" value="${editEntity.ogtt}" onblur="calssifyResultVal();" onkeyup="this.value=this.value.replace(/^ +| +$/g,'')">
											</div>
										</div>
										
										<div class="form-group">
											<label for="ogtt2h" class="col-md-4 control-lavel">OGTT2H（mmol/L）</label>
											<div class="col-md-8 controls">
												<input type="text" class="form-control" id="ogtt2h" name="ogtt2h" value="${editEntity.ogtt2h}" onblur="calssifyResultVal();" onkeyup="this.value=this.value.replace(/^ +| +$/g,'')">
											</div>
										</div>

										<div class="form-group">
											<label for="diagnoseDm" class="col-md-4 control-lavel">医生确诊糖尿病</label>
											<div class="col-md-8 controls checkbox-list">
												<label> 
													<input type="radio" id="diagnoseDmYes" name="diagnoseDm" value="是" <c:if test="${(editEntity.diagnoseDm=='是')}">checked="checked"</c:if> /> 是
												</label>
												<label>
													<input type="radio" id="diagnoseDmNo" name="diagnoseDm" value="否" <c:if test="${(editEntity.diagnoseDm=='否')}">checked="checked"</c:if> /> 否
												</label>
											</div>
										</div>
										<br>
							          	
							          	<h4 class="block-title"><span>生化检测结果</span></h4>
							          	
										<div class="form-group">
											<label for="tgDetail" class="col-md-4 control-lavel">甘油三脂（mmol/L）</label>
											<div class="col-md-8 controls">
												<input type="text" class="form-control" id="tgDetail" name="tgDetail" value="${editEntity.tgDetail}" onblur="calssifyResultVal();" onkeyup="this.value=this.value.replace(/^ +| +$/g,'')">
											</div>
										</div>
										<div class="form-group">
											<label for="tcDetail" class="col-md-4 control-lavel">总胆固醇（mmol/L）</label>
											<div class="col-md-8 controls">
												<input type="text" class="form-control" id="tcDetail" name="tcDetail" value="${editEntity.tcDetail}" onblur="calssifyResultVal();" onkeyup="this.value=this.value.replace(/^ +| +$/g,'')">
											</div>
										</div>
										<div class="form-group">
											<label for="hdlDetail" class="col-md-4 control-lavel">高密度脂蛋白胆固醇（mmol/L）</label>
											<div class="col-md-8 controls">
												<input type="text" class="form-control" id="hdlDetail" name="hdlDetail" value="${editEntity.hdlDetail}" onblur="calssifyResultVal();" onkeyup="this.value=this.value.replace(/^ +| +$/g,'')">
											</div>
										</div>
										<div class="form-group">
											<label for="ldlDetail" class="col-md-4 control-lavel">低密度脂蛋白胆固醇（mmol/L）</label>
											<div class="col-md-8 controls">
												<input type="text" class="form-control" id="ldlDetail" name="ldlDetail" value="${editEntity.ldlDetail}" onkeyup="this.value=this.value.replace(/^ +| +$/g,'')">
											</div>
										</div>
										<div class="form-group">
											<label for="diagnoseHpl" class="col-md-4 control-lavel">医生确诊血脂异常</label>
											<div class="col-md-8 controls checkbox-list">
												<label> 
													<input type="radio" id="diagnoseHplYes" name="diagnoseHpl" value="是" <c:if test="${(editEntity.diagnoseHpl=='是')}">checked="checked"</c:if> /> 是
												</label>
												<label>
													<input type="radio" id="diagnoseHplNo" name="diagnoseHpl" value="否" <c:if test="${(editEntity.diagnoseHpl=='否')}">checked="checked"</c:if> /> 否
												</label>
											</div>
										</div>
										<br>
										
										<h4 class="block-title"><span>尿检结果</span></h4>
							          	
										<div class="form-group">
											<label for="malb" class="col-md-4 control-lavel">尿微量白蛋白（mg/ml）</label>
											<div class="col-md-8 controls">
												<input type="text" class="form-control" id="malb" name="malb" value="${editEntity.malb}" onkeyup="this.value=this.value.replace(/^ +| +$/g,'')">
											</div>
										</div>
										<div class="form-group">
											<label for="ucr" class="col-md-4 control-lavel">尿肌酐mAlb（mg/L）</label>
											<div class="col-md-8 controls">
												<input type="text" class="form-control" id="ucr" name="ucr" value="${editEntity.ucr}" onkeyup="this.value=this.value.replace(/^ +| +$/g,'')">
											</div>
										</div>
										

										<h4 class="block-title"><span>精筛结果</span></h4>
							          	
										<div class="form-group" style="display:none;" id="fuxinDiv">
											<label class="col-md-4 control-lavel">基因检测</label>
											<div class="col-md-8 controls checkbox-list">
												<label>
													<input name="geneReport" type="checkbox" disabled="true" value="糖尿病用药套餐" id="fuxintangniaobing" <c:if test="${fn:contains(editEntity.geneReports,'糖尿病用药套餐')}">checked="checked"</c:if>>糖尿病用药套餐
												</label> 
												<label>
													<input name="geneReport" type="checkbox" disabled="true" value="高血压用药套餐" id="fuxingaoxueya" <c:if test="${fn:contains(editEntity.geneReports,'高血压用药套餐')}">checked="checked"</c:if>>高血压用药套餐
												</label> 
												<label>
													<input name="geneReport" type="checkbox" disabled="true" value="他汀类降脂药套餐" id="fuxingaoxuezhi" <c:if test="${fn:contains(editEntity.geneReports,'他汀类降脂药套餐')}">checked="checked"</c:if>>他汀类降脂药套餐
												</label>
											</div>
										</div>
										
										<div class="form-group" style="display:none;" id="kunmingDiv">
											<label for="url" class="col-sm-4 control-lavel"></label>
											<div class="col-sm-8 controls">
												<div style="width:1000px;">
													<label>
														<input name="geneReport" type="checkbox" disabled="true" value="糖尿病用药套餐或糖尿病基因检测4项" id="kunmingtangniaobing" <c:if test="${fn:contains(editEntity.geneReports,'糖尿病用药套餐或糖尿病基因检测4项')}">checked="checked"</c:if>>糖尿病用药套餐或糖尿病基因检测4项
													</label> 
													<label>
														<input name="geneReport" type="checkbox" disabled="true" value="高血压用药套餐" id="kunminggaoxueya" <c:if test="${fn:contains(editEntity.geneReports,'高血压用药套餐')}">checked="checked"</c:if>>高血压用药套餐
													</label> 
													<label>
														<input name="geneReport" type="checkbox" disabled="true" value="他汀类降脂药套餐" id="kunminggaoxuezhi" <c:if test="${fn:contains(editEntity.geneReports,'他汀类降脂药套餐')}">checked="checked"</c:if>>他汀类降脂药套餐
													</label>
												</div>
											</div>
										</div>
										<div class="form-group">
											<label for="ucr" class="col-md-4 control-lavel">精筛结果人群分类</label>
											<div class="col-md-8 controls">
												<input type="text" class="form-control" id="classifyResultJs" name="classifyResultJs" value="${editEntity.classifyResultJs}">
											</div>
										</div>
										
										<input type="hidden" id="visit" value="${editEntity.visit}">
										<div class="form-group" style="display: none;" id="visitDiv">
											<label class="col-sm-4 control-lavel">是否参加随访</label>
											<div class="col-sm-4">
												<label> 
													<input type="radio" id="visitYes" name="visit" value="是" <c:if test="${(editEntity.visit=='是')}">checked="checked"</c:if> /> 是
												</label>
												<label>
													<input type="radio" id="visitNo"  name="visit" value="否" <c:if test="${(editEntity.visit=='否')}">checked="checked"</c:if> /> 否
												</label>
											</div>
										</div>
							          	<h4 class="block-title"><span>足底检查结果</span></h4>
										<div class="form-group">
											<label class="col-md-3 control-lavel">干燥</label> 
											<label for="dryLeft" class="col-md-3 control-lavel">左足</label>
											<div class="col-md-6 controls checkbox-list">
												<label> 
													<input type="radio" id="dryLeftYes" name="dryLeft" value="有" <c:if test="${(editEntity.dryLeft=='有')}">checked="checked"</c:if> /> 有
												</label>
												<label>
													<input type="radio" id="dryLeftNo" name="dryLeft" value="无" <c:if test="${(editEntity.dryLeft=='无')}">checked="checked"</c:if> /> 无
												</label>
											</div>
										</div>
										<div class="form-group">
											<label for="dryRight" class="col-md-4 control-lavel">右足</label>
											<div class="col-md-8 controls checkbox-list">
												<label> 
													<input type="radio" id="dryRightYes" name="dryRight" value="有" <c:if test="${(editEntity.dryRight=='有')}">checked="checked"</c:if> /> 有
												</label>
												<label>
													<input type="radio" id="dryRightNo" name="dryRight" value="无" <c:if test="${(editEntity.dryRight=='无')}">checked="checked"</c:if> /> 无
												</label>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-3 control-lavel">皲裂</label> 
											<label for="chapLeft" class="col-md-3 control-lavel">左足</label>
											<div class="col-md-6 controls checkbox-list">
												<label> 
													<input type="radio" id="chapLeftYes" name="chapLeft" value="有" <c:if test="${(editEntity.chapLeft=='有')}">checked="checked"</c:if> /> 有
												</label>
												<label>
													<input type="radio" id="chapLeftNo" name="chapLeft" value="无" <c:if test="${(editEntity.chapLeft=='无')}">checked="checked"</c:if> /> 无
												</label>
											</div>
										</div>
										<div class="form-group">
											<label for="chapRight" class="col-md-4 control-lavel">右足</label>
											<div class="col-md-8 controls checkbox-list">
												<label> 
													<input type="radio" id="chapRightYes" name="chapRight" value="有" <c:if test="${(editEntity.chapRight=='有')}">checked="checked"</c:if> /> 有
												</label>
												<label>
													<input type="radio" id="chapRightNo" name="chapRight" value="无" <c:if test="${(editEntity.chapRight=='无')}">checked="checked"</c:if> /> 无
												</label>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-3 control-lavel">脱皮</label> 
											<label for="peelLeft" class="col-md-3 control-lavel">左足</label>
											<div class="col-md-6 controls checkbox-list">
												<label> 
													<input type="radio" id="peelLeftYes" name="peelLeft" value="有" <c:if test="${(editEntity.peelLeft=='有')}">checked="checked"</c:if> /> 有
												</label>
												<label>
													<input type="radio" id="peelLeftNo" name="peelLeft" value="无" <c:if test="${(editEntity.peelLeft=='无')}">checked="checked"</c:if> /> 无
												</label>
											</div>
										</div>
										<div class="form-group">
											<label for="peelRight" class="col-md-4 control-lavel">右足</label>
											<div class="col-md-8 controls checkbox-list">
												<label> 
													<input type="radio" id="peelRightYes" name="peelRight" value="有" <c:if test="${(editEntity.peelRight=='有')}">checked="checked"</c:if> /> 有
												</label>
												<label>
													<input type="radio" id="peelRightNo" name="peelRight" value="无" <c:if test="${(editEntity.peelRight=='无')}">checked="checked"</c:if> /> 无
												</label>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-3 control-lavel">鸡眼</label> 
											<label for="cornLeft" class="col-md-3 control-lavel">左足</label>
											<div class="col-md-6 controls checkbox-list">
												<label> 
													<input type="radio" id="cornLeftYes" name="cornLeft" value="有" <c:if test="${(editEntity.cornLeft=='有')}">checked="checked"</c:if> /> 有
												</label>
												<label>
													<input type="radio" id="cornLeftNo" name="cornLeft" value="无" <c:if test="${(editEntity.cornLeft=='无')}">checked="checked"</c:if> /> 无
												</label>
											</div>
										</div>
										<div class="form-group">
											<label for="cornRight" class="col-md-4 control-lavel">右足</label>
											<div class="col-md-8 controls checkbox-list">
												<label> 
													<input type="radio" id="cornRightYes" name="cornRight" value="有" <c:if test="${(editEntity.cornRight=='有')}">checked="checked"</c:if> /> 有
												</label>
												<label>
													<input type="radio" id="cornRightNo" name="cornRight" value="无" <c:if test="${(editEntity.cornRight=='无')}">checked="checked"</c:if> /> 无
												</label>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-3 control-lavel">畸形</label> 
											<label for="malLeft" class="col-md-3 control-lavel">左足</label>
											<div class="col-md-6 controls checkbox-list">
												<label> 
													<input type="radio" id="malLeftYes" name="malLeft" value="有" <c:if test="${(editEntity.malLeft=='有')}">checked="checked"</c:if> /> 有
												</label>
												<label>
													<input type="radio" id="malLeftNo" name="malLeft" value="无" <c:if test="${(editEntity.malLeft=='无')}">checked="checked"</c:if> /> 无
												</label>
											</div>
										</div>
										<div class="form-group">
											<label for="malRight" class="col-md-4 control-lavel">右足</label>
											<div class="col-md-8 controls checkbox-list">
												<label> 
													<input type="radio" id="malRightYes" name="malRight" value="有" <c:if test="${(editEntity.malRight=='有')}">checked="checked"</c:if> /> 有
												</label>
												<label>
													<input type="radio" id="malRightNo" name="malRight" value="无" <c:if test="${(editEntity.malRight=='无')}">checked="checked"</c:if> /> 无
												</label>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-3 control-lavel">足底胼胝</label> 
											<label for="callusLeft" class="col-md-3 control-lavel">左足</label>
											<div class="col-md-6 controls checkbox-list">
												<label> 
													<input type="radio" id="callusLeftYes" name="callusLeft" value="有" <c:if test="${(editEntity.callusLeft=='有')}">checked="checked"</c:if> /> 有
												</label>
												<label>
													<input type="radio" id="callusLeftNo" name="callusLeft" value="无" <c:if test="${(editEntity.callusLeft=='无')}">checked="checked"</c:if> /> 无
												</label>
											</div>
										</div>
										<div class="form-group">
											<label for="callusRight" class="col-md-4 control-lavel">右足</label>
											<div class="col-md-8 controls checkbox-list">
												<label> 
													<input type="radio" id="callusRightYes" name="callusRight" value="有" <c:if test="${(editEntity.callusRight=='有')}">checked="checked"</c:if> /> 有
												</label>
												<label>
													<input type="radio" id="callusRightNo" name="callusRight" value="无" <c:if test="${(editEntity.callusRight=='无')}">checked="checked"</c:if> /> 无
												</label>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-3 control-lavel">真菌感染</label> 
											<label for="fungalLeft" class="col-md-3 control-lavel">左足</label>
											<div class="col-md-6 controls checkbox-list">
												<label> 
													<input type="radio" id="fungalLeftYes" name="fungalLeft" value="有" <c:if test="${(editEntity.fungalLeft=='有')}">checked="checked"</c:if> /> 有
												</label>
												<label>
													<input type="radio" id="fungalLeftNo" name="fungalLeft" value="无" <c:if test="${(editEntity.fungalLeft=='无')}">checked="checked"</c:if> /> 无
												</label>
											</div>
										</div>
										<div class="form-group">
											<label for="fungalRight" class="col-md-4 control-lavel">右足</label>
											<div class="col-md-8 controls checkbox-list">
												<label> 
													<input type="radio" id="fungalRightYes" name="fungalRight" value="有" <c:if test="${(editEntity.fungalRight=='有')}">checked="checked"</c:if> /> 有
												</label>
												<label>
													<input type="radio" id="fungalRightNo" name="fungalRight" value="无" <c:if test="${(editEntity.fungalRight=='无')}">checked="checked"</c:if> /> 无
												</label>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-3 control-lavel">溃疡</label> 
											<label for="ulcerLeft" class="col-md-3 control-lavel">左足</label>
											<div class="col-md-6 controls checkbox-list">
												<label> 
													<input type="radio" id="ulcerLeftYes" name="ulcerLeft" value="有" <c:if test="${(editEntity.ulcerLeft=='有')}">checked="checked"</c:if> /> 有
												</label>
												<label>
													<input type="radio" id="ulcerLeftNo" name="ulcerLeft" value="无" <c:if test="${(editEntity.ulcerLeft=='无')}">checked="checked"</c:if> /> 无
												</label>
											</div>
										</div>
										<div class="form-group">
											<label for="ulcerRight" class="col-md-4 control-lavel">右足</label>
											<div class="col-md-8 controls checkbox-list">
												<label> 
													<input type="radio" id="ulcerRightYes" name="ulcerRight" value="有" <c:if test="${(editEntity.ulcerRight=='有')}">checked="checked"</c:if> /> 有
												</label>
												<label>
													<input type="radio" id="ulcerRightNo" name="ulcerRight" value="无" <c:if test="${(editEntity.ulcerRight=='无')}">checked="checked"</c:if> /> 无
												</label>
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-12 control-lavel" style="text-align: left!important;">足底感觉（10g尼龙丝）</label>
										</div>
										<br>
										<div class="form-group">
											<label for="feelLeftFirst" class="col-md-4 control-lavel">左足第一次</label>
											<div class="col-md-8 controls checkbox-list">
												<label> 
													<input type="radio" id="feelLeftFirstTrue" name="feelLeftFirst" value="对" <c:if test="${(editEntity.feelLeftFirst=='对')}">checked="checked"</c:if> /> 对
												</label>
												<label>
													<input type="radio" id="feelLeftFirstFalse" name="feelLeftFirst" value="错" <c:if test="${(editEntity.feelLeftFirst=='错')}">checked="checked"</c:if> /> 错
												</label>
											</div>
										</div>
										<div class="form-group">
											<label for="feelRightFirst" class="col-md-4 control-lavel">右足第一次</label>
											<div class="col-md-8 controls checkbox-list">
												<label> 
													<input type="radio" id="feelRightFirstTrue" name="feelRightFirst" value="对" <c:if test="${(editEntity.feelRightFirst=='对')}">checked="checked"</c:if> /> 对
												</label>
												<label>
													<input type="radio" id="feelRightFirstFalse" name="feelRightFirst" value="错" <c:if test="${(editEntity.feelRightFirst=='错')}">checked="checked"</c:if> /> 错
												</label>
											</div>
										</div>
										<div class="form-group">
											<label for="feelLeftSecond" class="col-md-4 control-lavel">左足第二次</label>
											<div class="col-md-8 controls checkbox-list">
												<label> 
													<input type="radio" id="feelLeftSecondTrue" name="feelLeftSecond" value="对" <c:if test="${(editEntity.feelLeftSecond=='对')}">checked="checked"</c:if> /> 对
												</label>
												<label>
													<input type="radio" id="feelLeftSecondFalse" name="feelLeftSecond" value="错" <c:if test="${(editEntity.feelLeftSecond=='错')}">checked="checked"</c:if> /> 错
												</label>
											</div>
										</div>
										<div class="form-group">
											<label for="feelRightSecond" class="col-md-4 control-lavel">右足第二次</label>
											<div class="col-md-8 controls checkbox-list">
												<label> 
													<input type="radio" id="feelRightSecondTrue" name="feelRightSecond" value="对" <c:if test="${(editEntity.feelRightSecond=='对')}">checked="checked"</c:if> /> 对
												</label>
												<label>
													<input type="radio" id="feelRightSecondFalse" name="feelRightSecond" value="错" <c:if test="${(editEntity.feelRightSecond=='错')}">checked="checked"</c:if> /> 错
												</label>
											</div>
										</div>
										<div class="form-group">
											<label for="feelLeftThird" class="col-md-4 control-lavel">左足第三次</label>
											<div class="col-md-8 controls checkbox-list">
												<label> 
													<input type="radio" id="feelLeftThirdTrue" name="feelLeftThird" value="对" <c:if test="${(editEntity.feelLeftThird=='对')}">checked="checked"</c:if> /> 对
												</label>
												<label>
													<input type="radio" id="feelLeftThirdFalse" name="feelLeftThird" value="错" <c:if test="${(editEntity.feelLeftThird=='错')}">checked="checked"</c:if> /> 错
												</label>
											</div>
										</div>
										<div class="form-group">
											<label for="feelRightThird" class="col-md-4 control-lavel">右足第三次</label>
											<div class="col-md-8 controls checkbox-list">
												<label> 
													<input type="radio" id="feelRightThirdTrue" name="feelRightThird" value="对" <c:if test="${(editEntity.feelRightThird=='对')}">checked="checked"</c:if> /> 对
												</label>
												<label>
													<input type="radio" id="feelRightThirdFalse" name="feelRightThird" value="错" <c:if test="${(editEntity.feelRightThird=='错')}">checked="checked"</c:if> /> 错
												</label>
											</div>
										</div>
										<div class="form-group">
											<label for="feelLeftResult" class="col-md-4 control-lavel">左足</label>
											<div class="col-md-8 controls checkbox-list">
												<label> 
													<input type="radio" id="feelLeftResultNormal" name="feelLeftResult" value="正常" <c:if test="${(editEntity.feelLeftResult=='正常')}">checked="checked"</c:if> />
													 正常
												</label>
												<label>
													<input type="radio" id="feelLeftResultFade" name="feelLeftResult" value="减弱" <c:if test="${(editEntity.feelLeftResult=='减弱')}">checked="checked"</c:if> />
													 减弱
												</label>
												<label>
													<input type="radio" id="feelLeftResultDisappear" name="feelLeftResult" value="消失" <c:if test="${(editEntity.feelLeftResult=='消失')}">checked="checked"</c:if> />
													 消失
												</label>
											</div>
										</div>
										<div class="form-group">
											<label for="feelRightResult" class="col-md-4 control-lavel">右足</label>
											<div class="col-md-8 controls checkbox-list">
												<label> 
													<input type="radio" id="feelRightResultNoraml" name="feelRightResult" value="正常" <c:if test="${(editEntity.feelRightResult=='正常')}">checked="checked"</c:if> />
													 正常
												</label>
												<label>
													<input type="radio" id="feelRightResultFade" name="feelRightResult" value="减弱" <c:if test="${(editEntity.feelRightResult=='减弱')}">checked="checked"</c:if> />
													 减弱
												</label>
												<label>
													<input type="radio" id="feelRightResultDisappear" name="feelRightResult" value="消失" <c:if test="${(editEntity.feelRightResult=='消失')}">checked="checked"</c:if> />
													 消失
												</label>
											</div>
										</div>
							          	<h4 class="block-title"><span>眼底镜报告结果</span></h4>
							          	<div class="form-group">
											<label for="url" class="col-sm-4 control-lavel"></label>
											<div class="col-sm-8">
												<textarea id="fundus" name="fundus" cols="100" rows="3">${editEntity.fundus}</textarea> 
											</div>
										</div>
										<hr>
										
										<br><br>
							          	<div class="form-group">
											<label for="url" class="col-sm-4 control-lavel">备注</label>
											<div class="col-sm-8">
												<textarea name="remarkJs" cols="100" rows="3" id="remarkJs">${editEntity.remarkJs}</textarea> 
											</div>
										</div>
										<br><br><br>
										<!--添加 -->
										<c:if test="${editEntity.customerId == null}">
											<div id="addImageDiv" class="form-group" style="min-height:30px!important;height:auto;display:block;">
												<label class="col-md-4 control-label">上传图片：</label>
												 <div  class='upimgcontent col-md-4' style="width:33%;top: -47px;">
												 	<div class="form-inline">
														<label><ul id="images"></ul></label>
													</div>
												 	
											        <div class="fileinput-wrap">
											            <span class="btn btn-success fileinput-button" style="position: relative;display: inline-block;float: left;" id="image">
											                <i class="glyphicon glyphicon-plus"></i>
											                <span>添加文件</span>
											                <input type="file" name="files" id="doc0"  class="tts"  onchange="javascript:setImagePreviews()" imgid="img0" >
											            </span>
											            <!-- <span id="image1" style="color: red;font-size: 12px;font-weight: 10%;display: inline-block;float: left;width: 50%;margin-left: 3%;">(请上传1-8张图片,默认第1张为产品展示图片,推荐尺寸1080×810px,推荐大小200~500kb)</span> -->
											   		 </div>  
											    </div>  
											    
												<div style="clear:both;"></div>
												<div id="dd"></div>
												<div style="clear:both;"></div>
											</div>
										</c:if>
										<!--修改-->
										<c:if test="${editEntity.customerId != null}">
											<div class="form-group" style="min-height:30px!important;height:auto;">
												<label class="col-md-4 control-label">上传图片：</label>
												 <div  class='upimgcontent col-md-4' style="width:33%;">
											        <div class="detail fileinput-wrap">
											            <span class="btn btn-success fileinput-button" style="position: relative;display: inline-block;float: left;"  id="datailGoodsImage">
											                <i class="glyphicon glyphicon-plus"></i>
											                <span>添加文件</span>
											                <input type="file" name="files"  class="tts"  onchange="javascript:setImagePreviewsD()"  >
											            </span>
											            <!-- <span id="updateImage" style="color: red;font-size: 12px;font-weight: 10%;display: inline-block;float: left;width: 50%;margin-left: 3%;">(请上传1-8张图片,默认第1张为产品展示图片,推荐尺寸1080×810px,推荐大小200~500kb)</span> -->
											    </div>  
											      											    </div>  
												<div style="clear:both;"></div>
												<div id="ddd"></div>
											    <div style="clear:both;"></div>
											    <input type="hidden" id="imageUrls" value="${editEntity.imageUrls }">
											</div>
										
										</c:if>
										
										<!-- 添加 输入身份证号加载图片 -->
										<div id="image1" class="form-group" style="min-height:30px!important;height:auto;display:none;">
												<label class="col-md-4 control-label">上传图片：</label>
												 <div  class='upimgcontent col-md-4' style="width:33%;">
											        <div class="detail fileinput-wrap">
											            <span class="btn btn-success fileinput-button" style="position: relative;display: inline-block;float: left;"  id="datailGoodsImage">
											                <i class="glyphicon glyphicon-plus"></i>
											                <span>添加文件</span>
											                <input type="file" name="files"  class="tts"  onchange="javascript:setImagePreviewsD()"  >
											            </span>
											            <!-- <span id="updateImage" style="color: red;font-size: 12px;font-weight: 10%;display: inline-block;float: left;width: 50%;margin-left: 3%;">(请上传1-8张图片,默认第1张为产品展示图片,推荐尺寸1080×810px,推荐大小200~500kb)</span> -->
											    	</div>  
											    </div>  
												<div style="clear:both;"></div>
												<div id="ddd"></div>
											    <div style="clear:both;"></div>
											    <input type="hidden" id="imageUrls" value="${editEntity.imageUrls }">
										</div>
										
<!-- 										<input class="btn btn-primary btn-lg" type="submit"  style="margin: 50px auto;display: block;" id="saveBtn" value="保存数据"> -->
										
										
										<div class="panel-footer"style="text-align: center;">
											<div class="form-group button-center-block">
												<div class="col-sm-6 controls" >
				 							<input class="btn btn-primary btn-lg" type="submit" id="saveBtn" value="保存数据"> </div> 
												<div class="col-sm-6 controls" >
											<input class="btn btn-primary btn-lg" type="submit"
												id="printBtn"
												value="保存并打印报告">
												</div>
											</div>
										</div>			
								
									</form>
                                 </div>
                             </div>
                         </div>
                     </div>
                   <!-- 以上这是页面需要编辑的区域 -->
   </div>

</body>
<script type="text/javascript" src="${ctx_static}/device/HXIDCard.js"></script>
<script language="JavaScript">
	var t2;
   	$(function () {
   		var item = $("#item").val();
		if (item == "fuxin") {
			$("#fuxinDiv").show();
		} else {
			$("#fuxinDiv").hide();
		}
		
		if (item == "kunming") {
			$("#kunmingDiv").show();
		} else {
			$("#kunmingDiv").hide();
		}
   		
		  var dataId = $('#dataId').val();
		  var customerId = $('#customerId').val();
		  if (dataId == '') {
		      t2 = window.setInterval("OnBtnReadIDCardContent()", 2500); 
		  } else if (customerId == '') {
			  alert('没有找到此对象的建档信息');
		  }
          if (window.history && window.history.pushState) {
              $(window).on('popstate', function () {
                  window.history.pushState('forward', null, ''); 
                  window.history.forward(1);
              });
          }
          window.history.pushState('forward', null, '');  //在IE中必须得有这两行
          window.history.forward(1);
          
       	  //图片
		  var imageUrls = $("#imageUrls").val();
		  var arr=new Array();
		  if (imageUrls != null) {
			  arr=imageUrls.split(',');
			  if (arr.length > 0) {
				 var trStr = "";
				 for(var i=0;i<arr.length-1;i++){
					var imageUrl = "${ctx }/files/img/"+arr[i]+".htm";
					 //$("#ddd").append("<div class='img-wrap' imgid='img"+i+"'><div class='pic'><img src='"+imageUrl+"' id='"+arr[i]+"' style='display:block;'/></div><div class='tip'><span class='up'>&lt;</span><span class='down'>&gt;</span><span class=' closeimg"+i+" close1'>×</span></div></div>");
					 $("#ddd").append("<div class='img-wrap' imgid='img"+i+"'><div class='tip'><span class='up'></span><span class='down'></span><span class=' closeimg"+i+" close1'>×</span></div><div class='pic'><img src='"+imageUrl+"' id='"+arr[i]+"' style='display:block;'/></div></div>");
				     $(".closeimg"+i).on("click",function(e){
				            var id=$(this).parents(".img-wrap").attr("imgid");
				            $(this).parents(".img-wrap").remove();
				     });
				     magId++;
				 }
				 $("#datailGoodsImage input[name='files']").attr("id","docd"+magId).attr("imgid","img"+magId);
			 }
		  }
		  
		  calssifyResultVal(); 
		  
		  var visit = $("#visit").val();
			if (visit == '是') {
				$("#visitDiv").hide();
			}
		  
   	});
   	
  /*   $("#customerId").bind("keydown", function(event) {
		if (event.keyCode == "13") {
			//回车执行查询
			queryByCustomerId();
		}
	 }); */
   	
    function OnBtnReadIDCardContent() {
        var result = HXIDCard.readIDCardContent(MyObject);
        if ("1" == result.code) {
        	var idCard = result.content.cardNo;
        	$('#customerId').val(idCard);
        	$('#customerName').val(result.content.name);
        	$('#gender').val(result.content.sexMc);
        	var age = idCardNoUtil.getAge(idCard);
        	$("#age").val(age);
        	var birthday = idCardNoUtil.getIdCardInfo(idCard).birthday;
        	$('#birthday').val(birthday);
        	queryInfo(idCard);
        	window.clearInterval(t2);
        }
    }
    
	function inputId() {
		$('#customerId').removeAttr("readonly");
		$('#inputQueryButton').show();
		$('#inputIdcardButton').hide();
	
		window.clearInterval(t2);
	}
	
	function queryByCustomerId() {
		var idCardNo = $('#customerId').val();
		if (idCardNo == null || idCardNo=='') {
			alert('请输入身份证号码！');
			return;
		}
		if (!idCardNoUtil.checkIdCardNo(idCardNo)) {
			alert('身份证号码格式错误，请重新输入！');
			return;
		}
		var idCardInfo = idCardNoUtil.getIdCardInfo(idCardNo);
		if (idCardInfo == null) {
			alert('身份证号码格式错误，请重新输入！');
			return;
		}
		
    	$('#gender').val(idCardInfo.gender);
    	$('#birthday').val(idCardInfo.birthday);
		queryInfo(idCardNo);
	}
	
	function queryInfo(idCard) {
		$.ajax({
			type : "post",
			url : "${ctx}/manage/hc/queryInfo.json",
			data : {
				customerId:idCard
			},
			success:function(data){
				if(data.code==0){
					$('#customerId').attr("readonly", "readonly");
					var uniqueId = data.dataMap.uniqueId;
					$('#uniqueId').val(uniqueId);
					var mobile = data.dataMap.mobile;
					if (mobile!='') {
						$('#mobile').val(mobile);
			        	$('#mobile').attr("readonly","readonly");
					}
					var customerName = data.dataMap.name;
					
					if (customerName != null && customerName != '') {
						$('#customerName').val(customerName);
					}

					var disease = data.dataMap.disease;
					var haveDisease = data.dataMap.haveDisease;
					$('#disease').val(disease);
					$('#haveDisease').val(haveDisease);
					if (haveDisease == "否") {
						$('#diseaseShow').val(haveDisease);
					} else if(haveDisease == "是") {
						if (disease == null) {
							disease = '';
						}
						$('#diseaseShow').val(disease);
					}
					
					var familyDisease = data.dataMap.familyDisease;
					var familyHistory = data.dataMap.familyHistory;
					$('#familyHistory').val(familyHistory);
					$('#familyDisease').val(familyDisease);
					if (familyHistory == "否") {
						$('#familyHistoryShow').val(familyHistory);
					} else if(familyHistory == "是") {
						if (familyDisease == null) {
							familyDisease = '';
						}
						$('#familyHistoryShow').val(familyDisease);
					}
					$('#inputQueryButton').hide();
					
					//生理指标
					$("#checkDate2").val(data.dataMap.checkDate2);
					$("#highPressure2").val(data.dataMap.highPressure2);
					$("#lowPressure2").val(data.dataMap.lowPressure2);
					$("#checkDate3").val(data.dataMap.checkDate3);
					$("#highPressure3").val(data.dataMap.highPressure3);
					$("#lowPressure3").val(data.dataMap.lowPressure3);
					$("#checkDate4").val(data.dataMap.checkDate4);
					$("#highPressure4").val(data.dataMap.highPressure4);
					$("#lowPressure4").val(data.dataMap.lowPressure4);
					
					if (data.dataMap.diagnoseHtn == "是") {
						$("#diagnoseHtnYes").prop("checked",true);
					} else if (data.dataMap.diagnoseHtn == "否") {
						$("#diagnoseHtnNo").prop("checked",true);
					}
					
					//OGTT
					$("#ogttDate").val(data.dataMap.ogttDate);
					$("#ogtt").val(data.dataMap.ogtt);
					$("#ogtt2h").val(data.dataMap.ogtt2h);
					if (data.dataMap.diagnoseDm == "是") {
						$("#diagnoseDmYes").prop("checked",true);
					} else if (data.dataMap.diagnoseDm == "否") {
						$("#diagnoseDmNo").prop("checked",true);
					}
					
					//生化检测
					$("#tgDetail").val(data.dataMap.tgDetail);
					$("#tcDetail").val(data.dataMap.tcDetail);
					$("#hdlDetail").val(data.dataMap.hdlDetail);
					$("#ldlDetail").val(data.dataMap.ldlDetail);
					
					if (data.dataMap.diagnoseHpl == "是") {
						$("#diagnoseHplYes").prop("checked",true);
					} else if (data.dataMap.diagnoseHpl == "否") {
						$("#diagnoseHplNo").prop("checked",true);
					}
					
					//尿检
					$("#malb").val(data.dataMap.malb);
					$("#ucr").val(data.dataMap.ucr);
					
					//初筛人群分类结果
					var classifyResultCs = data.dataMap.classifyResult;
					console.log("classifyResultCs:" + classifyResultCs);
					
					var geneTest = data.dataMap.geneTest;
					var dmTag = data.dataMap.dmTag;
					
					$("#classifyResultCs").val(classifyResultCs);
					$("#geneTest").val(geneTest);
					$("#dmTag").val(dmTag);
					calssifyResultVal();
					
					//足底检查
					if (data.dataMap.dryLeft == "有") {
						$("#dryLeftYes").prop("checked",true);
					} else if (data.dataMap.dryLeft == "无") {
						$("#dryLeftNo").prop("checked",true);
					}
					
					if (data.dataMap.dryRight == "有") {
						$("#dryRightYes").prop("checked",true);
					} else if (data.dataMap.dryRight == "无") {
						$("#dryRightNo").prop("checked",true);
					}
					
					if (data.dataMap.chapLeft == "有") {
						$("#chapLeftYes").prop("checked",true);
					} else if (data.dataMap.chapLeft == "无") {
						$("#chapLeftNo").prop("checked",true);
					}
					
					if (data.dataMap.chapRight == "有") {
						$("#chapRightYes").prop("checked",true);
					} else if (data.dataMap.chapRight == "无") {
						$("#chapRightNo").prop("checked",true);
					}
					
					if (data.dataMap.peelLeft == "有") {
						$("#peelLeftYes").prop("checked",true);
					} else if (data.dataMap.peelLeft == "无") {
						$("#peelLeftNo").prop("checked",true);
					}
					
					if (data.dataMap.peelRight == "有") {
						$("#peelRightYes").prop("checked",true);
					} else if (data.dataMap.peelRight == "无") {
						$("#peelRightNo").prop("checked",true);
					}
					
					if (data.dataMap.cornLeft == "有") {
						$("#cornLeftYes").prop("checked",true);
					} else if (data.dataMap.cornLeft == "无") {
						$("#cornLeftNo").prop("checked",true);
					}
					
					if (data.dataMap.cornRight == "有") {
						$("#cornRightYes").prop("checked",true);
					} else if (data.dataMap.cornRight == "无") {
						$("#cornRightNo").prop("checked",true);
					}
					
					if (data.dataMap.malLeft == "有") {
						$("#malLeftYes").prop("checked",true);
					} else if (data.dataMap.malLeft == "无") {
						$("#malLeftNo").prop("checked",true);
					}
					
					if (data.dataMap.malRight == "有") {
						$("#malRightYes").prop("checked",true);
					} else if (data.dataMap.malRight == "无") {
						$("#malRightNo").prop("checked",true);
					}
					
					if (data.dataMap.callusLeft == "有") {
						$("#callusLeftYes").prop("checked",true);
					} else if (data.dataMap.callusLeft == "无") {
						$("#callusLeftNo").prop("checked",true);
					}
					
					if (data.dataMap.callusRight == "有") {
						$("#callusRightYes").prop("checked",true);
					} else if (data.dataMap.callusRight == "无") {
						$("#callusRightNo").prop("checked",true);
					}
					
					if (data.dataMap.fungalLeft == "有") {
						$("#fungalLeftYes").prop("checked",true);
					} else if (data.dataMap.fungalLeft == "无") {
						$("#fungalLeftNo").prop("checked",true);
					}
					
					if (data.dataMap.fungalRight == "有") {
						$("#fungalRightYes").prop("checked",true);
					} else if (data.dataMap.fungalRight == "无") {
						$("#fungalRightNo").prop("checked",true);
					}
					
					if (data.dataMap.ulcerLeft == "有") {
						$("#ulcerLeftYes").prop("checked",true);
					} else if (data.dataMap.ulcerLeft == "无") {
						$("#ulcerLeftNo").prop("checked",true);
					}
					
					if (data.dataMap.ulcerRight == "有") {
						$("#ulcerRightYes").prop("checked",true);
					} else if (data.dataMap.ulcerRight == "无") {
						$("#ulcerRightNo").prop("checked",true);
					}
					
					if (data.dataMap.feelLeftFirst == "对") {
						$("#feelLeftFirstTrue").prop("checked",true);
					} else if (data.dataMap.feelLeftFirst == "错") {
						$("#feelLeftFirstFalse").prop("checked",true);
					}
					
					if (data.dataMap.feelRightFirst == "对") {
						$("#feelRightFirstTrue").prop("checked",true);
					} else if (data.dataMap.feelRightFirst == "错") {
						$("#feelRightFirstFalse").prop("checked",true);
					}
					
					if (data.dataMap.feelLeftSecond == "对") {
						$("#feelLeftSecondTrue").prop("checked",true);
					} else if (data.dataMap.feelLeftSecond == "错") {
						$("#feelLeftSecondFalse").prop("checked",true);
					}
					
					if (data.dataMap.feelRightSecond == "对") {
						$("#feelRightSecondTrue").prop("checked",true);
					} else if (data.dataMap.feelRightSecond == "错") {
						$("#feelRightSecondFalse").prop("checked",true);
					}
					
					if (data.dataMap.feelLeftThird == "对") {
						$("#feelLeftThirdTrue").prop("checked",true);
					} else if (data.dataMap.feelLeftThird == "错") {
						$("#feelLeftThirdFalse").prop("checked",true);
					}
					
					if (data.dataMap.feelRightThird == "对") {
						$("#feelRightThirdTrue").prop("checked",true);
					} else if (data.dataMap.feelRightThird == "错") {
						$("#feelRightThirdFalse").prop("checked",true);
					}
					
					if (data.dataMap.feelLeftResult == "正常") {
						$("#feelLeftResultNormal").prop("checked",true);
					} else if (data.dataMap.feelLeftResult == "减弱") {
						$("#feelLeftResultFade").prop("checked",true);
					} else if (data.dataMap.feelLeftResult == "消失") {
						$("#feelLeftResultDisappear").prop("checked",true);
					}
					
					if (data.dataMap.feelRightResult == "正常") {
						$("#feelRightResultNoraml").prop("checked",true);
					} else if (data.dataMap.feelRightResult == "减弱") {
						$("#feelRightResultFade").prop("checked",true);
					} else if (data.dataMap.feelRightResult == "消失") {
						$("#feelRightResultDisappear").prop("checked",true);
					}
					$("#fundus").text(data.dataMap.fundus);
					$("#remarkJs").text(data.dataMap.remarkJs);
					var visit = data.dataMap.visit;
					$("#visit").val(visit);
					if (visit == '是') {
						$("#visitDiv").hide();
					} else if (visit == '否'){
						$("#visitNo").prop("checked",true);
					}
					
					//图片
					$("#addImageDiv").hide();
					$("#image1").show();
					var imageUrls = data.dataMap.imageUrls;
					$("#imageUrls").val(imageUrls);
					  var arr=new Array();
					  if (imageUrls != null) {
						  arr=imageUrls.split(',');
						  if (arr.length > 0) {
							 var trStr = "";
							 for(var i=0;i<arr.length-1;i++){
								var imageUrl = "${ctx }/files/img/"+arr[i]+".htm";
								 //$("#ddd").append("<div class='img-wrap' imgid='img"+i+"'><div class='pic'><img src='"+imageUrl+"' id='"+arr[i]+"' style='display:block;'/></div><div class='tip'><span class='up'>&lt;</span><span class='down'>&gt;</span><span class=' closeimg"+i+" close1'>×</span></div></div>");
								 $("#ddd").append("<div class='img-wrap' imgid='img"+i+"'><div class='tip'><span class='up'></span><span class='down'></span><span class=' closeimg"+i+" close1'>×</span></div><div class='pic'><img src='"+imageUrl+"' id='"+arr[i]+"' style='display:block;'/></div></div>");
							     $(".closeimg"+i).on("click",function(e){
							            var id=$(this).parents(".img-wrap").attr("imgid");
							            $(this).parents(".img-wrap").remove();
							     });
							     magId++;
							 }
							 $("#datailGoodsImage input[name='files']").attr("id","docd"+magId).attr("imgid","img"+magId);
						 }
					  }
					
				} else {
					alert("此用户尚未建档，请建档后再进行筛查");
				}
			}
		});

	}
	//计算BMI:体重（千克）/（身高（米）*身高（米）
	function analyseBMI(){
		if($("#heightDetail").val()!="" && $("#weightDetail").val()!="" ){
			 //cm
			 var heightNum = parseFloat($("#heightDetail").val());
			 //kg
			 var weightNum = parseFloat($("#weightDetail").val());
			 var bmi = parseFloat(weightNum/(heightNum * heightNum/10000));
			 $("#bmiDetail").val(toDecimal2(bmi));
		 }else{
			 $("#bmiDetail").val("");
		 }
		
	}
	function toDecimal2(x) {
		var f = parseFloat(x);      
		if (isNaN(f)) {   
		 return false;     
		}          
		var f = Math.round(x*100)/100;  
		var s = f.toString();       
		var rs = s.indexOf('.');      
		if (rs < 0) {   
		 rs = s.length;      
		 s += '.';   
		            }       
		while (s.length <= rs + 2) {   
		 s += '0';       
		}            
		return s;   
	}
	//精筛结果人群分类
	function calssifyResultVal() {
		var classifyResult = "";
		var diseaseShow = $("#diseaseShow").val();
		var classifyResultCs = $("#classifyResultCs").val();
		var geneTest = $("#geneTest").val();
		var dmTag = $("#dmTag").val();
		
		var bloodGlucose = bloodGlucoseCon(diseaseShow, classifyResultCs, geneTest, dmTag);
		var bloodPressure = bloodPressureCon(diseaseShow, classifyResultCs, geneTest);
		var bloodLipid = bloodLipidCon(diseaseShow, classifyResultCs, geneTest);
		
		/* if (bloodGlucose=="血糖正常" && bloodPressure=="血压正常" && bloodLipid=="血脂正常") {
			classifyResult = "精筛检测指标无异常";
		} else */ 
		if (bloodGlucose=="" && bloodPressure=="" && bloodLipid=="") {
			classifyResult = "检测信息缺失，无法判断";
		} else {
			if (bloodGlucose != "") {
				classifyResult += bloodGlucose + ",";
			} 
			
			if (bloodPressure != "") {
				classifyResult += bloodPressure + ",";
			} 
			
			if (bloodLipid != "") {
				classifyResult += bloodLipid + ",";
			} 
			classifyResult = classifyResult.substring(0,classifyResult.length-1);
			
			$("#visitDiv").show();
		}
		
		$("#classifyResultJs").val(classifyResult);
		var item = $("#item").val();
		//console.log("classifyResult:" + classifyResult);
		if (classifyResult.indexOf("糖尿病患者")!=-1) {
			$("#"+item+"tangniaobing").prop("checked",true); 
		} else {
			$("#"+item+"tangniaobing").prop("checked",false); 
		}
		
		if (classifyResult.indexOf("高血压患者")!=-1) {
			$("#"+item+"gaoxueya").prop("checked",true); 
		} else {
			$("#"+item+"gaoxueya").prop("checked",false); 
		}
		
		if (classifyResult.indexOf("血脂异常患者")!=-1) {
			$("#"+item+"gaoxuezhi").prop("checked",true); 
		} else {
			$("#"+item+"gaoxuezhi").prop("checked",false); 
		}
	}
	
	  //血糖情况判断
    function bloodGlucoseCon(diseaseShow, classifyResultCs, geneTest, dmTag) {
		console.log(classifyResultCs+","+geneTest+","+dmTag);
    	var bloodGlucose = "";
    	debugger;
		if (geneTest != "" && geneTest != null && geneTest.indexOf("进入精筛环节") >= 0) {
			if (classifyResultCs != "") {
				if ((classifyResultCs.indexOf("糖尿病高风险人员") >= 0 || (classifyResultCs.indexOf("血糖异常人群") >= 0 && dmTag != "疑似糖尿病" ))){
					bloodGlucose = "血糖检测信息缺失";
				}
			}  
		}
		
		var ogttStr = $("#ogtt").val().trim();
		var ogtt2hStr = $("#ogtt2h").val().trim();
		
		var ogtt = 0;
		if (ogttStr.indexOf(">") != -1) {
			ogtt = 33.3;
		} else if(ogttStr.indexOf("<") != -1){
			ogtt = 1.1;
		} else {
			ogtt = ogttStr;
		}
		
		var ogtt2h = 0;
		if (ogtt2hStr.indexOf(">") != -1) {
			ogtt2h = 33.3;
		} else if(ogtt2hStr.indexOf("<") != -1){
			ogtt2h = 1.1;
		} else {
			ogtt2h = ogtt2hStr;
		}
		
		if (diseaseShow.indexOf("糖尿病")!=-1) {
			bloodGlucose = "糖尿病患者";
		} else if (ogtt != null && ogtt != "" && ogtt2h != null && ogtt2h != "") {
			if (ogtt >= 7 || ogtt2h >= 11.1) {
				bloodGlucose = "糖尿病患者";
			} else if ((ogtt<7 && ogtt2h>=7.8 && ogtt2h<11.1) || (ogtt>=6.1 && ogtt<7 && ogtt2h<7.8)) {
				bloodGlucose = "糖尿病前期人群";
			} else if (ogtt<6.1 && ogtt2h<7.8){
				bloodGlucose = "血糖正常";
			}
		}
		return bloodGlucose;
	}
	
	 //血压情况判断
   	function bloodPressureCon(diseaseShow, classifyResultCs, geneTest) {
		var bloodPressure = "";
		console.log(classifyResultCs+","+geneTest);
		if (geneTest != "" && geneTest != null && geneTest.indexOf("进入精筛环节") > 0) {
			if (classifyResultCs != "" && classifyResultCs.indexOf("血压异常人群") > 0 ) {
				bloodPressure = "血压检测信息缺失";
			}  
		}
		
    	var checkDate2 = $("#checkDate2").val();
    	var highPressure2 = $("#highPressure2").val().trim();
    	var lowPressure2 = $("#lowPressure2").val().trim();
    	
    	var checkDate3 = $("#checkDate3").val();
    	var highPressure3 = $("#highPressure3").val().trim();
    	var lowPressure3 = $("#lowPressure3").val().trim();
    	
    	var checkDate4 = $("#checkDate4").val();
    	var highPressure4 = $("#highPressure4").val().trim();
    	var lowPressure4 = $("#lowPressure4").val().trim();
    	
    	if (diseaseShow.indexOf("高血压")!=-1) {
    		bloodPressure = "高血压患者";
		} else if(checkDate2 != '' && checkDate2 != null && highPressure2 != '' && highPressure2 != null && lowPressure2 != '' && lowPressure2 != null
    			&& checkDate3 != '' && checkDate3 != null && highPressure3 != '' && highPressure3 != null && lowPressure3 != '' && lowPressure3 != null
    			&& checkDate4 != '' && checkDate4 != null && highPressure4 != '' && highPressure4 != null && lowPressure4 != '' && lowPressure4 != null) {
    		if(checkDate2 != checkDate3 && checkDate3 != checkDate4 && checkDate2 != checkDate4) {
    			if ((highPressure2 >= 140 || lowPressure2 >= 90) && (highPressure3 >= 140 || lowPressure3 >= 90) &&  (highPressure4 >= 140 || lowPressure4 >= 90)) {
    				bloodPressure = "高血压患者";
    			} else if ((highPressure2 < 140 && highPressure3 < 140 && highPressure4 < 140) 
    					&& (lowPressure2 < 90 && lowPressure3 < 90 && lowPressure4 < 90)) {
    				bloodPressure = "血压正常";
    			} else {
    				bloodPressure = "高血压前期人群";
    			}
    		}
    	}
    	return bloodPressure;
    }
	 
	 //血脂情况判断
     function bloodLipidCon(diseaseShow, classifyResultCs, geneTest) {
		 var bloodLipid = "";
		 console.log(classifyResultCs+","+geneTest);
		 if (geneTest != "" && geneTest != null && geneTest.indexOf("进入精筛环节") > 0) {
			if (classifyResultCs != "" && classifyResultCs.indexOf("血脂异常高风险人群") > 0 ) {
				bloodLipid = "血脂检测信息缺失";
			}  
		 }
		 
		 var tgDetailStr = $("#tgDetail").val().trim();
		 var tcDetailStr = $("#tcDetail").val().trim();
		 var hdlDetailStr = $("#hdlDetail").val().trim();
		 
		 var tgDetail = 0;
		 if (tgDetailStr!="") {
			if(tgDetailStr.indexOf(">") != -1){
				tgDetail = 10.36;
			}else if(tgDetailStr.indexOf("<") != -1){
				tgDetail = 2.59;
			}else{
				tgDetail = tgDetailStr;
			}
		 }
		 
		 var tcDetail = 0;
		 if (tcDetailStr!="") {
			if(tcDetailStr.indexOf(">") != -1){
				tcDetail = 5.65;
			}else if(tcDetailStr.indexOf("<") != -1){
				tcDetail = 0.57;
			}else{
				tcDetail = tcDetailStr;
			}
		 }
		 
		 var hdlDetail = 0;
		 if (hdlDetailStr!="") {
			if(hdlDetailStr.indexOf(">") != -1){
				hdlDetail = 2.59;
			}else if(hdlDetailStr.indexOf("<") != -1){
				hdlDetail = 0.39;
			}else{
				hdlDetail = hdlDetailStr;
			}
		 }
		 if (diseaseShow.indexOf("高血脂")!=-1) {
			 bloodLipid = "血脂异常患者";
		 } else if (tgDetail != '' && tgDetail != null && tcDetail != '' && tcDetail != null && hdlDetail != '' && hdlDetail != null) {
			 if (tcDetail >= 5.2 || tgDetail >= 1.7 || hdlDetail < 1) {
				 bloodLipid = "血脂异常患者";
			 } else if (tcDetail < 5.2 && tgDetail<1.7) {
				 bloodLipid = "血脂正常";
			 }
		 }
		 return bloodLipid;
	 }
	
</script>


<script src="${ctx_static }/healthcheck/js/healthEditUpload.js"></script>
<script src="${ctx_static }/healthcheck/js/updateUpload.js"></script>
</html>