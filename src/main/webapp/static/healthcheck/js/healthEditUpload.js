
jQuery(document).ready(function() {
	 if (jQuery().datepicker) {
          $('.date-picker').datepicker({
       	   language: "zh-CN",
              rtl: App.isRTL(),
              autoclose: true
          });
          $('body').removeClass("modal-open"); 
      }
	 $("input[name='goodsForm']").click(function(){
		   var val = $(this).val();
		   if(val == 1){
			   $("#gsp1").css("display","block");
			   $("#gsp2").css("display","none");
			   $("#gsc").css("display","none");
			   $("#gset").css("display","none");
			   $("#goodsRule").css("display","block");
			   $("#rules").css("display","block");
			   $("#upperFrame").css("display","block");
			   $("#image1").text("(请上传1-8张图片,默认第1张为产品展示图片,推荐尺寸1080×810px,推荐大小200~500kb)");
		   }else if(val == 2){
			   $("#gsp2").css("display","block");
			   $("#gsp1").css("display","none");
			   $("#gset").css("display","block");
			   $("#gsc").css("display","none");
			   $("#goodsRule").css("display","block");
			   $("#rules").css("display","block");
			   $("#upperFrame").css("display","block");
			   $("#image1").text("(请上传1-8张图片,默认第1张为产品展示图片,推荐尺寸1080×810px,推荐大小200~500kb)");
		   }else if(val == 3){
			   $("#gsp1").css("display","none");
			   $("#gsp2").css("display","none");
			   $("#gsc").css("display","block");
			   $("#gset").css("display","none");
			   $("#goodsRule").css("display","none");
			   $("#rules").css("display","none");
			   $("#upperFrame").css("display","none");
			   $("#image1").text("(请上传1张图片为产品展示图片,推荐尺寸1080×810px,推荐大小200~500kb)");
		   }
	 });
	 $("input[name='goodsServiceNoEndTime']").click(function() {  
         if($(this).is(":checked")){
        	 //事件控件不可点
        	 $("#goodsServiceEndTime").attr("disabled","disabled");
        	 $("#btu1").attr("disabled","disabled");
         }else{
        	 $("#goodsServiceEndTime").removeAttr("disabled");
        	 $("#btu1").removeAttr("disabled");
         }
 	 });  
//	 $("input[name='goodsCode']").blur(function(){
//		 checkGoodsCode($(this).val());
//     });
	 
//	 $("#addSave").keydown(function(event){
//		 var e = event || window.event; //兼容ie
//		  switch(e.keyCode) {
//		    case 13: // enter 键
//		      if (!window.buttonIsFocused){
//		    	  addGoodsInfoSubmit();
//		      }; 
//		      break; 
//		  }
//	 });
	 document.onkeydown = function(event){
		  var e = event || window.event; //兼容ie
		  switch(e.keyCode) {
		    case 13: // enter 键
		      if (!window.buttonIsFocused){
		    	  addGoodsInfoSubmit();
		      }; 
		      break; 
		  }
		};
});
function cancelOperation(index,str){
	if(confirm(str)){
		$(index).modal("hide");
	}
}

//function checkGoodsCode(goodsCode){
//	var reg = /^[0-9a-zA-Z,-_]{0,20}$/;
//	if(goodsCode=="" || goodsCode ==undefined || goodsCode==null){
//		$("#goodsCodeMsg").text('请输入商品编码');
//		$("#goodsCodeMsg").css("display","block");
//		return;
//	}
//	var r = goodsCode.match(reg);
//	//var r = reg.test(goodsCode);
//	debugger;
//	if (r==null || (r!=null && goodsCode.indexOf("@")!=-1)) {
//		$("#goodsCodeMsg").text('商品编码必须为数字和字母或中间下划线组合，长度为20位');
//		$("#goodsCodeMsg").css("display","block");
//		return;
//	}else{
//		$.ajax({
//			type : "post",
//			url : $('#ctx').val()+"/goodsInfo/checkGoodsCodeAjax.c",
//			dataType : "json",
//			data : {
//				"goodsCode" : goodsCode
//			},
//			success : function(data) {
//				if (data.success == "success") {
//					$("#goodsCodeMsg").text('商品编码已存在');
//					$("#goodsCodeMsg").css("display","block");
//				}else{
//					//清空文案提示
//					$("#goodsCodeMsg").text('');
//					$("#goodsCodeMsg").css("display","none");
//				}
//			}
//		});
//	}
//}
	function deleteTr(index){
		 $(index).parent().parent().remove();
		 $('#rules tr').each(function(i){
			if(i!=0){
				var index = i;
				$(this).find("td:first").html(i);
			}
		});
	}

	function updateTr(index){
		var indexNum = $(index).parent().parent().find("td").eq(0).text();
		var ruleItemName = $(index).parent().parent().find("td").eq(1).text();
		var ruleItemPrice = $(index).parent().parent().find("td").eq(2).text();
		var ruleItemStock = $(index).parent().parent().find("td").eq(3).text();
		$("#indexNum").val(indexNum);
		$("#ruleItemName").val(ruleItemName);
		$("#ruleItemPrice").val(ruleItemPrice);
		$("#ruleItemStock").val(ruleItemStock);
	}
	
	function funcResetGoodsInfo() {
		$("#goodsCodeMsg").html("");
		$("#goodsCode").val("");
		$("#goodsName").val("");
		$("input[name='goodsType']").parent().removeClass("checked");//商品分类
		$("input[name='goodsType']").attr("checked",false);//商品分类
		$("input[name='goodsForm']").parent().removeClass("checked");//商品分类
		$("input[name='goodsForm']").attr("checked",false);//商品分类
		$("#goodsMarketTitle").val("");
		
		//商品图片
		$("input[type='file']").remove();
		$("#image").children("span").after("<input type='file' name='files' id='doc0'  class='tts'  onchange='javascript:setImagePreviews()' imgid='img0' >");
		//图文详情
		$("#goodsDetailImage").children("span").after("<input type='file' name='files' id='doct0' imgid='imgt0' onchange='javascript:setImagePreviews1()'>");
		$("#dd").children().remove();
		$("#ddt").children().remove();
		magId=0;
		magIdt=0;
		
		$("input[name='goodsServiceCycle']").parent().removeClass("checked");//服务周期
		$("input[name='goodsServiceCycle']").attr("checked",false);//服务周期
		$("#goodsServiceEndTime").val("");
		$("input[name='goodsServiceNoEndTime']").parent().removeClass("checked");//永久有效
		$("input[name='goodsServiceNoEndTime']").attr("checked",false);//永久有效
		$("input[name='goodsServicePromise']").parent().removeClass("checked");//服务承诺
		$("input[name='goodsServicePromise']").attr("checked",false);//服务承诺
		
		$("#gsc").hide();
		$("#gset").hide();
		$("#gsp1").hide();
		$("#gsp2").hide();
		
		//清空规则
		$("#rules tbody tr").each(function(i){
			if(i!=0){
				$(this).remove();
			}
		});
		//清空套餐类型
		$("#meal input:text").each(function(i){
			$(this).val("");
		});
		$("#meal input:checkbox").each(function(i){
			$(this).attr("checked",false);
			$(this).parent().removeClass();
		});
		//商品上下架
		$("input[name='isUpperFrame']").parent().removeClass("checked");//商品上下架
		$("input[name='isUpperFrame']").attr("checked",false);//商品上下架
		//定位为上架
		$("input[name='isUpperFrame'][value='1']").parent().addClass("checked");//商品上架
		$("input[name='isUpperFrame'][value='1']").attr("checked",true);//商品上架
	}
	
//	function checkSubmit(){
//		var goodsCode = $("#goodsCode").val();
//		checkGoodsCode($("#goodsCode").val());
//		if($("#goodsCodeMsg").css("display")=="block"){
//			return false;
//		}
//		var goodsName = $("#goodsName").val();
//		if(goodsName==null||goodsName==""||goodsName==undefined){
//			toastr["info"]("请填写商品名称！", "");
//			return false;
//		}
//		
//		if(!$("input[name='goodsType']").is(":checked")){
//			toastr["info"]("请选择商品分类！", "");
//			return false;
//		}
//		if(!$("input[name='goodsForm']").is(":checked")){
//			toastr["info"]("请选择商品形式！", "");
//			return false;
//		}
//		var goodsMarketTitle = $("#goodsMarketTitle").val();
//		if(goodsMarketTitle==null||goodsMarketTitle==""||goodsMarketTitle==undefined){
//			toastr["info"]("请填写营销标题！", "");
//			return false;
//		}
//		//上传商品图片未做校验
//		var goodsImages = $("#dd").children().length;
//		var goodForm = $("input[name='goodsForm']:checked").val();
//		if(goodForm!=3){
//			if(goodsImages>8 || goodsImages==0){
//				toastr["info"]("请上传1-8张商品图片！", "");
//				return false;
//			}
//		}else{//线上服务类型商品形式--校验套餐类型
//			if(goodsImages>1 || goodsImages==0){
//				toastr["info"]("请上传1张商品图片！", "");
//				return false;
//			}
//		}
//		
//		
//		if(goodForm==1){
//			if($("#gsp1 input[name='gsp']:checked").length==0){
//				toastr["info"]("请选择服务承诺！", "");
//				return false;
//			}else{
//				var flag = true;
//				$("input[name='gsp']:checked").each(function() {
//					if($(this).val()==2){
//						var freeReturnDays = $("#freeReturnDays").val();
//						if(freeReturnDays==null||freeReturnDays==""||freeReturnDays==undefined || freeReturnDays==0){
//							toastr["info"]("请填写无忧退货期限！", "");
//							flag = false;
//						}
//					}
//				});
//				if(!flag){
//					return false;
//				}
//			}
//		}else if(goodForm==2){
//			var goodsServiceEndTime = $("#goodsServiceEndTime").val();
//			if((goodsServiceEndTime==null||goodsServiceEndTime==""||goodsServiceEndTime==undefined)&&
//				$("input[name='goodsServiceNoEndTime']:checked").val()!=2){
//				toastr["info"]("请填写服务截止时间或者勾选永久有效！", "");
//				return false;
//			}
//			if($("#gsp2 input[name='gsp']:checked").length==0){
//				toastr["info"]("请选择服务承诺！", "");
//				return false;
//			}
//		}else if(goodForm==3){
//			//线上服务类商品形式--套餐类型
//			var flag = true;
//			var prices = "";
//			$("#meal tr").each(function(i){
//			debugger;
//				if(i<4){
//					var t = false; 
//					var ta = $(this).find("input[type='text']").val();
//					var id = $(this).find("input[type='text']").attr("id");
//					if($(this).find("input[type='text']").val()==undefined || $(this).find("input[type='text']").val()==null 
//						|| $(this).find("input[type='text']").val()==""){
//						t=true;
//					}
//					var txt = "";
//					var mealName = "";
//					/*if(i==0){
//						if(t){
//							txt = "请填写连续包月费用！";
//						}
//						mealName = "连续包月";
//					}else */if(i==0){
//						if(t){
//							txt = "请填写1个月费用！";
//						}
//						mealName = "1个月";
//					}else if(i==1){
//						if(t){
//							txt = "请填写3个月费用！";
//						}
//						mealName = "3个月";
//					}else if(i==2){
//						if(t){
//							txt = "请填写6个月费用！";
//						}
//						mealName = "6个月";
//					}else if(i==3){
//						if(t){
//							txt = "请填写1年费用！";
//						}
//						mealName = "1年";
//					}
//					$("#mealMsg").text(txt);
//					if(t){
//						$("#mealMsg").css("display","block");
//						flag = false;
//						return false;
//					}else{
//						prices +=mealName+","+ta+","+id+"@";
//						flag = true;
//						$("#mealMsg").css("display","none");
//					}
//				}
//			});
//			debugger;
//			if(!flag){
//				return false;
//			}else{
//				$("#mealPrices").val(prices);
//			}
//		}
//		
//		//处理规格/价格/库存
//		if(goodForm!=3){
//			var ruleInfo = "";
//			$('#rules tr').each(function(i){
//				if(i!=0){
//					var ruleName = $(this).find("td:eq(1)").html();
//					var rulePrice = $(this).find("td:eq(2)").html();
//					var ruleStock = $(this).find("td:eq(3)").html();
//					ruleInfo += ruleName+","+rulePrice+","+ruleStock+"@";
//				}
//			});
//			if(ruleInfo==""){
//				toastr["info"]("请增加规则项！", "");
//				return false;
//			}
//			$("#ruleInfo").val(ruleInfo);//规则项信息
//			var goodsServicePromise = "";
//			if(goodForm==1){
//				$("#gsp1").find("input[name='gsp']:checked").each(function() {
//					goodsServicePromise += $(this).val() + ",";
//				});
//			}else if(goodForm==2){
//				$("#gsp2").find("input[name='gsp']:checked").each(function() {
//					goodsServicePromise += $(this).val() + ",";
//				});
//			}
//			
//			if(goodsServicePromise==""){
//				toastr["info"]("请选择服务承诺！", "");
//				return false;
//			}
//			$("#goodsServicePromise").val(goodsServicePromise);
//		}
//		
//		//图文详情校验
//		var goodsDetailImages = $("#ddt").children().length;
//		if(goodsDetailImages>20 || goodsDetailImages==0){
//			toastr["info"]("请上传1-20张图文详情的图片！", "");
//			return false;
//		}
//		return true;
//	}
	
	/*function addGoodsInfoSubmit() {
		if(checkSubmit()){
			$(".shade1").show();
	        var H = $(".shade1 img").height();
	        $(".shade1 span").height(H).css("line-height",H+"px");
			
			$.ajax({
				type : "post",
				url : $('#ctx').val()+"/goodsInfo/addGoodsInfo.c",
				data : $('#myFormAddGoods')
						.serialize(),
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
							var indexArr = new Array( $("#dd div").length);
						    var fromData = new FormData($("#goodsImage").children("input[type='file']")[0]);
							 $("#dd >div").each(function(i){
								 var imgId = $(this).attr("imgid");
								 var t = $("#goodsImage").children("input[type='file'][imgid='"+imgId+"']")[0].files[0];
								 fromData.append("file",$("#goodsImage").children("input[type='file'][imgid='"+imgId+"']")[0].files[0]);
							 });
							 fromData.append("id",id);
							 fromData.append("imageType",1);
							$.ajax({
								url:$('#ctx').val()+"/goodsInfo/uploadImage.c",
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

							var fromDataDetail = new FormData($("#goodsDetailImage").children("input[type='file']")[0]);
							 $("#ddt >div").each(function(i){
								 var imgId = $(this).attr("imgid");
								 var t = $("#goodsDetailImage").children("input[type='file'][imgid='"+imgId+"']")[0].files[0];
								 fromDataDetail.append("file",$("#goodsDetailImage").children("input[type='file'][imgid='"+imgId+"']")[0].files[0]);
							 });
							 fromDataDetail.append("id",id);
							 fromDataDetail.append("imageType",2);
							$.ajax({
								url:$('#ctx').val()+"/goodsInfo/uploadImage.c",
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
								 
							setTimeout("close('新建成功','#divAddGoods')",2000);//2秒，可以改动 
								
						}
						
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

	}*/
	function close(str,div){
		funcQueryGoodsInfo(1);
		toastr["success"](str, "");
		$(div).modal("hide");
		$(".shade1").hide();
	}
	
	function checkNum(index){
		var val = $(index).val();
		var regStrs = /^\d{1,2}?$/;
		if(val!=""&&val!=null&&val!=undefined&&!regStrs.test(val)){
			$(index).val("");
		}else{
			$(index).val(val);
		}
	}
	
	
var magId=0;
function setImagePreviews(){
    var docobj=document.getElementById("doc"+magId);
    var dd=document.getElementById("dd");
    var fileList=docobj.files;
    for( var i=0;i<fileList.length;i++){
        if(!fileChange(docobj.files[i])){
            return;
        };
        magId++;
//        $(dd).append("<div class='img-wrap' imgid='img"+(magId-1)+"'><div class='pic'><img id='img"+(magId-1)+"'/></div><div class='tip'><span class='up'>&lt;</span><span class='down'>&gt;</span><span class=' closeimg"+(magId-1)+" close1'>×</span></div></div>");
        $(dd).append("<div class='img-wrap' imgid='img"+(magId-1)+"'><div class='tip'><span class='up'></span><span class='down'></span><span class=' closeimg"+(magId-1)+" close1'>×</span></div><div class='pic'><img id='img"+(magId-1)+"'/></div></div>");
        $(".fileinput-wrap [imgid=img"+(magId-1)+"]")[0].style.position="absolute";
        $(".fileinput-wrap [imgid=img"+(magId-1)+"]")[0].style.left="-10000px";
        $(".fileinput-wrap>span").first().append('<input type="file"  name="files" id="doc'+magId+'" imgid="img'+magId+'" onchange="javascript:setImagePreviews()" />');
        $(".closeimg"+(magId-1)).on("click",function(e){
            var id=$(this).parents(".img-wrap").attr("imgid");
            $(this).parents(".img-wrap").remove();
            $(".newfileinput-wrap input[imgid="+id+"]").remove();
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




/*var magIdt=0;
function setImagePreviews1(){
    var docobj=document.getElementById("doct"+magIdt);
    var ddt=document.getElementById("ddt");
    var fileList=docobj.files;
    for( var i=0;i<fileList.length;i++){

        if(!fileChange(docobj.files[i])){
            return;
        };
        magIdt++;
        $(ddt).append("<div class='img-wrap' imgid='imgt"+(magIdt-1)+"'><div class='pic'><img id='imgt"+(magIdt-1)+"'/></div><div class='tip'><span class='up'>&lt;</span><span class='down'>&gt;</span><span class=' closeimgt"+(magIdt-1)+" close1'>×</span></div></div>");
        $(".newfileinput-wrap [imgid=imgt"+(magIdt-1)+"]")[0].style.position="absolute";
        $(".newfileinput-wrap [imgid=imgt"+(magIdt-1)+"]")[0].style.left="-10000px";
        $(".newfileinput-wrap>span").first().append('<input type="file"  name="files"  id="doct'+magIdt+'" imgid="imgt'+magIdt+'" style="width:100px;" onchange="javascript:setImagePreviews1()" />');
        $(".closeimgt"+(magIdt-1)).on("click",function(e){
        	 var indexId=$(this).parents(".img-wrap").attr("imgid");
             $(this).parents(".img-wrap").remove();
            $(".newfileinput-wrap input[imgid='imgt"+indexId+"']").remove();
        });
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
}*/
$("#dd").on('click', 'span',function(event) {
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
/*$("#ddt").on('click', 'span',function(event) {
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
});*/


$("#dd").on('click', '.pic',function(event) {
    event.preventDefault;
    var top =$(document).scrollTop(),
    H = $("section.content").height();
    $("body").css("overflow-y","hidden");
    var html=$(this).html();
    $(".shade").show().css("top",top).css("height",H).html(html);
});
$(".shade").click(function () {
	event.preventDefault;
	$(this).hide();
	$("body").css("overflow-y","scroll");
});

$("#ddt").on('click', '.pic',function(event) {
	    event.preventDefault;
	    var top =$(document).scrollTop(),
	    	H = $("section.content").height();
    $("body").css("overflow-y","hidden");
	    var html=$(this).html();
	    $(".shade").show().css("top",top).css("height",H).html(html);
	});

//检测文件大小和类型 
function fileChange(target){ 
	//检测上传文件的类型 
	if(!(/(?:jpg|gif|png|jpeg)$/i.test(target.type))) {
		toastr["info"]("请上传jpg | gif | png | jpeg格式的图片！", "");
		return false; 
	}/* else if(target.size > 307200){ 
		toastr["info"]("请上传200~300kb图片！", "");
        return false;
	} */else{
        return true;
	}
}	 