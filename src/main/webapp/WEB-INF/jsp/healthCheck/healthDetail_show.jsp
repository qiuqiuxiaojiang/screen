<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<%@include file="../main.jsp" %>
<head>
<title>展示精筛数据</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/component.css"></link>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/healthcheck/css/addGoods.css"></link>
	<script language="javascript" type="text/javascript" src="${ctx_static}/My97DatePicker/WdatePicker.js"></script>
    <script src="${ctx_jquery }/jquery.form.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/component.js"></script>
	<!-- 表单验证 -->
	<script src="${ctx_jquery }/jquery.validate.js"></script>
    <style type="text/css">
	    label.error{
	    	color:Red; 
			font-size:13px; 
			/* margin-left:5px; 
			padding-left:5px;  
			background:url("error.png") left no-repeat; */
	
	    }
	    .form-group {
		    margin-bottom: 15px;
		    height: 30px;
		    display: inline-block;
		    width: 49%;
			}
		.panel-title{
			padding: 5 1%;
		}
		
		.block-title{
			padding: 0 1%;
			color:0c8ece;
		}
		
		#images li{
			float:left;    
			list-style: none;
	    	margin: 10px 5px;
		}
		
    </style>
	<script type="text/javascript">
	 
	 /**预览pdf**/
     function previewPdf(fileName){
         $('#printIframe').attr('src',fileName);
         $('#myModal').modal('show');
     }
	   
	</script>
</head>
<body>
	<div class="container-fluid">
               	 <!-- 以下这是页面需要编辑的区域 -->
                     <div class="row">
                         <div >
                             <div class="panel panel-warning">
                                 <div class="panel-body">
									<input type="hidden" id="dataId" name="id" value="${showEntity.id}" />
									<input type="hidden" name="uniqueId" id="uniqueId" value="${showEntity.uniqueId }">
										          	<h4 class="block-title"><span>基本信息</span></h4>
										          	
										<div class="form-group">
											<label for="customerId" class="col-sm-4 control-lavel">身份证号</label>
											<div class="col-sm-8 controls">
												${showEntity.customerId}
											</div>
										</div>
										<div class="form-group">
											<label for="name" class="col-sm-4 control-lavel">姓名</label>
											<div class="col-sm-8 controls">
												${showEntity.name}
											</div>
										</div>
										
										<div class="form-group">
											<label for="gender" class="col-sm-4 control-lavel">性别</label>
											<div class="col-sm-8 controls">
												${showEntity.gender}
											</div>
										</div>
										
										<div class="form-group">
											<label for="customerId" class="col-sm-4 control-lavel">出生日期</label>
											<div class="col-sm-8 controls">
												${showEntity.birthday}
											</div>
										</div>
										
										<div class="form-group">
											<label for="age" class="col-sm-4 control-lavel">年龄</label>
											<div class="col-sm-8 controls">
												${showEntity.age}
											</div>
										</div>
										<div class="form-group">
											<label for="cardId" class="col-sm-4 control-lavel">手机号</label>
											<div class="col-sm-8 controls">
												${showEntity.mobile}
											</div>
										</div>
										<div class="form-group">
											<label for="url" class="col-sm-4 control-lavel">是否患有疾病</label>
											<div class="col-sm-8 controls">
												<c:if test="${showEntity.haveDisease=='是' }">
												${showEntity.disease }
												</c:if>
												<c:if test="${showEntity.haveDisease=='否' }">
												否
												</c:if>
											</div>
										</div>
										
										<div class="form-group">
											<label for="url" class="col-sm-4 control-lavel">是否有疾病家族史</label>
											<div class="col-sm-8 controls">
												<c:if test="${showEntity.familyHistory=='是' }">
												${showEntity.familyDisease }
												</c:if>
												<c:if test="${showEntity.familyHistory=='否' }">
												否
												</c:if>
											</div>
										</div>
										
							          	<h4 class="block-title"><span>生理指标</span></h4>
							          	
										<div class="form-group">
											<label for="checkDate2" class="col-md-4 control-lavel">第一次血压检测时间</label>
											<div class="col-md-8 controls">
											${showEntity.checkDate2}
											</div>
											
										</div>
										<br>
										<div class="form-group">
											<label for="highPressure" class="col-md-4 control-lavel">第一次复诊收缩压（mmHg）</label>
											<div class="col-md-8 controls">
												${showEntity.highPressure2}
											</div>
										</div>
										
										<div class="form-group">
											<label for="lowPressure" class="col-md-4 control-lavel">第一次复诊舒张压（mmHg）</label>
											<div class="col-md-8 controls">
												${showEntity.lowPressure2}
											</div>
										</div>
										<div class="form-group">
											<label for="checkDate3" class="col-md-4 control-lavel">第二次血压检测时间</label>
											<div class="col-md-8 controls">
											${showEntity.checkDate3}
											</div>
											
										</div>
										<br>
										
										<div class="form-group">
											<label for="highPressure" class="col-md-4 control-lavel">第二次复诊收缩压（mmHg）</label>
											<div class="col-md-8 controls">
												${showEntity.highPressure3}
											</div>
										</div>
										
										<div class="form-group">
											<label for="lowPressure" class="col-md-4 control-lavel">第二次复诊舒张压（mmHg）</label>
											<div class="col-md-8 controls">
												${showEntity.lowPressure3}
											</div>
										</div>
										<div class="form-group">
											<label for="checkDate4" class="col-md-4 control-lavel">第三次血压检测时间</label>
											<div class="col-md-8 controls">
											${showEntity.checkDate4}
											</div>
											
										</div>
										<br>
										
										<div class="form-group">
											<label for="highPressure4" class="col-md-4 control-lavel">第三次复诊收缩压（mmHg）</label>
											<div class="col-md-8 controls">
												${showEntity.highPressure4}
											</div>
										</div>
										
										<div class="form-group">
											<label for="lowPressure4" class="col-md-4 control-lavel">第三次复诊舒张压（mmHg）</label>
											<div class="col-md-8 controls">
												${showEntity.lowPressure4}
											</div>
										</div>
										<div class="form-group">
											<label for="diagnoseHtn" class="col-md-4 control-lavel">医生确诊高血压</label>
											<div class="col-md-8 controls">
												${showEntity.diagnoseHtn}
											</div>
										</div>
										<br>

										<h4 class="block-title"><span>OGTT检测结果</span></h4>
							          	
							          	<div class="form-group">
											<label for="ogttDate" class="col-md-4 control-lavel">OGTT检测时间</label>
											<div class="col-md-8 controls">
											${showEntity.ogttDate}
											</div>
											
										</div>
										<div class="form-group">
											<label for="ogtt" class="col-md-4 control-lavel">OGTT（mmol/L）</label>
											<div class="col-md-8 controls">
												${showEntity.ogtt}
											</div>
										</div>
										
										<div class="form-group">
											<label for="ogtt2h" class="col-md-4 control-lavel">OGTT2H（mmol/L）</label>
											<div class="col-md-8 controls">
												${showEntity.ogtt2h}
											</div>
										</div>

										<div class="form-group">
											<label for="diagnoseDm" class="col-md-4 control-lavel">医生确诊糖尿病</label>
											<div class="col-md-8 controls">
												${showEntity.diagnoseDm }
											</div>
										</div>
										<br>
							          	
							          	<h4 class="block-title"><span>生化检测结果</span></h4>
							          	
										<div class="form-group">
											<label for="tgDetail" class="col-md-4 control-lavel">甘油三脂（mmol/L）</label>
											<div class="col-md-8 controls">
												${showEntity.tgDetail}
											</div>
										</div>
										<div class="form-group">
											<label for="tcDetail" class="col-md-4 control-lavel">总胆固醇（mmol/L）</label>
											<div class="col-md-8 controls">
												${showEntity.tcDetail}
											</div>
										</div>
										<div class="form-group">
											<label for="hdlDetail" class="col-md-4 control-lavel">高密度脂蛋白胆固醇（mmol/L）</label>
											<div class="col-md-8 controls">
												${showEntity.hdlDetail}
											</div>
										</div>
										<div class="form-group">
											<label for="ldlDetail" class="col-md-4 control-lavel">低密度脂蛋白胆固醇（mmol/L）</label>
											<div class="col-md-8 controls">
												${showEntity.ldlDetail}
											</div>
										</div>
										<div class="form-group">
											<label for="diagnoseHpl" class="col-md-4 control-lavel">医生确诊血脂异常</label>
											<div class="col-md-8 controls">
												${showEntity.diagnoseHpl }
											</div>
										</div>
										<br>
										
										<h4 class="block-title"><span>尿检结果</span></h4>
							          	
										<div class="form-group">
											<label for="malb" class="col-md-4 control-lavel">尿微量白蛋白（mg/ml）</label>
											<div class="col-md-8 controls">
												${showEntity.malb}
											</div>
										</div>
										<div class="form-group">
											<label for="ucr" class="col-md-4 control-lavel">尿肌酐mAlb（mg/L）</label>
											<div class="col-md-8 controls">
												${showEntity.ucr}
											</div>
										</div>
										
										<h4 class="block-title"><span>精筛结果</span></h4>
							          	
										<div class="form-group" >
											<label class="col-md-4 control-lavel">基因检测</label>
											<div class="col-md-8 controls">
												${showEntity.geneReports }
											</div>
										</div>
										<div class="form-group">
											<label for="ucr" class="col-md-4 control-lavel">精筛结果人群分类</label>
											<div class="col-md-8 controls">
												${showEntity.classifyResultJs}
											</div>
										</div>
							          	<h4 class="block-title"><span>足底检查结果</span></h4>
										
										<div class="form-group">
											<label class="col-md-3 control-lavel">干燥</label> 
											<label for="dryLeft" class="col-md-3 control-lavel">左足</label>
											<div class="col-md-6 controls">
												${showEntity.dryLeft }
											</div>
										</div>
										<div class="form-group">
											<label for="dryRight" class="col-md-4 control-lavel">右足</label>
											<div class="col-md-8 controls">
												${showEntity.dryRight }
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-3 control-lavel">皲裂</label> 
											<label for="chapLeft" class="col-md-3 control-lavel">左足</label>
											<div class="col-md-6 controls">
												${showEntity.chapLeft }
											</div>
										</div>
										<div class="form-group">
											<label for="chapRight" class="col-md-4 control-lavel">右足</label>
											<div class="col-md-8 controls">
												${showEntity.chapRight }
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-3 control-lavel">脱皮</label> 
											<label for="peelLeft" class="col-md-3 control-lavel">左足</label>
											<div class="col-md-6 controls">
												${showEntity.peelLeft }
											</div>
										</div>
										<div class="form-group">
											<label for="peelRight" class="col-md-4 control-lavel">右足</label>
											<div class="col-md-8 controls">
												${showEntity.peelRight }
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-3 control-lavel">鸡眼</label> 
											<label for="cornLeft" class="col-md-3 control-lavel">左足</label>
											<div class="col-md-6 controls">
												${showEntity.cornLeft }
											</div>
										</div>
										<div class="form-group">
											<label for="cornRight" class="col-md-4 control-lavel">右足</label>
											<div class="col-md-8 controls">
												${showEntity.cornRight }
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-3 control-lavel">畸形</label> 
											<label for="malLeft" class="col-md-3 control-lavel">左足</label>
											<div class="col-md-6 controls">
												${showEntity.malLeft }
											</div>
										</div>
										<div class="form-group">
											<label for="malRight" class="col-md-4 control-lavel">右足</label>
											<div class="col-md-8 controls">
												${showEntity.malRight }
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-3 control-lavel">足底胼胝</label> 
											<label for="callusLeft" class="col-md-3 control-lavel">左足</label>
											<div class="col-md-6 controls">
												${showEntity.callusLeft }
											</div>
										</div>
										<div class="form-group">
											<label for="callusRight" class="col-md-4 control-lavel">右足</label>
											<div class="col-md-8 controls">
												${showEntity.callusRight }
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-3 control-lavel">真菌感染</label> 
											<label for="fungalLeft" class="col-md-3 control-lavel">左足</label>
											<div class="col-md-6 controls">
												${showEntity.fungalLeft }
											</div>
										</div>
										<div class="form-group">
											<label for="fungalRight" class="col-md-4 control-lavel">右足</label>
											<div class="col-md-8 controls">
												${showEntity.fungalRight }
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-3 control-lavel">溃疡</label> 
											<label for="ulcerLeft" class="col-md-3 control-lavel">左足</label>
											<div class="col-md-6 controls">
												${showEntity.ulcerLeft }
											</div>
										</div>
										<div class="form-group">
											<label for="ulcerRight" class="col-md-4 control-lavel">右足</label>
											<div class="col-md-8 controls">
												${showEntity.ulcerRight }
											</div>
										</div>
										<div class="form-group">
											<label class="col-md-12 control-lavel" style="text-align: left!important;">足底感觉（10g尼龙丝）</label>
										</div>
										<br>
										<div class="form-group">
											<label for="feelLeftFirst" class="col-md-4 control-lavel">左足第一次</label>
											<div class="col-md-8 controls">
												${showEntity.feelLeftFirst }
											</div>
										</div>
										<div class="form-group">
											<label for="feelRightFirst" class="col-md-4 control-lavel">右足第一次</label>
											<div class="col-md-8 controls">
												${showEntity.feelRightFirst }
											</div>
										</div>
										<div class="form-group">
											<label for="feelLeftSecond" class="col-md-4 control-lavel">左足第二次</label>
											<div class="col-md-8 controls">
												${showEntity.feelLeftSecond }
											</div>
										</div>
										<div class="form-group">
											<label for="feelRightSecond" class="col-md-4 control-lavel">右足第二次</label>
											<div class="col-md-8 controls">
												${showEntity.feelRightSecond }
											</div>
										</div>
										<div class="form-group">
											<label for="feelLeftThird" class="col-md-4 control-lavel">左足第三次</label>
											<div class="col-md-8 controls">
												${showEntity.feelLeftThird }
											</div>
										</div>
										<div class="form-group">
											<label for="feelRightThird" class="col-md-4 control-lavel">右足第三次</label>
											<div class="col-md-8 controls">
												${showEntity.feelRightThird }
											</div>
										</div>
										<div class="form-group">
											<label for="feelLeftResult" class="col-md-4 control-lavel">左足</label>
											<div class="col-md-8 controls">
												${showEntity.feelLeftResult }
											</div>
										</div>
										<div class="form-group">
											<label for="feelRightResult" class="col-md-4 control-lavel">右足</label>
											<div class="col-md-8 controls">
												${showEntity.feelRightResult }
											</div>
										</div>
							          	<h4 class="block-title"><span>眼底镜报告结果</span></h4>
							          	<div class="form-group">
											<label for="url" class="col-sm-4 control-lavel"></label>
											<div class="col-sm-8">
												${showEntity.fundus} 
											</div>
										</div>
										<hr>
							          	<div class="form-group">
											<label for="url" class="col-sm-4 control-lavel">备注</label>
											<div class="col-sm-8">
												${showEntity.remarkJs} 
											</div>
										</div>
										<br><br><br>
										<!--添加 -->
										<c:if test="${showEntity.customerId == null}">
											<div id="addImageDiv" class="form-group" style="min-height:30px!important;height:auto;display:block;">
												<label class="col-md-4 control-label">上传图片：</label>
												 <div  class='upimgcontent col-md-4' style="width:33%;top: -25px;">
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
										<c:if test="${showEntity.customerId != null}">
											<div class="form-group" style="min-height:30px!important;height:auto;">
												<label class="col-md-4 control-label">查看图片：</label>
												 <div  class='upimgcontent col-md-4' style="width:33%;top: -6px;">
											        <div class="detail fileinput-wrap">
											            <!-- <span id="updateImage" style="color: red;font-size: 12px;font-weight: 10%;display: inline-block;float: left;width: 50%;margin-left: 3%;">(请上传1-8张图片,默认第1张为产品展示图片,推荐尺寸1080×810px,推荐大小200~500kb)</span> -->
											    </div>  
											      											    </div>  
												<div style="clear:both;"></div>
												<div id="ddd"></div>
											    <div style="clear:both;"></div>
											    <input type="hidden" id="imageUrls" value="${showEntity.imageUrls }">
											</div>
										
										</c:if>
										
										<!-- 添加 输入身份证号加载图片 -->
										<div id="image1" class="form-group" style="min-height:30px!important;height:auto;display:none;">
												<label class="col-md-4 control-label">上传图片：</label>
												 <div  class='upimgcontent col-md-4' style="width:33%;top: -6px;">
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
											    <input type="hidden" id="imageUrls" value="${showEntity.imageUrls }">
										</div>
										<div class="panel-footer" style="text-align: center;">
											<div class="form-group button-center-block">
											<input class="btn btn-primary btn-lg" type="button"
												onclick="javascript:history.back(-1);"
												value="返回">
											</div>
										</div>
                                 </div>
                             </div>
                         </div>
                     </div>
                   <!-- 以上这是页面需要编辑的区域 -->
   </div>

</body>
	<script language="JavaScript">
	var t2;
   	$(function () {
          
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
					 $("#ddd").append("<div class='img-wrap' imgid='img"+i+"'><div class='tip'><span class='up'></span><span class='down'></span></div><div class='pic'><img src='"+imageUrl+"' id='"+arr[i]+"' style='display:block;'/></div></div>");
				     $(".closeimg"+i).on("click",function(e){
				            var id=$(this).parents(".img-wrap").attr("imgid");
				            $(this).parents(".img-wrap").remove();
				     });
				     magId++;
				 }
				 $("#datailGoodsImage input[name='files']").attr("id","docd"+magId).attr("imgid","img"+magId);
			 }
		  }
   	});
</script>

<script src="${ctx_static }/healthcheck/js/healthEditUpload.js"></script>
<script src="${ctx_static }/healthcheck/js/updateUpload.js"></script>
</html>