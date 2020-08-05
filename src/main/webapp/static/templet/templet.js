/**
 * Created by chunyangji on 2015/11/10.
 */
(function($){
    var mo={
        "grid":12,
        "flag":"show",
        "data":"",
        "deviceMap":{
            "lg":{"columns":4,"grids":3},
            "md":{"columns":3,"grids":4},
            "sm":{"columns":2,"grids":6},
            "xs":{"columns":1,"grids":12}
        }
    };

    function Field(field,mo){
        var self=this;
        this.isCustomerAdd=false;
        this.type=field["fieldType"];
        this.title=field["name"];
        this.code=field["code"];
        this.options=field["options"];
        this.isRow=false;
        //this.val=field["value"];
        if(mo.data!=null){
        	  if(mo.data[this.code]==null || mo.data[this.code]=="undefined"){
                  this.val="";
              }else{
                  this.val=mo.data[this.code];
              }
        }else{
        	this.val="";
        }
      
        this.cols=function(){
            var field_len=12;
            if (field["len"] != "" && field["len"] != null && field["len"] != "undefined") {
                field_len = field["len"];
            }
            return field_len;
        };
        this.label={
            /**
             * 录入框标题布局
             * **/
            layout:function(){
                var label_title = $("<label for=" + self.code + ">" + self.title + ":</label>").addClass("cell-label"); //录入框名称及样式
                var label_div = $("<div></div>").addClass("cell-label-box");
                label_title.appendTo(label_div);
                return label_div;
            }
        };
        this.inBox={
            /**
             * 录入框布局
             * **/
            layout:function(){
                return setInPutBox(self,mo);
            }
        };
        this.layout=function(){

            var module_column = $("<div></div>").addClass("cell-column");
            module_column.append(self.label.layout()).append(self.inBox.layout());
            /**
             * 用户自添加字段布局
             * **/
             if (this.isCustomerAdd) {
                module_column.append("<a class='delete-field' href='#'><span class='glyphicon glyphicon-trash'></span></a>");
                 $(".form-af-btn").parent().before(module_column);
                $(".delete-field").on('click',function(){
                    $(this).parent().remove();
                });
            } else {
                module_column.appendTo(mo.container);
            }

            /**
             * 设置单列的跨列数及是否换行
             * **/
            setColumnGrids(module_column,self.cols(),self.isRow,mo.deviceMap);


        }
    }

    //增加字段方法
    function setInPutBox(obj,mo) {
    	var input_temp_box;
        if (obj.type == "textarea") {
        	input_temp_box = drawTextarea(obj,mo);
        } else if (obj.type == "checkbox") {
        	input_temp_box = drawCheckBox(obj,mo);
        } else if (obj.type == "radio") {
        	input_temp_box = drawRadio(obj,mo);
        } else if (obj.type == "select") {
        	input_temp_box = drawSelect(obj,mo);
        } else if(obj.type == "image"){
        	input_temp_box = drawImage(obj,mo);
        } else if(obj.type == "attachment"){
        	input_temp_box = drawAttachment(obj,mo);
        } else {
        	input_temp_box = drawInputDefault(obj,mo);
        }
        return input_temp_box;
    }

    /**
     * 根据录入框类型绘制录入框内容和样式
     */
    function drawTextarea(obj,mo){
        var input_box_div = $("<div></div>").addClass("cell-input-box");
        if(mo.flag=="add"){
            var input_box = $("<textarea id=" + obj.code + " name=" + obj.code + "></textarea>").addClass("cell-textarea");
            input_box_div.append(input_box);
        }else if(mo.flag=="edit"){
            var input_box = $("<textarea id=" + obj.code + " name=" + obj.code + "></textarea>").addClass("cell-textarea").val(obj.val);
            input_box_div.append(input_box);
        }else{  //默认均为展示状态
            input_box_div.addClass("cell-input-box-value").text(obj.val);
        }

        obj.isRow = true;
        return input_box_div;
    }
    function drawCheckBox(obj,mo){
        var input_box_div = $("<div></div>").addClass("cell-input-box");
        //添加复选框内容
        if(mo.flag=="add"){
            $.each(obj.options, function (v, option) {
                var input_box = $("<input name=" + obj.code + " type='" + obj.type + "' value='"+option.key+"' />").addClass("cell-checkbox");
                var lable_checkbox = $("<label for=" + obj.code + ">" + option.value + "</lable>").addClass("label-options");
                input_box_div.append(input_box).append(lable_checkbox);
            });
        }else if(mo.flag=="edit"){
            $.each(obj.options, function (v, option) {
                var isChecked=false;
                $.each(obj.val,function(vi,vitem){
                    if(option.key==vitem){
                        isChecked=true;
                        return;
                    }
                });

                if (isChecked) {
                    var input_box = $("<input name=" + obj.code + " type='" + obj.type + "' value='"+option.key+"' />").addClass("cell-checkbox").prop("checked", isChecked);
                    var lable_checkbox = $("<label for=" + obj.code + ">" + option.value + "</lable>").addClass("label-options");
                    input_box_div.append(input_box).append(lable_checkbox);
                } else {
                    var input_box = $("<input name=" + obj.code + " type='" + obj.type + "' value='"+option.key+"' />").addClass("cell-checkbox");
                    var lable_checkbox = $("<label for=" + obj.code + ">" + option.value + "</lable>").addClass("label-options");
                    input_box_div.append(input_box).append(lable_checkbox);
                }
            });
        }else{
            $.each(obj.val, function (v, option) {
                var lable_checkbox = $("<span>" + option[obj.code] + "</span>").css({
                    "margin-left":"10px",
                    "display":"inline-block"
                });
                input_box_div.addClass("cell-input-box-value").append(lable_checkbox);
            });
        }
        obj.isRow = false;
        return input_box_div;
    }
    function drawRadio(obj,mo){
        var input_box_div = $("<div></div>").addClass("cell-input-box");
        //添加单选值内容
        if(mo.flag=="add"){
            $.each(obj.options, function (v, option) {
                var input_box = $("<input name=" + obj.code + " type='" + obj.type + "' value='"+option.key+"' />").addClass("cell-radio");
                var lable_checkbox = $("<label for=" + obj.code + ">" + option.value + "</lable>").addClass("label-options");
                input_box_div.append(input_box).append(lable_checkbox);
            });
        }else if(mo.flag=="edit"){
            $.each(obj.options, function (v, option) {
                var isChecked=false;
                if(option.key==obj.val){
                    isChecked=true;
                }
                if(isChecked){
                    var input_box = $("<input name=" + obj.code + " type='" + obj.type + "' value='"+option.key+"' />").addClass("cell-radio").prop("checked", isChecked);
                }else{
                    var input_box = $("<input name=" + obj.code + " type='" + obj.type + "' value='"+option.key+"' />").addClass("cell-radio");
                }
                var lable_checkbox = $("<label for=" + obj.code + ">" + option.value + "</lable>").addClass("label-options");
                input_box_div.append(input_box).append(lable_checkbox);
            });
        }else{
        	 var lable_checkbox = $("<span>" + obj.val + "</span>").css({
                 "margin-left":"10px",
                 "display":"inline-block"
             });
             input_box_div.addClass("cell-input-box-value").append(lable_checkbox);
        }

        obj.isRow = false;
        return input_box_div;
    }
    function drawSelect(obj,mo){
        var input_box_div = $("<div></div>").addClass("cell-input-box");
        var input_box = $("<select id=" + obj.code + " name=" + obj.code + "></select>").addClass("cell-select");
        if(mo.flag=="add"){
            $.each(obj.options, function (v, option) {
                var select_option = $("<option value=" + option.key + ">" + option.value + "</option>");
                input_box.append(select_option);
            });
            input_box_div.append(input_box);
        }else if(mo.flag=="edit"){
            $.each(obj.options, function (v, option) {
                var isChecked=false;
                if(option.key==obj.val){
                    isChecked=true;
                }
                if(isChecked){
                    var select_option = $("<option selected='selected' value=" + option.key + ">" + option.value + "</option>");
                }else{
                    var select_option = $("<option value=" + option.key + ">" + option.value + "</option>");
                }
                input_box.append(select_option);
            });
            input_box_div.append(input_box);
        }else{
                var select_option = $("<span>" + obj.val + "</span>");
                input_box_div.addClass("cell-input-box-value").append(select_option);
        }

        obj.isRow = false;
        return input_box_div;
    }
    function drawImage(obj,mo){
        var input_box_div = $("<div></div>").addClass("cell-input-box");
        if(mo.flag=="add"){
            //var input_box = $("<input id=" + obj.code + " name=" + obj.code + " type='" + obj.type + "' />").addClass("cell-input cell-img-input");
            var input_box = $("<input type='button' value='上传头像' />").addClass("cell-input cell-img-input")
                .attr("data-toggle","modal")
                .attr("data-target","#pop-upload-layer").on("click",function(){
                	$("#pop-upload-type").val("1");
                    $("#fileuploadMadel").empty();  //清空demo上传层
                    //$("#fileuploadMadel").append($("<div id='demo'></div>").addClass("demo"));
                    $("#fileuploadMadel").uploadFilePop({
                        title:"选择文件",
                        url:"/ehr/files/userAllFiles.json",
                        params:{
                        	currentPage:1,
                        	showCount:20,
                        	fileType:1
                        }
                    });
                    //重新初始化数据
                    ZYFILE.init();
                    //绘制上传层
                    drawDemoMadel();
                });
            var imgs=$("<div></div>").css({"margin":"5px 0px"});
            input_box_div.append(input_box).append(imgs);
            //添加隐藏域
            $("<input id=" + obj.code + " name=" + obj.code + " type='hidden' value='' />").appendTo(input_box_div);
        }else if(mo.flag=="edit"){
            //var input_box = $("<input id=" + obj.code + " name=" + obj.code + " type='" + obj.type + "' />").addClass("cell-input cell-img-input");
            var input_box = $("<input type='button' value='上传头像' />").addClass("cell-input cell-img-input")
                .attr("data-toggle","modal")
                .attr("data-target","#pop-upload-layer").on("click",function(){
                	$("#pop-upload-type").val("1");
                    $("#fileuploadMadel").empty();  //清空demo上传层
                    //$("#fileuploadMadel").append($("<div id='demo'></div>").addClass("demo"));
                    $("#fileuploadMadel").uploadFilePop({
                        title:"选择文件",
                        url:"/ehr/files/userAllFiles.json",
                        params:{
                        	currentPage:1,
                        	showCount:20,
                        	fileType:1
                        }
                    });
                    //重新初始化数据
                    ZYFILE.init();
                    //绘制上传层
                    drawDemoMadel();
                });
            //展示已上传的图片
            var imgs=$("<div></div>").css({"margin":"5px 0px"});
            var img_ids="";
            $.each(obj.val,function(i,item){
                var img_block=$("<div></div>").css({"margin-right":"15px","width":"100px","height":"100px","display":"inline-block","position":"relative"});
                var img_a=$("<a></a>").css({"display":"block"}).attr("href","/ehr/files/downFile/"+item[obj.code]+".htm").attr("data-lightbox","example-1");
                var img_item=$("<img/>").attr("src","/ehr/files/downThumbFile/"+item[obj.code]+".htm").css({"width":"100%","height":"100%"});
                var img_del=$("<span title='删除'></span>")
                .addClass("glyphicon glyphicon-remove")
                .attr("imgId",item[obj.code])
                .css({"position":"absolute","top":"5px","right":"5px","cursor":"pointer"})
                .on("click",function(){
                	var tempArr=$("#"+obj.code).val().split(",");
                	for(var i in tempArr){
                		if(tempArr[i]==item[obj.code]){
                			tempArr.splice(i,1);
                		}
                	}
                	$("#"+obj.code).val(arrToStr(tempArr,","));
                	$(this).parent().remove();
                });
                imgs.append(img_block.append(img_a.append(img_item)).append(img_del));
                img_ids+=item[obj.code]+",";
            });
            input_box_div.append(input_box).append(imgs);
            img_ids=img_ids.substring(0,img_ids.length-1);
            $("<input id=" + obj.code + " name=" + obj.code + " type='hidden' value='"+img_ids+"' />").appendTo(input_box_div);
        }else{
            //展示已上传的图片
            var imgs=$("<div id='ss'></div>").css({"margin":"0px"});
            $.each(obj.val,function(i,item){
                var img_block=$("<div></div>").css({"margin-right":"15px","width":"100px","height":"100px","display":"inline-block","position":"relative"});
                var img_a=$("<a></a>").css({"display":"block"}).attr("href","/ehr/files/downFile/"+item[obj.code]+".htm").attr("data-lightbox","example-set");
                var img_item=$("<img/>").attr("src","/ehr/files/downThumbFile/"+item[obj.code]+".htm").css({"width":"100%","height":"100%"});
                //var img_banner=$("<span></span>").addClass("glyphicon glyphicon-remove").css({"position":"absolute","top":"5px","right":"5px"});
                imgs.append(img_block.append(img_a.append(img_item)));
            });
            input_box_div.addClass("cell-input-box-value").append(imgs);
        }
        obj.isRow = true;
        return input_box_div;
    }
    function drawAttachment(obj,mo){
        var input_box_div = $("<div></div>").addClass("cell-input-box");
        if(mo.flag=="add"){
            var input_box = $("<input type='button' value='上传附件' />").addClass("cell-input cell-attach-input")
                .attr("data-toggle","modal")
                .attr("data-target","#pop-upload-layer").on("click",function(){
                	$("#pop-upload-type").val("2");
                    $("#fileuploadMadel").empty();  //清空demo上传层
                    //$("#fileuploadMadel").append($("<div id='demo'></div>").addClass("demo"));
                    $("#fileuploadMadel").uploadFilePop({
                    	title:"选择文件",
                        url:"/ehr/files/userAllFiles.json",
                        params:{
                        	currentPage:1,
                        	showCount:20
                        }
                    });
                    //重新初始化数据
                    ZYFILE.init();
                    //绘制上传层
                    drawDemoMadel();
                });
            var atts=$("<div></div>").css({"margin":"5px 0px"});
            input_box_div.append(input_box).append(atts);
            
            
            
            
            //添加隐藏域
            $("<input id=" + obj.code + " name=" + obj.code + " type='hidden' value='' />").appendTo(input_box_div);
        }else if(mo.flag=="edit"){
            var input_box = $("<input type='button' value='上传附件' />").addClass("cell-input cell-attach-input")
                .attr("data-toggle","modal")
                .attr("data-target","#pop-upload-layer").on("click",function(){
                	$("#pop-upload-type").val("2");
                    $("#fileuploadMadel").empty();  //清空demo上传层
                    //$("#fileuploadMadel").append($("<div id='demo'></div>").addClass("demo"));
                    $("#fileuploadMadel").uploadFilePop({
                    	title:"选择文件",
                        url:"/ehr/files/userAllFiles.json",
                        params:{
                        	currentPage:1,
                        	showCount:20
                        }
                    });
                    //重新初始化数据
                    ZYFILE.init();
                    //绘制上传层
                    drawDemoMadel();
                });
            //展示已上传的附件
            var atts=$("<div></div>").css({"margin":"5px 0px"});
            var attach_ids="";
            $.each(obj.val,function(i,item){
                var attr_block=$("<div></div>").css({"margin-right":"15px","display":"inline-block"});
                var attr_item=$("<a></a>").attr("href","/ehr/files/downFile/"+item[obj.code]+".htm").text(item.filename);
                var attr_del=$("<span title='删除'></span>")
                .addClass("glyphicon glyphicon-remove")
                .attr("imgId",item[obj.code])
                .css({"margin-left":"2px","cursor":"pointer"})
                .on("click",function(){
                	delAtta(this,item[obj.code])
                });
                atts.append(attr_block.append(attr_item).append(attr_del));
                attach_ids+=item[obj.code]+",";
            });
            input_box_div.append(input_box).append(atts);
            attach_ids=attach_ids.substring(0,attach_ids.length-1);
            $("<input id=" + obj.code + " name=" + obj.code + " type='hidden' value='"+attach_ids+"' />").appendTo(input_box_div);
        }else{
            //展示已上传的附件
            var atts=$("<div></div>").css({"margin":"0px"});
            $.each(obj.val,function(i,item){
                var attr_block=$("<div></div>").css({"margin-right":"15px","display":"inline-block"});
                var attr_item=$("<a></a>").attr("href","/ehr/files/downFile/"+item[obj.code]+".htm").text(item.filename);
                // var attr_del=$("<span></span>").addClass("glyphicon glyphicon-remove").css({"margin-left":"2px"});
                atts.append(attr_block.append(attr_item));
            });
            input_box_div.addClass("cell-input-box-value").append(atts);
        }
        //var input_box = $("<input id=" + obj.code + " name=" + obj.code + " type='" + obj.type + "' />").addClass("cell-input cell-attach-input");
        obj.isRow = true;
        return input_box_div;
    }
    function drawInputDefault(obj,mo){
        var input_box_div = $("<div></div>").addClass("cell-input-box");
        if(mo.flag=="add"){
            var input_box = $("<input id=" + obj.code + " name=" + obj.code + " type='" + obj.type + "' />").addClass("cell-input");
            input_box_div.append(input_box);
        }else if(mo.flag=="edit"){
            var input_box = $("<input id=" + obj.code + " name=" + obj.code + " type='" + obj.type + "' />").addClass("cell-input").val(obj.val);
            input_box_div.append(input_box);
        }else{
            input_box_div.addClass("cell-input-box-value").text(obj.val);
        }
        obj.isRow = false;
        return input_box_div;
    }

    function addFieldLayout(data,mo) {
        if (data["isAddField"]) {
            /**
             * 定义自添加字段按钮布局
             * **/
        	
           var showPopoverBtn = $("<button type='button' class='form-af-btn' data-container='body' data-placement='top' data-toggle='popover'><span class='glyphicon glyphicon-plus'></span></button>");
        	 //var showPopoverBtn = $("<a tabindex='0' class='btn form-af-btn' role='button' data-container='body' data-placement='top' data-toggle='popover' data-trigger='focus'><span class='glyphicon glyphicon-plus'></span></a>");
            var addFieldColumn = $("<div></div>").addClass("cell-column");
            setColumnGrids(addFieldColumn, 1, false,mo.deviceMap);
            addFieldColumn.append(showPopoverBtn);
            mo.container.append(addFieldColumn);
            var c_i=0;
            showPopoverBtn.click(function() {
                    if(c_i%2==0){
                        $(this).find("span").removeClass("addField-transform-back");
                        $(this).find("span").addClass("addField-transform");
                        c_i=1;
                    }else{
                        $(this).find("span").removeClass("addField-transform");
                        $(this).find("span").addClass("addField-transform-back");
                        c_i=0;
                    }
                }
            )
            /**
             * 弹出面板内容布局设置
             * **/
            showPopoverBtn.popover({
                "html": true,
                "content": function () {
                    //设置添加字段内容
                    var boxDiv = $("<div></div>").addClass("popover-box");
                    $("<div class='popover-line'><label class='popover-label'>字段名称: </label><input id='addFieldName' class='popover-input' name='add_field_name' /></div>").appendTo(boxDiv);
                    $("<div class='popover-line'><label class='popover-label'>字段类型: </label><select id='addFiledType' class='popover-select'>" +
                        "<option value='text'>文本</option>" +
                        "<option value='number'>数值</option>" +
                        "<option value='textarea'>大文本框</option>" +
                        "</select></div>").appendTo(boxDiv);
                    $("<div class='popover-line pull-right'></div>")
                        .append($("<button type='button' id='addFieldBtn' class='btn btn-save-field btn-sm pull-right'>确定</button>").on('click', function () {
                            //增加字段
                            var myfield = {
                                "code": "myfield" + "-" + new Date().getTime(),
                                "name": $("#addFieldName").val(),
                                "len": 1,
                                "fieldType": $("#addFiledType").val()
                            }
                            var customerField = new Field(myfield,mo);
                            customerField.isCustomerAdd = true;
                            customerField.layout();
                            $(".popover").popover('hide');
                            $(".form-btn-group").before($(".form-af-btn").parent());

                            $(showPopoverBtn).find("span").removeClass("addField-transform");
                            $(showPopoverBtn).find("span").addClass("addField-transform-back");
                            c_i=0;
                        })).appendTo(boxDiv);
                    return boxDiv;
                }
            });
        }
    }


    /**
     * 根据不同设备(分辨率)设置每列的跨列样式
     */
    function setColumnGrids(module_column,field_len,isRow,deviceMap){
        for(var dt in deviceMap){
            if(deviceMap.hasOwnProperty(dt)){
                var devObj=deviceMap[dt];
                var column_colspan=devObj.grids+devObj.grids*(field_len-1);
                if(column_colspan>12 || isRow){
                    column_colspan=12;
                }
                module_column.addClass("col-"+dt+"-"+column_colspan);

            }
        }
    }
    
    
    $.templet={
    		getColumns:function(data){
    			var columnArr=new Array();
    	        if(data["showFields"]!="undefined" && data["showFields"]!=null && data["showFields"].length>0){
    	            $.each(data["showFields"],function(i,showfield){
    	                var tempField={
    	                    "field":showfield.code,
    	                    "title":showfield.field
    	                };
    	                columnArr.push(tempField);
    	            });
    	        }else{
    	            $.each(data["fields"],function(i,showfield){
    	                if(i<10) {
    	                    var tempField = {
    	                        "field": showfield.code,
    	                        "title": showfield.name
    	                    };
    	                    columnArr.push(tempField);
    	                }else{
    	                    return;
    	                }
    	            });
    	        }
    	    	return columnArr;
    		}
    };
    
    
    
    /**
     * 单选多选下拉框控制项显示隐藏方法
     */

    function initRadio(contianer,obj,options){
        var temp_val=$(obj).val();
        if($(obj).prop("checked")){    //选中则显示内容
            $.each(options["options"],function(o,item){
                var tempVal=item["key"];
                var fcodes=item["control"];
                if(fcodes!=null && fcodes!=undefined && fcodes!=""){
                    if(temp_val==tempVal){
                        for(var m=0;m<fcodes.length;m++){
                            var fcode=fcodes[m];
                            $(contianer).find("div[fieldcode='"+fcode+"']").show();
                        }
                    }
                }
            });
        }else{
            $.each(options["options"],function(o,item){
                var tempVal=item["key"];
                var fcodes=item["control"];
                if(fcodes!=null && fcodes!=undefined && fcodes!=""){
                    if(temp_val==tempVal){
                        for(var m=0;m<fcodes.length;m++){
                            var fcode=fcodes[m];
                            $(contianer).find("div[fieldcode='"+fcode+"']").hide();
                        }
                    }
                }

            });
        }
    }
    function handleRadio(contianer,obj,options){
        var temp_val=$(obj).val();
        if($(obj).prop("checked")){    //选中则显示内容
            $.each(options["options"],function(o,item){
                var tempVal=item["key"];
                var fcodes=item["control"];
                if(fcodes!=null && fcodes!=undefined && fcodes!=""){
                    if(temp_val==tempVal){
                        for(var m=0;m<fcodes.length;m++){
                            var fcode=fcodes[m];
                            $(contianer).find("div[fieldcode='"+fcode+"']").show();
                        }
                    }else{
                        for(var m=0;m<fcodes.length;m++){
                            var fcode=fcodes[m];
                            $(contianer).find("div[fieldcode='"+fcode+"']").hide();
                        }
                    }
                }

            });
        }
    }

    function initCheckBox(contianer,obj,options){
        var temp_val=$(obj).val();
        if($(obj).prop("checked")){    //选中则显示内容
            $.each(options["options"],function(o,item){
                var tempVal=item["key"];
                var fcodes=item["control"];
                if(fcodes!=null && fcodes!=undefined && fcodes!=""){
                    if(temp_val==tempVal){
                        for(var m=0;m<fcodes.length;m++){
                            var fcode=fcodes[m];
                            $(contianer).find("div[fieldcode='"+fcode+"']").show();
                        }
                    }
                }

            });
        }else{
            $.each(options["options"],function(o,item){
                var tempVal=item["key"];
                var fcodes=item["control"];
                if(fcodes!=null && fcodes!=undefined && fcodes!=""){
                    if(temp_val==tempVal){

                        for(var m=0;m<fcodes.length;m++){
                            var fcode=fcodes[m];

                            $(contianer).find("div[fieldcode='"+fcode+"']").hide();
                        }
                    }
                }

            });
        }
    }

    function handleCheckBox(contianer,obj,options){
        var temp_val=$(obj).val();
        if($(obj).prop("checked")){    //选中则显示内容
            $.each(options["options"],function(o,item){
                var tempVal=item["key"];
                var fcodes=item["control"];
                if(fcodes!=null && fcodes!=undefined && fcodes!=""){
                    if(temp_val==tempVal){
                        for(var m=0;m<fcodes.length;m++){
                            var fcode=fcodes[m];
                            $(contianer).find("div[fieldcode='"+fcode+"']").show();
                        }
                    }
                }

            });
        }else{
            $.each(options["options"],function(o,item){
                var tempVal=item["key"];
                var fcodes=item["control"];
                if(fcodes!=null && fcodes!=undefined && fcodes!=""){
                    if(temp_val==tempVal){
                        for(var m=0;m<fcodes.length;m++){
                            var fcode=fcodes[m];
                            $(contianer).find("div[fieldcode='"+fcode+"']").hide();
                        }
                    }
                }

            });
        }
    }



    function initSelect(contianer,obj,options){
        var temp_val=$(obj).val();
        if($(obj).prop("selected")){    //选中则显示内容
            $.each(options["options"],function(o,item){
                var tempVal=item["key"];
                var fcodes=item["control"];
                if(fcodes!=null && fcodes!=undefined && fcodes!=""){
                    if(temp_val==tempVal){
                        for(var m=0;m<fcodes.length;m++){
                            var fcode=fcodes[m];
                            $(contianer).find("div[fieldcode='"+fcode+"']").show();
                        }
                    }
                }

            });
        }else{
            $.each(options["options"],function(o,item){
                var tempVal=item["key"];
                var fcodes=item["control"];
                if(fcodes!=null && fcodes!=undefined && fcodes!=""){
                    if(temp_val==tempVal){

                        for(var m=0;m<fcodes.length;m++){
                            var fcode=fcodes[m];

                            $(contianer).find("div[fieldcode='"+fcode+"']").hide();
                        }
                    }
                }

            });
        }
    }

    function handleSelect(contianer,obj,options){
        var temp_val=$(obj).val();
        $.each(options["options"],function(o,item){
            var tempVal=item["key"];
            var fcodes=item["control"];
            if(fcodes!=null && fcodes!=undefined && fcodes!=""){
                if(temp_val==tempVal){
                    for(var m=0;m<fcodes.length;m++){
                        var fcode=fcodes[m];
                        $(contianer).find("div[fieldcode='"+fcode+"']").show();
                    }
                }else{
                    for(var m=0;m<fcodes.length;m++){
                        var fcode=fcodes[m];
                        $(contianer).find("div[fieldcode='"+fcode+"']").hide();
                    }
                }
            }

        });
    }



    function showColumnsByChecked(container,data){
            var _this=container;
            $.each(data["fields"],function(i,field){
                var fieldType=field["fieldType"];
                if(fieldType=="radio"){
                    $(_this).find("input[name='"+field["code"]+"']").each(function(){
                        var _this_=$(this);
                        initRadio(_this,_this_,field);
                    });
                    if(field["options"].length>0){
                        $(_this).find("input[name='"+field["code"]+"']").on("click",function(){
                            var _this_=$(this);
                            handleRadio(_this,_this_,field);
                        });
                    }
                }

                if(fieldType=="checkbox"){
                    $(_this).find("input[name='"+field["code"]+"']").each(function(){
                        var _this_=$(this);
                        initCheckBox(_this,_this_,field);
                    });
                    if(field["options"].length>0){
                        $(_this).find("input[name='"+field["code"]+"']").on("click",function(){
                            var _this_=$(this);
                            handleCheckBox(_this,_this_,field);
                        });
                    }
                }



                if(fieldType=="select"){
                    $(_this).find("#"+field["code"]+">option").each(function(){
                        var _this_=$(this);
                        initSelect(_this,_this_,field);
                    });
                    $(_this).find("#"+field["code"]).on("change",function(){
                        var _this_=$(this);
                        handleSelect(_this,_this_,field);
                    });
                }
            });
    }

    
    
    

    $.fn.extend({
        loadMD:function(data,dataVal,flag){ //单值 新增、修改、展示
            var _this=this;
            _this.empty();
                /**
                 * 设备类型对应栅格系统比例
                 * @type {number}
                 */
                if(data["showTemplates"]!=null && data["showTemplates"]!="undefined" && data["showTemplates"].length>0){
                    var tempDeviceMap={};
                    $.each(data["showTemplates"],function(i,dev){
                        var deviceType=dev["type"];
                        var device={
                            "columns":dev["module_col"],
                            "grids":mo.grid/dev["module_col"]
                        };
                        tempDeviceMap[deviceType]=device;
                    });
                    mo.deviceMap= $.extend(true,mo.deviceMap,tempDeviceMap);
                }
                mo.container=_this;
                mo.flag=flag;
                mo.data=dataVal;
                /**
                 * 动态加载模块字段布局
                 */
                $.each(data["fields"],function(i,field){
                    var myField=new Field(field,mo);
                    myField.layout();
                });
                /**
                 * 自定义字段
                 */
                /**
                if(flag!="show"){
                    addFieldLayout(data,mo);
                }
                **/
                _this.append("<div class='clearfix'></div>");
                showColumnsByChecked(_this,data);
        }
    });
})(jQuery);