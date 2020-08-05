<?xml version="1.0" encoding="UTF-8"?>   
<document margin="45,45,40,20">
<#assign colorWords = "0,0,0,0.8">
<#assign color1 = "0.01,0.41,0.96,0">
<#assign color2 = "0.01,0.91,0.99,0">
<#assign colorArrowRed = "212,22,22">
<#assign colorArrowBlue = "37,73,215">
<chapter name="阜新市三高慢病防控项目筛查报告" id="projectScreening" fontName="simsun" fontSize="1" color="255,255,255" spacingAfter="1" >
	<text indent= "0" align="center" fontName="simsun" fontSize="15"  style="bold" spacing="1">
		阜新市三高慢病防控项目筛查报告
	</text>
	<table cols="155,195,20,125,85,65,145" borderColor="${colorWords}" spacing="-5">
		<row>
			<cell padding="5" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold">姓名</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" >${name}</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="2">报告日期</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">${checkDate}</cell>
		</row>
		<row>
			<cell padding="5" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold">身份证号</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" >${customerId}</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="2">手机号码</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">${mobile}</cell>
		</row>
		<row>
			<cell padding="5" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold">既往疾病史</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" >${disease}</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="2">初筛结果人群分类</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">${classifyResult}</cell>
		</row>
	</table>
	<table cols="155,195,20,125,85,65,145" borderColor="${colorWords}" spacing="-5">
		<header>
			<cell padding="5" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold">项目</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold">检查内容</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="2">参考范围</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="2">检测结果</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" >评分</cell>
		</header>
		<row>
			<cell padding="5" rowspan="3" border="13"></cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold">年龄</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="2">-</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="2">${age}</cell>
			<cell  rowspan="2" border="13"></cell>
		</row>
		<row>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold">性别</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="2">-</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="2">${gender}</cell>
		</row>
		<row>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold">糖尿病家族史</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="2">-</cell>
		
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="2">
				<#if familyDisease?? && familyDisease?contains('糖尿病')>
					是
				<#else>
					否
				</#if>
			</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" rowspan="2" border="12">${riskScore}</cell>
		</row>
		<row>
			<cell padding="5" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" border="12">糖尿病风险评分表</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold">身高(cm)</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="2">-</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="2">${height}</cell>
		</row>
		<row>
			<cell  paddingLeft="1"  fontName="simsun" fontSize="9" color="${colorWords}" rowspan="2" border="12">《中国2型糖尿病防治指南（2013年版）》</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold">体重(kg)</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="2">-</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="2">${weight}</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" rowspan="2" border="12">综合评分≥25分为糖尿病高风险</cell>
		</row>
		<row>			
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold">腰围(cm)</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="2">男＜85/女＜80</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="2">${waistline}</cell>			
		</row>
		<row>
			<cell padding="5" rowspan="2" border="14"></cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold">BMI(kg/m2)</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="2">18.5-23.9</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="2">${BMI}</cell>
			<cell  rowspan="2" border="14"></cell>			
		</row>
		<row>			
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold">血压(mmHg)</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="2">90-130/60-85</cell>
			<cell  align="center"   colspan="2">
			<complex>
				<text fontName="simsun" fontSize="9" color="${colorWords}">${highPressure}</text>
				<#if highPressureState?? && highPressureState == "up">
					<text fontName="simsun" fontSize="9" color="${colorArrowRed}">↑</text>
				<#elseif highPressureState?? && highPressureState == "down">
					<text fontName="simsun" fontSize="9" color="${colorArrowRed}">↓</text>
				</#if>
				<text fontName="simsun" fontSize="9" color="${colorWords}">/</text>
				<text fontName="simsun" fontSize="9" color="${colorWords}">${lowPressure}</text>
				<#if lowPressureState?? && lowPressureState == "up">
					<text fontName="simsun" fontSize="9" color="${colorArrowRed}">↑</text>
				<#elseif lowPressureState?? && lowPressureState == "down">
					<text fontName="simsun" fontSize="9" color="${colorArrowRed}">↓</text>
				</#if>
			</complex>
			
			
			</cell>			
		</row>
	</table>
	<table cols="155,195,20,125,85,65,145" borderColor="${colorWords}" spacing="-5">
		<row height="0.01">
			<cell colspan="7" border="1"></cell>
		</row>
		<row>
			<cell padding="5" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" rowspan="10">生理、生化检测指标</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}"  style="bold" >空腹血糖(mmol/L)</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}"  colspan="2">3.9-6.1</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">
				<#if bloodGlucose??>
					<complex>
						<text fontName="simsun" fontSize="9" color="${colorWords}">
							${bloodGlucose}
						</text>
						<#if bloodGlucoseState?? && bloodGlucoseState == "up">
							<text fontName="simsun" fontSize="9" color="${colorArrowRed}">↑</text>
						<#elseif bloodGlucoseState?? && bloodGlucoseState == "down">
							<text fontName="simsun" fontSize="9" color="${colorArrowRed}">↓</text>
						</#if>
					</complex>
				<#else>
					-
				</#if>
			</cell>
		</row>
		<row>			
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}"  style="bold" >餐后2小时血糖(mmol/L)</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}"  colspan="2">3.9-7.8</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">
				<#if bloodGlucose2h??>
					<complex>
						<text fontName="simsun" fontSize="9" color="${colorWords}">
							${bloodGlucose2h}
						</text>
						<#if bloodGlucose2hState?? && bloodGlucose2hState == "up">
							<text fontName="simsun" fontSize="9" color="${colorArrowRed}">↑</text>
						<#elseif bloodGlucose2hState?? && bloodGlucose2hState == "down">
							<text fontName="simsun" fontSize="9" color="${colorArrowRed}">↓</text>
						</#if>
					</complex>
				<#else>
					-
				</#if>
			</cell>
		</row>
		<row>			
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}"  style="bold" >随机血糖(mmol/L)</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}"  colspan="2">3.9-11.1</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">
				<#if bloodGlucoseRandom??>
					<complex>
						<text fontName="simsun" fontSize="9" color="${colorWords}">
							${bloodGlucoseRandom}
						</text>
						<#if bloodGlucoseRandomState?? && bloodGlucoseRandomState == "up">
							<text fontName="simsun" fontSize="9" color="${colorArrowRed}">↑</text>
						<#elseif bloodGlucoseRandomState?? && bloodGlucoseRandomState == "down">
							<text fontName="simsun" fontSize="9" color="${colorArrowRed}">↓</text>
						</#if>
					</complex>
				<#else>
					-
				</#if>
			</cell>
		</row>
		<row>			
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}"  style="bold" >总胆固醇(mmol/L)</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}"  colspan="2">＜5.2</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">
				<#if tc??>
					<complex>
						<text fontName="simsun" fontSize="9" color="${colorWords}">
							${tc}
						</text>
						<#if tcState?? && tcState == "up">
							<text fontName="simsun" fontSize="9" color="${colorArrowRed}">↑</text>
						<#elseif tcState?? && tcState == "down">
							<text fontName="simsun" fontSize="9" color="${colorArrowRed}">↓</text>
						</#if>
					</complex>
				<#else>
					-
				</#if>
			</cell>
		</row>
		<row>			
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}"  style="bold" >高密度脂蛋白胆固醇(mmol/L)</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}"  colspan="2">≥1.0</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">
				<#if hdl??>
					<complex>
						<text fontName="simsun" fontSize="9" color="${colorWords}">
							${hdl}
						</text>
						<#if hdlState?? && hdlState == "up">
							<text fontName="simsun" fontSize="9" color="${colorArrowRed}">↑</text>
						<#elseif hdlState?? && hdlState == "down">
							<text fontName="simsun" fontSize="9" color="${colorArrowRed}">↓</text>
						</#if>
					</complex>
				<#else>
					-
				</#if>
			</cell>
		</row>
		<row>			
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}"  style="bold" >低密度脂蛋白胆固醇(mmol/L)</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}"  colspan="2">＜3.4</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">
				<#if ldl??>
					<complex>
						<text fontName="simsun" fontSize="9" color="${colorWords}">
							${ldl}
						</text>
						<#if ldlState?? && ldlState == "up">
							<text fontName="simsun" fontSize="9" color="${colorArrowRed}">↑</text>
						<#elseif ldlState?? && ldlState == "down">
							<text fontName="simsun" fontSize="9" color="${colorArrowRed}">↓</text>
						</#if>
					</complex>
				<#else>
					-
				</#if>
			</cell>
		</row>
		<row>			
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}"  style="bold" >甘油三酯(mmol/L)</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}"  colspan="2">＜1.7</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">
				<#if tg??>
					<complex>
						<text fontName="simsun" fontSize="9" color="${colorWords}">
							${tg}
						</text>
						<#if tgState?? && tgState == "up">
							<text fontName="simsun" fontSize="9" color="${colorArrowRed}">↑</text>
						<#elseif tgState?? && tgState == "down">
							<text fontName="simsun" fontSize="9" color="${colorArrowRed}">↓</text>
						</#if>
					</complex>
				<#else>
					-
				</#if>
			</cell>
		</row>
		<row>			
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}"  style="bold" >血氧</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}"  colspan="2">-</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">
				<#if oxygen??>
					${oxygen}
				<#else>
					-
				</#if>
			</cell>
		</row>
		<row>			
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}"  style="bold" >脉搏</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}"  colspan="2">-</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">
				<#if pulse??>
					${pulse}
				<#else>
					-
				</#if>
			</cell>
		</row>
		<row>			
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}"  style="bold" >体温</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}"  colspan="2">-</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">
				<#if temperature??>
					${temperature}
				<#else>
					-
				</#if>
			</cell>
		</row>
	</table>
	
	<table cols="155,127,127,127,127,127" borderColor="${colorWords}" spacing="-5">
		<header height="0.01">
			<cell ></cell>
			<cell ></cell>
			<cell ></cell>
			<cell ></cell>
			<cell ></cell>
			<cell ></cell>
		</header>
		<#if tizhi?contains('平和') || tizhi?contains('气虚') || tizhi?contains('阳虚') || tizhi?contains('阴虚') || tizhi?contains('痰湿') || tizhi?contains('湿热') || tizhi?contains('血瘀') || tizhi?contains('气郁') || tizhi?contains('特禀')>
		<row>
			<cell padding="5" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" rowspan="2">中医体质辨识</cell>
			<cell border="5">
				<complex>
					<#if tizhi?contains('平和')>
						
						<image   width="6" height="6">img/pdf/projectScreening/yes.png</image>
					<#else>
						
						<image   width="6" height="6">img/pdf/projectScreening/no.png</image>
					</#if>	
					<text fontName="simsun"  fontSize="9" color="${colorWords}" whole = "true">
						A 平和质   
					</text>
				</complex>
			</cell>
			<cell border="1">
				<complex>
					<#if tizhi?contains('气虚')>
						<image   width="6" height="6">img/pdf/projectScreening/yes.png</image>
					<#else>
						<image   width="6" height="6">img/pdf/projectScreening/no.png</image>
					</#if>	
					<text fontName="simsun"  fontSize="9" color="${colorWords}" whole = "true">
						B 气虚质   
					</text>
				</complex>
			</cell>
			<cell border="1">
				<complex>
					<#if tizhi?contains('阳虚')>
						<image   width="6" height="6">img/pdf/projectScreening/yes.png</image>
					<#else>
						<image   width="6" height="6">img/pdf/projectScreening/no.png</image>
					</#if>	
					<text fontName="simsun"  fontSize="9" color="${colorWords}" whole = "true">
						C 阳虚质   
					</text>
				</complex>
			</cell>
			<cell colspan="2" border="9">
				<complex>
					<#if tizhi?contains('阴虚')>
						<image   width="6" height="6">img/pdf/projectScreening/yes.png</image>
					<#else>
						<image   width="6" height="6">img/pdf/projectScreening/no.png</image>
					</#if>	
					<text fontName="simsun"  fontSize="9" color="${colorWords}" whole = "true">
						D 阴虚质   
					</text>
				</complex>
			</cell>			
		</row>
		<row>
			
			<cell border="6">
				<complex>
					<#if tizhi?contains('痰湿')>
						<image   width="6" height="6">img/pdf/projectScreening/yes.png</image>
					<#else>
						<image   width="6" height="6">img/pdf/projectScreening/no.png</image>
					</#if>	
					<text fontName="simsun"  fontSize="9" color="${colorWords}" whole = "true">
						E 痰湿质   
					</text>
				</complex>
			</cell>
			<cell border="2">
				<complex>
					<#if tizhi?contains('湿热')>
						<image   width="6" height="6">img/pdf/projectScreening/yes.png</image>
					<#else>
						<image   width="6" height="6">img/pdf/projectScreening/no.png</image>
					</#if>	
					<text fontName="simsun"  fontSize="9" color="${colorWords}" whole = "true">
						F 湿热质   
					</text>
				</complex>
			</cell>
			<cell border="2">
				<complex>
					<#if tizhi?contains('血瘀')>
						<image   width="6" height="6">img/pdf/projectScreening/yes.png</image>
					<#else>
						<image   width="6" height="6">img/pdf/projectScreening/no.png</image>
					</#if>	
					<text fontName="simsun"  fontSize="9" color="${colorWords}" whole = "true">
						G 血瘀质   
					</text>
				</complex>
			</cell>
			<cell border="2">
				<complex>
					<#if tizhi?contains('气郁')>
						<image   width="6" height="6">img/pdf/projectScreening/yes.png</image>
					<#else>
						<image   width="6" height="6">img/pdf/projectScreening/no.png</image>
					</#if>	
					<text fontName="simsun"  fontSize="9" color="${colorWords}" whole = "true">
						H 气郁质   
					</text>
				</complex>
			</cell>	
            <cell border="10">
				<complex>
					<#if tizhi?contains('特禀')>
						<image   width="6" height="6">img/pdf/projectScreening/yes.png</image>
					<#else>
						<image   width="6" height="6">img/pdf/projectScreening/no.png</image>
					</#if>	
					<text fontName="simsun"  fontSize="9" color="${colorWords}" whole = "true">
						I 特禀质   
					</text>
				</complex>
			</cell>			
		</row>
		<#else>
			<row>
				<cell padding="5" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" >中医体质辨识</cell>
				<cell padding="5" align="left" fontName="simsun" fontSize="9" color="${colorWords}" colspan="5">
					未检测
				</cell>
			</row>
		</#if>
		<row>
			<cell padding="5" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" >中医眼象检测</cell>
			<cell align="left" colspan="5">
				<complex>
					<#if eyeCheck?contains('已')>
						<image   width="6" height="6">img/pdf/projectScreening/yes.png</image>
					<#else>
						<image   width="6" height="6">img/pdf/projectScreening/no.png</image>
					</#if>	
					<text fontName="simsun"  fontSize="9" color="${colorWords}" whole = "true">
						已检测 
					</text>
				</complex>
			</cell>
		</row>
	</table>
	<table cols="155,195,20,125,85,65,145" borderColor="${colorWords}" spacing="-5">
		<header>
			<cell padding="5" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold">项目</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="5">结果说明</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" >健康风险</cell>
		</header>
		<row>
			<cell padding="5" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold">血糖情况</cell>
			
			<#if bloodSugarResultDesc??>
				<cell  align="left" padding="8" Leading="5"  colspan="5">
					<text fontName="simsun"  fontSize="9" color="${colorWords}">
						${bloodSugarResultDesc}
					</text>
				</cell>
			<#else>
				<cell  align="center" padding="8" Leading="5"  colspan="5">
					<text fontName="simsun"  fontSize="9" color="${colorWords}">
						-
					</text>
				</cell>
			</#if>	

			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" >
				<#if bloodSugarHealthRisk??>
					${bloodSugarHealthRisk}
				<#else>
					无法判断
				</#if>
			</cell>
		</row>
		<row>
			<cell padding="5" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold">血脂情况</cell>
			
			<#if bloodLipidResultDesc??>
				<cell  align="left" padding="8" Leading="5"  colspan="5">
					<text fontName="simsun"  fontSize="9" color="${colorWords}">
						${bloodLipidResultDesc}
					</text>
				</cell>
			<#else>
				<cell  align="center" padding="8" Leading="5"  colspan="5">
					<text fontName="simsun"  fontSize="9" color="${colorWords}">
						-
					</text>
				</cell>
			</#if>	
				
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" >
				<#if bloodLipidHealthRisk??>
					${bloodLipidHealthRisk}
				<#else>
					无法判断
				</#if>
			</cell>
		</row>
		<row>
			<cell padding="5" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold">血压情况</cell>
			
			<#if bloodPressureResultDesc??>
				<cell  align="left" padding="8" Leading="5"  colspan="5">
					<text fontName="simsun"  fontSize="9" color="${colorWords}">
						${bloodPressureResultDesc}
					</text>
				</cell>
			<#else>
				<cell  align="center" padding="8" Leading="5"  colspan="5">
					<text fontName="simsun"  fontSize="9" color="${colorWords}">
						-
					</text>
				</cell>
			</#if>
				
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" >
				<#if bloodPressureHealthRisk??>
					${bloodPressureHealthRisk}
				<#else>
					无法判断
				</#if>
			</cell>
		</row>
	</table>
	
	<table cols="155,195,20,125,85,65,145" borderColor="${colorWords}" spacing="-5">
		<row>
			<cell padding="5" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="7" border="13">健康提示</cell>
		</row>
		<row>
			<cell padding="5" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="7" border="14">基于您目前的健康状况，您需要通过以下检查确定健康隐患，我们会尽快通知您前往医院做相关检查。</cell>
		</row>
		<row>
			<cell padding="5" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold">OGTT检测</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="2" >${OGTTTest}
			</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="2">血脂检测</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="2">${bloodLipidTest}</cell>
		</row>
		<row>
			<cell padding="5" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold">血压检测</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="2">${bloodPressureTest}</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="2">基因检测</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="2">${geneTest}</cell>
		</row>
	</table>
	<#if !geneTest?contains('不需要')>
	<table cols="2.6,1.8,1.3,1.6,0.5,1.2,1.6,0.5,3.5,3.5" borderColor="${colorWords}" spacing="5">
		<header>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold"  colspan="10">基因检测</cell>
		</header>
		<row>
			<cell padding="3" align="left"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="4" border="5">糖尿病用药套餐</cell>
			<cell  align="center"  border="1">
				<#if geneReportCs?? && geneReportCs?contains('糖尿病用药套餐')>
					<image   width="6" height="6">img/pdf/projectScreening/yes.png</image>
				<#else>
					<image   width="6" height="6">img/pdf/projectScreening/no.png</image>
				</#if>
			</cell>
			<cell padding="3" align="left"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="4" border="1">高血压用药套餐</cell>
			<cell  align="center" border="9" >
				<#if geneReportCs?? && geneReportCs?contains('高血压用药套餐')>
					<image   width="6" height="6">img/pdf/projectScreening/yes.png</image>
				<#else>
					<image   width="6" height="6">img/pdf/projectScreening/no.png</image>
				</#if>
			</cell>		
		</row>
		<row>
			<cell padding="3" align="left"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="4" border="6">他汀类降脂药套餐</cell>
			<cell  align="center"  border="2">
				<#if geneReportCs?? && geneReportCs?contains('他汀类降脂药套餐')>
					<image   width="6" height="6">img/pdf/projectScreening/yes.png</image>
				<#else>
					<image   width="6" height="6">img/pdf/projectScreening/no.png</image>
				</#if>
			</cell>
			<cell padding="3" align="left"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="4" border="2"></cell>
			<cell  align="center" border="10" >
				
			</cell>		
		</row>
	</table>
	</#if>
	<text fontName="simsun" fontSize="9" color="${colorWords}">
		*注：本报告中各检测指标的参考范围均出自国家权威指南及文献。
	</text>
</chapter>
	<header >
	    <headeritem align="left" x="56" y="796" ><image width="98" height="40">img/pdf/projectScreening/ehLOGO.png</image></headeritem>
	</header>
</document>