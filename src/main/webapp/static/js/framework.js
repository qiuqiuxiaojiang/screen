$( function(){
//    var a_active_seq=sessionStorage.getItem('menu_a_active_seq') || 0;
//    $.getJSON("menu.json",function(data){
//        $.each(data,function(index,menu){
//            var _li_a_=$("<a></a>").attr("seq",menu["id"])
//                .attr("data-toggle","tooltip")
//                .attr("data-placement","right")
//                .attr("title",menu["name"]);
//            _li_a_.append("<span class='"+menu["icon"]+"'></span>")
//                .append("<span>"+menu["name"]+"</span>");
//            var nodes=menu["nodes"];
//            if(nodes!=null && nodes!="" && nodes!="undefined" && nodes.length>0){
//                //二级菜单
//                //是否展开
//                var _li_nodes_=$("<ul></ul>").addClass('sidebar-menu-child');
//                $.each(nodes,function(index,node){
//                    var _node_li_a_=$("<a></a>").attr("seq",menu["id"]+"-"+node["id"])
//                        .attr("data-toggle","tooltip")
//                        .attr("data-placement","right")
//                        .attr("title",node["name"]);
//                    if(menu["id"]+"-"+node["id"]==a_active_seq){ //设置菜单选中效果
//                        _node_li_a_.addClass("active");
//                        _li_nodes_.css("display","block");
//                        $(".breadcrumb").append($("<li></li>").text(menu["name"]))
//                        $(".breadcrumb").append($("<li></li>").text(node["name"]).addClass("active"))
//                    }
//                    _node_li_a_.append("<span class='"+node["icon"]+"'></span>")
//                        .append("<span>"+node["name"]+"</span>")
//                        .attr("href",node["url"]);
//                    _li_nodes_.append($("<li></li>").append(_node_li_a_));
//                });
//                _li_a_.attr("href","#")
//                    .append("<span class='glyphicon glyphicon-chevron-left pull-right'></span>");
//                $('.sidebar-menu').append(_li_a_);
//                _li_a_.wrap($('<li></li>')).after(_li_nodes_);
//            }else{
//                _li_a_.attr("href",menu["url"]);
//                if(menu["id"]==a_active_seq){ //设置菜单选中效果
//                    _li_a_.addClass("active");
//                    $(".breadcrumb").append($("<li></li>").text(menu["name"]).addClass("active"))
//                }
//                $('.sidebar-menu').append(_li_a_);
//                _li_a_.wrap('<li></li>');
//            }
//        });
//    });

    /**
     * 子集菜单显示隐藏处理操作
     */
	 $('.sidebar-menu').on('click','li',function( event ){
        $(this).find('a:first>span:last-child').toggleClass('glyphicon-menu-down');
        $(this).children('ul').slideToggle();
        event.stopPropagation();
	 });

    /**
     * session存储当前点击菜单
     */
    $(".sidebar-menu").on("click","a",function(event){
        if($(this).attr("href")!="undefined" && $(this).attr("href")!="#"){
            sessionStorage.setItem('menu_a_active_seq',$(this).attr("seq"));
        }
    });
    /**
     * 左侧导航菜单样式操作
     */
    var toggleFlag=0;
    $('#menu-toggle').click(function(){
        if(toggleFlag%2==0){
            //隐藏文字菜单
            $('.sidebar>.user-panel .info')
                .add($('.sidebar>.sidebar-menu li')
                    .find('span:nth-child(2)'))
                .hide();
            //更改用户图标大小
            $('.sidebar>.user-panel')
                .find("img")
                .css({"width":"35px","height":"35px","border-radius":"17.5px"}).end()
                .css({"padding":"5px"});
            //动画处理
            $('#side-content').find('.left-side')
                .animate({
                    width: "45px"
                }, "fast").end()
                .find('.right-side')
                .animate({
                    marginLeft:"45px"
                },"fast");
            //展示所有子集菜单
            $('.sidebar-menu-child').css({"border-top":"0px","background":"#f4f4f4"}).slideDown("fast");
            $('.sidebar-menu-child>li').css({"border-top":"1px solid #fff","border-bottom":"1px solid #dbdbdb","background":"#f4f4f4"});
            $('.sidebar-menu-child>li:first-child').css({"border-top":"1px solid #ffffff","border-bottom":"1px solid #dbdbdb"});
            $('.sidebar-menu-child>li:last-child').css({"border-top":"1px solid #ffffff","border-bottom":"0px"});
            //隐藏有子集菜单的上级菜单文字信息
            $('.sidebar-menu>li:has(ul)').css({"border-top":"0px"}).find("a:first").hide();
            $('[data-toggle="tooltip"]').tooltip(); //引用工具提示栏
        }else{
            //动画处理还原页面展示效果
            $('#side-content').find('.left-side')
                .animate({
                    width: "200px"
                }, "fast",function(){
                    $('.sidebar>.user-panel .info')
                        .add($('.sidebar>.sidebar-menu li')
                            .find('span:nth-child(2)'))
                        .fadeIn("slow");
                    $('.sidebar>.user-panel')
                        .find("img")
                        .css({"width":"45px","height":"45px","border-radius":"22.5px"}).end()
                        .css({"padding":"10px"});
                }).end()
                .find(".right-side")
                .animate({
                    marginLeft:"200px"
                },"fast");
            //展示所有子集菜单
            $('.sidebar-menu-child').css({"border-top":"1px solid #dbdbdb","background":"#ececec"}).slideDown("fast");
            $('.sidebar-menu-child').prev().find("span:last-child").removeClass('glyphicon-menu-down').addClass('glyphicon-menu-down');
            $('.sidebar-menu-child>li').css({"border":"0px","background":"#e6e6e6"});
            //显示有子集菜单的上级菜单文字信息
            $('.sidebar-menu>li:has(ul)').css({"border-top":"1px solid #ffffff"}).find("a:first").show();
            $('[data-toggle="tooltip"]').tooltip('destroy');
        }
        toggleFlag++;
    });
    
    /** 引入内容 **/
    $(".right-side>.content").html($(".container-fluid"));
});