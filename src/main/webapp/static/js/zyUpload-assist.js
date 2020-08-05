
/*
 * id 选择器
 *imgId 后台返回的图片id
 *imgName 后台返回的图片名称
 *
 * */

function setSrc(divId,imgId){
	$('#'+divId).attr('src','/ehr/files/downThumbFile/'+imgId+'.htm');
}

function setVal(divId,imgId){
	$('#'+divId).next().val(imgId);
}

function setImg(divId,imgId){
	var html="";
	html+='<div style="float:left;width:100px;height:75px;">'
		+	'<a href="/ehr/files/downFile/'+imgId+'.htm" data-lightbox="example-1">'
		+		'<img id="img_thumb" src="/ehr/files/downThumbFile/'+imgId+'.htm"/>'
		+ 	'</a>'
		+ 	'<img id="imgDelBnt" parent="'+divId+'" imgId="'+imgId+'" onclick="delElement(this)" title="删除" src="/ehr/static/img/cancel.png"/>'
		+ '</div>';
	$('#'+divId).append(html);
	$('#'+divId).show();
	var hidden_div=$('#'+divId).next();
	if($(hidden_div).val()!=""){
		$(hidden_div).val($(hidden_div).val()+","+imgId);
	}else{
		$(hidden_div).val(imgId);
	}
	
}

function setAtta(divId,imgName,imgId){
	var html="<div style='float:left'>"
			+	"<img src='/ehr/static/img/attachment.png' />&nbsp;&nbsp;"
			+	"<span id='attaName'>"+imgName+"</span>&nbsp;&nbsp;&nbsp;&nbsp;"
			+	"<span id='attaDelBnt' parent='"+divId+"' imgId='"+imgId+"' onclick='delElement(this)'>删除</span>&nbsp;&nbsp;&nbsp;&nbsp;"
			+"<div>";
	$('#'+divId).append(html);
	$('#'+divId).show();
	var hidden_div=$('#'+divId).next();
	if($(hidden_div).val()!=""){
		$(hidden_div).val($(hidden_div).val()+","+imgId);
	}else{
		$(hidden_div).val(imgId);
	}
}

function delElement(that){
	var imgId=$(that).attr('imgId');
	$.ajax({
		url:'/ehr/files/delFile.json',
		type:'post',
		async:false,
		data:{
			'id':imgId
		},
		success:function(data){
			if(data.message=="success"){
				$(that).parent().remove();//删除按钮的父元素---图片
				var divId=$(that).attr('parent');
				var html=$('#'+divId).html();
				if(html==""){
					$('#'+divId).hide();
				}
				var imgIdList=$('#'+divId).next().val();//获取隐藏域中的图片id
				if(imgIdList!=""){
					var imgIds=imgIdList.split(",");
					for(var i in imgIds){
						if(imgIds[i]==imgId){
							imgIds.splice(i,1);//删除id
						}
					}
					if(imgIds.length!=0){
						imgIdList=imgIds.join(",");
					}else{
						imgIdList="";
					}
				}
				$('#'+divId).next().val(imgIdList);
			}
		}
	})
		
}

function analysisResult(imgId){
	var zy=$('#upload-demo').attr('witchDiv');
	//显示目诊图片
	if(zy=="left"){
		$('#leftshowResultImg').append('<div class="col-sm-6" id="div_'+imgId+'" style="margin-top:5px;min-width:450px;">'
				+ '<table class="table table-bordered">'
				+ 	'<tr>'
				+ 		'<td style="width:160px">'
				+ 			'<div>'
				+				'<img class="leftimgcss" src="/ehr/files/downThumbFile/'+imgId+'.htm" style="display:block;">'
				+				'<img id="'+imgId+'" class="imgdeldiv" onclick="leftimgdelword(this)" title="删除" src="/ehr/static/img/cancel.png" />'
				+			'</div>'
				+ 			'<div id="imgweizhi">眼像位置：'
				+ 				'<select class="lefteyepost">'
				+ 					'<option value="0">- 请选择 -</option>'
				+ 					'<option value="F">正视</option>'
				+ 					'<option value="U">上斜视</option>'
				+ 					'<option value="D">下斜视</option>'
				+ 					'<option value="L">左斜视</option>'
				+ 					'<option value="R">右斜视</option>'
				+ 				'</select>'
				+ 			'</div>'
				+ 		'</td>'
				+ 		'<td>'
				+ 			'眼像特征：<textarea class="leftEyeFeature" rows="2" cols="30"></textarea><br>'
				+ 			'眼像结果：<textarea class="leftEyeResult" rows="2" cols="30"></textarea><br>'
				+ 			'眼像说明：<textarea class="leftEyeDesc" rows="2" cols="30"></textarea>'
				+ 		'</td>'
				+ 	'</tr>'
				+ '</table>'
				+'</div>');
		$('#hidden_leftimgId').append(imgId+",");
	}else if(zy=="right"){
		$('#rightshowResultImg').append('<div class="col-sm-6" id="div_'+imgId+'" style="margin-top:5px;min-width:450px;">'
				+ '<table class="table table-bordered">'
				+ 	'<tr>'
				+ 		'<td style="width:160px">'
				+ 			'<div>'
				+				'<img class="leftimgcss" src="/ehr/files/downThumbFile/'+imgId+'.htm" style="display:block;">'
				+				'<img id="'+imgId+'" class="imgdeldiv" onclick="rightimgdelword(this)" title="删除" src="/ehr/static/img/cancel.png" />'
				+				'</div>'
				+ 			'<div id="imgweizhi">眼像位置：'
				+ 				'<select class="righteyepost">'
				+ 					'<option value="0">- 请选择 -</option>'
				+ 					'<option value="F">正视</option>'
				+ 					'<option value="U">上斜视</option>'
				+ 					'<option value="D">下斜视</option>'
				+ 					'<option value="L">左斜视</option>'
				+ 					'<option value="R">右斜视</option>'
				+ 				'</select>'
				+ 			'</div>'
				+ 		'</td>'
				+ 		'<td>'
				+ 			'眼像特征：<textarea class="rightEyeFeature" rows="2" cols="30"></textarea><br>'
				+ 			'眼像结果：<textarea class="rightEyeResult" rows="2" cols="30"></textarea><br>'
				+ 			'眼像说明：<textarea class="rightEyeDesc" rows="2" cols="30"></textarea>'
				+ 		'</td>'
				+ 	'</tr>'
				+ '</table>'
				+'</div>');
		
		$('#hidden_rightimgId').append(imgId+",");
	}
}

function setimg(imgIds,filename){
	var type=$('#pop-upload-type').val();
	if(type=="1"){//图片
	    var existIds=$('#photo').val();
	    if(existIds!=""){
    		var existImgIdList=existIds.split(",");
			var imgListId=imgIds.split(",");
			for(var j in imgListId){
				if(existImgIdList.indexOf(imgListId[j])<0){
					imgStyle(imgListId[j]);
					$('#photo').val(existIds+","+imgListId[j]);
				}
			}
	    }else{
    		var imgIdList=imgIds.split(",");
    		for(var i in imgIdList){
    			imgStyle(imgIdList[i]);
			}
	    	$('#photo').val(imgIds);
	    }
	}else{//附件
		var existIds=$('#attachment').val();
		var fileNameList=filename.split(",");
	    if(existIds!=""){
    		var existImgIdList=existIds.split(",");
			var imgListId=imgIds.split(",");
			
			for(var j in imgListId){
				if(existImgIdList.indexOf(imgListId[j])<0){
					attaStyle(imgListId[j],fileNameList[j]);
					$('#attachment').val(existIds+","+imgListId[j]);
				}
			}
	    }else{
    		var imgIdList=imgIds.split(",");
    		for(var i in imgIdList){
    			attaStyle(imgIdList[i],fileNameList[i]);
			}
	    	$('#attachment').val(imgIds);
	    }
	}
}

function imgStyle(imgId){
	var img_block=$("<div></div>").css({"margin-right":"15px","width":"100px","height":"100px","display":"inline-block","position":"relative"});
    var img_a=$("<a></a>").css({"display":"block"}).attr("href","/ehr/files/downFile/"+imgId+".htm").attr("data-lightbox","example-1");
    var img_item=$("<img/>").attr("src","/ehr/files/downThumbFile/"+imgId+".htm").css({"width":"100%","height":"100%"});
    var img_del=$("<span title='删除'></span>")
    .addClass("glyphicon glyphicon-remove")
    .css({"position":"absolute","top":"5px","right":"5px","cursor":"pointer"})
    .on("click",function(){
        	delImg(this,imgId);
    });;
    $('#photo').prev().append(img_block.append(img_a.append(img_item)).append(img_del));
}
function attaStyle(attaId,filename){
	 var attr_block=$("<div></div>").css({"margin-right":"15px","display":"inline-block"});
     var attr_item=$("<a></a>").attr("href","/ehr/files/downFile/"+attaId+".htm").text(filename);
     var attr_del=$("<span title='删除'></span>")
     .addClass("glyphicon glyphicon-remove")
     .attr("imgId",attaId)
     .css({"margin-left":"2px","cursor":"pointer"})
     .on("click",function(){
    	 delAtta(this,attaId);
     });
     $('#attachment').prev().append(attr_block.append(attr_item).append(attr_del));
}

function delImg(that,imgId){
	var tempArr=$('#photo').val().split(',')
	for(var i in tempArr){
		if(tempArr[i]==imgId){
			tempArr.splice(i,1);
		}
	}
	$("#photo").val(arrToStr(tempArr,","));
	$(that).parent().remove();
}

function delAtta(that,attaId){
	var tempArr=$("#attachment").val().split(",");
 	for(var i in tempArr){
 		if(tempArr[i]==attaId){
 			tempArr.splice(i,1);
 		}
 	}
 	$("#attachment").val(arrToStr(tempArr,","));
 	$(that).parent().remove();
}

