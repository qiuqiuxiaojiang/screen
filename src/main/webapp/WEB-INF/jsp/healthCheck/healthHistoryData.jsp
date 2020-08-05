<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ include file="../main.jsp"%>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>建档情况</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/component.css"></link>
    <link rel="stylesheet" type="text/css" href="${ctx_static }/healthcheck/css/common.css"></link><%-- 
    <link rel="stylesheet" type="text/css" href="${ctx_static }/healthcheck/css/bootstrap.min.css"></link> --%>
    <link rel="stylesheet" type="text/css" href="${ctx_static }/healthcheck/css/healthcheck.css"></link>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/component.js"></script>
	<style type="text/css">
		.tab ul li {
		    float: left;
		    height: 60px;
		    line-height: 60px;
		    padding: 0 22px 12px;
		    border-left: 1px #DCDCDC solid;
		    cursor: pointer;
		}
		
		.main{
        width: 100%;
        min-height: 100%;
        position: absolute;
        background: #F2F3F7;
        background-size: 100% 100%;
    }
    .tab{
        width: 86%;
        margin: 0% 1% 1% 214px;
        height: 75px;
        background: #fafafa;
        border: 1px #DCDCDC solid;
    }
    .tab ul{
        width: 100%;
        height: 100%;
        padding: 5px 0 0;
    }
    .tab ul li a{
        color: #6E6E6E;
    }
    .tab ul li a:hover{
        text-decoration: none;
    }
    .active a{
        color: #42A0D3!important;
    }
    .active{
        background: #ffffff!important;
        color: #42A0D3!important;
        border-bottom: 3px #42A0D3 solid;
        padding-bottom: 65px!important;
    }
    .chartDiv,.total{
        width: 86%;
        margin: 1% 1% 1% 214px;
        background: #ffffff;
        padding: 50px 10%;
        border: 1px #E5E5E5 solid;
        border-radius: 8px;
    }
    .total{
        padding: 30px 5%!important;
        color: #646464!important;
    }
    .chartDiv h1{
        font-size: 18px;
        text-align: center;
        margin-bottom: 20px;
    }
    .chartDiv table tr td{
        text-align: center;
        border-top: none;
        height: 30px;
        line-height: 30px;
        border-bottom: 1px #DCDCDC solid;
    }
    .chartDiv table tr:first-child td{
        background: #F8F8F8;
        border-bottom: none;
    }
    #jd_age{
        width: 70%;
        margin: 10px auto;
        height: 350px;
    }
    .g2-label-item {
        font-size: 12px;
        text-align: center;
        line-height: 0.4;
    }

    .g2-label-item-value {
        color: #595959
    }

    .g2-label-item-percent {
        color: #8c8c8c
    }
    .txt{
        width: 50px;
        background: #ffffff;
        float: left;
        font-size: 12px;
        position: relative;
        z-index: 9999;
    }
    .txt li{
      	height: 45px;
	    line-height: 27px;
	    padding-left: 4px;
    }
    .lineD{
         float: left;
         width: 95%;
         padding: 13px 5px 32px;
     }
    .lineZ{
        float: left;
        width:24%;
        background: #ffffff;
        position: relative;
        z-index: 9999;
    }
    .lineD p,.lineZ p{
        height: 1px;
        margin-bottom: 44.2px;
        border-bottom: 1px #9E9E9E dashed;
    }
    .lineZ p{
        border:none;
    }
    .sidebar-menu li a{
    	    padding-bottom: 12px!important;
    border: none;
    }
	</style>
<body>
<div class="main">
	<div class="search-panel" style="text-align: right;padding:10px 15px;">
        <input type="button" class="btn btn-primary" value="返回" onclick="goBack();" />
    </div> 
	<div class="tab">
        <ul>
            <a type="height"><li id="heightli">身高</li></a>
            <a type="weight"><li id="weightli">体重</li></a>
            <a type="BMI"><li id="BMIli">体质指数</li></a>
            <a type="waistline"><li id="waistlineli">腰围</li></a>
            <a type="bloodPressure"><li id="bloodPressureli">血压</li></a>
            <a type="pulse"><li id="pulseli">脉搏</li></a>
            <a type="temperature"><li id="temperatureli">体温</li></a>
            <a type="oxygen"><li id="oxygenli">血氧</li></a>
            <a type="hipline"><li id="hiplineli">臀围</li></a>
            <a type="WHR"><li id="WHRli">腰臀比</li></a>
            <a type="fatContent"><li id="fatContentli">体脂率</li></a>
            <a type="bloodSugger"><li id="bloodSuggerli">血糖</li></a>
            <a type="bloodLipid"><li id="bloodLipidli">血脂</li></a>
            <a type="tizhi"><li id="tizhili">中医体质分类</li></a>
            <a type="riskScore"><li id="riskScoreli">糖尿病危险因素评估分数</li></a>
        </ul>
    </div>
    <input type="hidden" id="type" value="${type}">
    <input type="hidden" id="uniqueId" value="${uniqueId}">
    <input type="hidden" id="dataId" value="${dataId}">
    <input type="hidden" id="checkDate" value="${checkDate}">
    <div class="chartDiv">
        <h1></h1>
        <div class="tizhi1" id="tizhi1" style="display: none;">
            <!-- <ul class="txt" >
                <li></li>
                <li>平和质</li>
                <li>气虚质</li>
                <li>阳虚质</li>
                <li>阴虚质</li>
                <li>痰湿质</li>
                <li>血瘀质</li>
                <li>湿热质</li>
                <li>气郁质</li>
                <li>特禀质</li>
            </ul>
            <div class="lineD">
            	<P style="border-color:#fff;"></P>
                <p></p>
                <p></p>
                <p></p>
                <p></p>
                <p></p>
                <p></p>
                <p></p>
                <p></p>
                <p></p>
            </div> -->
           <!--  <div class="lineZ">
            	<P></P>
                <p></p>
                <p></p>
                <p></p>
                <p></p>
                <p></p>
                <p></p>
                <p></p>
                <p></p>
                <p></p>
                <p></p>
            </div> -->
        </div>
        <div id="${type}"></div>
    </div>
    
</div>
<script src="${ctx_static }/healthcheck/js/g2.min.js"></script>
<script src="${ctx_static }/healthcheck/js/data-set.min.js"></script>
<script src="${ctx_static }/healthcheck/js/jquery-1.11.2.js"></script>
<script>
	function goBack() {
		var dataId = $("#dataId").val();
		var uniqueId = $("#uniqueId").val();
		var checkDate = $("#checkDate").val();
		window.location.href = "${ctx}/manage/hc/editHealthCheck.htm?id="+dataId+"&uniqueId="+uniqueId+"&checkDate="+checkDate;
	}

	$(function () {
		var type = $("#type").val();
		var uniqueId = $("#uniqueId").val();
		var dataId = $("#dataId").val();
		var checkDate = $("#checkDate").val();
		
		var children = $(".tab").find("a");
		children.each(function(index, element) {
			var type = element.type;
			$(element).attr("href", "${ctx}/manage/hc/getHealthHistoryData.htm?uniqueId="+uniqueId+"&type="+type+"&dataId="+dataId+"&checkDate="+checkDate);
		});
		
		
		if (type == "tizhi") {
			$("#tizhi1").show();
		}
		
		var children = $("ul").find("li");
		children.each(function(index, element) {
			if (element.id==type+'li') {
				$("#"+element.id).attr('class', 'activeLi');
			} else {
				$("#"+element.id).removeAttr('class');
			}
		});
		$.ajax({
			url : "${ctx}/manage/hc/getHealthHistoryDataByType.json",
			dataType : "json",
			data : {
				"type" : type,
				"uniqueId" : uniqueId,
				"dataId" : dataId,
				"checkDate" : checkDate
			},
			success : function(o) {
				console.log(o);
				var data = o.dataMap.list;
				
				$("#type").val(o.dataMap.type);
				$("#uniqueId").val(o.dataMap.uniqueId);
				$("#dataId").val(o.dataMap.dataId);
				$("#checkDate").val(o.dataMap.checkDate);
				
				//身高
				if(type == 'height') {
					var chart = new G2.Chart({
						 container: 'height',
					     forceFit: true,
					     height: 500,
					     padding: '10%'
				    });

				    chart.source(data, {
				    	checkDate: {
				            alias: '日期',
				            range: [0, 1]
				        },
				        height: {
				        	min: 0,
			    	    	nice: true,
				            alias: '身高(cm)'
				        }
				    });
				    
				   /*  chart.scale({
				    	checkDate: {
			    	    	range: [0, 1],
			    	  	},
			    	  	height: {
			    	    	min: 0,
			    	    	nice: true,
			    	  	},
			    	}); */

			    	chart.tooltip({
			    	   showCrosshairs: true, // 展示 Tooltip 辅助线
			    	   shared: true,
			    	});
			    	
			    	chart.axis('height', {
					      label: {
					        formatter: function(val){
					          return val + ' cm';
					        },
					      },
					});
			    	
			    	chart.line().position('checkDate*height').label('height');
			    	chart.point().position('checkDate*height').size(4).shape('circle').style({stroke:'#fff', lineWidth: 1});

			    	chart.render();
				} else if (type == 'weight') {
					var chart = new G2.Chart({
						 container: 'weight',
					     forceFit: true,
					     height: 500,
					     padding: '10%'
				    });

				    chart.source(data, {
				    	checkDate: {
				            alias: '日期',
				            range: [0, 1]
				        },
				        weight: {
				        	min: 0,
			    	    	nice: true,
				            alias: '体重'
				        }
				    });
			    	chart.tooltip({
			    	   showCrosshairs: true, // 展示 Tooltip 辅助线
			    	   shared: true,
			    	});
			    	
			    	chart.axis('weight', {
					      label: {
					        formatter: function(val){
					          return val + ' kg';
					        },
					      },
					});
			    	
			    	chart.line().position('checkDate*weight').label('weight');
			    	chart.point().position('checkDate*weight').size(4).shape('circle').style({stroke:'#fff', lineWidth: 1});
			    	chart.render();
				} else if (type == 'BMI') {
					var chart = new G2.Chart({
						 container: 'BMI',
					     forceFit: true,
					     height: 500,
					     padding: '10%'
				    });

				    chart.source(data, {
				    	checkDate: {
				            alias: '日期',
				            range: [0, 1]
				        },
				        BMI: {
				        	min: 0,
			    	    	nice: true,
				            alias: '体质指数'
				        }
				    });
			    	chart.tooltip({
			    	   showCrosshairs: true, // 展示 Tooltip 辅助线
			    	   shared: true,
			    	});
			    	
			    	chart.line().position('checkDate*BMI').label('BMI');
			    	chart.point().position('checkDate*BMI').size(4).shape('circle').style({stroke:'#fff', lineWidth: 1});
			    	chart.render();
				} else if (type == 'hipline') {
					var chart = new G2.Chart({
						 container: 'hipline',
					     forceFit: true,
					     height: 500,
					     padding: '10%'
				    });

				    chart.source(data, {
				    	checkDate: {
				            alias: '日期',
				            range: [0, 1]
				        },
				        hipline: {
				        	min: 0,
			    	    	nice: true,
				            alias: '臀围'
				        }
				    });
			    	chart.tooltip({
			    	   showCrosshairs: true, // 展示 Tooltip 辅助线
			    	   shared: true,
			    	});
			    	
			    	chart.axis('hipline', {
					      label: {
					        formatter: function(val){
					          return val + ' cm';
					        },
					      },
					});
			    	
			    	chart.line().position('checkDate*hipline').label('hipline');
			    	chart.point().position('checkDate*hipline').size(4).shape('circle').style({stroke:'#fff', lineWidth: 1});
			    	chart.render();
				} else if (type == 'waistline') {
					var chart = new G2.Chart({
						 container: 'waistline',
					     forceFit: true,
					     height: 500,
					     padding: '10%'
				    });

				    chart.source(data, {
				    	checkDate: {
				            alias: '日期',
				            range: [0, 1]
				        },
				        waistline: {
				        	min: 0,
			    	    	nice: true,
				            alias: '腰围'
				        }
				    });
			    	chart.tooltip({
			    	   showCrosshairs: true, // 展示 Tooltip 辅助线
			    	   shared: true,
			    	});
			    	
			    	chart.axis('waistline', {
					      label: {
					        formatter: function(val){
					          return val + ' cm';
					        },
					      },
					});
			    	
			    	chart.line().position('checkDate*waistline').label('waistline');
			    	chart.point().position('checkDate*waistline').size(4).shape('circle').style({stroke:'#fff', lineWidth: 1});
			    	chart.render();
				} else if (type == 'bloodPressure') {
					var chart = new G2.Chart({
						  container: 'bloodPressure',
						  forceFit: true,
						  height: 500,
						  padding: 'auto'
					});

					chart.source(data, {
				    	checkDate: {
				            alias: '日期',
				            range: [0, 1]
				        }
				    });
					
					chart.tooltip({
						  showCrosshairs: true,
						  shared: true,
						  /* formatter:function(state, value) {
							  var obj = {"highPressure":"高血压","lowPressure":"低血压"};
							  return {
								  name:obj[state],
								  value:value
							  }
						  } */
					});
					
					/* chart.legend({
						itemFormatter:function(val) {
							var obj = {"highPressure":"高血压","lowPressure":"低血压"};
							return obj[val];
						}
					}); */

					/* chart.axis('temperature', {
						  label: {
						    formatter: (val) => {
						      return val + ' °C';
						    },
						  },
					}); */

					chart
					  .line()
					  .position('checkDate*value')
					  .color('state')
					  .shape('smooth').label("value");

					chart
					  .point()
					  .position('checkDate*value')
					  .color('state')
					  .shape('circle');

					chart.render();
				} else if (type == 'pulse') {
					var chart = new G2.Chart({
						 container: 'pulse',
					     forceFit: true,
					     height: 500,
					     padding: 'auto'
				    });

				    chart.source(data, {
				    	checkDate: {
				            alias: '日期',
				            range: [0, 1]
				        },
				        pulse: {
				        	min: 0,
			    	    	nice: true,
				            alias: '脉搏'
				        }
				    });
			    	chart.tooltip({
			    	   showCrosshairs: true, // 展示 Tooltip 辅助线
			    	   shared: true,
			    	});
			    	
			    	chart.axis('pulse', {
					      label: {
					        formatter: function(val){
					          return val + ' 次/min';
					        },
					      },
					});
			    	
			    	chart.line().position('checkDate*pulse').label('pulse');
			    	chart.point().position('checkDate*pulse').size(4).shape('circle').style({stroke:'#fff', lineWidth: 1});
			    	chart.render();
				}else if (type == 'temperature') {
						var chart = new G2.Chart({
							 container: 'temperature',
						     forceFit: true,
						     height: 500,
						     padding: 'auto'
					    });

					    chart.source(data, {
					    	checkDate: {
					            alias: '日期',
					            range: [0, 1]
					        },
					        temperature: {
					        	min: 0,
				    	    	nice: true,
					            alias: '体温'
					        }
					    });
				    	chart.tooltip({
				    	   showCrosshairs: true, // 展示 Tooltip 辅助线
				    	   shared: true,
				    	});
				    	
				    	chart.axis('temperature', {
						      label: {
						        formatter: function(val){
						          return val + ' °C';
						        },
						      },
						});
				    	
				    	chart.line().position('checkDate*temperature').label('temperature');
				    	chart.point().position('checkDate*temperature').size(4).shape('circle').style({stroke:'#fff', lineWidth: 1});
				    	chart.render();
					} else if (type == 'oxygen') {
						var chart = new G2.Chart({
							 container: 'oxygen',
						     forceFit: true,
						     height: 500,
						     padding: '10%'
					    });

					    chart.source(data, {
					    	checkDate: {
					            alias: '日期',
					            range: [0, 1]
					        },
					        oxygen: {
					        	min: 0,
				    	    	nice: true,
					            alias: '血氧'
					        }
					    });
				    	chart.tooltip({
				    	   showCrosshairs: true, // 展示 Tooltip 辅助线
				    	   shared: true,
				    	});
				    	
				    	chart.axis('oxygen', {
						      label: {
						        formatter: function(val){
						          return val + ' %';
						        },
						      },
						});
				    	
				    	chart.line().position('checkDate*oxygen').label('oxygen');
				    	chart.point().position('checkDate*oxygen').size(4).shape('circle').style({stroke:'#fff', lineWidth: 1});
				    	chart.render();
					} else if (type == 'WHR') {
						var chart = new G2.Chart({
							 container: 'WHR',
						     forceFit: true,
						     height: 500,
						     padding: '10%'
					    });

					    chart.source(data, {
					    	checkDate: {
					            alias: '日期',
					            range: [0, 1]
					        },
					        WHR: {
					        	min: 0,
				    	    	nice: true,
					            alias: '腰臀比'
					        }
					    });
				    	chart.tooltip({
				    	   showCrosshairs: true, // 展示 Tooltip 辅助线
				    	   shared: true,
				    	});
				    	
				    	chart.axis('WHR', {
						      label: {
						        formatter: function(val){
						          return val;
						        },
						      },
						});
				    	
				    	chart.line().position('checkDate*WHR').label('WHR');
				    	chart.point().position('checkDate*WHR').size(4).shape('circle').style({stroke:'#fff', lineWidth: 1});
				    	chart.render();
					} else if (type == 'fatContent') {
						var chart = new G2.Chart({
							 container: 'fatContent',
						     forceFit: true,
						     height: 500,
						     padding: '10%'
					    });

					    chart.source(data, {
					    	checkDate: {
					            alias: '日期',
					            range: [0, 1]
					        },
					        fatContent: {
					        	min: 0,
				    	    	nice: true,
					            alias: '体脂率'
					        }
					    });
				    	chart.tooltip({
				    	   showCrosshairs: true, // 展示 Tooltip 辅助线
				    	   shared: true,
				    	});
				    	
				    	chart.axis('fatContent', {
						      label: {
						        formatter: function(val){
						          return val + ' %';
						        },
						      },
						});
				    	
				    	chart.line().position('checkDate*fatContent').label('fatContent');
				    	chart.point().position('checkDate*fatContent').size(4).shape('circle').style({stroke:'#fff', lineWidth: 1});
				    	chart.render();
					} else if (type == 'bloodSugger') {
						var chart = new G2.Chart({
							  container: 'bloodSugger',
							  forceFit: true,
							  height: 500,
							  padding: '15%'
						});

						chart.source(data, {
					    	checkDate: {
					            alias: '日期',
					            range: [0, 1]
					        }
					    });
						
						chart.tooltip({
							  showCrosshairs: true,
							  shared: true,
						});
						
						chart
						  .line()
						  .position('checkDate*value')
						  .color('state')
						  .shape('smooth').label("value");

						chart
						  .point()
						  .position('checkDate*value')
						  .color('state')
						  .shape('circle');

						chart.render();
					} else if (type == 'bloodLipid') {
						var chart = new G2.Chart({
							  container: 'bloodLipid',
							  forceFit: true,
							  height: 500,
							  padding: '15%'
						});

						chart.source(data, {
					    	checkDate: {
					            alias: '日期',
					            range: [0, 1]
					        }
					    });
						
						chart.tooltip({
							showCrosshairs: true,
							shared: true,
						});
						
						chart
						  .line()
						  .position('checkDate*value')
						  .color('state')
						  .shape('smooth').label("value");

						chart
						  .point()
						  .position('checkDate*value')
						  .color('state')
						  .shape('circle');

						chart.render();
					} else if (type == 'riskScore') {
						var chart = new G2.Chart({
							 container: 'riskScore',
						     forceFit: true,
						     height: 500,
						     padding: '10%'
					    });

					    chart.source(data, {
					    	checkDate: {
					            alias: '日期',
					            range: [0, 1]
					        },
					        riskScore: {
					        	min: 0,
				    	    	nice: true,
					            alias: '糖尿病危险因素评估分数'
					        }
					    });
				    	chart.tooltip({
				    	   showCrosshairs: true, // 展示 Tooltip 辅助线
				    	   shared: true,
				    	});
				    	
				    	chart.axis('riskScore', {
						      label: {
						        formatter: function(val){
						          return val + ' 分';
						        },
						      },
						});
				    	
				    	chart.line().position('checkDate*riskScore').label('riskScore');
				    	chart.point().position('checkDate*riskScore').size(4).shape('circle').style({stroke:'#fff', lineWidth: 1});
				    	chart.render();
					} else if (type == 'tizhi') {
						console.log(data);
						 var chart = new G2.Chart({
						        container: 'tizhi',
						        forceFit: true,
						        height: 500,
						        padding: '15%',
						        background:"rgba(0,0,0,0)"
						    });

						    chart.source(data, {
						        checkDate: {
						            alias: '日期',
						            range: [0, 1]
						        },
						        tizhi: {
						            min: 0,
						            alias: '体质'
						        }
						    });
						    chart.scale('tizhi', {
						    	ticks:[0, 10, 20, 30, 40, 50, 60, 70, 80, 90],
						    	formatter: function(val){
						    		return val;
						    	},
						    	nice:true
						    });
						    
						   chart.scale('checkDate',{
							   nice: true
						   });
						    
						    chart.axis('tizhi', {
							      label: {
							        formatter: function(tizhi){
							        	if (tizhi == 90) {
							        		tizhi = "平和质"
							        	} else if (tizhi == 80) {
							        		tizhi = "气虚质";
							        	} else if (tizhi == 70) {
							        		tizhi = "阳虚质";
							        	} else if (tizhi == 60) {
							        		tizhi = "阴虚质";
							        	} else if (tizhi == 50) {
							        		tizhi = "血瘀质";
							        	} else if (tizhi == 40) {
							        		tizhi = "痰湿质";
							        	} else if (tizhi == 30) {
							        		tizhi = "湿热质";
							        	} else if (tizhi == 20) {
							        		tizhi = "气郁质";
							        	} else if (tizhi == 10) {
							        		tizhi = "特禀质";
							        	} else if (tizhi == 0) {
							        		tizhi = "";
							        	}
							          return tizhi;
							        },
							      },
							      
							});

						    chart.tooltip({
						       // showTitle: true,
						       // showCrosshairs: true,
						       /*  crosshairs: {
						            type: 'xy',
						        }, */
						        itemTpl: '<li class="g2-tooltip-list-item" data-index={index} style="margin-bottom:4px;">'
						            + '<span style="background-color:{color};" class="g2-tooltip-marker"></span>'
						            + '{value}'
						            + '</li>'
						    });
						    chart
						        .point()
						        .position('checkDate*tizhi')
						        .shape('circle')
						        .tooltip('checkDate*tizhi', function(checkDate, tizhi) {
						        	if (tizhi == 90) {
						        		tizhi = "平和质"
						        	} else if (tizhi == 80) {
						        		tizhi = "气虚质";
						        	} else if (tizhi == 70) {
						        		tizhi = "阳虚质";
						        	} else if (tizhi == 60) {
						        		tizhi = "阴虚质";
						        	} else if (tizhi == 50) {
						        		tizhi = "血瘀质";
						        	} else if (tizhi == 40) {
						        		tizhi = "痰湿质";
						        	} else if (tizhi == 30) {
						        		tizhi = "湿热质";
						        	} else if (tizhi == 20) {
						        		tizhi = "气郁质";
						        	} else if (tizhi == 10) {
						        		tizhi = "特禀质";
						        	} else if (tizhi == 0) {
						        		tizhi = "";
						        	}
						            return {
						               // name: "体质",
						               // value: checkDate + ', ' + tizhi
						                value: tizhi
						            };
						        })
						        .style({
						            fillOpacity: 0.85
						        });
						    // chart.interaction('legend-highlight');
						    chart.render();
					} 
			}
		});

		var tizhiW = $("#tizhi").width();
		$(".tizhi1").css({ "width": tizhiW, "position": "absolute"});
	    window.onresize = function () {
	        $(".tizhi1").css({"width": tizhiW, "position": "absolute"});
	    };
	});
</script>
</body>
</html>
