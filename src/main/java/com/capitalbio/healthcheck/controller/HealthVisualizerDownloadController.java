package com.capitalbio.healthcheck.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capitalbio.common.model.Message;
import com.capitalbio.common.service.FileManageService;
import com.capitalbio.common.util.FileUtil;
import com.capitalbio.common.util.ParamUtils;
import com.capitalbio.common.util.PropertyUtils;
import com.capitalbio.common.util.XlsxWorkBookUtil;
import com.capitalbio.healthcheck.service.HealthCheckService;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("hv")
public class HealthVisualizerDownloadController {
	
	@Autowired
	private HealthCheckService healthCheckService;
	
	@Autowired
	private FileManageService fileManageService;
	
	public String[] dataColumns = {"人数", "比例"};
	public String[] dataKeys = {"countList", "percList"};
	
	public String[] fuxinAges = {"", "35岁以下", "35-39岁", "40-44岁", "45-49岁", "50-54岁", "55-59岁", "60-65岁", "65岁以上", "合计"};
	public String[] fuxinAge1s = {"", "35-39岁", "40-44岁", "45-49岁", "50-54岁", "55-59岁", "60-65岁", "合计"};
	
	public String[] kunmingAges = {"", "44岁以下", "45-49岁", "50-54岁", "55-59岁", "60-65岁", "65岁以上", "合计"};
	public String[] kunmingAge1s = {"", "45-49岁", "50-54岁", "55-59岁", "60-65岁", "合计"};
	
	public String[] genderColumns = {"男", "女"};
	public String[] genders = {"", "男", "女", "合计"};
	public String[] genderKeys = {"maleList", "femaleList"};
	
	public String[] tnbs = {"", "糖尿病患者", "血糖异常人群", "糖尿病高风险人群", "血糖正常人群", "合计"};
	public String[] tnbColumns = {"糖尿病患者", "血糖异常人群", "糖尿病高风险人群", "血糖正常人群"};
	public String[] tnbKeys = {"diabetes", "bloodAbnormal", "highRisk", "normal"};
	public String[] tnbDetails = {"", "新发现糖尿病患者", "糖尿病前期人群", "血糖正常人群", "合计"};
	public String[] tnbColumnDetails = {"新发现糖尿病患者", "糖尿病前期人群", "血糖正常人群"};
	public String[] tnbKeyDetails = {"newDiabetes", "preDiabetes", "normal"};
	public String[] complicationDetails = {"", "已做并发症", "未做并发症", "合计"};
	
	public String[] gxys = {"", "高血压患者", "血压异常人群", "血压正常人群", "合计"};
	public String[] gxyColumns = {"高血压患者", "血压异常人群", "血压正常人群"};
	public String[] gxyKeys = {"highBlood", "bloodAbnormal", "normal"};
	public String[] xys = {"", "正常血压", "正常高值", "高血压", "合计"};
	public String[] gxyLevels = {"", "轻度", "中度", "重度", "合计"};
	public String[] gxyDetails = {"", "新发现高血压人群", "高血压前期人群", "血压正常人群", "合计"};
	public String[] gxyKeyDetails = {"newHypertendion", "preHypertendion", "normal"};
	public String[] gxyColumnDetails = {"新发现高血压人群", "高血压前期人群", "血压正常人群"};

	public String[] gxzs = {"", "血脂异常患者", "血脂异常高风险人群", "血脂正常人群", "合计"};
	public String[] gxzColumns = {"血脂异常患者", "血脂异常高风险人群", "血脂正常人群"};
	public String[] gxzKeys = {"hyperlipemia", "bloodLipidAbnormal", "normal"};
	public String[] gxzDetails = {"", "新发现血脂异常患者", "血脂正常人群", "合计"};
	public String[] gxzDetailKeys = {"newHighLipid", "normal"};
	public String[] gxzCloumnDetails = {"新发现血脂异常患者", "血脂正常人群"};
	
	public String[] fps = {"", "肥胖人群", "超重人群", "BMI正常人群", "偏瘦人群", "合计"};
	public String[] fpColumns = {"肥胖人群", "超重人群", "BMI正常人群", "偏瘦人群"};
	public String[] fpKeys = {"obesity", "overweight", "normal", "thin"};
	public String[] fpKeys2 = {"fps", "czs", "zcs", "pss"};
	
	public String[] tizhis = {"", "A平和质", "B气虚质", "C阳虚质", "D阴虚质", "E痰湿质", "F湿热质", "G血瘀质", "H气郁质", "I特禀质", "合计"};
	public String[] tizhiDiseasekeys = {"diabetesList", "bloodPressureList", "bloodLipidList", "fatList"};
	public String[] tizhiDiseaseDetailkeys = {"diabetesList", "hypertensionList", "hplList", "fatList"};
	
	public String[] diseaseColumns = {"糖尿病", "高血压", "血脂异常患者", "肥胖"};
	public String[] mzTitle = {"序号", "中医证候", "中医证候人数", "糖尿病人数", "糖尿病比例", "高血压人数", "高血压比例", "血脂异常患者人数", "血脂异常患者比例", "肥胖人数", "肥胖比例"};
	
	XlsxWorkBookUtil wbUtil = new XlsxWorkBookUtil();
	
	@RequestMapping(value = "/download/exportRecordConditionByAge")
	@ResponseBody
	public Message exportRecordConditionByAge(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findRecordConditionByAge(district);
		
//		List<Integer> countList = (List<Integer>) returnMap.get("countList");
//		List<Double> percList = (List<Double>) returnMap.get("percList");
		
		String item = PropertyUtils.getProperty("item");
		String url = "";
		if (item.equals("fuxin")) {
			url = downloadData1(fuxinAges, dataColumns, returnMap, "建档人群年龄分布", dataKeys, response);
		} else {
			url = downloadData1(kunmingAges, dataColumns, returnMap, "建档人群年龄分布", dataKeys, response);
		}
//		String path = wbUtil.createExcel(ages, dataColumns, returnMap, "建档人群年龄分布", dataKeys, response);
//		wbUtil.downloadExcel(response, path, "建档人群年龄分布.xlsx");
		return Message.data(url);
	}
	
	@RequestMapping(value = "/download/exportRecordConditionByGender")
	@ResponseBody
	public Message exportRecordConditionByGender(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findRecordConditionByGender(district);
		
//		List<Integer> countList = (List<Integer>) returnMap.get("countList");
//		List<Double> percList = (List<Double>) returnMap.get("percList");
		
//		String url = downloadData(genders, countList, percList, "建档人群性别分布", response);
//		String path = wbUtil.createExcel(genders, dataColumns, returnMap, "建档人群性别分布", dataKeys, response);
//		wbUtil.downloadExcel(response, path, "建档人群性别分布.xlsx");
		
		String url = downloadData1(genders, dataColumns, returnMap, "建档人群性别分布", dataKeys, response);
		return Message.data(url);
	}
	
	@RequestMapping(value = "/download/exportBloodSugarConditionPeopleDistribution")
	@ResponseBody
	public Message exportBloodSugarConditionPeopleDistribution(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodSugarConditionPeopleDistribution(district);
		
//		List<Integer> countList = (List<Integer>) returnMap.get("countList");
//		List<Double> percList = (List<Double>) returnMap.get("percList");
		
//		String url = downloadData(tnbs, countList, percList, "血糖情况人群分布", response);
//		String path = wbUtil.createExcel(tnbs, dataColumns, returnMap, "血糖情况人群分布", dataKeys, response);
//		wbUtil.downloadExcel(response, path, "血糖情况人群分布.xlsx");
		
		String url = downloadData1(tnbs, dataColumns, returnMap, "血糖情况人群分布", dataKeys, response);
		return Message.data(url);
	}
	
	@RequestMapping(value = "/download/exportBloodSugarConditionAgeDistributionHeadthCheck2")
	@ResponseBody
	public Message exportBloodSugarConditionAgeDistributionHeadthCheck2(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodSugarConditionAgeDistributionHeadthCheck2(district);
		
//		String url = wbUtil.createExcel(age1s, tnbColumns, returnMap, "血糖情况年龄分布", tnbKeys, response);
//		wbUtil.downloadExcel(response, url, "血糖情况年龄分布.xlsx");
		
		String item = PropertyUtils.getProperty("item");
		String url = "";
		if (item.equals("fuxin")) {
			url = downloadData1(fuxinAge1s, tnbColumns, returnMap, "血糖情况年龄分布", tnbKeys, response);
		} else {
			url = downloadData1(kunmingAges, tnbColumns, returnMap, "血糖情况年龄分布", tnbKeys, response);
		}
		
		return Message.data(url);
	}
	
	@RequestMapping(value = "/download/exportBloodSugarConditionPeopleDistributionByGender")
	@ResponseBody
	public Message exportBloodSugarConditionPeopleDistributionByGender(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodSugarConditionPeopleDistributionByGender(district);
		
//		String url = wbUtil.createExcel(tnbs, genderColumns, returnMap, "血糖情况性别分布", genderKeys, response);
//		wbUtil.downloadExcel(response, url, "血糖情况性别分布.xlsx");
		
		String url = downloadData1(tnbs, genderColumns, returnMap, "血糖情况性别分布", genderKeys, response);
		return Message.data(url);
	}
	
	@RequestMapping(value = "/download/exportBloodSugarConditionPeopleDistributionByTizhi")
	@ResponseBody
	public Message exportBloodSugarConditionPeopleDistributionByTizhi(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodSugarConditionPeopleDistributionByTizhi(district);
		
//		String url = wbUtil.createExcel(tizhis, tnbColumns, returnMap, "血糖情况体质分布", tnbKeys, response);
//		wbUtil.downloadExcel(response, url, "血糖情况体质分布.xlsx");
		
		String url = downloadData1(tizhis, tnbColumns, returnMap, "血糖情况体质分布", tnbKeys, response);
		return Message.data(url);
	}
	
	
	
	@RequestMapping(value = "/download/exportBloodPressureConditionPeopleDistribution")
	@ResponseBody
	public Message exportBloodPressureConditionPeopleDistribution(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodPressureConditionPeopleDistribution(district);
		
//		List<Integer> countList = (List<Integer>) returnMap.get("countList");
//		List<Double> percList = (List<Double>) returnMap.get("percList");
		
//		String url = downloadData(gxys, countList, percList, "血压情况人群分布", response);
//		String url = wbUtil.createExcel(gxys, dataColumns, returnMap, "血压情况人群分布", dataKeys, response);
//		wbUtil.downloadExcel(response, url, "血压情况人群分布.xlsx");
		
		String url = downloadData1(gxys, dataColumns, returnMap, "血压情况人群分布", dataKeys, response);
		return Message.data(url);
	}
	
	@RequestMapping(value = "/download/exportBloodPressureConditionAgeDistributionHeadthCheck2")
	@ResponseBody
	public Message exportBloodPressureConditionAgeDistributionHeadthCheck2(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodPressureConditionAgeDistributionHeadthCheck2(district);
		
//		String url = wbUtil.createExcel(age1s, gxyColumns, returnMap, "血压情况年龄分布", gxyKeys, response);
//		wbUtil.downloadExcel(response, url, "血压情况年龄分布.xlsx");
		
		String item = PropertyUtils.getProperty("item");
		String url = "";
		if (item.equals("fuxin")) {
			url = downloadData1(fuxinAge1s, gxyColumns, returnMap, "血压情况年龄分布", gxyKeys, response);
		} else {
			url = downloadData1(kunmingAges, gxyColumns, returnMap, "血压情况年龄分布", gxyKeys, response);
		}
		
		return Message.data(url);
	}
	
	@RequestMapping(value = "/download/exportBloodPressureConditionPeopleDistributionByGender")
	@ResponseBody
	public Message exportBloodPressureConditionPeopleDistributionByGender(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodPressureConditionPeopleDistributionByGender(district);
		
//		String url = wbUtil.createExcel(gxys, genderColumns, returnMap, "血压情况性别分布", genderKeys, response);
//		wbUtil.downloadExcel(response, url, "血压情况性别分布.xlsx");
		
		String url = downloadData1(gxys, genderColumns, returnMap, "血压情况性别分布", genderKeys, response);
		return Message.data(url);
	}
	
	@RequestMapping(value = "/download/exportBloodPressurePeopleDistributionByTizhi")
	@ResponseBody
	public Message exportBloodPressurePeopleDistributionByTizhi(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodPressurePeopleDistributionByTizhi(district);
		
//		String url = wbUtil.createExcel(tizhis, gxyColumns, returnMap, "血压情况体质分布", gxyKeys, response);
//		wbUtil.downloadExcel(response, url, "血压情况体质分布.xlsx");
		
		String url = downloadData1(tizhis, gxyColumns, returnMap, "血压情况体质分布", gxyKeys, response);
		return Message.data(url);
	}
	
	@RequestMapping(value = "/download/exportBloodPressureDistribution")
	@ResponseBody
	public Message exportBloodPressureDistribution(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodPressureDistribution(district);
		
//		String url = wbUtil.createExcel(xys, dataColumns, returnMap, "血压分布", dataKeys, response);
//		wbUtil.downloadExcel(response, url, "血压分布.xlsx");
		
		String url = downloadData1(xys, dataColumns, returnMap, "血压分布", dataKeys, response);
		return Message.data(url);
	}
	
	@RequestMapping(value = "/download/exportHighBloodPressureDistribution")
	@ResponseBody
	public Message exportHighBloodPressureDistribution(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findHighBloodPressureDistribution(district);
		
//		String url = wbUtil.createExcel(gxyLevels, dataColumns, returnMap, "高血压分布", dataKeys, response);
//		wbUtil.downloadExcel(response, url, "高血压分布.xlsx");
		
		String url =downloadData1(gxyLevels, dataColumns, returnMap, "高血压分布", dataKeys, response);
		return Message.data(url);
	}
	
	@RequestMapping(value = "/download/exportBloodLipidPeopleDistribution")
	@ResponseBody
	public Message exportBloodLipidPeopleDistribution(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodLipidPeopleDistribution(district);
		
//		List<Integer> countList = (List<Integer>) returnMap.get("countList");
//		List<Double> percList = (List<Double>) returnMap.get("percList");
		
//		String url = downloadData(gxzs, countList, percList, "血脂情况人群分布", response);
		
//		String url = wbUtil.createExcel(gxzs, dataColumns, returnMap, "血脂情况人群分布", dataKeys, response);
//		wbUtil.downloadExcel(response, url, "血脂情况人群分布.xlsx");
		
		String url = downloadData1(gxzs, dataColumns, returnMap, "血脂情况人群分布", dataKeys, response);
		return Message.data(url);
	}
	
	@RequestMapping(value = "/download/exportBloodLipidConditionAgeDistributionHeadthCheck2")
	@ResponseBody
	public Message exportBloodLipidConditionAgeDistributionHeadthCheck2(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodLipidConditionAgeDistributionHeadthCheck2(district);
		
//		String url = wbUtil.createExcel(age1s, gxzColumns, returnMap, "血脂情况年龄分布", gxzKeys, response);
//		wbUtil.downloadExcel(response, url, "血脂情况年龄分布.xlsx");
		
		String item = PropertyUtils.getProperty("item");
		String url = "";
		if (item.equals("fuxin")) {
			url = downloadData1(fuxinAge1s, gxzColumns, returnMap, "血脂情况年龄分布", gxzKeys, response);
		} else {
			url = downloadData1(kunmingAges, gxzColumns, returnMap, "血脂情况年龄分布", gxzKeys, response);
		}
		
		return Message.data(url);
	}
	
	@RequestMapping(value = "/download/exportBloodLipidConditionPeopleDistributionByGender")
	@ResponseBody
	public Message exportBloodLipidConditionPeopleDistributionByGender(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodLipidConditionPeopleDistributionByGender(district);
		
//		String url = wbUtil.createExcel(gxzs, genderColumns, returnMap, "血脂情况性别分布", genderKeys, response);
//		wbUtil.downloadExcel(response, url, "血脂情况性别分布.xlsx");
		
		String url = downloadData1(gxzs, genderColumns, returnMap, "血脂情况性别分布", genderKeys, response);
		return Message.data(url);
	}
	
	@RequestMapping(value = "/download/exportBloodLipidPeopleDistributionByTizhi")
	@ResponseBody
	public Message exportBloodLipidPeopleDistributionByTizhi(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodLipidPeopleDistributionByTizhi(district);
		
//		String url = wbUtil.createExcel(tizhis, gxzColumns, returnMap, "血脂情况体质分布", gxzKeys, response);
//		wbUtil.downloadExcel(response, url, "血脂情况体质分布.xlsx");
		String url = downloadData1(tizhis, gxzColumns, returnMap, "血脂情况体质分布", gxzKeys, response);
		return Message.data(url);
	}
	
	@RequestMapping(value = "/download/exportFatPeopleDistribution")
	@ResponseBody
	public Message exportFatPeopleDistribution(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findFatPeopleDistribution(district);
		
//		List<Integer> countList = (List<Integer>) returnMap.get("countList");
//		List<Double> percList = (List<Double>) returnMap.get("percList");
		
//		String url = downloadData(fps, countList, percList, "肥胖情况人群分布", response);
//		String url = wbUtil.createExcel(fps, dataColumns, returnMap, "肥胖情况人群分布", dataKeys, response);
//		wbUtil.downloadExcel(response, url, "肥胖情况人群分布.xlsx");
		
		String url = downloadData1(fps, dataColumns, returnMap, "肥胖情况人群分布", dataKeys, response);
		return Message.data(url);
	}
	
	@RequestMapping(value = "/download/exportFatPeopleDistributionByAge2")
	@ResponseBody
	public Message exportFatPeopleDistributionByAge2(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findFatPeopleDistributionByAge2(district);
		
//		String url = wbUtil.createExcel(age1s, fpColumns, returnMap, "肥胖指数年龄分布", fpKeys, response);
//		wbUtil.downloadExcel(response, url, "肥胖指数年龄分布.xlsx");
		
		String item = PropertyUtils.getProperty("item");
		String url = "";
		if (item.equals("fuxin")) {
			url = downloadData1(fuxinAge1s, fpColumns, returnMap, "肥胖指数年龄分布", fpKeys, response);
		} else {
			url = downloadData1(kunmingAges, fpColumns, returnMap, "肥胖指数年龄分布", fpKeys, response);
		}
		
		return Message.data(url);
	}
	
	@RequestMapping(value = "/download/exportFatPeopleDistributionByGender")
	@ResponseBody
	public Message exportFatPeopleDistributionByGender(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findFatPeopleDistributionByGender(district);
		
//		String url = wbUtil.createExcel(fps, genderColumns, returnMap, "肥胖情况性别分布", genderKeys, response);
//		wbUtil.downloadExcel(response, url, "肥胖情况性别分布.xlsx");
		
		String url = downloadData1(fps, genderColumns, returnMap, "肥胖情况性别分布", genderKeys, response);
		return Message.data(url);
	}
	
	@RequestMapping(value = "/download/findFatPeopleDistributionByTizhi")
	@ResponseBody
	public Message exportFatPeopleDistributionByTizhi(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findFatPeopleDistributionByTizhi(district);
		
//		String url = wbUtil.createExcel(tizhis, fpColumns, returnMap, "肥胖情况体质分布", fpKeys2, response);
//		wbUtil.downloadExcel(response, url, "肥胖情况体质分布.xlsx");
		
		String url = downloadData1(tizhis, fpColumns, returnMap, "肥胖情况体质分布", fpKeys2, response);
		return Message.data(url);
	}
	
	@RequestMapping(value = "/download/exportTizhiPeopleDistribution")
	@ResponseBody
	public Message exportTizhiPeopleDistribution(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
		String type = request.getParameter("type");
		Map<String, Object> returnMap = healthCheckService.findTizhiPeopleDistribution(district, type);
		
//		List<Integer> countList = (List<Integer>) returnMap.get("countList");
//		List<Double> percList = (List<Double>) returnMap.get("percList");
		
//		String url = downloadData(tizhis, countList, percList, "社区人群体质分类", response);
//		String url = wbUtil.createExcel(tizhis, dataColumns, returnMap, "社区人群体质分类", dataKeys, response);
//		wbUtil.downloadExcel(response, url, "社区人群体质分类.xlsx");
		
		String url = downloadData1(tizhis, dataColumns, returnMap, "社区人群体质分类", dataKeys, response);
		return Message.data(url);
	}
	
	@RequestMapping(value = "/download/exportTizhiPeopleDistributionByGender")
	@ResponseBody
	public Message exportTizhiPeopleDistributionByGender(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findTizhiPeopleDistributionByGender(district);
		
//		String url = wbUtil.createExcel(tizhis, genderColumns, returnMap, "体质情况性别分布", genderKeys, response);
//		wbUtil.downloadExcel(response, url, "体质情况性别分布.xlsx");
		
		String url = downloadData1(tizhis, genderColumns, returnMap, "体质情况性别分布", genderKeys, response);
		return Message.data(url);
	}
	
	@RequestMapping(value = "/download/exportTizhiPeopleDistributionByDisease")
	@ResponseBody
	public Message exportTizhiPeopleDistributionByDisease(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findTizhiPeopleDistributionByDisease(district);
		
//		String url = wbUtil.createExcel(tizhis, diseaseColumns, returnMap, "体质与代谢性疾病患病率的关系", tizhiDiseasekeys, response);
//		wbUtil.downloadExcel(response, url, "体质与代谢性疾病患病率的关系.xlsx");
		
		String url = downloadData1(tizhis, diseaseColumns, returnMap, "体质与代谢性疾病患病率的关系", tizhiDiseasekeys, response);
		return Message.data(url);
	}
	
	@RequestMapping(value = "/download/exportBloodSugarConditionPeopleDistributionHealthCheckDetail")
	@ResponseBody
	public Message exportBloodSugarConditionPeopleDistributionHealthCheckDetail(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodSugarConditionPeopleDistributionHealthCheckDetail(district);
		
//		String url = wbUtil.createExcel(tnbDetails, dataColumns, returnMap, "血糖情况人群分布", dataKeys, response);
//		wbUtil.downloadExcel(response, url, "血糖情况人群分布.xlsx");
		
		String url = downloadData1(tnbDetails, dataColumns, returnMap, "OGTT检测情况人群分布", dataKeys, response);
		return Message.data(url);
	}
	
	@RequestMapping(value = "/download/exportBloodSugarConditionAgeDistributionHealthCheckDetail")
	@ResponseBody
	public Message exportBloodSugarConditionAgeDistributionHealthCheckDetail(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodSugarConditionAgeDistributionHealthCheckDetail(district);
		
//		String url = wbUtil.createExcel(age1s, tnbColumnDetails, returnMap, "血糖情况年龄分布", tnbKeyDetails, response);
//		wbUtil.downloadExcel(response, url, "血糖情况年龄分布.xlsx");
		
		String item = PropertyUtils.getProperty("item");
		String url = "";
		if (item.equals("fuxin")) {
			url = downloadData1(fuxinAge1s, tnbColumnDetails, returnMap, "OGTT检测情况年龄分布", tnbKeyDetails, response);
		} else {
			url = downloadData1(kunmingAges, tnbColumnDetails, returnMap, "OGTT检测情况年龄分布", tnbKeyDetails, response);
		}
		
		return Message.data(url);
	}
	
	@RequestMapping(value = "/download/exportComplicationDistributionHealthCheckDetail")
	@ResponseBody
	public Message findComplicationDistributionHealthCheckDetail(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findComplicationDistributionHealthCheckDetail(district);
		
		String url = downloadData1(complicationDetails, dataColumns, returnMap, "并发症筛查统计", dataKeys, response);
		return Message.data(url);
	}
	
	@RequestMapping(value = "/download/exportBloodPressureConditionPeopleDistributionHealthCheck")
	@ResponseBody
	public Message exportBloodPressureConditionPeopleDistributionHealthCheck(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodPressureConditionPeopleDistributionHealthCheck(district);
		
//		String url = wbUtil.createExcel(gxyDetails, dataColumns, returnMap, "血压情况人群分布", dataKeys, response);
//		wbUtil.downloadExcel(response, url, "血压情况人群分布.xlsx");
		
		String url = downloadData1(gxyDetails, dataColumns, returnMap, "血压情况人群分布", dataKeys, response);
		return Message.data(url);
	}
	
	@RequestMapping(value = "/download/exportBloodPressureConditionAgeDistributionHealthCheckDetail")
	@ResponseBody
	public Message exportBloodPressureConditionAgeDistributionHealthCheckDetail(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodPressureConditionAgeDistributionHealthCheckDetail(district);
		
//		String url = wbUtil.createExcel(age1s, gxyColumnDetails, returnMap, "血压情况年龄分布", gxyKeyDetails, response);
//		wbUtil.downloadExcel(response, url, "血压情况年龄分布.xlsx");
		
		String item = PropertyUtils.getProperty("item");
		String url = "";
		if (item.equals("fuxin")) {
			url = downloadData1(fuxinAge1s, gxyColumnDetails, returnMap, "血压情况年龄分布", gxyKeyDetails, response);
		} else {
			url = downloadData1(kunmingAges, gxyColumnDetails, returnMap, "血压情况年龄分布", gxyKeyDetails, response);
		}
		
		return Message.data(url);
	}
	
	@RequestMapping(value = "/download/exportloodLipidConditionPeopleDistributionHealthCheck")
	@ResponseBody
	public Message exportloodLipidConditionPeopleDistributionHealthCheck(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodLipidConditionPeopleDistributionHealthCheck(district);
		
//		String url = wbUtil.createExcel(gxzDetails, dataColumns, returnMap, "血脂情况人群分布", dataKeys, response);
//		wbUtil.downloadExcel(response, url, "血脂情况人群分布.xlsx");
		
		String url = downloadData1(gxzDetails, dataColumns, returnMap, "血脂情况人群分布", dataKeys, response);
		return Message.data(url);
	}
	
	@RequestMapping(value = "/download/exportBloodLipidConditionAgePeopleDistributionHealthCheck")
	@ResponseBody
	public Message exportBloodLipidConditionAgePeopleDistributionHealthCheck(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findBloodLipidConditionAgePeopleDistributionHealthCheck(district);
		
//		String url = wbUtil.createExcel(age1s, gxzCloumnDetails, returnMap, "血脂情况年龄分布", gxzDetailKeys, response);
//		wbUtil.downloadExcel(response, url, "血脂情况年龄分布.xlsx");
		
		String item = PropertyUtils.getProperty("item");
		String url = "";
		if (item.equals("fuxin")) {
			url = downloadData1(fuxinAge1s, gxzCloumnDetails, returnMap, "血脂情况年龄分布", gxzDetailKeys, response);
		} else {
			url = downloadData1(kunmingAges, gxzCloumnDetails, returnMap, "血脂情况年龄分布", gxzDetailKeys, response);
		}
		
		return Message.data(url);
	}
	
	@RequestMapping(value = "/download/exportizhiPeopleDistribution")
	@ResponseBody
	public Message exportizhiPeopleDistribution(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		String type = request.getParameter("type");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findTizhiPeopleDistribution(district, type);
		
//		String url = wbUtil.createExcel(tizhis, dataColumns, returnMap, "社区人群体质分布", dataKeys, response);
//		wbUtil.downloadExcel(response, url, "社区人群体质分布.xlsx");
		
		String url = downloadData1(tizhis, dataColumns, returnMap, "社区人群体质分布", dataKeys, response);
		return Message.data(url);
	}
	
	@RequestMapping(value = "/download/exportTizhiConditionByDiseaseHealthCheckDetail")
	@ResponseBody
	public Message exportTizhiConditionByDiseaseHealthCheckDetail(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
		Map<String, Object> returnMap = healthCheckService.findTizhiConditionByDiseaseHealthCheckDetail(district);
		
//		String url = wbUtil.createExcel(tizhis, diseaseColumns, returnMap, "体质与代谢性疾病患病率的关系", tizhiDiseaseDetailkeys, response);
//		wbUtil.downloadExcel(response, url, "体质与代谢性疾病患病率的关系.xlsx");
		
		String url = downloadData1(tizhis, diseaseColumns, returnMap, "体质与代谢性疾病患病率的关系", tizhiDiseaseDetailkeys, response);
		return Message.data(url);
	}
	
	@RequestMapping(value = "/download/exportEyeRecordDistribution")
	@ResponseBody
	public Message exportEyeRecordDistribution(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String district = request.getParameter("district");
		district = parseDistrict(district);
//		Map<String, Object> returnMap = healthCheckService.findTizhiConditionByDiseaseHealthCheckDetail(district);
		
		List<Map<String, Object>> list = healthCheckService.downloadEyeRecordDistribution(district);
		
//		String url = wbUtil.createMzExcel(mzTitle, list, "中医证候与代谢性疾病患病率的关系", response);
//		wbUtil.downloadExcel(response, url, "中医证候与代谢性疾病患病率的关系.xlsx");
		
		String url = createMzExcel(mzTitle, list, "中医证候与代谢性疾病患病率的关系", response);
		return Message.data(url);
	}
	
	
	/*private String downloadData(String[] headerList, List<Integer> countList, List<Double> percList, String fileName
			, HttpServletResponse response) throws UnsupportedEncodingException {
		String url = "";
		
		String tempDir = PropertyUtils.getProperty("system.temp.dir");
		File dir = new File(tempDir, "downloads");
	    if(!dir.exists()){
	    	dir.mkdirs();
	    }
	    
//	    String  fileName1 = URLEncoder.encode(fileName, "UTF-8");
		String uuid =  UUID.randomUUID().toString();
		File path = new File(dir, uuid);
	    if(!path.exists()){
	    	path.mkdirs();
	    }
		
		File newFile = new File(path, fileName + ".xlsx");
		SXSSFWorkbook wb = new SXSSFWorkbook();
		Sheet sheet = wb.createSheet();
		
		// 表格第一行合并单元格
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headerList.length-1));
		
		// 设置第一行样式
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 水平居中
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); // 垂直居中
		
		cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框    
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框   
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框    
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
		
		Font font = wb.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体显示
		cellStyle.setFont(font);
		
		*//** 第一行 创建标题行 *//*
		Row row = sheet.createRow(0);
		Cell cellTitle = row.createCell(0);
		cellTitle.setCellValue(fileName);
		cellTitle.setCellStyle(cellStyle);
		
		*//** 第二行 *//*
		CellStyle style = wb.createCellStyle();
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框    
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框   
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框    
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
		
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 水平居中
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); // 垂直居中
		
		Row row2 = sheet.createRow(1);
        for (int i = 0; i < headerList.length; i++) {
            Cell cell = row2.createCell(i);
            cell.setCellValue(headerList[i]);
            cell.setCellStyle(style);
        }
        
        *//** 第三行 *//*
        Row row3 = sheet.createRow(2);
        Cell cell30 = row3.createCell(0);
        cell30.setCellValue("人数");
        cell30.setCellStyle(style);
        
        int count = 0;
        for (int i = 0; i < countList.size(); i++) {
        	Cell cell = row3.createCell(i + 1);
        	cell.setCellValue(countList.get(i));
        	cell.setCellStyle(style);
        	count += countList.get(i);
		}
        
        Cell cellCount = row3.createCell(countList.size() + 1);
        cellCount.setCellValue(count);
        cellCount.setCellStyle(style);
        
        *//** 第四行 *//*
        Row row4 = sheet.createRow(3);
        Cell cell40 = row4.createCell(0);
        cell40.setCellValue("比例");
        cell40.setCellStyle(style);
        
        Double percCount = (double) 0;
        for (int i = 0; i < percList.size(); i++) {
        	Cell cell = row4.createCell(i + 1);
        	cell.setCellValue(percList.get(i) + "%");
        	cell.setCellStyle(style);
        	percCount += percList.get(i);
		}
        
        Cell cellPercCount = row4.createCell(percList.size() + 1);
        cellPercCount.setCellValue(percCount + "%");
        cellPercCount.setCellStyle(style);
        
         * 写入到文件中
         
        File file = new File(newFile.getAbsolutePath());
        OutputStream outputStream;
		try {
			outputStream = new FileOutputStream(file);
			
			response.setContentType("application/vnd.ms-excel");
		    response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			
			wb.write(outputStream);
	        wb.close();
	        wb.dispose();
	        
	        outputStream.flush();
	        outputStream.close();
	        
	        //上传OSS
			fileManageService.uploadFile(file, fileName + ".xlsx");
			
			//删除临时文件
			FileUtil.delFolder(path.getAbsolutePath());
			
			//下载文件
			url = fileManageService.getFileUrl(fileName + ".xlsx");
			
			//fileManageService.deleteFile(title + ".xlsx");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return url;
    	
	}*/
	
	private String downloadData1(String[] headerList, String[] columnList, Map<String, Object> result,
			String fileName, String[] keys, HttpServletResponse response) throws UnsupportedEncodingException {
		String url = "";
		
		String tempDir = PropertyUtils.getProperty("system.temp.dir");
		File dir = new File(tempDir, "downloads");
	    if(!dir.exists()){
	    	dir.mkdirs();
	    }
	    
		String uuid =  UUID.randomUUID().toString();
		File path = new File(dir, uuid);
	    if(!path.exists()){
	    	path.mkdirs();
	    }
		
		File newFile = new File(path, fileName + ".xlsx");
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet();
		
		// 表格第一行合并单元格
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headerList.length-1));
		
		/** 第一行 创建标题行 */
		Row row = sheet.createRow(0);
		Cell cellTitle = row.createCell(0);
		cellTitle.setCellValue(fileName);
		cellTitle.setCellStyle(setTitleStyle(wb));
		
		/** 第二行 */
		Row row2 = sheet.createRow(1);
        for (int i = 0; i < headerList.length; i++) {
            Cell cell = row2.createCell(i);
            cell.setCellValue(headerList[i]);
            cell.setCellStyle(setTitleStyle(wb));
        }
        
        
        for (int n = 0; n < keys.length; n ++) {
        	if (keys[n].equals("percList")) {
        		List<Double> list = (List<Double>) result.get(keys[n]);
    			Row rown = sheet.createRow(n + 2);
    	        Cell celln0 = rown.createCell(0);
    	        celln0.setCellValue(columnList[n]);
    	        celln0.setCellStyle(setContentStyle(wb));
    	        
    	        Double count = (double) 0;
    	        for (int i = 0; i < list.size(); i++) {
    	        	Cell cell = rown.createCell(i + 1);
    	        	cell.setCellValue(list.get(i) + "%");
    	        	cell.setCellStyle(setContentStyle(wb));
    	        	count += list.get(i);
    			}
    	        
    	        Cell cellCount = rown.createCell(list.size() + 1);
    	        cellCount.setCellValue(ParamUtils.doubleScale(count, 0) + "%");
    	        cellCount.setCellStyle(setContentStyle(wb));
        	} else if (keys[n].equals("countList")){
        		
        		List<Integer> list = (List<Integer>) result.get(keys[n]);
    			Row rown = sheet.createRow(n + 2);
    	        Cell celln0 = rown.createCell(0);
    	        celln0.setCellValue(columnList[n]);
    	        celln0.setCellStyle(setContentStyle(wb));
    	        
    	        int count = 0;
    	        for (int i = 0; i < list.size(); i++) {
    	        	Cell cell = rown.createCell(i + 1);
    	        	cell.setCellValue(list.get(i));
    	        	cell.setCellStyle(setContentStyle(wb));
    	        	count += list.get(i);
    			}
    	        
    	        Cell cellCount = rown.createCell(list.size() + 1);
    	        cellCount.setCellValue(count);
    	        cellCount.setCellStyle(setContentStyle(wb));
    	        
        	} else {
        		List<Integer> list = (List<Integer>) result.get(keys[n]);
    			Row rown = sheet.createRow(n + 2);
    	        Cell celln0 = rown.createCell(0);
    	        celln0.setCellValue(columnList[n] + "（人）");
    	        celln0.setCellStyle(setContentStyle(wb));
    	        
    	        int count = 0;
    	        for (int i = 0; i < list.size(); i++) {
    	        	Cell cell = rown.createCell(i + 1);
    	        	cell.setCellValue(list.get(i));
    	        	cell.setCellStyle(setContentStyle(wb));
    	        	count += list.get(i);
    			}
    	        
    	        Cell cellCount = rown.createCell(list.size() + 1);
    	        cellCount.setCellValue(count);
    	        cellCount.setCellStyle(setContentStyle(wb));
        	}
		}
      
        for (int k = 0; k < headerList.length; k++) {
            sheet.autoSizeColumn(k);
        }
      
        /*
         * 写入到文件中
         */
        File file = new File(newFile.getAbsolutePath());
        OutputStream outputStream;
		try {
			outputStream = new FileOutputStream(file);
			
			response.setContentType("application/vnd.ms-excel");
		    response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			
			wb.write(outputStream);
	        wb.close();
//	        wb.dispose();
	        
	        outputStream.flush();
	        outputStream.close();
	        
	        //上传OSS
			fileManageService.uploadFile(file, fileName + ".xlsx");
			
			//删除临时文件
			FileUtil.delFolder(path.getAbsolutePath());
			
			//下载文件
			url = fileManageService.getFileUrl(fileName + ".xlsx");
			
			//fileManageService.deleteFile(title + ".xlsx");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return url;
    	
	}
	
	
	public String createMzExcel(String[] headerList, List<Map<String, Object>> list, String fileName, HttpServletResponse response) throws UnsupportedEncodingException, FileNotFoundException {
		String url = "";
		
		String tempDir = PropertyUtils.getProperty("system.temp.dir");
		File dir = new File(tempDir, "downloads");
	    if(!dir.exists()){
	    	dir.mkdirs();
	    }
	    
		String uuid =  UUID.randomUUID().toString();
		File path = new File(dir, uuid);
	    if(!path.exists()){
	    	path.mkdirs();
	    }
		
		File newFile = new File(path, fileName + ".xlsx");
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet();
		
		// 表格第一行合并单元格
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headerList.length-1));
		
		/** 第一行 创建标题行 */
		Row row = sheet.createRow(0);
		Cell cellTitle = row.createCell(0);
		cellTitle.setCellValue(fileName);
		cellTitle.setCellStyle(setTitleStyle(wb));
		
		/** 第二行 */
		Row row2 = sheet.createRow(1);
        for (int i = 0; i < headerList.length; i++) {
            Cell cell = row2.createCell(i);
            cell.setCellValue(headerList[i]);
            cell.setCellStyle(setTitleStyle(wb));
        }
        
        for (int n = 0; n < list.size(); n ++) {
        	Map<String, Object> map = list.get(n);
        	
        	Row rown = sheet.createRow(n + 2);
	        Cell celln0 = rown.createCell(0);
	        celln0.setCellValue(map.get("count").toString());
	        celln0.setCellStyle(setContentStyle(wb));
	        
	        Cell celln1 = rown.createCell(1);
	        celln1.setCellValue(map.get("syndrome").toString());
	        celln1.setCellStyle(setContentStyle(wb));
	        sheet.autoSizeColumn(1);
	        
	        Cell celln2 = rown.createCell(2);
	        celln2.setCellValue(map.get("num").toString());
	        celln2.setCellStyle(setContentStyle(wb));
	        
	        Cell celln3 = rown.createCell(3);
	        celln3.setCellValue(map.get("tnbCount").toString());
	        celln3.setCellStyle(setContentStyle(wb));
	        
	        Cell celln4 = rown.createCell(4);
	        celln4.setCellValue(map.get("tnbPerc").toString() + "%");
	        celln4.setCellStyle(setContentStyle(wb));
	        
	        Cell celln5 = rown.createCell(5);
	        celln5.setCellValue(map.get("gxyCount").toString());
	        celln5.setCellStyle(setContentStyle(wb));
	        
	        Cell celln6 = rown.createCell(6);
	        celln6.setCellValue(map.get("gxyPerc").toString() + "%");
	        celln6.setCellStyle(setContentStyle(wb));
	        
	        Cell celln7 = rown.createCell(7);
	        celln7.setCellValue(map.get("xzCount").toString());
	        celln7.setCellStyle(setContentStyle(wb));
	        
	        Cell celln8 = rown.createCell(8);
	        celln8.setCellValue(map.get("xzPerc").toString() + "%");
	        celln8.setCellStyle(setContentStyle(wb));
	        
	        Cell celln9 = rown.createCell(9);
	        celln9.setCellValue(map.get("fpCount").toString());
	        celln9.setCellStyle(setContentStyle(wb));
	        
	        Cell celln10 = rown.createCell(10);
	        celln10.setCellValue(map.get("fpPerc").toString() + "%");
	        celln10.setCellStyle(setContentStyle(wb));
        }
        
        for (int k = 0; k < headerList.length; k++) {
            sheet.autoSizeColumn(k);
        }
        // 处理中文不能自动调整列宽的问题
       // setSizeColumn(sheet, headerList.length);

        /*
         * 写入到文件中
         */
        File file = new File(newFile.getAbsolutePath());
        OutputStream outputStream;
		try {
			outputStream = new FileOutputStream(file);
			
			response.setContentType("application/vnd.ms-excel");
		    response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			
			wb.write(outputStream);
	        wb.close();
//	        wb.dispose();
	        
	        outputStream.flush();
	        outputStream.close();
	        
	        //上传OSS
			fileManageService.uploadFile(file, fileName + ".xlsx");
			
			//删除临时文件
			FileUtil.delFolder(path.getAbsolutePath());
			
			//下载文件
			url = fileManageService.getFileUrl(fileName + ".xlsx");
			
			//fileManageService.deleteFile(title + ".xlsx");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return url;
    	
	}
	
	public CellStyle setTitleStyle(XSSFWorkbook wb) {
		
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 水平居中
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); // 垂直居中
		
		cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框    
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框   
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框    
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
		
		Font font = wb.createFont();
		font.setFontName("微软雅黑");
		font.setFontHeightInPoints((short) 9);
		font.setBold(true);
		cellStyle.setFont(font);
		return cellStyle;
	}
	
	
	public CellStyle setContentStyle(XSSFWorkbook wb) {
		
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 水平居中
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); // 垂直居中
		
		cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框    
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框   
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框    
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
		
		Font font = wb.createFont();
		font.setFontName("微软雅黑");
		font.setFontHeightInPoints((short) 9);
		cellStyle.setFont(font);
		return cellStyle;
	}

	public String parseDistrict(String district) {
		if (district.equals("fxs")) { //阜新市
			district = "";
		} else if (district.equals("hzq")) { //海州区
			district = "海州区";
		} else if (district.equals("fmx")) { //阜蒙县
			district = "阜蒙县";
		} else if (district.equals("zwx")) { //彰武县
			district = "彰武县";
		} else if (district.equals("kms")) { //昆明市
			district = "昆明市";
		} else if (district.equals("plq")) { //盘龙区
			district = "盘龙区";
		}
		return district;
	}
}
