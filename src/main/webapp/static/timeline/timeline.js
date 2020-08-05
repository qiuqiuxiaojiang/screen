$(function(){
    var initPage=1;
    var userId=$('#userId').val();
    var totalCount=totalPage(userId);
	var _timeline_row_ = $("<div></div>").addClass("row");
	$(".timeline-container").append(_timeline_row_);
    var loadData=function(){
        $.getJSON("/ehr/analysisResult/reportList/"+userId+".json?page="+initPage, function (data) {
        	$.each(data.dataMap.list, function (i, tl){
                var _timeline_ = $("<div></div>").addClass("timeline");
                _timeline_row_.append(_timeline_);
                var _time_block_ = $("<div></div>").addClass("timeline-block");
                var _time_content_ = $("<div></div>").addClass("popover timeline-content");
                _time_block_.append(_time_content_);
           
                 //设置显示内容
                var _popover_title_ = $("<h3></h3>").addClass("popover-title").text(tl.diagTime);
                var _popover_footer_ = $("<div></div>").addClass("popover-footer").text(tl["result"]);
                var _popover_content_ = $("<div></div>").addClass("popover-content");
                _time_content_.append($("<div></div>").addClass("arrow"))
                    .append(_popover_title_)
                    .append(_popover_content_)
                    .append(_popover_footer_);
                
                
                 //主页展示内容布局
                var imgObj=tl["leftImages"];
                if(imgObj!=null && imgObj != "undefined"){
                	if(imgObj.length>1){
                		var _img_container_ = $("<div></div>").css("margin-bottom", "10px");
                        var _table_container_ = $("<table></table>").addClass("table table-bordered table-condensed");
                        for (var m = 0; m < imgObj.length; m++) {
                            _img_container_.append($("<img/>").attr("src", "/ehr/files/downThumbFile/"+imgObj[m].imageId+".htm").css({"width":"60px","height":"60px","margin-right":"8px"}));
                        }
                        _popover_content_.append(_img_container_);
                        _table_container_.append($("<tr></tr>")
                                .append($("<td nowrap></td>").text("眼象特征"))
                                .append($("<td></td>").text(tl["feature"]))
                        );

                        _table_container_.append($("<tr></tr>")
                                .append($("<td nowrap></td>").text("匹配结果"))
                                .append($("<td></td>").text(tl["matchList"]))
                        );

                        _table_container_.append($("<tr></tr>")
                                .append($("<td nowrap></td>").text("结论说明"))
                                .append($("<td></td>").text(tl["desc"]))
                        );

                        _popover_content_.append(_img_container_).append(_table_container_);
                	}else if(imgObj.length==1){
                		 var _img_container_ = $("<div></div>")
                         .addClass("pull-left")
                         .append($("<img/>").attr("src","/ehr/files/downThumbFile/"+imgObj[0].imageId+".htm").css({"margin": "5px 10px","width": "100px", "height": "100px"}));
	                     var _text_container_ = $("<p></p>").css({"margin-left": "10px", "margin-top": "5px", "font-size": "12px"})
	                         .append("<span>眼象特征</span>" + tl["feature"]).append("<br/>")
	                         .append("<span>匹配结果</span>" + tl["matchList"]).append("<br/>")
	                         .append("<span>结论说明</span>" + tl["desc"]).append("<br/>");
	                     _popover_content_.append(_img_container_).append(_text_container_);
                	}
                }else{
                	 var _text_container_ = $("<p></p>").css({"margin-left": "10px", "margin-top": "5px", "font-size": "12px"})
                     .append("<span>眼象特征</span>" + tl["feature"]).append("<br/>")
                     .append("<span>匹配结果</span>" + tl["matchList"]).append("<br/>")
                     .append("<span>结论说明</span>" + tl["desc"]).append("<br/>");
                	 _popover_content_.append(_text_container_);
                }
               
                $(_timeline_).append(_time_block_)
                    .append($("<div></div>").addClass("timeline-img"))
                    .append($("<div></div>").addClass("timeline-line"))
                    .append($("<div></div>").addClass("clearfix"));
                if ($(_timeline_).prev().find(".timeline-block").hasClass("timeline-block-odd")) {
                    $(_timeline_).find(".timeline-block").addClass("timeline-block-even");
                    $(_timeline_).find(".timeline-block-even>.timeline-content").addClass("right").css({"left": "150px"});
                } else {
                    $(_timeline_).find(".timeline-block").addClass("timeline-block-odd");
                    $(_timeline_).find(".timeline-block-odd>.timeline-content").addClass("left").css({"left": "-150px"});
                }
                $(_timeline_).find(".timeline-block>.timeline-content").animate({
                    left: "0px"
                }, 1000);
            });
            if($(window).height()>=document.documentElement.scrollHeight){
                //没有出现滚动条,继续加载下一页
            	initPage++;
            	if(initPage<=totalCount){
            		loadData();
            	}else{
            		initPage--;
            	}
                
            }
        });
    };
    
    var tcScroll=function(){
        $(window).on('scroll', function () {
            var scrollTop = $(this).scrollTop();
            var scrollHeight = $(document).height();
            var windowHeight = $(this).height();
            if (scrollTop + windowHeight == scrollHeight) {
                //此处是滚动条到底部时候触发的事件，在这里写要加载的数据，或者是拉动滚动条的操作
            	initPage++;
            	if(initPage<=totalCount){
            		loadData();
            	}else{
            		initPage--;
            	}
            }
        });
    };
    loadData();
    tcScroll();

    
    
    function totalPage(userId){
    	var totalPage=0;
    	$.ajax({
        	url:'/ehr/analysisResult/totalCount/'+userId+'.json',
        	async:false,
        	type:'post',
        	success:function(data){
        		totalPage=data.data;
        	}
        })
        return totalPage;
    }
});