<?xml version="1.0" encoding="UTF-8"?>   
<document margin="45,45,40,20">
<#assign colorWords = "0,0,0,0.8">
<#assign color1 = "0.01,0.41,0.96,0">
<#assign color2 = "0.01,0.91,0.99,0">
<#assign colorArrowRed = "212,22,22">
<#assign colorArrowBlue = "37,73,215">
<chapter name="阜新市三高慢病防控项目筛查报告" id="projectScreening" fontName="simsun" fontSize="1" color="255,255,255" spacingAfter="10" >
	<text indent= "0" align="center" fontName="simsun" fontSize="15"  style="bold" spacing="10">
		阜新市三高慢病防控项目筛查报告
	</text>
	<table cols="2.6,1.8,1.3,1.6,0.5,1.2,1.6,0.5,3.5,3.5" borderColor="${colorWords}" spacing="5">
		<row>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold">姓名</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">${name}</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="3">报告日期</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">${checkDateJs}</cell>
		</row>
		<row>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold">身份证号</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">${customerId}</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="3">手机号码</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">${mobile}</cell>
		</row>
		<row>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold">既往疾病史</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">
				<#if disease??>
					${disease}
				<#else>
					-
				</#if>
			</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="3">精筛结果人群分类</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">${classifyResultJs}</cell>
		</row>
	</table>
	<table cols="2.6,1.8,1.3,1.6,0.5,1.2,1.6,0.5,3.5,3.5" borderColor="${colorWords}" spacing="5">
		<header>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold"  colspan="10">血压检测结果</cell>
		</header>
		<row>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="6">第一次血压检测时间</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="4">${checkDate2}</cell>
		</row>
		<row>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="3">第一次复诊收缩压（mmHg）</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">${highPressure2}</cell>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="3">第一次复诊舒张压（mmHg）</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}">${lowPressure2}</cell>			
		</row>
		<row>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="6">第二次血压检测时间</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="4">${checkDate3}</cell>
		</row>
		<row>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="3">第二次复诊收缩压（mmHg）</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">${highPressure3}</cell>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="3">第二次复诊舒张压（mmHg）</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}">${lowPressure3}</cell>			
		</row>
			<row>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="6">第三次血压检测时间</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="4">${checkDate4}</cell>
		</row>
		<row>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="3">第三次复诊收缩压（mmHg）</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">${highPressure4}</cell>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="3">第三次复诊舒张压（mmHg）</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}">${lowPressure4}</cell>			
		</row>
	</table>
	
	<table cols="2.6,1.8,1.3,1.6,0.5,1.2,1.6,0.5,3.5,3.5" borderColor="${colorWords}" spacing="5">
		<header>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold"  colspan="10">OGTT检测结果</cell>
		</header>
		<row>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="6">OGTT检测时间</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="4">${ogttDate}</cell>
		</row>
		<row>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="3">OGTT2h（mmol/L）</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">${ogtt2h}</cell>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="3">OGTT（mmol/L）</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}">${ogtt}</cell>			
		</row>
	</table>
		
	<table cols="2.6,1.8,1.3,1.6,0.5,1.2,1.6,0.5,3.5,3.5" borderColor="${colorWords}" spacing="5">
		<header>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold"  colspan="10">生化检测结果</cell>
		</header>
		<row>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="3">甘油三酯（mmol/L）</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">${tgDetail}</cell>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="3">总胆固醇（mmol/L）</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}">${tcDetail}</cell>			
		</row>
		<row>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="3">高密度脂蛋白胆固醇（mmol/L）</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">${hdlDetail}</cell>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="3">低密度脂蛋白胆固醇（mmol/L）</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}">${ldlDetail}</cell>			
		</row>
	</table>
			
	<table cols="2.6,1.8,1.3,1.6,0.5,1.2,1.6,0.5,3.5,3.5" borderColor="${colorWords}" spacing="5">
		<header>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold"  colspan="10">尿检结果</cell>
		</header>
		<row>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="3">尿肌酐mAlb（mg/L）</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">${ucr}</cell>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="3">尿微量白蛋白（mg/ml）</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}">${malb}</cell>		
		</row>
	</table>
	
	<table cols="2.6,1.8,1.3,1.6,0.5,1.2,1.6,0.5,3.5,3.5" borderColor="${colorWords}" spacing="5">
		<header>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold"  colspan="10">足底检查结果</cell>
		</header>
		<row>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="2">干燥</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">左足</cell>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">${dryLeft}</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" >右足</cell>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" >${dryRight}</cell>		
		</row>
		<row>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="2">皲裂</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">左足</cell>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">${chapLeft}</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" >右足</cell>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" >${chapRight}</cell>		
		</row>
		<row>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="2">脱皮</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">左足</cell>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">${peelLeft}</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" >右足</cell>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" >${peelRight}</cell>		
		</row>
		<row>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="2">鸡眼</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">左足</cell>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">${cornLeft}</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" >右足</cell>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" >${cornRight}</cell>		
		</row>
		<row>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="2">畸形</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">左足</cell>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">${malLeft}</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" >右足</cell>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" >${malRight}</cell>		
		</row>
		<row>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="2">足底胼胝</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">左足</cell>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">${callusLeft}</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" >右足</cell>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" >${callusRight}</cell>		
		</row>
		<row>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="2">真菌感染</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">左足</cell>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">${fungalLeft}</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" >右足</cell>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" >${fungalRight}</cell>		
		</row>
		<row>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="2">溃疡</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">左足</cell>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">${ulcerLeft}</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" >右足</cell>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" >${ulcerRight}</cell>		
		</row>
		<row>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="2" rowspan="4">足底感觉&#x000A;（10g 尼龙丝）</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">左足第一次</cell>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">${feelLeftFirst}</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" >右足第一次</cell>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" >${feelRightFirst}</cell>		
		</row>
		<row>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">左足第二次</cell>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">${feelLeftSecond}</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" >右足第二次</cell>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" >${feelRightSecond}</cell>		
		</row>
		<row>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">左足第三次</cell>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">${feelLeftThird}</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" >右足第三次</cell>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" >${feelRightThird}</cell>		
		</row>
		<row>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">左足</cell>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" colspan="3">${feelLeftResult}</cell>
			<cell  align="center"  fontName="simsun" fontSize="9" color="${colorWords}" >右足</cell>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" >${feelRightResult}</cell>		
		</row>
		<row>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold"  colspan="10">眼底镜报告结果</cell>
		</row>
		<row height="50">
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold"  colspan="10">${fundus}</cell>
		</row>
	</table>
	<#if geneReports?? && geneReports != ''>
	<table cols="2.6,1.8,1.3,1.6,0.5,1.2,1.6,0.5,3.5,3.5" borderColor="${colorWords}" spacing="5">
		<header>
			<cell padding="3" align="center"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold"  colspan="10">基因检测</cell>
		</header>
		<row>
			<cell padding="3" align="left"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="4" border="5">糖尿病用药套餐</cell>
			<cell  align="center"  border="1">
				<#if geneReports?? && geneReports?contains('糖尿病用药套餐')>
					<image   width="6" height="6">img/pdf/projectScreening/yes.png</image>
				<#else>
					<image   width="6" height="6">img/pdf/projectScreening/no.png</image>
				</#if>
			</cell>
			<cell padding="3" align="left"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="4" border="1">高血压用药套餐</cell>
			<cell  align="center" border="9" >
				<#if geneReports?? && geneReports?contains('高血压用药套餐')>
					<image   width="6" height="6">img/pdf/projectScreening/yes.png</image>
				<#else>
					<image   width="6" height="6">img/pdf/projectScreening/no.png</image>
				</#if>
			</cell>		
		</row>
		<row>
			<cell padding="3" align="left"  fontName="simsun" fontSize="9" color="${colorWords}" style="bold" colspan="4" border="6">他汀类降脂药套餐</cell>
			<cell  align="center"  border="2">
				<#if geneReports?? && geneReports?contains('他汀类降脂药套餐')>
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