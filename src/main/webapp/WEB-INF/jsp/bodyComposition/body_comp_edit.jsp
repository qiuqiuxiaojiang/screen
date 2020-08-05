<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<%@include file="../main.jsp" %>
<head>
<title>添加数据</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/component.css"></link>
	<link href="${ctx_static}/css/lyz.calendar.css" rel="stylesheet" type="text/css"/>
	<script src="${ctx_static}/calendar/lyz.calendar.min.js" type="text/javascript"></script>
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
    </style>
	<script type="text/javascript">
	function addDateModule(inp){
		var id = $(inp).attr('id');
		if($("#"+id+"Calendar").length == 0){
			//alert("0")
			var $Top = $("#"+id).offset().top+ $("#"+id).height() + 16;
			$Left = $("#"+id).offset().left;
			$("#"+id).calendar();
			$("#"+id+"Calendar").css({"left":$Left,"top":$Top,"width": "190px","z-index":"999999",
		    "height":"180px"});
			addDateModule(inp);
		}else{
		}	
		
		}
	
	   $(function () {
/* 	        $("#checkDate").calendar({
	            controlId: "divDate",                                 // 弹出的日期控件ID，默认: $(this).attr("id") + "Calendar"
	            speed: 200,                                           // 三种预定速度之一的字符串("slow", "normal", or "fast")或表示动画时长的毫秒数值(如：1000),默认：200
	            complement: true,                                     // 是否显示日期或年空白处的前后月的补充,默认：true
	            readonly: true,                                       // 目标对象是否设为只读，默认：true
	            upperLimit: new Date(),                               // 日期上限，默认：NaN(不限制)
	            lowerLimit: new Date("2011/01/01"),                   // 日期下限，默认：NaN(不限制)
	            hms:"on",
	            callback: function () {                               // 点击选择日期后的回调函数
	                //alert("您选择的日期是：" + $("#txtBeginDate").val());
	            }
	        });
 */
 

	        $("#bodyCompositionInfo").validate({
				rules:{
					name:{
						required: true
					},
					memberId:{
						required: true
					},
					height:{
						number:true
					},
					birthYear:{
						number:true
					},
					Weight:{
						number:true
					},
					Fat:{
						number:true
					},
					Bone:{
						number:true
					},
					Protein:{
						number:true
					},
					Water:{
						number:true
					},
					Muscle:{
						number:true
					},
					SMM:{
						number:true
					},
					PBF:{
						number:true
					},
					BMI:{
						number:true
					},
					BMR:{
						number:true
					},
					WHR:{
						number:true
					},
					Edema:{
						number:true
					},
					VFI:{
						number:true
					},
					BodyAge:{
						number:true
					},
					Score:{
						number:true
					},
					LBM:{
						number:true
					},
					ICW:{
						number:true
					},
					ECW:{
						number:true
					},
					Standard_weight:{
						number:true
					},
					Weight_control:{
						number:true
					},
					Fat_control:{
						number:true
					},
					Muscle_control:{
						number:true
					},
					LiverRisk:{
						number:true
					},
					TR_fat:{
						number:true
					},
					LA_fat:{
						number:true
					},
					RA_fat:{
						number:true
					},
					LL_fat:{
						number:true
					},
					RL_fat:{
						number:true
					},
					TR_water:{
						number:true
					},
					LA_water:{
						number:true
					},
					RA_water:{
						number:true
					},
					LL_water:{
						number:true
					},
					RL_water:{
						number:true
					},
					TR_muscle:{
						number:true
					},
					LA_muscle:{
						number:true
					},
					RA_muscle:{
						number:true
					},
					LL_muscle:{
						number:true
					},
					RL_muscle:{
						number:true
					},
					testDate:{
						required:true
					}
				},  
				
    	        messages: {  
    	        	name: {
    	        		required:"请输入用户名",
    	        	},
    	        	memberId:{
						required: "请输入身份证号"
					},
					height:{
						number:"请输入合法数字"
					},
					birthYear:{
						number:"请输入合法数字"
					},
					Weight:{
						number:"请输入合法数字"
					},
					Fat:{
						number:"请输入合法数字"
					},
					Bone:{
						number:"请输入合法数字"
					},
					Protein:{
						number:"请输入合法数字"
					},
					Water:{
						number:"请输入合法数字"
					},
					Muscle:{
						number:"请输入合法数字"
					},
					SMM:{
						number:"请输入合法数字"
					},
					PBF:{
						number:"请输入合法数字"
					},
					BMI:{
						number:"请输入合法数字"
					},
					BMR:{
						number:"请输入合法数字"
					},
					WHR:{
						number:"请输入合法数字"
					},
					Edema:{
						number:"请输入合法数字"
					},
					VFI:{
						number:"请输入合法数字"
					},
					BodyAge:{
						number:"请输入合法数字"
					},
					Score:{
						number:"请输入合法数字"
					},
					LBM:{
						number:"请输入合法数字"
					},
					ICW:{
						number:"请输入合法数字"
					},
					ECW:{
						number:"请输入合法数字"
					},
					Standard_weight:{
						number:"请输入合法数字"
					},
					Weight_control:{
						number:"请输入合法数字"
					},
					Fat_control:{
						number:"请输入合法数字"
					},
					Muscle_control:{
						number:"请输入合法数字"
					},
					LiverRisk:{
						number:"请输入合法数字"
					},
					TR_fat:{
						number:"请输入合法数字"
					},
					LA_fat:{
						number:"请输入合法数字"
					},
					RA_fat:{
						number:"请输入合法数字"
					},
					LL_fat:{
						number:"请输入合法数字"
					},
					RL_fat:{
						number:"请输入合法数字"
					},
					TR_water:{
						number:"请输入合法数字"
					},
					LA_water:{
						number:"请输入合法数字"
					},
					RA_water:{
						number:"请输入合法数字"
					},
					LL_water:{
						number:"请输入合法数字"
					},
					RL_water:{
						number:"请输入合法数字"
					},
					TR_muscle:{
						number:"请输入合法数字"
					},
					LA_muscle:{
						number:"请输入合法数字"
					},
					RA_muscle:{
						number:"请输入合法数字"
					},
					LL_muscle:{
						number:"请输入合法数字"
					},
					RL_muscle:{
						number:"请输入合法数字"
					},
					testDate:{
						required:"请输入检测时间"
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
		$("#saveBtn").click(function(){
			var options = {
			    url:"${ctx}/body/saveBodyComposition.json",
			    data:{
			    },
				success:function(o){
				
					if(o.message="success"){
						alert("保存成功!");
						$("#urlresourceid").val(o.data);
						window.location.href="${ctx}/body/addBodyComposition.htm";
					}else{
						alert("保存失败！");
					}
				}
			};
			$("#bodyCompositionInfo").ajaxForm(options);
		})
		
		 /*  $('#banklist').click(function(){
        	  window.location.href="${ctx }/urlresource/resourcelist.htm";
          }) */
          
	}); 
	</script>
</head>
<body>
	<div class="container-fluid">
               	 <!-- 以下这是页面需要编辑的区域 -->
                     <div class="row">
                         <div >
                             <div class="panel panel-warning">
                                 <div class="panel-heading">添加数据</div>
                                 <div class="panel-body">
                                 	 <form id="bodyCompositionInfo" role="form" method="get">
											<div class="form-group">
												<label for="name" class="col-sm-4 control-lavel">*姓名</label>
												<div class="col-sm-8 controls">
													<input type="text" class="form-control" name="name" placeholder="请输入用户名">
												</div>
											</div>
											<div class="form-group">
												<label for="memberId" class="col-sm-4 control-lavel">*身份证号</label>
												<div class="col-sm-8 controls">
													<input type="text" class="form-control" name="memberId" placeholder="请输入身份证号">
												</div>
											</div>
											<div class="form-group">
												<label for="sex" class="col-sm-4 control-lavel">性别</label>
												<div class="col-sm-8 controls">
													 <label>
													      <input type="radio" name="sex" value="1" checked> 男
													 </label>
													 
													 <label>
													      <input type="radio" name="sex" value="2"> 女
													 </label>
													 
												</div>
											</div>
											
											
											<div class="form-group">
												<label for="birthYear" class="col-sm-4 control-lavel">出生年份</label>
												<div class="col-sm-8 controls">
													<input type="text" class="form-control" name="birthYear">
												</div>
											</div>
											
											<hr>
											
											<div class="form-group">
												<label for="height" class="col-sm-4 control-lavel">身高（cm）</label>
												<div class="col-sm-8 controls">
													<input type="text" class="form-control" name="height">
												</div>
											</div>
										<div class="form-group">
											<label for="Weight" class="col-sm-4 control-lavel">体重（kg）</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="Weight">
											</div>
										</div>
										<div class="form-group">
											<label for="Fat" class="col-sm-4 control-lavel">脂肪</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="Fat">
											</div>
										</div>
										
										<div class="form-group">
											<label for="Bone" class="col-sm-4 control-lavel">骨质</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="Bone">
											</div>
										</div>
										<div class="form-group">
											<label for="Protein" class="col-sm-4 control-lavel">蛋白质</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="Protein">
											</div>
										</div>
										
										<div class="form-group">
											<label for="Water" class="col-sm-4 control-lavel">水分</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="Water">
											</div>
										</div>
										
										<div class="form-group">
											<label for="Muscle" class="col-sm-4 control-lavel">肌肉</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="Muscle">
											</div>
										</div>
										
										
										<div class="form-group">
											<label for="SMM" class="col-sm-4 control-lavel">骨骼肌</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="SMM">
											</div>
										</div>
										
										<div class="form-group">
											<label for="PBF" class="col-sm-4 control-lavel">体脂百分比</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="PBF">
											</div>
										</div>
										
										
										<div class="form-group">
											<label for="BMI" class="col-sm-4 control-lavel">体质指数</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="BMI">
											</div>
										</div>
										
										
										<hr>

										<div class="form-group">
											<label for="BMR" class="col-sm-4 control-lavel">基础代谢</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="BMR">
											</div>
										</div>
										
										<div class="form-group">
											<label for="WHR" class="col-sm-4 control-lavel">腰臀比</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="WHR">
											</div>
										</div>
										
										<hr>		
										
										<div class="form-group">
											<label for="Edema" class="col-sm-4 control-lavel">水肿系数</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="Edema">
											</div>
										</div>
										<div class="form-group">
											<label for="VFI" class="col-sm-4 control-lavel">内脏脂肪指数</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="VFI">
											</div>
										</div>
										<div class="form-group">
											<label for="BodyAge" class="col-sm-4 control-lavel">身体年龄</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="BodyAge">
											</div>
										</div>
										<div class="form-group">
											<label for="Score" class="col-sm-4 control-lavel">健康评分</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="Score">
											</div>
										</div>
										<div class="form-group">
											<label for="BodyType" class="col-sm-4 control-lavel">体型判定</label>
											<div class="col-sm-8">
												<label><input name="BodyType"  type="radio" value="1">隐形肥胖型</label>
												<label><input name="BodyType"  type="radio" value="2">肌肉不足型</label>
												<label><input name="BodyType"  type="radio" value="3">消瘦型</label>
												<label><input name="BodyType"  type="radio" value="4">脂肪过多型</label>
												<label><input name="BodyType"  type="radio" value="5">健康匀称型</label>
												<label><input name="BodyType"  type="radio" value="6">低脂肪型</label>
												<label><input name="BodyType"  type="radio" value="7">肥胖型</label>
												<label><input name="BodyType"  type="radio" value="8">超重肌肉型</label>
												<label><input name="BodyType"  type="radio" value="9">运动员型</label>
											</div>
										</div>
										<hr>		
										<div class="form-group">
											<label for="LBM" class="col-sm-4 control-lavel">瘦体重（去脂体重）</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="LBM">
											</div>
										</div>
										
										<div class="form-group">
											<label for="ICW" class="col-sm-4 control-lavel">细胞内液</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="ICW">
											</div>
										</div>
										<div class="form-group">
											<label for="ECW" class="col-sm-4 control-lavel">细胞外液</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="ECW">
											</div>
										</div>
										<div class="form-group">
											<label for="Standard_weight" class="col-sm-4 control-lavel">目标体重</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="Standard_weight">
											</div>
										</div>
										<div class="form-group">
											<label for="Weight_control" class="col-sm-4 control-lavel">体重控制</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="Weight_control">
											</div>
										</div>
										<div class="form-group">
											<label for="Fat_control" class="col-sm-4 control-lavel">脂肪控制量</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="Fat_control">
											</div>
										</div>
										<div class="form-group">
											<label for="Muscle_control" class="col-sm-4 control-lavel">肌肉控制量</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="Muscle_control">
											</div>
										</div>
										<div class="form-group">
											<label for="LiverRisk" class="col-sm-4 control-lavel">脂肪肝风险系数</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="LiverRisk">
											</div>
										</div>
										<hr>
										<div class="form-group">
											<label for="TR_fat" class="col-sm-4 control-lavel">躯干脂肪量</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="TR_fat">
											</div>
										</div>
										<div class="form-group">
											<label for="LA_fat" class="col-sm-4 control-lavel">左上肢脂肪</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="LA_fat">
											</div>
										</div>
										<div class="form-group">
											<label for="RA_fat" class="col-sm-4 control-lavel">右上肢脂肪</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="RA_fat">
											</div>
										</div>
										<div class="form-group">
											<label for="LL_fat" class="col-sm-4 control-lavel">左下肢脂肪</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="LL_fat">
											</div>
										</div>
										<div class="form-group">
											<label for="RL_fat" class="col-sm-4 control-lavel">右下肢脂肪</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="RL_fat">
											</div>
										</div>
										<div class="form-group">
											<label for="TR_water" class="col-sm-4 control-lavel">躯干水分量</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="TR_water">
											</div>
										</div>
										<div class="form-group">
											<label for="LA_water" class="col-sm-4 control-lavel">左上肢水分</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="LA_water">
											</div>
										</div>
										<div class="form-group">
											<label for="RA_water" class="col-sm-4 control-lavel">右上肢水分</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="RA_water">
											</div>
										</div>
										<div class="form-group">
											<label for="LL_water" class="col-sm-4 control-lavel">左下肢水分</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="LL_water">
											</div>
										</div>
										<div class="form-group">
											<label for="RL_water" class="col-sm-4 control-lavel">右下肢水分</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="RL_water">
											</div>
										</div>
										<div class="form-group">
											<label for="TR_muscle" class="col-sm-4 control-lavel">躯干肌肉量</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="TR_muscle">
											</div>
										</div>
										<div class="form-group">
											<label for="LA_muscle" class="col-sm-4 control-lavel">左上肢肌肉</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="LA_muscle">
											</div>
										</div>
										<div class="form-group">
											<label for="RA_muscle" class="col-sm-4 control-lavel">右上肢肌肉</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="RA_muscle">
											</div>
										</div>
										<div class="form-group">
											<label for="LL_muscle" class="col-sm-4 control-lavel">左下肢肌肉</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="LL_muscle">
											</div>
										</div>
										<div class="form-group">
											<label for="RL_muscle" class="col-sm-4 control-lavel">右下肢肌肉</label>
											<div class="col-sm-8 controls">
												<input type="text" class="form-control" name="RL_muscle">
											</div>
										</div>
																			
										<div class="form-group">
											<label for="testDate" class="col-sm-4 control-lavel">*检测时间</label>
											<div class="col-sm-8 controls">
											<input  id="checkDate" name="testDate" class="date" onclick="addDateModule(this);" style="width:170px;padding:7px 10px;border:1px solid #ccc;margin-right:10px;"/> 
											</div>
											
										</div>
										<input class="btn btn-primary btn-lg" type="submit"  style="margin: 30px auto;display: block;" id="saveBtn" value="提交">
									</form>
                                 </div>
                             </div>
                         </div>
                     </div>
                   <!-- 以上这是页面需要编辑的区域 -->
   </div>

</body>

</html>