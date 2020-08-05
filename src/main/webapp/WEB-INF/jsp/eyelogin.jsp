<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%   
String path = request.getContextPath();  
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;   
%>
<!DOCTYPE html>
<%@ include file="/_header.jsp"%>
<html lang="zh-CN">
  <head lang="en">
    <meta charset="UTF-8">
    <title></title>
    <link rel="stylesheet" type="text/css" href="../css/bootstrap.min.css" />
    <style type="text/css">
        .body{
            font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
        }
        .container-fluid{
            position: relative;
            background:url(${ctx_static}/img/2-1bg.jpg) no-repeat;
            background-size: 100% 100%;
        }
        .inputDiv{
            width:500px;
            height:500px;
            position:absolute;
            top:20%;
            right:10%;
            background: url(${ctx_static}/img/2-1bg.png) no-repeat;
            background-size: 100% 100%;
        }
        form{
            width:230px;
            height:auto;
            position: relative;
            top:100px;
            left:130px;
        }
        form h3{
            display: block;
            color: #FFFFFF;
            font-weight: bold;
            text-align:center;
            margin-bottom: 10px;
        }
        .form-group{
            margin:3px;
        }
        .input-group input{
            border:1px solid #AABD9C;
            color: #9FAF93;
            width:100%;
            height:35px;
            outline-color:#AABD9C;
            background: transparent;
        }
        .input-group input:focus{
            border:1px solid #AABD9C;
            outline-color:#AABD9C;
        }
        .input-group .input-group-addon{
            background: transparent;
            color: #fcfcfc;
        }
        .inputDiv .glyphicon{
            color:#BEBEBE;
        }
        .inputDiv a{
            color: #c4ab33;
            padding-right:5px;
            font-weight: bold;
            font-size: 9px;
            margin-top:3px;
        }
        .form-group>.input-group{
            -webkit-border-radius: 5px;
            -moz-border-radius: 5px;
            -o-border-radius:5px;
            border-radius: 5px;
        }
        .checkbox{
            margin:3px;
        }
        .checkbox label{
            color:#9FAF93;
            font-weight: bold;
            font-size: 11px;
        }
        .login-btn{
            background: #58aa61;
            width:100%;
            color:#ffffff;
            font-weight: bold;
            height:40px;
            outline-color: #59aa62;
        }
    </style>
</head>
<body>
<div class="container-fluid">
    <div class="inputDiv">
        <form action="<c:url value='${basePath }/j_spring_security_check' />" method="post">
                <input type="hidden" value="pc" name="loginType">
            <h3>用户登录</h3>
            <div class="form-group clearfix">
                    <div class="input-group">
                        <span class="input-group-addon" id="sizing-addon1"><span class="glyphicon glyphicon-user"></span></span>
                        <input type="text" name="username" id="username" class="form-control" placeholder="请输入用户名" required aria-describedby="sizing-addon1">
                    </div>
<!--                 <a class="pull-right" href="#">忘记登录名?</a> -->
            </div>
            <div class="form-group clearfix">
                    <div class="input-group">
                        <span class="input-group-addon" id="sizing-addon2"><span class="glyphicon glyphicon-lock"></span></span>
                        <input type="password" name="password" id="password" class="form-control" placeholder="请输入密码" required aria-describedby="sizing-addon2">
                    </div>
<!--                <a class="pull-right" href="#">忘记登录密码?</a>-->
            </div>
<!--             <div class="checkbox" style="padding-left:5px">
                <label style="height: 26px;line-height: 26px;">
                    <input type="checkbox" style="width:18px;height:18px;"> 下次自动登录
                </label>
            </div>
             -->
            <div class="form-group clearfix">
                <button class="btn login-btn">登 录</button>
<!--                <a class="pull-right" href="#">免费注册</a>-->
            </div>
        </form>
    </div>
</div>

</body>
<script src="../js/jquery-1.11.2.js"></script>
<script src="../js/bootstrap.min.js"></script>
<script type="text/javascript">
    $(function(){
      $(".container-fluid").height(window.screen.height *.85).width(window.screen.width *.85);
    })
</script>
</html>
