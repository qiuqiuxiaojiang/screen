<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head lang="en">
<%@include file="/_header.jsp" %>
<meta charset="UTF-8">
<title>社区健康筛查数据采集与管理系统</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/framework.css" />
    <script type="text/javascript">
    $(function(){
 	   var $menu_session$ ='${sessionScope.menuList }';
       var a_active_seq=sessionStorage.getItem('menu_a_active_seq') || 0;
       if($menu_session$!="undefined" && $menu_session$!=""&& $menu_session$!=null&& $menu_session$!='null'){
           $.each(jQuery.parseJSON($menu_session$),function(index,menu){
               var _li_a_=$("<a></a>").attr("seq",menu["id"])
                   .attr("data-toggle","tooltip")
                   .attr("data-placement","right")
                   .attr("title",menu["name"]);
               var icon = menu['icon'];
               _li_a_.append("<img src='${ctx_static}/healthcheck/image/menu/"+icon+"' />")
                   .append("<span>"+menu["name"]+"</span>");
               var nodes=menu["nodes"];
               if(nodes!=null && nodes!="" && nodes!="undefined" && nodes.length>0){
                   //二级菜单
                   //是否展开
                   var is_checked=false;
                   var _li_nodes_=$("<ul></ul>").addClass('sidebar-menu-child');
                   $.each(nodes,function(index,node){
                       var _node_li_a_=$("<a></a>").attr("seq",menu["id"]+"-"+node["id"])
                           .attr("data-toggle","tooltip")
                           .attr("data-placement","right")
                           .attr("title",node["name"]);
                       if(menu["id"]+"-"+node["id"]==a_active_seq){ //设置菜单选中效果
                    	   is_checked=true;
                           _node_li_a_.addClass("active");
                           _li_nodes_.css("display","block");
                           $(".breadcrumb").append($("<li></li>").text(menu["name"]));
                           $(".breadcrumb").append($("<li></li>").text(node["name"]));
                       }
                       _node_li_a_.append("<span class='"+node["icon"]+"'></span>")
                       .append("<span>"+node["name"]+"</span>");
                       if (node["url"] != null && node["url"] != "") {
                    	   _node_li_a_.attr("href","${ctx}"+node["url"]);
                       }
                   	   _li_nodes_.append($("<li></li>").append(_node_li_a_));
                       
                   	 // ----------- 三级菜单 开始 -----------
 					   var node_children = node["nodes"];
 					   var _node_child_li_nodes_ = $("<ul></ul>");
					   if (node_children != null && node_children != "" && node_children != "undefined" && node_children.length > 0) {
						    // 三级菜单
						    // 是否展开
						    var is_checked_child = false;
							_node_child_li_nodes_.addClass("sidebar-menu-child").css("display","none");
							$.each(node_children, function(index, node_child) {
								var _node_child_li_a_ = $("<a class='sidebar-menu-3rd-child-a' style='padding-left: 30px;'></a>").attr("seq", menu["id"] + "-" + node["id"] + "-" + node_child["id"])
									.attr("data-toggle","tooltip")
									.attr("data-placement","right")
									.attr("title",node_child["name"]);

								// 设置菜单选中效果
								var menu_id = menu["id"] + "-" + node["id"] + "-" + node_child["id"];
								if (menu_id == a_active_seq){
									is_checked_child = true;
									_li_nodes_.css("display", "block");
									_node_child_li_a_.addClass("active");
									_node_child_li_nodes_.css("display","block");
									$(".breadcrumb").append($("<li></li>").text(menu["name"]));
									$(".breadcrumb").append($("<li></li>").text(node["name"]));
									$(".breadcrumb").append($("<li></li>").text(node_child["name"]));
								}
								
								_node_child_li_a_.append("<span class='" + node_child["icon"]+"'></span>")
									.append("<span>" + node_child["name"] + "</span>")
									.attr("href","${ctx}" + node_child["url"]);
								
								_node_child_li_nodes_.append($("<li></li>").append(_node_child_li_a_));
							});
							
							if(is_checked_child){
								is_checked = true;
								_node_li_a_.attr("href","#")
			                     .append("<span class='glyphicon glyphicon-menu-left glyphicon-menu-down pull-right'></span>");
			                }else{
			                	_node_li_a_.attr("href","#")
			                     .append("<span class='glyphicon glyphicon-menu-left pull-right'></span>");
			                }
							
							_node_li_a_.after(_node_child_li_nodes_);
							
						}
						// ----------- 三级菜单 结束 -----------
                   });
                   if(is_checked){
                       _li_a_.attr("href","#")
                           .append("<span class='glyphicon glyphicon-menu-left glyphicon-menu-down pull-right'></span>");
                   }else{
                       _li_a_.attr("href","#")
                           .append("<span class='glyphicon glyphicon-menu-left pull-right'></span>");
                   }
                   $('.sidebar-menu').append(_li_a_);
                   _li_a_.wrap($('<li></li>')).after(_li_nodes_);
               }else{
                   _li_a_.attr("href","${ctx}"+menu["url"]);
                   if(menu["id"]==a_active_seq){ //设置菜单选中效果
                       _li_a_.addClass("active");
                       $(".breadcrumb").append($("<li></li>").text(menu["name"]));
                   }
                   $('.sidebar-menu').append(_li_a_);
                   _li_a_.wrap('<li></li>');
               }
           });
       }
 	});
      
    </script>
</head>

<body style="position:relative;">
<div class="shade"></div>								
<header class="header clearfix">
    <a class="logo"><img src="${ctx_static}/img/logo.png" style="vertical-align: inherit;margin-left: 15px;margin-top: 5px;"/></a>
    <nav class="navbar navbar-static-top">
        <a id="menu-toggle" href="#" class="navbar-btn sidebar-toggle" role="button">
            <span class="glyphicon glyphicon-menu-hamburger"></span>
        </a>
        <div class="navbar-right">
             <ul class="navbar-nav">
<!--                 <li>
                    <a class="btn" role="button">
                        <span class="glyphicon glyphicon-pencil"></span>
                    </a>
                </li>
                <li>
                    <a class="btn" role="button">
                        <span class="glyphicon glyphicon-list"></span>
                        <span class="badge">5</span>
                    </a>
                </li>
 -->              
            </ul>
 
            <div class="btn-group" role="group">
                <a class="btn dropdown-toggle" role="button" data-toggle="dropdown" aria-expanded="false">
                    <span class="glyphicon glyphicon-user"></span>&nbsp;&nbsp;<span>${sessionScope.user.username}</span> <span class="caret"></span>
                </a>
                <ul class="dropdown-menu" role="menu">
                    <li><a href="${ctx}/user/updatePass.htm">修改密码</a></li>
                    <li><a href="${ctx}/logout/quit.htm">注销登录</a></li>
                </ul>
            </div>
        </div>
    </nav>
</header>
<div id="side-content">
    <aside class="left-side">
        <section class="sidebar">
            <div class="user-panel">
                <div class="pull-left info">
                    <p>Hello,${sessionScope.user.username }</p>
                    <a href="#">欢迎您</a>
                </div>
            </div>
            <ul class="sidebar-menu">
            </ul>
        </section>
    </aside>
    <aside class="right-side">
        <section class="content-header">
            <ol class="breadcrumb">
                <li><a href="#"> 主页</a></li>
            </ol>
        </section>
        <section class="content"></section>
    </aside>
</div>
</body>
<script src="${pageContext.request.contextPath}/static/js/framework.js"></script>
<script type="text/javascript">
jQuery(document).ready(function() {
	loadRemindCount();
 });
//加载需要提醒的人数
function loadRemindCount() {
	$.ajax({
		type : "POST",
		url : "${ctx}/visit/loadRemindCount.json",
		success : function(data) {
			if (data.code == 0) {
				$("#newCount").html(data.dataMap.newCount);
				$("#expireCount").html(data.dataMap.expireCount);
				$("#allCount").html(data.dataMap.allCount);
			} else {
				console.log("加载失败");
			}
		},
        error: function (xhr, ajaxOptions, thrownError) {
			console.log("加载失败");
        }
	});
}
</script>
</html>