(function($,undefined){
	$.fn.zyUpload = function(options,param){
		alert('in');
		var otherArgs = Array.prototype.slice.call(arguments, 1);
		if (typeof options == 'string') {
			var fn = this[0][options];
			if($.isFunction(fn)){
				return fn.apply(this, otherArgs);
			}else{
				throw ("zyUpload - No such method: " + options);
			}
		}

		return this.each(function(){
			var para = {};    // 保留参数
			var self = this;  // 保存组件对象
			
			var defaults = {
					width            : "700px",  					// 宽度
					height           : "auto",  					// 宽度
					itemWidth        : "140px",                     // 文件项的宽度
					itemHeight       : "120px",                     // 文件项的高度
					url              : "/uploadFile.json",  	// 上传文件的路径
					multiple         : true,  						// 是否可以多个文件上传
					dragDrop         : false,  						// 是否可以拖动上传文件
					del              : true,  						// 是否可以删除文件
					finishDel        : false,  						// 是否在上传文件完成后删除预览
					/* 提供给外部的接口方法 */
					onSelect         : function(selectFiles, files){},// 选择文件的回调方法  selectFile:当前选中的文件  allFiles:还没上传的全部文件
					onDelete		 : function(file, files){},     // 删除一个文件的回调方法 file:当前删除的文件  files:删除之后的文件
					onSuccess		 : function(file){},            // 文件上传成功的回调方法
					onFailure		 : function(file){},            // 文件上传失败的回调方法
					onComplete		 : function(responseInfo){},    // 上传完成的回调方法
			};
			
			para = $.extend(defaults,options);
			
			this.init = function(url){
				alert('init');
				this.createHtml();  // 创建组件html
				this.createCorePlug();  // 调用核心js
			};
			
			/**
			 * 功能：创建上传所使用的html
			 * 参数: 无
			 * 返回: 无
			 */
			this.createHtml = function(url){
				alert('createhtml');
				var multiple = "";  // 设置多选的参数
				para.multiple ? multiple = "multiple" : multiple = "";
				var html= '';
				
				if(para.dragDrop){
					// 创建带有拖动的html
					html += '<form id="uploadForm" action="'+url+'" method="post" enctype="multipart/form-data">';
					html += '	<div class="upload_box">';
					html += '		<div class="upload_main">';
					html += '			<div class="upload_choose">';
	            	html += '				<div class="convent_choice">';
	            	html += '					<div class="andArea">';
	            	html += '						<div class="filePicker">点击选择文件</div>';
	            	html += '						<input id="fileImage" type="file" size="30" name="fileselect[]" '+multiple+'>';
	            	html += '					</div>';
	            	html += '				</div>';
					html += '				<span id="fileDragArea" class="upload_drag_area">或者将文件拖到此处</span>';
					html += '			</div>';
		            html += '			<div class="status_bar">';
		            html += '				<div id="status_info" class="info">选中0张文件，共0B。</div>';
		            html += '				<div class="btns">';
		            html += '					<div class="webuploader_pick">继续选择</div>';
		            html += '					<div class="upload_btn">开始上传</div>';
		            html += '				</div>';
		            html += '			</div>';
					html += '			<div id="preview" class="upload_preview"></div>';
					html += '		</div>';
					html += '		<div class="upload_submit">';
					html += '			<button type="button" id="fileSubmit" class="upload_submit_btn">确认上传文件</button>';
					html += '		</div>';
					html += '		<div id="uploadInf" class="upload_inf"></div>';
					html += '	</div>';
					html += '</form>';
				}else{
					var imgWidth = parseInt(para.itemWidth.replace("px", ""))-15;
					
					// 创建不带有拖动的html
					html += '<form id="uploadForm" action="'+url+'" method="post" enctype="multipart/form-data">';
					html += '	<div class="upload_box">';
					html += '		<div class="upload_main single_main">';
		            html += '			<div class="status_bar">';
		            html += '				<div id="status_info" class="info">选中0张文件，共0B。</div>';
		            html += '				<div class="btns">';
		            html += '					<input id="fileImage" type="file" size="30" name="fileselect[]" '+multiple+'>';
		            html += '					<div class="webuploader_pick">选择文件</div>';
		            html += '					<div class="upload_btn">开始上传</div>';
		            html += '				</div>';
		            html += '			</div>';
		            html += '			<div id="preview" class="upload_preview" style="min-height:100px">';
		            /**
				    html += '				<div class="add_upload" style="height:auto">';
				    html += '					<a style="height:'+para.itemHeight+';width:'+para.itemWidth+';" title="点击添加文件" id="rapidAddImg" class="add_imgBox" href="javascript:void(0)">';
				    html += '						<div class="uploadImg" style="width:'+imgWidth+'px">';
				    html += '							<img class="upload_image" src="control/images/add_img.png" style="width:expression(this.width > '+imgWidth+' ? '+imgWidth+'px : this.width)" />';
				    html += '						</div>';
				    html += '					</a>';
				    html += '				</div>';
				    **/
					html += '			</div>';
					html += '		</div>';
					html += '		<div class="upload_submit">';
					html += '			<button type="button" id="fileSubmit" class="upload_submit_btn">确认上传文件</button>';
					html += '		</div>';
					html += '		<div id="uploadInf" class="upload_inf"></div>';
					html += '	</div>';
					html += '</form>';
					/**
					 *
					 html += '<form id="uploadForm" action="/ehr/files/uploadFile.json" method="post" enctype="multipart/form-data">';
					html += '	<div class="upload_box">';
					html += '		<div class="upload_main single_main">';
		            html += '			<div class="status_bar">';
		            html += '				<div id="status_info" class="info">选中0张文件，共0B。</div>';
		            html += '				<div class="btns">';
		            html += '					<input id="fileImage" type="file" size="30" name="fileselect[]" '+multiple+'>';
		            html += '					<div class="webuploader_pick">选择文件</div>';
		            html += '					<div class="upload_btn">开始上传</div>';
		            html += '				</div>';
		            html += '			</div>';
		            html += '			<div id="preview" class="upload_preview">';
				    html += '				<div class="add_upload">';
				    html += '					<a style="height:'+para.itemHeight+';width:'+para.itemWidth+';" title="点击添加文件" id="rapidAddImg" class="add_imgBox" href="javascript:void(0)">';
				    html += '						<div class="uploadImg" style="width:'+imgWidth+'px">';
				    html += '							<img class="upload_image" src="control/images/add_img.png" style="width:expression(this.width > '+imgWidth+' ? '+imgWidth+'px : this.width)" />';
				    html += '						</div>';
				    html += '					</a>';
				    html += '				</div>';
					html += '			</div>';
					html += '		</div>';
					html += '		<div class="upload_submit">';
					html += '			<button type="button" id="fileSubmit" class="upload_submit_btn">确认上传文件</button>';
					html += '		</div>';
					html += '		<div id="uploadInf" class="upload_inf"></div>';
					html += '	</div>';
					html += '</form>';
					 *
					 */
				}
				
	            $(self).append(html).css({"width":para.width,"height":para.height});
	            
	            // 初始化html之后绑定按钮的点击事件
	            this.addEvent();
			};
			
			/**
			 * 功能：显示统计信息和绑定继续上传和上传按钮的点击事件
			 * 参数: 无
			 * 返回: 无
			 */
			this.funSetStatusInfo = function(files){
				var size = 0;
				var num = files.length;
				$.each(files, function(k,v){
					// 计算得到文件总大小
					size += v.size;
				});
				
				// 转化为kb和MB格式。文件的名字、大小、类型都是可以现实出来。
				if (size > 1024 * 1024) {                    
					size = (Math.round(size * 100 / (1024 * 1024)) / 100).toString() + 'MB';                
				} else {                    
					size = (Math.round(size * 100 / 1024) / 100).toString() + 'KB';                
				}  
				
				// 设置内容
				$("#status_info").html("选中"+num+"张文件，共"+size+"。");
			};
			
			/**
			 * 功能：过滤上传的文件格式等
			 * 参数: files 本次选择的文件
			 * 返回: 通过的文件
			 */
			this.funFilterEligibleFile = function(files){
				var arrFiles = [];  // 替换的文件数组
				for (var i = 0, file; file = files[i]; i++) {
					/*if (file.size >= 51200000) {
						alert('您这个"'+ file.name +'"文件大小过大');	
					} else {
						// 在这里需要判断当前所有文件中
						arrFiles.push(file);	
					}*/
					
					
					arrFiles.push(file);
				}
				return arrFiles;
			};
			
			/**
			 * 功能： 处理参数和格式上的预览html
			 * 参数: files 本次选择的文件
			 * 返回: 预览的html
			 */
			this.funDisposePreviewHtml = function(file, e){
				var html = "";
				var imgWidth = parseInt(para.itemWidth.replace("px", ""))-15;
				
				// 处理配置参数删除按钮
				var delHtml = "";
				if(para.del){  // 显示删除按钮
					delHtml = '<span class="file_del" data-index="'+file.index+'" title="删除"></span>';
				}
				
				// 处理不同类型文件代表的图标
				var fileImgSrc = "../static/img/";
				if(file.name.toLocaleLowerCase().indexOf("rar") > 0){
					fileImgSrc = fileImgSrc + "rar.png";
				}else if(file.name.toLocaleLowerCase().indexOf("zip") > 0){
					fileImgSrc = fileImgSrc + "zip.png";
				}else if(file.name.toLocaleLowerCase().indexOf("txt") > 0){
					fileImgSrc = fileImgSrc + "txt.png";
				}else if(file.name.toLocaleLowerCase().indexOf("doc") > 0){
					fileImgSrc = fileImgSrc + "doc.png";
				}else if(file.name.toLocaleLowerCase().indexOf("pdf") > 0){
					fileImgSrc = fileImgSrc + "pdf.png";
				}else if(file.name.toLocaleLowerCase().indexOf("ppt") > 0){
					fileImgSrc = fileImgSrc + "ppt.jpg";
				}else if(file.name.toLocaleLowerCase().indexOf("xls") > 0){
					fileImgSrc = fileImgSrc + "xls.png";
				}else{
					fileImgSrc = fileImgSrc + "add_img.png";
				}
				
				
				// 图片上传的是图片还是其他类型文件
				if (file.type.indexOf("image") == 0) {
					html += '<div id="uploadList_'+ file.index +'" class="upload_append_list" style="height:auto">';
					html += '	<div class="file_bar">';
					html += '		<div style="padding:5px;">';
					html += '			<p class="file_name">' + file.name + '</p>';
					html += delHtml;   // 删除按钮的html
					html += '		</div>';
					html += '	</div>';
					html += '	<a style="height:'+para.itemHeight+';width:'+para.itemWidth+';" href="#" class="imgBox">';
					html += '		<div class="uploadImg" style="width:'+imgWidth+'px">';				
					html += '			<img id="uploadImage_'+file.index+'" class="upload_image" src="' + e.target.result + '" style="width:expression(this.width > '+imgWidth+' ? '+imgWidth+'px : this.width)" />';                                                                 
					html += '		</div>';
					html += '	</a>';
					html += '	<p id="uploadProgress_'+file.index+'" class="file_progress"></p>';
					html += '	<p id="uploadFailure_'+file.index+'" class="file_failure">上传失败，请重试</p>';
					html += '	<p id="uploadSuccess_'+file.index+'" class="file_success"></p>';
					html += '</div>';
                	
				}else{
					html += '<div id="uploadList_'+ file.index +'" class="upload_append_list" style="height:auto">';
					html += '	<div class="file_bar">';
					html += '		<div style="padding:5px;">';
					html += '			<p class="file_name">' + file.name + '</p>';
					html += delHtml;   // 删除按钮的html
					html += '		</div>';
					html += '	</div>';
					html += '	<a style="height:'+para.itemHeight+';width:'+para.itemWidth+';" href="#" class="imgBox">';
					html += '		<div class="uploadImg" style="width:'+imgWidth+'px">';				
					html += '			<img id="uploadImage_'+file.index+'" class="upload_image" src="' + fileImgSrc + '" style="width:expression(this.width > '+imgWidth+' ? '+imgWidth+'px : this.width)" />';                                                                 
					html += '		</div>';
					html += '	</a>';
					html += '	<p id="uploadProgress_'+file.index+'" class="file_progress"></p>';
					html += '	<p id="uploadFailure_'+file.index+'" class="file_failure">上传失败，请重试</p>';
					html += '	<p id="uploadSuccess_'+file.index+'" class="file_success"></p>';
					html += '</div>';
				}
				
				return html;
			};
			
			/**
			 * 功能：调用核心插件
			 * 参数: 无
			 * 返回: 无
			 */
			this.createCorePlug = function(){
				var params = {
					fileInput: $("#fileImage").get(0),
					uploadInput: $("#fileSubmit").get(0),
					dragDrop: $("#fileDragArea").get(0),
					url: $("#uploadForm").attr("action"),
					filterFile: function(files) {
						// 过滤合格的文件
						return self.funFilterEligibleFile(files);
					},
					onSelect: function(selectFiles, allFiles) {
						para.onSelect(selectFiles, allFiles);  // 回调方法
						self.funSetStatusInfo(ZYFILE.funReturnNeedFiles());  // 显示统计信息
						var html = '', i = 0;
						// 组织预览html
						var funDealtPreviewHtml = function() {
							file = selectFiles[i];
							if (file) {
								var reader = new FileReader()
								reader.onload = function(e) {
									// 处理下配置参数和格式的html
									html += self.funDisposePreviewHtml(file, e);
									
									i++;
									// 再接着调用此方法递归组成可以预览的html
									funDealtPreviewHtml();
								}
								reader.readAsDataURL(file);
							} else {
								// 走到这里说明文件html已经组织完毕，要把html添加到预览区
								funAppendPreviewHtml(html);
							}
						};
						
						// 添加预览html
						var funAppendPreviewHtml = function(html){
							// 添加到添加按钮前
							if(para.dragDrop){
								$("#preview").append(html);
							}else{
								//$(".add_upload").before(html);
								$("#preview").append(html);
							}
							// 绑定删除按钮
							funBindDelEvent();
							funBindHoverEvent();
						};
						
						// 绑定删除按钮事件
						var funBindDelEvent = function(){
							if($(".file_del").length>0){
								// 删除方法
								$(".file_del").click(function() {
									ZYFILE.funDeleteFile(parseInt($(this).attr("data-index")), true);
									return false;	
								});
							}
							
							if($(".file_edit").length>0){
								// 编辑方法
								$(".file_edit").click(function() {
									// 调用编辑操作
									//ZYFILE.funEditFile(parseInt($(this).attr("data-index")), true);
									return false;	
								});
							}
						};
						
						// 绑定显示操作栏事件
						var funBindHoverEvent = function(){
							$(".upload_append_list").hover(
								function (e) {
									$(this).find(".file_bar").addClass("file_hover");
								},function (e) {
									$(this).find(".file_bar").removeClass("file_hover");
								}
							);
						};
						
						funDealtPreviewHtml();		
					},
					onDelete: function(file, files) {
						// 移除效果
						//$("#uploadList_" + file.index).fadeOut();
                        $("#uploadList_" + file.index).remove();
						// 重新设置统计栏信息
						self.funSetStatusInfo(files);
						console.info("剩下的文件");
						console.info(files);
					},
					onProgress: function(file, loaded, total) {
						var eleProgress = $("#uploadProgress_" + file.index), percent = (loaded / total * 100).toFixed(2) + '%';
						if(eleProgress.is(":hidden")){
							eleProgress.show();
						}
						eleProgress.css("width",percent);
					},
					onSuccess: function(file, response) {
						var jsondata= eval('('+response +')');
						$("#uploadProgress_" + file.index).hide();
						$("#uploadSuccess_" + file.index).show();
						//s[0] 文件名 s[1] gridFS中的图片id s[2]图片缩略图id
						var str=jsondata.data.split(",");
						//提示上传成功 
						$("#uploadInf").append("<p>"+str[0]+"&nbsp;&nbsp;上传成功。</p>");
						
						var tempsType=$('#pop-upload-type').val();
						if(tempsType=="1" || tempsType=="2"){
							setimg(str[1],str[0]);
						}
						
						//获取要显示缩略图容器的id
						var showDiv=$('#getShowImgDivId').val();
						//获取上传类型，img 图片，atta附件
						var type=$('#getShowImgDivId').attr('type');
						//获取是图片列表还是单个图片
						var listOrImg=$('#getShowImgDivId').attr('inner');
						if(listOrImg!=undefined || listOrImg!=""){
							if(listOrImg=="list"){
								if(type=="img"){
									setImg(showDiv, str[1]);
								}else{
									setAtta(showDiv,str[0],str[1]);
								}
							}else if(listOrImg=="img"){
								if(type=="img"){
									setSrc(showDiv, str[1]);
									setVal(showDiv, str[1]);
								}
							}else if(listOrImg=="analysisResult"){//上传目诊图片时的方法
								analysisResult(str[1]);
							}
						}
						
						
						// 根据配置参数确定隐不隐藏上传成功的文件
						if(para.finishDel){
							// 移除效果
							$("#uploadList_" + file.index).remove();
							// 重新设置统计栏信息
							self.funSetStatusInfo(ZYFILE.funReturnNeedFiles());
						}
					},
					onFailure: function(file) {
						$("#uploadProgress_" + file.index).hide();
						$("#uploadSuccess_" + file.index).show();
						$("#uploadInf").append("<p>文件" + file.name + "上传失败！</p>");	
						//$("#uploadImage_" + file.index).css("opacity", 0.2);
					},
					onComplete: function(response){
						console.info(response);
					},
					onDragOver: function() {
						$(this).addClass("upload_drag_hover");
					},
					onDragLeave: function() {
						$(this).removeClass("upload_drag_hover");
					}

				};
				
				ZYFILE = $.extend(ZYFILE, params);
				ZYFILE.init();
			};
			
			/**
			 * 功能：绑定事件
			 * 参数: 无
			 * 返回: 无
			 */
			this.addEvent = function(){
				// 如果快捷添加文件按钮存在
				if($(".filePicker").length > 0){
					// 绑定选择事件
					$(".filePicker").bind("click", function(e){
		            	$("#fileImage").click();
		            });
				}
	            
				// 绑定继续添加点击事件
				$(".webuploader_pick").bind("click", function(e){
	            	$("#fileImage").click();
	            });
				
				// 绑定上传点击事件
				$(".upload_btn").bind("click", function(e){
					// 判断当前是否有文件需要上传
					if(ZYFILE.funReturnNeedFiles().length > 0){
						$("#fileSubmit").click();
					}else{
						alert("请先选中文件再点击上传");
					}
	            });
				
				// 如果快捷添加文件按钮存在
				if($("#rapidAddImg").length > 0){
					// 绑定添加点击事件
					$("#rapidAddImg").bind("click", function(e){
						$("#fileImage").click();
		            });
				}
			};
			
			
			// 初始化上传控制层插件
			this.init();
		});
	};
	
	
	var cu={
            init:function(obj,args){
                return (function(){
                    if(!$.isEmptyObject(args)){
                        //绘制tab布局结构
                        var tabContanier=$("<div></div>").addClass("bs-example bs-example-tabs").attr("data-example-id","togglable-tabs");
                        var ulNav=$("<ul></ul>").attr("id","myTabsNav").addClass("nav nav-tabs").attr("role","tablist");
                        var tabContent=$("<div></div>").attr("id","myTabContent").addClass("tab-content");
                        //绘制必须后的demo层(即从本地选择文件上传层)
                        $("<li></li>").attr("role","presentation")
                            .addClass("active")
                            .append($("<a></a>").attr("href","#upload-demo")
                                .attr("id","demo-tab")
                                .attr("role","tab")
                                .attr("data-toggle","tab")
                                .attr("aria-controls","demo")
                                .attr("aria-expanded","true")
                                .text("上传文件"))
                            .appendTo(ulNav);
                        $("<div></div>").attr("id","upload-demo")
                            .attr("role","tabpanel")
                            .attr("aria-labelledby","demo-tab")
                            .addClass("tab-pane fade demo")
                            .addClass("in active")
                            .appendTo(tabContent);
                        //绘制文件列表内容
                        //绘制文件列表tab导航
                        $("<li></li>").attr("role","presentation")
                            .append($("<a></a>").attr("href","#fileTab")
                                .attr("id","demo-fileTab")
                                .attr("role","tab")
                                .attr("data-toggle","tab")
                                .attr("aria-controls","filesTab")
                                .text(args.title))
                            .appendTo(ulNav);
                        //绘制查询条件和操作按钮面板
                        var showFilesPanel=$("<div></div>").addClass("showAttach clearfix").attr("id","fileTab-showAttach");
                        var ordersPanel=$("<div></div>").addClass("file-orders");
                        var finishBtnPanel=$("<div></div>").addClass("finishBtnPanel").append("<button id='finishBtn' class='btn btn-danger'><span class='glyphicon glyphicon-ok'></span>  完成</button>");
                        //绘制查询面板
                        var orderpanelhtm='<div class="row"><div class="col-lg-4 col-md-4 col-sm-6 col-xs-12">'
                            +'<label>文件名称:</label>'
                            +'<input id="fileName" name="fileName" type="text" />'
                            +'</div>'
                            +' <div class="col-lg-4 col-md-4 col-sm-6 col-xs-12">'
                            +'<label>文件类型:</label>'
                            +'<select id="fileType" name="fileType">'
                            +'<option name="all" value="">全部</option>'
                            +'<option name="img" value="1">图像</option>'
                            +'<option name="attach" value="2">附件</option>'
                            +'</select>'
                            +'</div>'
                            +' <div class="col-lg-4 col-md-4 col-sm-6 col-xs-12">'
                            +'<label>文件后缀:</label>'
                            +'<select id="fileSuffix" name="fileSuffix">'
                            +'<option name="all" value="">全部</option>'
                            +'<option name="img" value=".jpg">jpg</option>'
                            +'<option name="img" value=".jpeg">jpeg</option>'
                            +'<option name="img" value=".png">png</option>'
                            +'<option name="img" value=".bmp">bmp</option>'
                            +'<option name="img" value=".doc">doc</option>'
                            +'<option name="img" value=".docx">docx</option>'
                            +'<option name="img" value=".xls">xls</option>'
                            +'<option name="img" value=".ppt">ppt</option>'
                            +'<option name="img" value=".pdf">pdf</option>'
                            +'</select>'
                            +'</div>'
                            +'<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">'
                            +'<button id="fileSearch" type="button" class="btn btn-sm btn-primary pull-right"><span class="glyphicon glyphicon-search"></span>  查询</button>'
                            +'</div></div>';
                        ordersPanel.append(orderpanelhtm);
                        //追加内容
                        $("<div></div>").attr("id","fileTab")
                            .attr("role","tabpanel")
                            .attr("aria-labelledby","fileTab-tab")
                            .addClass("tab-pane fade")
                            .css({"padding":"10px"})
                            .appendTo(tabContent)
                            .append(ordersPanel)
                            .append(showFilesPanel)
                            .append(finishBtnPanel);
                        obj.append(tabContanier.append(ulNav).append(tabContent));
                    }else{
                        obj.append('<div id="upload-demo" class="demo"></div>');
                    }
                    cu.bindTabEvent(obj,args);
                    cu.scrollTriper(obj,args);
                })();
            },
            drawTabData:function(obj,args){
                return (function(){
                    $.getJSON(args.url,args.params,function(returnData){
                    	var data=returnData.dataList;
                        if(data.length<1){
                            return;
                        }
                        $.each(data,function(i,att){
                            var file_block= $("<div></div>").appendTo($("#fileTab-showAttach"));
                            file_block.addClass("file-block")
                                .append("<input type='checkbox' class='sel-img-box' value='"+att["id"]+"' />")
                                .append("<span>"+att["filename"]+"</span>");
                            //判断文件类型图片还是附件
                            //针对不同类型增加不同按钮和事件
                            if(att["uploadType"]=="1"){   //图片
                                file_block.append("<img src='/ehr/files/downThumbFile/"+att["id"]+".htm' />");/*.append(
                                    $("<input type='button' value='预览' />")
                                        .addClass("btn btn-sm btn-success display-img-btn")
                                );*/
                            }
                            if(att["uploadType"]=="2"){   //附件
                            	
                            	
                            	
                            	var imgIconDefault="add_img.png";
                            	var val=att["contentType"];
            					if(val==".pdf"){
            						imgIconDefault="pdf.png";
            					}else if(val==".txt"){
            						imgIconDefault="txt.png";
            					}else if(val==".rar"){
            						imgIconDefault="rar.png";
            					}else if(val==".zip"){
            						imgIconDefault="zip.png";
            					}else if(val==".doc"){
            						imgIconDefault="doc.png";
            					}else if(val==".ppt"){
            						imgIconDefault="ppt.jpg";
            					}else if(val==".xls"){
            						imgIconDefault="xls.png";
            					}
            					var imgIcon="/ehr/static/img/"+imgIconDefault;
                            	
                                file_block.append($("<img src='"+imgIcon+"' />")).append(
                                    $("<input type='button' value='下载' />")
                                        .addClass("btn btn-sm btn-success display-img-btn")
                                );
                            }

                            file_block.find("img").on("click",function(){
                                if( file_block.find(":checkbox").is(":checked")){
                                    file_block.find(":checkbox").prop("checked", false);
                                    file_block.removeClass("file-block-checked");
                                }else{
                                    file_block.find(":checkbox").prop("checked", true);
                                    file_block.addClass("file-block-checked");
                                }
                            });
                            file_block.find(":checkbox").on("change",function(){
                                if( file_block.find(":checkbox").is(":checked")){
                                    file_block.addClass("file-block-checked");
                                }else{
                                    file_block.removeClass("file-block-checked");
                                }
                            });
                        });
                        if($("#fileTab-showAttach").height()>=$("#fileTab-showAttach")[0].scrollHeight){   //没有滚动条
                        	var nextPage=args.params.currentPage+1;
                            args.params= $.extend(args.params,{
                            	currentPage:nextPage
                            });
                           cu.drawTabData(obj,args);
                           
                        }

                    });


                })();
            },
            bindTabEvent:function(obj,args){
                return (function(){
                    $("#demo-fileTab").one("click",function(){
                        $("#fileTab").addClass("in active");
                            cu.drawTabData(obj,args);
                    });

                    $("#fileSearch").on("click",function(){
                        var fname=$("#fileName").val();
                        var fType=$("#fileType").val();
                        var fStuffix=$("#fileSuffix").val();
                        args.params= $.extend(args.params,{
                        	currentPage:1,
                            fileName:fname,
                            fileType:fType,
                            fileStuffix:fStuffix
                        });
                        $("#fileTab-showAttach").empty();
                        cu.drawTabData(obj,args);
                    });
                   $("#finishBtn").on('click',function(){
                       var ids=$("#fileTab-showAttach").find(".file-block :checkbox:checked").map(function(){
                            return $(this).val();
                        }).get().join(",");
                       
                       var fn_str=$("#fileTab-showAttach").find(".file-block :checkbox:checked").next().map(function(){
                           return $(this).text();
                       }).get().join(",");
                       if(ids.length>0){
                    	   setimg(ids,fn_str);
                    	   alert("上传成功！");
                       }else{
                    	   alert("请选择文件！");
                       }
                    });

                })();
            },
            scrollTriper:function(obj,args){
                $("#fileTab-showAttach").on('scroll', function () {
                    var scrollTop = $(this).scrollTop();
                    var scrollHeight = $(this)[0].scrollHeight;
                    var docHeight = $(this).height();
                    if ((scrollTop + docHeight == scrollHeight) && scrollHeight>0) {
                        //此处是滚动条到底部时候触发的事件，在这里写要加载的数据，或者是拉动滚动条的操作
                        //判断是否有数据,有就继续加载
                    	var nextPage=args.params.currentPage+1;
                        args.params= $.extend(args.params,{
                        	currentPage:nextPage
                        });
                        cu.drawTabData(obj,args);
                    }
                });
            }
    }
    $.fn.uploadFilePop=function(options){
        var args= $.extend({},options);
        cu.init(this,args);
    };
	
})(jQuery);

