<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
	<%-- <jsp:include page="/main.jsp"></jsp:include> --%>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title></title>
	<!-- 样式 -->
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/component.css"></link>
	<!-- ajax工具js -->
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/component.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/highcharts/highcharts.js" ></script>
	
	
    <style type="text/css">
        .panel-new{
            font-size:1em;
            color: #000000;
        }
        .table{
            font-size: 0.8em;
        }
    </style>
    <script type="text/javascript">
        $(function () {
            $('#container1').highcharts({
                chart: {
                    type: 'column'
                },
                title: {
                    text: 'Monthly Average Rainfall'
                },
                subtitle: {
                    text: 'Source: WorldClimate.com'
                },
                xAxis: {
                    categories: [
                        'Jan',
                        'Feb',
                        'Mar',
                        'Apr',
                        'May',
                        'Jun',
                        'Jul',
                        'Aug',
                        'Sep',
                        'Oct',
                        'Nov',
                        'Dec'
                    ]
                },
                yAxis: {
                    min: 0,
                    title: {
                        text: 'Rainfall (mm)'
                    }
                },
                tooltip: {
                    headerFormat: '<span style="font-size:10px">{point.key}</span>',
                    pointFormat: '' +
                            '',
                    footerFormat: '<table><tbody><tr><td style="color:{series.color};padding:0">{series.name}: </td><td style="padding:0"><b>{point.y:.1f} mm</b></td></tr></tbody></table>',
                    shared: true,
                    useHTML: true
                },
                plotOptions: {
                    column: {
                        pointPadding: 0.2,
                        borderWidth: 0
                    }
                },
                series: [{
                    name: 'Tokyo',
                    data: [49.9, 71.5, 106.4, 129.2, 144.0, 176.0, 135.6, 148.5, 216.4, 194.1, 95.6, 54.4]

                }, {
                    name: 'New York',
                    data: [83.6, 78.8, 98.5, 93.4, 106.0, 84.5, 105.0, 104.3, 91.2, 83.5, 106.6, 92.3]

                }, {
                    name: 'London',
                    data: [48.9, 38.8, 39.3, 41.4, 47.0, 48.3, 59.0, 59.6, 52.4, 65.2, 59.3, 51.2]

                }, {
                    name: 'Berlin',
                    data: [42.4, 33.2, 34.5, 39.7, 52.6, 75.5, 57.4, 60.4, 47.6, 39.1, 46.8, 51.1]

                }]
            });
            
            $('#container4').highcharts({
                chart: {
                },
                title: {
                    text: 'Combination chart'
                },
                xAxis: {
                    categories: ['Apples', 'Oranges', 'Pears', 'Bananas', 'Plums']
                },
                tooltip: {
                    formatter: function() {
                        var s;
                        if (this.point.name) { // the pie chart
                            s = ''+
                                    this.point.name +': '+ this.y +' fruits';
                        } else {
                            s = ''+
                                    this.x  +': '+ this.y;
                        }
                        return s;
                    }
                },
                labels: {
                    items: [{
                        html: 'Total fruit consumption',
                        style: {
                            left: '40px',
                            top: '8px',
                            color: 'black'
                        }
                    }]
                },
                series: [{
                    type: 'column',
                    name: 'Jane',
                    data: [3, 2, 1, 3, 4]
                }, {
                    type: 'column',
                    name: 'John',
                    data: [2, 3, 5, 7, 6]
                }, {
                    type: 'column',
                    name: 'Joe',
                    data: [4, 3, 3, 9, 0]
                }, {
                    type: 'spline',
                    name: 'Average',
                    data: [3, 2.67, 3, 6.33, 3.33],
                    marker: {
                        lineWidth: 2,
                        lineColor: Highcharts.getOptions().colors[3],
                        fillColor: 'white'
                    }
                }, {
                    type: 'pie',
                    name: 'Total consumption',
                    data: [{
                        name: 'Jane',
                        y: 13,
                        color: Highcharts.getOptions().colors[0] // Jane's color
                    }, {
                        name: 'John',
                        y: 23,
                        color: Highcharts.getOptions().colors[1] // John's color
                    }, {
                        name: 'Joe',
                        y: 19,
                        color: Highcharts.getOptions().colors[2] // Joe's color
                    }],
                    center: [100, 80],
                    size: 100,
                    showInLegend: false,
                    dataLabels: {
                        enabled: false
                    }
                }]
            });
        });
    </script>
</head>
<body>
<div class="container-fluid">
    <!-- 头部导航自动加载 -->
    <header id="header"></header>
    <!-- 内容区域 -->
    <section>
        <div class="row">
            <!-- 左侧菜单自动加载 -->
            <div id="leftpanelside"></div>
            <div id="mainpanelside">
                <!-- 面包屑导航 -->
                <div class="trackerbar">Home >> user info</div>
                <div id="maincontent" class="col-lg-10 col-md-10 col-sm-9 col-xs-12">
                    <!-- 以下这是页面需要编辑的区域 -->
                            <div class="row" style="margin-top:10px;">
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <div class="panel panel-warning">
                                        <div class="panel-heading">面板标题1</div>
                                        <div class="panel-body">
								                                            这是面板内容<br/>
								                                            这是面板内容<br/>
								                                            这是面板内容<br/>
								                                            这是面板内容<br/>
								                                            这是面板内容<br/>
								                                            这是面板内容<br/>
								                                            这是面板内容<br/>
								                                            这是面板内容<br/>
								                                            这是面板内容<br/>
								                                            这是面板内容<br/>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-6 col-sm-6 col-xs-12">
	                                <div class="panel panel-warning panel-noborder">
	                                    <div class="panel-heading panel-heading-liner"  aria-controls-id="collapse3">面板标题1</div>
	                                    <div id="collapse3" class="panel-collapse collapse in">
	                                        <div class="panel-body">
	                                           <div id="container4"></div>
	                                        </div>
	                                    </div>
	                                </div>
                                </div>
                            </div>


                    <div class="row" style="margin-top:20px;">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <div id="container1"></div>
                        </div>
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <div class="panel panel-success panel-noborder">
                                <div class="panel-heading panel-heading-liner" aria-controls-id="collapse2">
                                    	这是标题
                                </div>
                                <div id="collapse2" class="panel-collapse collapse in">
                                    <div class="panel-body">
                                        <table class="table table-striped">
                                            <thead>
                                            <tr>
                                                <th>序号</th>
                                                <th>列名1</th>
                                                <th>列名1</th>
                                                <th>列名1</th>
                                                <th>列名1</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <tr>
                                                <td>列值内容1</td>
                                                <td>列值内容1</td>
                                                <td>列值内容1</td>
                                                <td>列值内容1</td>
                                                <td>列值内容1</td>
                                            </tr>
                                            <tr>
                                                <td>列值内容1</td>
                                                <td>列值内容1</td>
                                                <td>列值内容1</td>
                                                <td>列值内容1</td>
                                                <td>列值内容1</td>
                                            </tr>
                                            <tr>
                                                <td>列值内容1</td>
                                                <td>列值内容1</td>
                                                <td>列值内容1</td>
                                                <td>列值内容1</td>
                                                <td>列值内容1</td>
                                            </tr> <tr>
                                                <td>列值内容1</td>
                                                <td>列值内容1</td>
                                                <td>列值内容1</td>
                                                <td>列值内容1</td>
                                                <td>列值内容1</td>
                                            </tr>
                                            <tr>
                                                <td>列值内容1</td>
                                                <td>列值内容1</td>
                                                <td>列值内容1</td>
                                                <td>列值内容1</td>
                                                <td>列值内容1</td>
                                            </tr>

                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!-- 以上这是页面需要编辑的区域 -->

                    <!-- 以下为右侧悬浮功能栏,如果不需要，则删除以下内容 -->
                    <div id="rightpanel">
                        <div id="rightpanelicon">
                            <span class="glyphicon glyphicon-th"></span>
                        </div>
                        <ul id="rightpanelmenu" class="list-group">
                            <li class="list-group-item">
                                <span class="glyphicon glyphicon-th-list"></span>
                            </li>
                            <li class="list-group-item">
                                <span class="glyphicon glyphicon-plus"></span>
                            </li>
                            <li class="list-group-item">
                                <span class="glyphicon glyphicon-plus"></span>
                            </li>
                            <li class="list-group-item">
                                <span class="glyphicon glyphicon-plus"></span>
                            </li>
                        </ul>
                    </div>
                    <!-- 以上为右侧悬浮功能栏,如果不需要，则删除以上内容 -->

                </div>
            </div>
        </div>
    </section>


</div>
</body>
</html>