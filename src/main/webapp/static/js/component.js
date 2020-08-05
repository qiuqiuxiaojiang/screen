/**
 * Created by chunyangji on 2015/6/1.
 */
$(function(){
	/**
	 * 自定义panel组件
	 */
		$(".panel-heading-liner").each(function(i){
		        var _panel_header_title=$(this).text().trim();
		        var _panel_aria_controls=$(this).attr("aria-controls-id");
		        $(this).empty();
		        var _panel_header_layout='<div class="row">' +
		                                        '<div class="col-md-4 col-sm-4 col-xs-4">' +
		                                            _panel_header_title +
		                                        '</div>' +
		                                        '<div class="col-md-4 col-sm-4 col-xs-4" style="text-align: right">'+
		                                            '<a class="a-expand" data-toggle="collapse" href="#'+_panel_aria_controls+'" aria-expanded="true" aria-controls='+_panel_aria_controls+'>'+
		                                                '<span class="glyphicon glyphicon-chevron-up"></span>'+
		                                                '<span class="glyphicon glyphicon-chevron-down"></span>'+
		                                            '</a>'+
		                                        '</div>'+
		                                    '</div>';
		        $(this).append(_panel_header_layout);
		        $(this).find('.a-expand')
		            .click(function(){
		                if($(this).find('span:first').is(':hidden')){
		                    $(this).find('span:first').show();
		                    $(this).find('span:last').hide();
		                }else{
		                    $(this).find('span:first').hide();
		                    $(this).find('span:last').show();
		                }
		            })
		            .css({'color':$(this).css('borderColor')})
		            .find('span:last').hide();
		
		    });

})