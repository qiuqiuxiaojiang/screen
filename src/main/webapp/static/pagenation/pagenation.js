/**
 * Created by chunyangji on 2015/9/21.
 */
(function($){
	
	var args={
	        params:{},
	        multiChecked:true,
	        hasRowNumber:true,
	        width:"auto",
	        pagenation:true,
	        pageCount : 1,
	        current : 1,
	        showRowsArr:[10,20,30],
	        backFn : function(){
	        	$("[data-toggle='tooltip']").tooltip();
	        	//点击显示放大热力图	
	    		$("#mytable tr td:last-child").click(function(){
	    			var height = document.body.scrollTop;
	    			$(".pop").height($(document.body).height());
	    			$(".pop").css("display","block");
	    			var img=$(this).html();
	    			$(".pop").append(img);
	    			$(".pop").css("padding-top",height+"px");
	    			$(document.body).css({
	    				   "overflow-x":"hidden",
	    				   "overflow-y":"hidden"
	    				 });
	    		});
	    		$("#mytable tr td:last-child").prev().click(function(){
	    			var height = document.body.scrollTop;
	    			$(".pop").height($(document.body).height());
	    			$(".pop").css("display","block");
	    			var img1=$(this).html();
	    			$(".pop").append(img1);
	    			$(".pop").css("padding-top",height+"px");
	    			$(document.body).css({
	    				   "overflow-x":"hidden",
	    				   "overflow-y":"hidden"
	    				 });
	    		});
	    		$(".pop").click(function(){
	    			$(".pop").css("display","none");
	    			$(".pop").html("");
	    			$(document.body).css({
	    				   "overflow-x":"auto",
	    				   "overflow-y":"auto"
	    				 });
	    		});
	        }
	    }

    var ms = {
        init:function(obj,args){
            return (function(){
                ms.fillHtml(obj,args);
                ms.bindEvent(obj,args);
            })();
        },
        //绘制分页内容
        fillHtml:function(obj,args){
            return (function(){
                //每次都先清空容器
                //并添加分页栏
                obj.empty();
                var page_num_bar=$("<ul></ul>").addClass("pagination pagination-sm")
                obj.append(page_num_bar);
                //设置上一页展示样式


                if(args.current > 1){
                    var pb_li=$("<li></li>");
                    var pb_li_a=$("<a href='javascript:;' class='prevPage'></a>");
                    pb_li_a.append("<span aria-hidden='true'>&laquo;</span>");
                    pb_li.append(pb_li_a);
                    page_num_bar.append(pb_li);
                }else{
                    page_num_bar.remove('.prevPage')
                    var pb_li=$("<li></li>").addClass("disabled").append("<span aria-hidden='true'>&laquo;</span>");
                    page_num_bar.append(pb_li);
                }
                //设置分页条展示样式
                /**
                 * 当当前展示页大于等于一定值后,需要将第一页展展现,以便可以直接点击返回首页
                 */
                if(args.current != 1 && args.current >= 4 && args.pageCount != 4){
                    var pb_li=$("<li></li>");
                    var pb_li_a=$("<a href='javascript:;' class='tcdNumber'>1</a>");
                    pb_li.append(pb_li_a);
                    page_num_bar.append(pb_li);
                }
                /**
                 * 计算何时显示更多提示
                 */
                if(args.current-2 > 2 && args.current <= args.pageCount && args.pageCount > 5){

                    page_num_bar.append('<li><span>...</span></li>');
                }
                var start = args.current -2,end = args.current+2;
                if((start > 1 && args.current < 4)||args.current == 1){
                    end++;
                }
                if(args.current > args.pageCount-4 && args.current >= args.pageCount){
                    start--;
                }
                for (;start <= end; start++) {
                    if(start <= args.pageCount && start >= 1){
                        if(start != args.current){
                            var pb_li=$("<li></li>");
                            var pb_li_a=$("<a href='javascript:;' class='tcdNumber'>"+start+"</a>");
                            pb_li.append(pb_li_a);
                            page_num_bar.append(pb_li);
                        }else{
                            var pb_li=$("<li></li>").addClass("active");
                            var pb_li_a=$("<span>"+start+"</span>");
                            pb_li.append(pb_li_a);
                            page_num_bar.append(pb_li);
                        }
                    }
                }
                if(args.current + 2 < args.pageCount - 1 && args.current >= 1 && args.pageCount > 5){
                    page_num_bar.append('<li><span>...</span></li>');
                }
                if(args.current != args.pageCount && args.current < args.pageCount -2  && args.pageCount != 4){
                    var pb_li=$("<li></li>");
                    var pb_li_a=$("<a href='javascript:;' class='tcdNumber'>"+args.pageCount+"</a>");
                    pb_li.append(pb_li_a);
                    page_num_bar.append(pb_li);
                }
                //下一页展示样式
                if(args.current < args.pageCount){

                    var pb_li=$("<li></li>");
                    var pb_li_a=$("<a href='javascript:;' class='nextPage'></a>");
                    pb_li_a.html("<span aria-hidden='true'>&raquo;</span>");
                    pb_li.append(pb_li_a);
                    page_num_bar.append(pb_li);
                }else{
                    page_num_bar.remove(".nextPage");
                    var pb_li=$("<li></li>").addClass("disabled").append("<span aria-hidden='true'>&raquo;</span>");
                    page_num_bar.append(pb_li);
                }

                //添加每页可选展示行数
                var page_show_rows=$("<div></div>").addClass("input-group input-group-sm page-show-rows");
                var select_rows_box=$("<select></select>").addClass("select-show-rows");
                 for(var i=0;i<args.showRowsArr.length;i++){
                     if(args.params.showRows==args.showRowsArr[i]){
                      select_rows_box.append('<option selected="selected" value='+args.showRowsArr[i]+'>'+args.showRowsArr[i]+'</option>');
                     }
                     else
                     select_rows_box.append('<option value='+args.showRowsArr[i]+'>'+args.showRowsArr[i]+'</option>');
                 }
                page_show_rows.append(select_rows_box);

                obj.append(page_show_rows);

                //添加跳转框
                var page_turn_group=$("<div></div>").addClass("input-group input-group-sm page-turn-box");
                page_turn_group.append('<input type="text" class="form-control input-page-num" />')
                    .append($('<span class="input-group-btn"></span>')
                        .append('<button class="btn btn-primary gotoPage" type="button" style="padding:0px 5px">GO</button>'));
                obj.append(page_turn_group);
                //添加总计条数
                var page_counts_bar=$("<div></div>").addClass("page-count-bar");
                page_counts_bar.html("总计<span class='pageCounts'>"+args.totalCount+"</span>条")
                obj.append(page_counts_bar);
            })();
        },
        //绑定事件
        bindEvent:function(obj,args){
            return (function(){
                obj.on("click","a.tcdNumber",function(){
                    var current = parseInt($(this).text());
                    var showRows=obj.find(".select-show-rows").val();
                    args= $.extend(args,{"current":current,"pageCount":args.pageCount})
                    args.params= $.extend(args.params,{
                        current:current,
                        showRows:showRows
                    });
                    ms.fillHtml(obj,args);
                    ct.drawTableData($("#"+args.refId),args);
                    if(typeof(args.backFn)=="function"){
                        args.backFn(current);
                    }
                });
                //点击上一页
                obj.on("click","a.prevPage",function(){
                    var current = parseInt(obj.find(".pagination").children("li.active").text());
                    var showRows=obj.find(".select-show-rows").val();
                    args= $.extend(args,{"current":current-1,"pageCount":args.pageCount});
                    args.params= $.extend(args.params,{
                        current:current-1,
                        showRows:showRows
                    });
                    ms.fillHtml(obj,args);
                    ct.drawTableData($("#"+args.refId),args);
                    if(typeof(args.backFn)=="function"){
                        args.backFn(current-1);
                    }
                });
                //点击下一页
                obj.on("click","a.nextPage",function(){
                    var current = parseInt(obj.find(".pagination").children("li.active").text());
                    var showRows=obj.find(".select-show-rows").val();
                    args= $.extend(args,{"current":current+1,"pageCount":args.pageCount});
                    args.params= $.extend(args.params,{
                        current:current+1,
                        showRows:showRows
                    });
                    ms.fillHtml(obj,args);
                    ct.drawTableData($("#"+args.refId),args);
                    if(typeof(args.backFn)=="function"){
                        args.backFn(current+1);
                    }
                });

                //编号展示行数
                obj.on("change",".select-show-rows",function(){
                    var showRows=obj.find(".select-show-rows").val();
                    args= $.extend(args,{"current":1});
                    args.params= $.extend(args.params,{
                        current:1,
                        showRows:showRows
                    });
                    ms.fillHtml(obj,args);
                    ct.drawTableData($("#"+args.refId),args);
                });
                //点击跳转
                obj.on("click",".gotoPage",function(){
                    if(parseInt( obj.find(".input-page-num").val())){
                        var current = parseInt(obj.find(".input-page-num").val());
                        var showRows=obj.find(".select-show-rows").val();
                        current=current<1 ? 1 : current;
                        current=current>args.pageCount ? args.pageCount : current;
                        args= $.extend(args,{"current":current,"pageCount":args.pageCount});
                        args.params= $.extend(args.params,{
                            current:current,
                            showRows:showRows
                        });
                        ms.fillHtml(obj,args);
                        ct.drawTableData($("#"+args.refId),args);
                        if(typeof(args.backFn)=="function"){
                            args.backFn(current);
                        }
                    }else{
                        alert("请输入数值型");
                    }

                })
            })();
        }
    }
    var ct={
        init:function(obj,args){
            return (function(){
            	if($(obj.parent().parent().parent()).hasClass("panel")){
            		//$(obj.parent().parent().parent(".panel")).remove();
            		ct.drawTableData(obj,args); //绘制展示数据
            	}else{
            		
            	
        		var table_assist="";
                if(obj.prev().hasClass("table-assist") && obj.attr("id")==obj.prev().attr("for")){
                    table_assist=obj.prev();
                }
                var panel=$('<div class="panel panel-primary"></div>');
                obj.wrap(panel);
                obj.before('<div class="panel-heading">'+args.title+'</div>');
                obj.wrap('<div class="panel-body"></div>');
                obj.wrap("<div class='table-responsive'></div>");
                obj.parent().before(table_assist);


                ct.drawTHeader(obj,args);   //绘制表头
                ct.drawTableData(obj,args); //绘制展示数据
                ct.bindDataEvent(obj,args); //表格事件绑定
            	}
                //绘制分页栏
                /**
                if(args.pagenation){
                    var pageBar=$("<nav></nav>").addClass("pageBar pull-right"); 
                    args= $.extend(args,{refId:obj.attr("id")});
                    pageBar.createPage(args);
                    console.log("=========================================="+args.pageCount);
                    obj.after(pageBar);
                }
                **/
                
            })();
        },
        drawTHeader:function(obj,args){
            return (function(){
                obj.empty();
                /**
                 * 绘制table表头
                 */
                var thead_tr=$("<tr></tr>");
                if(args.multiChecked){
                    thead_tr.append('<th nowrap><input type="checkbox" name="ck_all" /></th>');
                }
                if(args.hasRowNumber){
                    thead_tr.append('<th nowrap>序号</th>');
                }
                for(var item in args.columns){
                    var column=args.columns[item];
                    var tr_th=$("<th></th>")
                        .css({"width":column.width})
                        .attr("name",column.field)
                        .html(column.title);
                    if(column.sorting){
                        tr_th.addClass("sorting");
                        $("<div></div>").addClass("sorting-box pull-right")
                            .append($("<span></span>").addClass("glyphicon glyphicon-triangle-top sorting-top"))
                            .append($("<span></span>").addClass("glyphicon glyphicon-triangle-bottom sorting-bottom"))
                            .appendTo(tr_th);
                    }

                    thead_tr.append(tr_th);
                }
                obj.append($("<thead></thead>").append(thead_tr));
            })();
        },
        drawTableData:function(obj,args){
            return (function(){
                /**
                 * 绘制内容展示行
                 */
                obj.find("tbody").remove();
                var tbody=$("<tbody></tbody>");
                var showRows=args.params.showRows==undefined?10:args.params.showRows;
                args.params= $.extend(args.params,{
                    "showRows":showRows
                });
                /**
                $.getJSON(args.url,args.params,function(returnObj){
                	var data=returnObj.dataMap.list;
                	var totalCount=returnObj.dataMap.totalCount;
                	var pageCount=Math.ceil(totalCount/showRows);
                	args=$.extend(args,{"pageCount":pageCount,totalCount:totalCount});
                	
                	
                    $.each(data,function(i,row){
                        var tbody_tr=$("<tr></tr>");
                        if(args.multiChecked){
                            tbody_tr.append('<th><input type="checkbox" name="ck_box" value="'+row['id']+'" /></th>');
                        }
                        if(args.hasRowNumber){
                            tbody_tr.append('<th>'+Number(i+1)+'</th>');
                        }
                        alert(args.columns.length);
                        for(var item in args.columns){
                            var column= $.extend({ formatter:function(val){
                                if(val=="null"||val==null||val=='')
                                    return "";
                                else
                                    return val;
                            }},args.columns[item]);
                            $("<td></td>").html(column.formatter(row[column.field],row)).appendTo(tbody_tr);

                        }
                        tbody_tr.appendTo(tbody);
                    });
                    
                    
                    if(args.pagenation){
                    	obj.next("nav.pageBar").remove();
                        var pageBar=$("<nav></nav>").addClass("pageBar pull-right"); 
                        args= $.extend(args,{refId:obj.attr("id")});
                        pageBar.createPage(args);
                        obj.after(pageBar);
                    }    
                });
                **/
                
                $.ajax({ 
                	 url:args.url,
                     async:false,
                     cache:false,
                     dataType:"json",
                     data:args.params, 
                     success:function (returnObj){
                    	 var data=returnObj.dataMap.list;
                     	var totalCount=returnObj.dataMap.totalCount;
                     	var pageCount=Math.ceil(totalCount/showRows);
                     	args=$.extend(args,{"pageCount":pageCount,totalCount:totalCount});
                     	
                     	
                         $.each(data,function(i,row){
                             var tbody_tr=$("<tr></tr>");
                             if(args.multiChecked){
                                 tbody_tr.append('<th><input type="checkbox" name="ck_box" value="'+row['id']+'" /></th>');
                             }
                             if(args.hasRowNumber){
                                 tbody_tr.append('<th>'+Number(i+1)+'</th>');
                             }
                             for(var item in args.columns){
                                 var column= $.extend({ formatter:function(val){
                                     if(val=="null"||val==null||val=='')
                                         return "";
                                     else
                                         return val;
                                 }},args.columns[item]);
                                 $("<td></td>").html(column.formatter(row[column.field],row)).appendTo(tbody_tr);

                             }
                             tbody_tr.appendTo(tbody);
                         });
                         
                         /**
                         if(args.pagenation){
                         	obj.next("nav.pageBar").remove();
                             var pageBar=$("<nav></nav>").addClass("pageBar pull-right"); 
                             args= $.extend(args,{refId:obj.attr("id")});
                             pageBar.createPage(args);
                             obj.after(pageBar);
                         }   
                         **/ 
                         if(args.pagenation){
                          	obj.parent().next("nav.pageBar").remove();
                              var pageBar=$("<nav></nav>").addClass("pageBar pull-right"); 
                              args= $.extend(args,{refId:obj.attr("id")});
                              pageBar.createPage(args);
                              obj.parent().after(pageBar);
                          } 
         	        }
                });
                
                obj.append(tbody);
            })();
        },
        bindDataEvent:function(obj,args){
            return (function(){
                obj.find("input[name='ck_all']").on("click",function(event){
                    if($(this).is(":checked")){
                        obj.find("input[name='ck_box']").prop("checked", true);
                    }else{
                        obj.find("input[name='ck_box']").prop("checked", false);
                    }
                });

                obj.find("th.sorting").on("click",function(event){
                        //var $this=$(event.srcElement ? event.srcElement :event.target);
                         var sort_field=$(this).attr("name");
                         var sort_order=1;
                         obj.find("th.sorting")
                             .not($(this))
                             .removeClass("desc")
                             .removeClass("asc")
                             .find(".sorting-box span")
                             .show();
                         if($(this).hasClass("asc")){
                             $(this).removeClass("asc").addClass("desc");
                             $(this).find("span.sorting-bottom").show()
                             $(this).find("span.sorting-top").hide();
                             sort_order=-1
                         }else{
                             $(this).removeClass("desc").addClass("asc");
                             $(this).find("span.sorting-bottom").hide()
                             $(this).find("span.sorting-top").show();
                             sort_order=1;
                         }
                        //设定参数
                        args.params= $.extend(args.params,{
                           sortField:sort_field,
                           sortOrder:sort_order,
                           current:1,
                        });
                        //重新绘制表格数据集
                       ct.drawTableData(obj,args);
                       if(typeof(args.backFn)=="function"){
                           args.backFn($("[data-toggle='tooltip']").tooltip());
                         //点击显示放大热力图	
                   		$("#mytable tr td:last-child").click(function(){
                   			var height = document.body.scrollTop;
                   			$(".pop").height($(document.body).height());
                   			$(".pop").css("display","block");
                   			var img=$(this).html();
                   			$(".pop").append(img);
                   			$(".pop").css("padding-top",height+"px");
                   			$(document.body).css({
                   				   "overflow-x":"hidden",
                   				   "overflow-y":"hidden"
                   				 });
                   		});
                   		$("#mytable tr td:last-child").prev().click(function(){
                   			var height = document.body.scrollTop;
                   			$(".pop").height($(document.body).height());
                   			$(".pop").css("display","block");
                   			var img1=$(this).html();
                   			$(".pop").append(img1);
                   			$(".pop").css("padding-top",height+"px");
                   			$(document.body).css({
                   				   "overflow-x":"hidden",
                   				   "overflow-y":"hidden"
                   				 });
                   		});
                   		$(".pop").click(function(){
                   			$(".pop").css("display","none");
                   			$(".pop").html("");
                   			$(document.body).css({
                   				   "overflow-x":"auto",
                   				   "overflow-y":"auto"
                   				 });
                   		});
                       }
                });
            })();
        }
    }
    $.fn.createPage = function(options){
        args = $.extend(args,options);
        ms.init(this,args);
    }
    $.fn.createTable=function(options){
    	args = $.extend(args,options);
        ct.init(this,args);
    }
    $.fn.getSelected=function(){
        return this.find("tbody :checkbox:checked").map(function(){
             return $(this).val();
         }).get().join(", ");
     }
})(jQuery);