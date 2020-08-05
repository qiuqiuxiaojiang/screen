var magId=0;
var magIdt=0;
	jQuery(document).ready(function() {
		 if (jQuery().datepicker) {
	          $('.date-picker').datepicker({
	       	   language: "zh-CN",
	              rtl: App.isRTL(),
	              autoclose: true
	          });
	          $('body').removeClass("modal-open"); 
	      }
		 $("input[name='updateGoodsForm']").click(function(){
			   var val = $(this).val();
			   if(val == 1){
				   $("#updateGsp1").css("display","block");
				   $("#updateGsp2").css("display","none");
				   $("#updateGsc").css("display","none");
				   $("#updateGset").css("display","none");
			   }else if(val == 2){
				   $("#updateGsp2").css("display","block");
				   $("#updateGsp1").css("display","none");
				   $("#updateGset").css("display","block");
				   $("#updateGsc").css("display","none");
			   }else if(val == 3){
				   $("#updateGsp1").css("display","none");
				   $("#updateGsp2").css("display","none");
				   $("#updateGsc").css("display","block");
				   $("#updateGset").css("display","none");
			   }
		 });
		 $("input[name='updateGoodsServiceNoEndTime']").click(function() {  
			 if($(this).is(":checked")){
	        	 //事件控件不可点
	        	 $("#updateGoodsServiceEndTime").attr("disabled","disabled");
	        	 $("#btu2").attr("disabled","disabled");
	         }else{
	        	 $("#updateGoodsServiceEndTime").removeAttr("disabled");
	        	 $("#btu2").removeAttr("disabled");
	         }
	 	 });  
		 $("input[name='updateGoodsCode']").blur(function(){
			 checkUpdateGoodsCode($(this).val());
			
	     });
	});
	function funcUpdateResetGoodsInfo() {
		$("#updateGoodsCodeMsg").html("");
		$("#goodsCode").val("");
		$("#goodsName").val("");
		$("input[name='updateGoodsType']").parent().removeClass("checked");//商品分类
		$("input[name='updateGoodsType']").attr("checked",false);//商品分类
		$("input[name='updateGoodsForm']").parent().removeClass("checked");//商品分类
		$("input[name='updateGoodsForm']").attr("checked",false);//商品分类
		$("#goodsMarketTitle").val("");
		$("input[name='updateGoodsServiceCycle']").parent().removeClass("checked");//服务周期
		$("input[name='updateGoodsServiceCycle']").attr("checked",false);//服务周期
		$("#updateGoodsServiceEndTime").val("");
		$("input[name='updateGoodsServiceNoEndTime']").parent().removeClass("checked");//永久有效
		$("input[name='updateGoodsServiceNoEndTime']").attr("checked",false);//永久有效
		$("input[name='updateGoodsServicePromise']").parent().removeClass("checked");//服务承诺
		$("input[name='updateGoodsServicePromise']").attr("checked",false);//服务承诺
		$("#updateGsc").hide();
		$("#updateGset").hide();
		$("#updateGsp1").hide();
		$("#updateGoodsRule").hide();
		$("#updateRules tr").each(function(i){
			if(i!=0){
				$(this).remove();
			}
		});
		//商品图片
		//图文详情
		$("input[type='file']").remove();
		$("#datailGoodsImage").children("span").after("<input type='file' name='files' id='docd0'  class='tts'  onchange='javascript:setImagePreviewsD()' imgid='img0' >");
		//图文详情
		$("#detailImage").children("span").after("<input type='file' name='files' id='doctd0' imgid='imgt0' onchange='javascript:setImagePreviews1D()'>");
		magId=0;
		magIdt=0;
	}
	
	function updateGoodsInfo(id) {
		
		//先清空相关值
		funcUpdateResetGoodsInfo();
		$.ajax({
			type : "post",
			url : "${ctx}/goodsInfo/findGoodsInfoByIdAjax.c",
			dataType : "json",
			data : {
				"id" : id
			},
			success : function(data) {
				$("#ddd").children().each(function(i,n){
					$(this).remove();
				});
				$("#ddtd").children().each(function(i,n){
					$(this).remove();
				});
				if (data.success == "success") {
					//商品信息
					$("#pkId").val(data.goodsInfo.id);
					$("#oldGoodsCode").val(data.goodsInfo.goodsCode);
					$("#updateGoodsCode").val(data.goodsInfo.goodsCode);//商品编码
					$("#updateGoodsName").val(data.goodsInfo.goodsName);//商品名称
					$("input[name='updateGoodsType'][value="+data.goodsInfo.goodsType+"]").parent().attr("class","checked");//商品分类
					$("input[name='updateGoodsType'][value="+data.goodsInfo.goodsType+"]").attr("checked","checked");//商品分类
					var goodsForm = data.goodsInfo.goodsForm;//商品形式
					$("#updateGoodsForm1").val(goodsForm);
					var goodsServicePromise = data.goodsInfo.goodsServicePromise;//服务承诺
					var rules = data.goodsRule;//商品规则/套餐类型
					var arry = new Array();
					if(goodsForm==3){//线上服务类
						var isFreeTryOut = data.goodsInfo.isFreeTryOut;
						$("#updateGsc").show();
						//套餐类型
						if(rules.length>0){
							for(var i=0;i<rules.length;i++){
								var ruleItemPrice = rules[i].ruleItemPrice;
								var id = rules[i].id;
								$("#updateMeal tr").eq(i).find("input[type='text']").val(ruleItemPrice);
								$("#updateMeal tr").eq(i).attr("id",id);
							}
						}
						if(isFreeTryOut!=null&&isFreeTryOut==1){
							$("input[name='updateIsFreeTryOut']").attr("checked","checked");//新用户免费试用7天
							$("input[name='updateIsFreeTryOut']").parent().attr("class","checked");
						}
						$("#updateImage").text("(请上传1张图片为产品展示图片,推荐尺寸1080×810px,推荐大小200~500kb)");
						$("#updateUpperFrame").hide();
						$("#updateGoodsRule").hide();
						$("#updateRules").hide();
					}else if(goodsForm==2){//到店服务类
						$("#updateGsp1").hide();
						$("#updateGset").show();
						$("#updateGsp2").show();
						$("#updateUpperFrame").show();
						$("#updateGoodsRule").show();
						$("#updateRules").show();
						var goodsServiceNoEndTime = data.goodsInfo.goodsServiceNoEndTime;
						if(goodsServiceNoEndTime==1){
							var goodsServiceEndTime = data.goodsInfo.goodsServiceEndTime;
							$("#updateGoodsServiceEndTime").val(new Date(goodsServiceEndTime).Format("yyyy-MM-dd"));
						}else if(goodsServiceNoEndTime==2){
							$("input[name='updateGoodsServiceNoEndTime'][value=2]").parent().attr("class","checked");
							$("input[name='updateGoodsServiceNoEndTime'][value=2]").attr("checked","checked");
						}
						arry = goodsServicePromise.split(",");
						for(var i=0;i<arry.length;i++){
							$("input[name='updateGsp'][value="+arry[i]+"]").parent().attr("class","checked");
							$("input[name='updateGsp'][value="+arry[i]+"]").attr("checked","checked");
						}
					}else if(goodsForm==1){//实物类
						$("#updateGsp1").show();
						$("#updateGset").hide();
						$("#updateGsp2").hide();
						$("#updateUpperFrame").show();
						$("#updateGoodsRule").show();
						$("#updateRules").show();
						arry = goodsServicePromise.split(",");
						for(var i=0;i<arry.length;i++){
							$("input[name='updateGsp'][value="+arry[i]+"]").parent().attr("class","checked");
							$("input[name='updateGsp'][value="+arry[i]+"]").attr("checked","checked");
							if(arry[i]==2){
								$("#updateFreeReturnDays").val(data.goodsInfo.freeReturnDays);
							}
						}
					}
					
					if(goodsForm!=3){
						//商品规则
						if(rules.length>0){
							for(var i=0;i<rules.length;i++){
								var indexNum = rules[i].orderId;
								var ruleItemName = rules[i].ruleItemName;
								var ruleItemPrice = rules[i].ruleItemPrice;
								var ruleItemStock = rules[i].ruleItemStock;
								var updateStr = "<a href='#divUpdateAddRule' class='btn default yellow-stripe' data-toggle='modal' onclick='updateUTr(this)'>编辑</a>"
								var deleteStr = "<a href='javascript:void(0);' class='btn default yellow-stripe' data-toggle='modal' onclick='deleteUpdateTr(this)'>删除</a>";
								var trStr = "<tr id="+rules[i].id+">";
								    trStr += "<td>"+indexNum+"</td><td>"+ruleItemName+"</td><td>"+ruleItemPrice+"</td><td>"+ruleItemStock+"</td>";
								    trStr +="<td>"+updateStr+deleteStr+"</td>";
								    trStr +="</tr>";
								$("#updateRules").find("tbody").append(trStr);
							}
						}
					}
					$("input[name='updateGoodsForm'][value="+goodsForm+"]").parent().attr("class","checked");
					$("input[name='updateGoodsForm'][value="+goodsForm+"]").attr("checked","checked");
					$("#updateGoodsMarketTitle").val(data.goodsInfo.goodsMarketTitle);
					$("input[name='updateIsUpperFrame'][value="+data.goodsInfo.isUpperFrame+"]").parent().attr("class","checked");
					$("input[name='updateIsUpperFrame'][value="+data.goodsInfo.isUpperFrame+"]").attr("checked","checked");
					
					//商品图片
					var image = data.goodsImage;
					if(image.length>0){
						for(var i=0;i<image.length;i++){
							var imageUrl = "${ctx }/oss/img/"+image[i].imageUrl+".c";
							$("#ddd").append("<div class='img-wrap' imgid='img"+i+"'><div class='tip'><span class='up'></span><span class='down'></span><span class=' closeimg"+i+" close1'>×</span></div><div class='pic'><img src='"+imageUrl+"' id='"+arr[i]+"' style='display:block;'/></div></div>");
						     $(".closeimg"+i).on("click",function(e){
						            var id=$(this).parents(".img-wrap").attr("imgid");
						            $(this).parents(".img-wrap").remove();
						     });
						     magId++;
						}
						$("#datailGoodsImage input[name='files']").attr("id","docd"+magId).attr("imgid","img"+magId);
					}
					//商品图文详情图片
					var imageD = data.goodsInfoImage;
					if(imageD.length>0){
						for(var i=0;i<imageD.length;i++){
							var imageUrl = "${ctx }/oss/img/"+imageD[i].imageUrl+".c";
							 $("#ddtd").append("<div class='img-wrap' imgid='img"+i+"'><div class='pic'><img src='"+imageUrl+"' id='"+imageD[i].id+"' style='display:block;'/></div><div class='tip'><span class='up'>&lt;</span><span class='down'>&gt;</span><span class=' closeimg"+i+" close1'>×</span></div></div>");
						     $(".closeimg"+i).on("click",function(e){
						            var id=$(this).parents(".img-wrap").attr("imgid");
						            $(this).parents(".img-wrap").remove();
						     });
						     magIdt++;
						}
						$("#detailImage input[name='files']").attr("id","doctd"+magIdt).attr("imgid","imgt"+magIdt);
					}
				} else {
					toastr["error"]("请联系管理员！", "系统通知");
				}
			}
		});
	}
	
	function deleteUpdateTr(index){
		 $(index).parent().parent().remove();
		 $('#updateRules tr').each(function(i){
			if(i!=0){
				var index = i;
				$(this).find("td:first").html(i);
			}
		});
	}

	function updateUTr(index){
		var indexNum = $(index).parent().parent().find("td").eq(0).text();
		var ruleItemName = $(index).parent().parent().find("td").eq(1).text();
		var ruleItemPrice = $(index).parent().parent().find("td").eq(2).text();
		var ruleItemStock = $(index).parent().parent().find("td").eq(3).text();
		$("#updateIndexNum").val(indexNum);
		$("#updateRuleItemName").val(ruleItemName);
		$("#updateRuleItemPrice").val(ruleItemPrice);
		$("#updateRuleItemStock").val(ruleItemStock);
	}
	
	function checkUpdateGoodsCode(goodsCode){
		var reg = /^[0-9a-zA-Z,-_]{0,20}$/;
		var oldGoodsCode = $("#oldGoodsCode").val()
		if(goodsCode=="" || goodsCode ==undefined || goodsCode==null){
			$("#updateGoodsCodeMsg").text('请输入商品编码');
			$("#updateGoodsCodeMsg").css("display","block");
			return;
		}
		var r = goodsCode.match(reg);
		if (r == null || (r!=null && goodsCode.indexOf("@")!=-1)) {
			$("#updateGoodsCodeMsg").text('商品编码必须为数字和字母或中间下划线组合，长度为20位');
			$("#updateGoodsCodeMsg").css("display","block");
			return;
		}else if(oldGoodsCode!=goodsCode){
			$.ajax({
				type : "post",
				url : "${ctx}/goodsInfo/checkGoodsCodeAjax.c",
				dataType : "json",
				data : {
					"goodsCode" : goodsCode
				},
				success : function(data) {
					if (data.success == "success") {
						$("#updateGoodsCodeMsg").text('商品编码已存在');
						$("#updateGoodsCodeMsg").css("display","block");
					}else{
						//清空文案提示
						$("#updateGoodsCodeMsg").text('');
						$("#updateGoodsCodeMsg").css("display","none");
					}
				}
			});
		}
	}
	function checkUpdateSubmit(){
		var goodsCode = $("#updateGoodsCode").val();
		checkUpdateGoodsCode($("#updateGoodsCode").val());
		if($("#updateGoodsCodeMsg").css("display")=="block"){
			return false;
		}
		var goodsName = $("#updateGoodsName").val();
		if(goodsName==null||goodsName==""||goodsName==undefined){
			toastr["info"]("请填写商品名称！", "");
			return false;
		}
		
		if(!$("input[name='updateGoodsType']").is(":checked")){
			toastr["info"]("请选择商品分类！", "");
			return false;
		}
		if(!$("input[name='updateGoodsForm']").is(":checked")){
			toastr["info"]("请选择商品形式！", "");
			return false;
		}
		var goodsMarketTitle = $("#updateGoodsMarketTitle").val();
		if(goodsMarketTitle==null||goodsMarketTitle==""||goodsMarketTitle==undefined){
			toastr["info"]("请填写营销标题！", "");
			return false;
		}
		//上传商品图片未做校验
		var goodsImages = $("#ddd").children().length;
		var goodsForm = $("input[name='updateGoodsForm']:checked").val();
		if(goodsForm!=3){
			if(goodsImages>8 || goodsImages==0){
				toastr["info"]("请上传1-8张商品图片！", "");
				return false;
			}
		}else{//线上服务类型商品形式--校验套餐类型
			if(goodsImages>1 || goodsImages==0){
				toastr["info"]("请上传1张商品图片！", "");
				return false;
			}
		}
		
		if(goodsForm==1){
			if(!$("input[name='updateGsp']:checked").length>0){
				toastr["info"]("请选择服务承诺！", "");
				return false;
			}else{
				var flag = true;
				$("input[name='updateGsp']:checked").each(function() {
					if($(this).val()==2){
						var freeReturnDays = $("#updateFreeReturnDays").val();
						if(freeReturnDays==null||freeReturnDays==""||freeReturnDays==undefined || freeReturnDays==0){
							toastr["info"]("请填写无忧退货期限！", "");
							flag = false;
						}
					}
				});
				if(!flag){
					return false;
				}
			}
		}else if(goodsForm==2){
			var goodsServiceEndTime = $("#updateGoodsServiceEndTime").val();
			if((goodsServiceEndTime==null||goodsServiceEndTime==""||goodsServiceEndTime==undefined)&&
				$("input[name='updateGoodsServiceNoEndTime']:checked").val()!=2){
				toastr["info"]("请填写服务截止时间或者勾选永久有效！", "");
				return false;
			}
			if(!$("input[name='updateGsp']:checked").length>0){
				toastr["info"]("请选择服务承诺！", "");
				return false;
			}
		}else if(goodsForm==3){
			//线上服务类商品形式--套餐类型
			var flag = true;
			var prices = "";
			$("#updateMeal tr").each(function(i){
				if(i<4){
					var t = false; 
					var ta = $(this).find("input[type='text']").val();
					var index = $(this).find("input[type='text']").attr("id")+'';
					index = index.replace('#','');
					if($(this).find("input[type='text']").val()==undefined || $(this).find("input[type='text']").val()==null 
						|| $(this).find("input[type='text']").val()==""){
						t=true;
					}
					var txt = "";
					var mealName = "";
					/* if(i==0){
						if(t){
							txt = "请填写连续包月费用！";
						}
						mealName = "连续包月";
					}else  */if(i==0){
						if(t){
							txt = "请填写1个月费用！";
						}
						mealName = "1个月";
					}else if(i==1){
						if(t){
							txt = "请填写3个月费用！";
						}
						mealName = "3个月";
					}else if(i==2){
						if(t){
							txt = "请填写6个月费用！";
						}
						mealName = "6个月";
					}else if(i==3){
						if(t){
							txt = "请填写1年费用！";
						}
						mealName = "1年";
					}
					$("#updateMealMsg").text(txt);
					if(t){
						$("#updateMealMsg").css("display","block");
						flag = false;
						return false;
					}else{
						var id = $(this).attr("id");
						prices +=mealName+","+ta+","+id+","+index+"@";
						flag = true;
						$("#updateMealMsg").css("display","none");
					}
				}
			});
			if(!flag){
				return false;
			}else{
				$("#updateMealPrices").val(prices);
			}
		}
		
		
		if(goodsForm!=3){
			//处理规格/价格/库存
			var ruleInfo = "";
			var ruleInfoIds = "";
			$('#updateRules tr').each(function(i){
				if(i!=0){
					var ruleId = $(this).attr("id");
					var ruleName = $(this).find("td:eq(1)").html();
					var rulePrice = $(this).find("td:eq(2)").html();
					var ruleStock = $(this).find("td:eq(3)").html();
					if(ruleId!=undefined){
						ruleInfo += ruleName+","+rulePrice+","+ruleStock+","+ruleId+"@";
					}else{
						ruleInfo += ruleName+","+rulePrice+","+ruleStock+"@";
					}
					
				}
			});
			if(ruleInfo==""){
				toastr["info"]("请增加规则项！", "");
				return false;
			}
			$("#updateRuleInfo").val(ruleInfo);//规则项信息
			var goodsForm = $("input[name='updateGoodsForm']:checked").val();
			var goodsServicePromise = "";
			if(goodsForm==1){//实体类
				$("#updateGsp1").find("input[name='updateGsp']:checked").each(function() {
					goodsServicePromise += $(this).val() + ",";
				});
			}else if(goodsForm==2){//到店服务类
				$("#updateGsp2").find("input[name='updateGsp']:checked").each(function() {
					goodsServicePromise += $(this).val() + ",";
				});
			}
			if(goodsServicePromise==""){
				toastr["info"]("请选择服务承诺！", "");
				return false;
			}
			$("#updateGoodsServicePromise").val(goodsServicePromise);
		}
		
		//图文详情校验
		var goodsDetailImages = $("#ddtd").children().length;
		if(goodsDetailImages>20 || goodsDetailImages==0){
			toastr["info"]("请上传1-20张图文详情的图片！", "");
			return false;
		}
		return true;
	}
	
	function addupdateGoodsInfoSubmit() {
		if(checkUpdateSubmit()){
			$(".shade1").show();
	        var H = $(".shade1 img").height();
	        $(".shade1 span").height(H).css("line-height",H+"px");
			$.ajax({
				type : "post",
				url : "${ctx}/goodsInfo/updateGoodsInfo.c",
				data : $('#myFormUpdateGoodsInfo').serialize(),
				beforeSend : function(
						XMLHttpRequest) {
					App.startPageLoading();
				},
				success : function(data) {
					App.stopPageLoading();
					if (data.success == "success") {
						var id = data.id;
						if(id!=null&&id!=""){
							 //上传商品图片
							//获取图片顺序
						    var fromData = new FormData($("#datailGoodsImage").children("input[type='file']")[0]);
							var imageOrder = "";//原有图片的顺序
							var fileOrder = "";//上传的图片的顺序
							 $("#ddd >div").each(function(i){
								 var id = $(this).find("img").attr("id");
								 if(id!=null&&id.indexOf("img")==-1){//表示是原上传图片，则需拼装一下
									 imageOrder +=id+","+i+"@";
								 }else{
									 fileOrder +=i+",";
									 var imgId = $(this).attr("imgid");
									 var t = $("#datailGoodsImage").children("input[type='file'][imgid='"+imgId+"']")[0].files[0];
									 fromData.append("file",$("#datailGoodsImage").children("input[type='file'][imgid='"+imgId+"']")[0].files[0]);
								 }
							 });
							 fromData.append("id",id);
							 fromData.append("imageType",1);
							 fromData.append("imageOrder",imageOrder);
							 fromData.append("fileOrder",fileOrder);
							$.ajax({
								url:"${ctx}/goodsInfo/uploadUpdateImage.c",
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

							var fromDataDetail = new FormData($("#detailImage").children("input[type='file']")[0]);
							var imageOrderD = "";//原有图片的顺序
							var fileOrderD = "";//上传的图片的顺序
							$("#ddtd >div").each(function(i){
								 var id = $(this).find("img").attr("id");
								 if(id!=null&&id.indexOf("img")==-1){//表示是原上传图片，则需拼装一下
									 imageOrderD +=id+","+i+"@";
								 }else{
									 fileOrderD +=i+",";
									 var imgId = $(this).attr("imgid");
									 var t = $("#detailImage").children("input[type='file'][imgid='"+imgId+"']")[0].files[0];
									 fromDataDetail.append("file",$("#detailImage").children("input[type='file'][imgid='"+imgId+"']")[0].files[0]);
								 }
								 
							 });
							 fromDataDetail.append("id",id);
							 fromDataDetail.append("imageType",2);
							 fromDataDetail.append("imageOrder",imageOrderD);
							 fromDataDetail.append("fileOrder",fileOrderD);
							$.ajax({
								url:"${ctx}/goodsInfo/uploadUpdateImage.c",
								contentType: "multipart/form-data",
								type:"POST",
								data:fromDataDetail,
								dataType:"text",
								processData: false,
								contentType: false,
								cache: false,
								beforeSend : function(){
								},
								success : function(data){
								}

							}); 
								 
							setTimeout("close('修改成功','#divUpdateGoodsInfo')",2000);//2秒，可以改动 
								
						}
						
						/* toastr["success"]("更新成功！", "");
						funcQueryGoodsInfo(1);
						$("#divUpdateGoodsInfo").modal("hide"); */
					} else {
						toastr["error"]( "请联系管理员！", "系统通知");
					}
				},
				error : function(xhr,
						ajaxOptions,
						thrownError) {
					App.stopPageLoading();
				}
			});
		}
	}
	
	function setImagePreviewsD(){
		debugger;
	    var docobj=document.getElementById("docd"+magId);
	    var dd=document.getElementById("ddd");
	    var fileList=docobj.files;
	    for( var i=0;i<fileList.length;i++){
	        if(!fileChange(docobj.files[i])){
	            return;
	        };
	        //magId = $("#ddd").children().length+1;
	        magId ++;
	        var inputImg = $("#datailGoodsImage input").last().attr("imgid");//最后一个file框
	        if(inputImg=="img0"){
	        	$("#datailGoodsImage input").last().attr("imgid","img"+(magId-1));
	        	inputImg = "img"+(magId-1);
	        }
	       // $(dd).append("<div class='img-wrap' imgid='img"+(magId-1)+"'><div class='pic'><img id='img"+(magId-1)+"'/></div><div class='tip'><span class='up'>&lt;</span><span class='down'>&gt;</span><span class=' closeimg"+(magId-1)+" close1'>×</span></div></div>");
	        $(dd).append("<div class='img-wrap' imgid='img"+(magId-1)+"'><div class='tip'><span class=' closeimg"+(magId-1)+" close1'>×</span></div><div class='pic'><img id='img"+(magId-1)+"'/></div></div>");
	        $(".detail.fileinput-wrap [imgid="+inputImg+"]")[0].style.position="absolute";
	        $(".detail.fileinput-wrap [imgid="+inputImg+"]")[0].style.left="-10000px";
	        $(".detail.fileinput-wrap>span").first().append('<input type="file"  name="files" id="docd'+magId+'" imgid="img'+magId+'" onchange="javascript:setImagePreviewsD()" />');
	        $(".closeimg"+(magId-1)).on("click",function(e){
	       		 var id=$(this).parents(".img-wrap").attr("imgid");
	             $(this).parents('.img-wrap').remove();
	             $(".fileinput-wrap input[imgid="+id+"]").remove();
	        });
	        var imgObjPreview=document.getElementById("img"+(magId-1));
	        if(docobj.files&&docobj.files[i]){
	            imgObjPreview.style.display="block";
	            imgObjPreview.src=window.URL.createObjectURL(docobj.files[i]);
	        }else{
	            //IE
	            docobj.select();
	            var imgsrc=document.selection.createRange().text;
	            var localImageId=document.getElementById("img"+(magId-1));
	            try{
	                localIamgeId.style.filter="progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale)";
	                localIamgeId.filters.item("DXImageTransform.Microsoft.AlphaImageLoader").src=imgsrc;
	            }catch(e){
	            	toastr["info"]("上传图片出错！", "");
	                return false;
	            }
	            imgObjPreview.style.display="none";
	            document.selection.empty();
	        }
	    }
	    return true;
	}





	function setImagePreviews1D(){
		debugger;
	    var docobj=document.getElementById("doctd"+magIdt);
	    var ddt=document.getElementById("ddtd");
	    var fileList=docobj.files;
	    for( var i=0;i<fileList.length;i++){

	        if(!fileChange(docobj.files[i])){
	            return;
	        };
	       // magIdt = $("#ddtd").children().length+1;
	       magIdt ++;
	        var inputImg = $("#detailImage input").last().attr("imgid");
	        if(inputImg=="imgt0"){
	        	$("#detailImage input").last().attr("imgid","imgt"+(magIdt-1));
	        	inputImg = "imgt"+(magIdt-1);
	        }
	        $(ddt).append("<div class='img-wrap' imgid='imgt"+(magIdt-1)+"'><div class='pic'><img id='imgt"+(magIdt-1)+"'/></div><div class='tip'><span class='up'>&lt;</span><span class='down'>&gt;</span><span class=' closeimgt"+(magIdt-1)+" close1'>×</span></div></div>");
	        $(".detail.newfileinput-wrap [imgid="+inputImg+"]")[0].style.position="absolute";
	        $(".detail.newfileinput-wrap [imgid="+inputImg+"]")[0].style.left="-10000px";
	        $(".detail.newfileinput-wrap>span").first().append('<input type="file"  name="files"  id="doctd'+magIdt+'" imgid="imgt'+magIdt+'" style="width:100px;" onchange="javascript:setImagePreviews1D()" />');
	        $(".closeimgt"+(magIdt-1)).on("click",function(e){
	            var id=$(this).parents(".img-wrap").attr("imgid");
	            $(this).parents(".img-wrap").remove();
	            $(".newfileinput-wrap input[imgid="+id+"]").remove();
	        })
	        var imgObjPreview=document.getElementById("imgt"+(magIdt-1));


	        if(docobj.files&&docobj.files[i]){
	            imgObjPreview.style.display="block";
	            imgObjPreview.src=window.URL.createObjectURL(docobj.files[i]);
	        }else{
	            //IE
	            docobj.select();
	            var imgsrc=document.selection.createRange().text;
	            var localImageId=document.getElementById("imgt"+(magIdt-1));
	            try{
	                localIamgeId.style.filter="progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale)";
	                localIamgeId.filters.item("DXImageTransform.Microsoft.AlphaImageLoader").src=imgsrc;
	            }catch(e){
	            	toastr["info"]("上传图片出错！", "");
	                return false;
	            }
	            imgObjPreview.style.display="none";
	            document.selection.empty();
	        }
	    }
	    return true;
	}
	$("#ddd").on('click', 'span',function(event) {
	    event.preventDefault;
	    var parent=$(this).parents('.img-wrap');
	    var parents=$(this).parents("#dd");
	    var len=parents.children().length;
	    if(($(this).is(".up") || $(this).is(".top")) && parent.index()==0){
	    	toastr["info"]("已经置顶！", "");
	        return false;
	    }else if(($(this).is(".down") || $(this).is(".bottom")) && parent.index()==len-1){
	    	toastr["info"]("已经置底！", "");
	        return false;
	    }
	    switch (true) {
	        case $(this).is(".up"):
	            var prev = parent.prev();
	            parent.insertBefore(prev);
	            break;
	        case $(this).is(".down"):
	            var next = parent.next();
	            parent.insertAfter(next);
	            break;
	        case $(this).is(".top"):
	            parents.prepend(parent);
	            break;
	        case $(this).is(".bottom"):
	            parents.append(parent);
	            break;
	    }
	});
	$("#ddtd").on('click', 'span',function(event) {
	    event.preventDefault;
	    var parent=$(this).parents('.img-wrap');
	    var parents=$(this).parents("#ddt");
	    var len=parents.children().length;
	    if(($(this).is(".up") || $(this).is(".top")) && parent.index()==0){
	    	toastr["info"]("已经置顶！", "");
	        return false;
	    }else if(($(this).is(".down") || $(this).is(".bottom")) && parent.index()==len-1){
	    	toastr["info"]("已经置底！", "");
	        return false;
	    }
	    switch (true) {
	        case $(this).is(".up"):
	            var prev = parent.prev();
	            parent.insertBefore(prev);
	            break;
	        case $(this).is(".down"):
	            var next = parent.next();
	            parent.insertAfter(next);
	            break;
	        case $(this).is(".top"):
	            parents.prepend(parent);
	            break;
	        case $(this).is(".bottom"):
	            parents.append(parent);
	            break;
	    }
	});

	$("#ddd").on('click', '.pic',function(event) {
 	    event.preventDefault;
 	    var top =$(document).scrollTop(),
 	    	H = $("section.content").height();
 	    $("body").css("overflow-y","hidden");
 	    var html=$(this).html();
 	    $(".shade").show().css("top",top).css("height",H).html(html);
 	}); 
	$("#ddtd").on('click', '.pic',function(event) {
	    event.preventDefault;
	    var html=$(this).html();
	    $(".shade").show().html(html);
	});