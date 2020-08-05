package com.capitalbio.healthcheck.service;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.service.BaseService;
import com.capitalbio.common.util.DateUtil;
import com.capitalbio.common.util.IdcardValidator;
import com.capitalbio.common.util.ParamUtils;
import com.capitalbio.healthcheck.dao.BodyCompositionDAO;

@Service
public class BodyCompositionService extends BaseService {
	@Autowired BodyCompositionDAO bodyCompostionDAO;
	@Override
	public MongoBaseDAO getMongoBaseDAO() {
		return bodyCompostionDAO;
	}

	@Override
	public String getCollName() {
		return "bodyComposition";
	}
	
	public Map<String,Object> parseBodyCompostion(Map<String,Object> params) {
		String customerId = (String)params.get("memberId");
		params.put("customerId", customerId);
		
		String name = (String)params.get("name");
		if (name == null) {
			name = (String)params.remove("Name");
			if (name != null) {
				params.put("name", name);
			}
		}

		Map<String,Object> validMap = IdcardValidator.computeAgeAndSex(customerId);
		String valid = (String)validMap.get("valid");
		String computeSex = (String)validMap.get("sex");
		Integer computeAge = (Integer)validMap.get("age");
		params.put("valid", valid);
		
		Integer birthYear = ParamUtils.getIntValue(String.valueOf(params.get("BirthYear")));
		if (computeAge != null) {
			params.put("age", computeAge);
		} else if (birthYear != null) {
			int age = Calendar.getInstance().get(Calendar.YEAR) - birthYear;
			params.put("age", age);
		}
		params.put("birthYear", birthYear);
		
		
		String sex = (String)params.get("sex");
		if (sex == null) {
			sex = (String)params.remove("Sex");
			if (sex != null) {
				params.put("sex", sex);
				if ("1".equals(sex)) {
					sex = "男";
				} else if ("2".equals(sex)){
					sex = "女";
				}
				params.put("sex", sex);
			}
		}
		
		if (params.get("sex") == null) {
			params.put("sex", computeSex);
		}
		
		if (params.get("height") != null) {
			params.put("height", ParamUtils.getDoubleValue(String.valueOf(params.get("height"))));
		} else if (params.get("Height") != null) {
			params.put("height", ParamUtils.getDoubleValue(String.valueOf(params.remove("Height"))));
		}
		if (params.get("weight") != null) {
			params.put("weight", ParamUtils.getDoubleValue(String.valueOf(params.get("weight"))));
		} else if (params.get("Weight") != null) {
			params.put("weight", ParamUtils.getDoubleValue(String.valueOf(params.remove("Weight"))));
		}
		
		if (params.get("BMI") != null) {
			params.put("BMI", ParamUtils.getDoubleValue(String.valueOf(params.get("BMI"))));
		} else if(params.get("height") != null && params.get("weight") != null){
			Integer height = (Integer)params.get("height");
			double heightM = (double)((double)height/(double)100);
			Double weight = (Double)params.get("weight");
			double bmi = (double)(weight.doubleValue()/(heightM*heightM));
			params.put("BMI",  bmi);
		}
		if (params.get("WHR") != null) {//腰臀比
			params.put("WHR", ParamUtils.getDoubleValue(String.valueOf(params.get("WHR"))));
		} else if (params.get("waistline") != null && params.get("hipline") != null) {
			Integer waistline = (Integer)params.get("waistline");
			Integer hipline = (Integer)params.get("hipline");
			params.put("WHR", (double)(waistline.doubleValue()/hipline.doubleValue()));
		}
		
		if (params.get("Fat") != null) {//脂肪
			params.put("fat", ParamUtils.getDoubleValue(String.valueOf(params.remove("Fat"))));
		}
		if (params.get("Bone") != null) {//骨质
			params.put("bone", ParamUtils.getDoubleValue(String.valueOf(params.remove("Bone"))));
		}
		if (params.get("Protein") != null) {//蛋白质
			params.put("protein", ParamUtils.getDoubleValue(String.valueOf(params.remove("Protein"))));
		}
		if (params.get("Water") != null) {//水分
			params.put("water", ParamUtils.getDoubleValue(String.valueOf(params.remove("Water"))));
		}
		if (params.get("Muscle") != null) {//肌肉
			params.put("muscle", ParamUtils.getDoubleValue(String.valueOf(params.remove("Muscle"))));
		}
		if (params.get("SMM") != null) {//骨骼肌
			params.put("SMM", ParamUtils.getDoubleValue(String.valueOf(params.remove("SMM"))));
		}
		if (params.get("PBF") != null) {//体脂百分比
			params.put("PBF", ParamUtils.getDoubleValue(String.valueOf(params.remove("PBF"))));
		}
		if (params.get("BMR") != null) {//基础代谢
			params.put("BMR", ParamUtils.getDoubleValue(String.valueOf(params.remove("BMR"))));
		}
		if (params.get("Edema") != null) {//水肿系数（2位小数）
			params.put("edema", ParamUtils.getDoubleValue(String.valueOf(params.remove("Edema"))));
		}
		if (params.get("VFI") != null) {//内脏脂肪指数
			params.put("VFI", ParamUtils.getDoubleValue(String.valueOf(params.remove("VFI"))));
		}
		if (params.get("Score") != null) {//健康评分
			params.put("score", ParamUtils.getDoubleValue(String.valueOf(params.remove("Score"))));
		}
		if (params.get("BodyType") != null) {//体型判定
			params.put("bodyType", params.remove("BodyType"));

		}
		if (params.get("BodyAge") != null) {//身体年龄
			params.put("bodyAge", ParamUtils.getIntValue(String.valueOf(params.remove("BodyAge"))));
		}

		if (params.get("LBM") != null) {//瘦体重
			params.put("LBM", ParamUtils.getDoubleValue(String.valueOf(params.get("LBM"))));
		}
		if (params.get("ICW") != null) {//细胞内液
			params.put("ICW", ParamUtils.getDoubleValue(String.valueOf(params.get("ICW"))));
		}
		if (params.get("ECW") != null) {//细胞外液
			params.put("ECW", ParamUtils.getDoubleValue(String.valueOf(params.get("ECW"))));
		}
		if (params.get("Standard_Weight") != null) {//目标体重
			params.put("standardWeight", ParamUtils.getDoubleValue(String.valueOf(params.remove("Standard_Weight"))));
		}
		if (params.get("Weight_control") != null) {//体重控制
			params.put("weightControl", ParamUtils.getDoubleValue(String.valueOf(params.remove("Weight_control"))));
		}
		if (params.get("Fat_control") != null) {//脂肪控制
			params.put("fatControl", ParamUtils.getDoubleValue(String.valueOf(params.remove("Fat_control"))));
		}
		if (params.get("Muscle_control") != null) {//肌肉控制
			params.put("muscleControl", ParamUtils.getDoubleValue(String.valueOf(params.remove("Muscle_control"))));
		}
		if (params.get("LiverRisk") != null) {//脂肪肝风险系数
			params.put("liverRisk", ParamUtils.getDoubleValue(String.valueOf(params.remove("LiverRisk"))));
		}
		if (params.get("TR_fat") != null) {//躯干脂肪量
			params.put("trFat", ParamUtils.getDoubleValue(String.valueOf(params.remove("TR_fat"))));
		}
		if (params.get("LA_fat") != null) {//左上肢脂肪
			params.put("laFat", ParamUtils.getDoubleValue(String.valueOf(params.remove("LA_fat"))));
		}
		if (params.get("RA_fat") != null) {//右上肢脂肪
			params.put("raFat", ParamUtils.getDoubleValue(String.valueOf(params.remove("RA_fat"))));
		}
		if (params.get("LL_fat") != null) {//左下肢脂肪
			params.put("llFat", ParamUtils.getDoubleValue(String.valueOf(params.remove("LL_fat"))));
		}
		if (params.get("RL_fat") != null) {//右下肢脂肪
			params.put("rlFat", ParamUtils.getDoubleValue(String.valueOf(params.remove("RL_fat"))));
		}

		if (params.get("TR_water") != null) {//躯干水分量
			params.put("trWater", ParamUtils.getDoubleValue(String.valueOf(params.remove("TR_water"))));
		}
		if (params.get("LA_water") != null) {//左上肢水分
			params.put("laWater", ParamUtils.getDoubleValue(String.valueOf(params.remove("LA_water"))));
		}
		if (params.get("RA_water") != null) {//右上肢水分
			params.put("raWater", ParamUtils.getDoubleValue(String.valueOf(params.remove("RA_water"))));
		}
		if (params.get("LL_water") != null) {//左下肢水分
			params.put("llWater", ParamUtils.getDoubleValue(String.valueOf(params.remove("LL_water"))));
		}
		if (params.get("RL_water") != null) {//右下肢水分
			params.put("rlWater", ParamUtils.getDoubleValue(String.valueOf(params.remove("RL_water"))));
		}
		
		if (params.get("TR_muscle") != null) {//躯干肌肉量
			params.put("trMuscle", ParamUtils.getDoubleValue(String.valueOf(params.remove("TR_muscle"))));
		}
		if (params.get("LA_muscle") != null) {//左上肢肌肉
			params.put("laMuscle", ParamUtils.getDoubleValue(String.valueOf(params.remove("LA_muscle"))));
		}
		if (params.get("RA_muscle") != null) {//右上肢肌肉
			params.put("raMuscle", ParamUtils.getDoubleValue(String.valueOf(params.remove("RA_muscle"))));
		}
		if (params.get("LL_muscle") != null) {//左下肢肌肉
			params.put("llMuscle", ParamUtils.getDoubleValue(String.valueOf(params.remove("LL_muscle"))));
		}
		if (params.get("RL_muscle") != null) {//右下肢肌肉
			params.put("rlMuscle", ParamUtils.getDoubleValue(String.valueOf(params.remove("RL_muscle"))));
		}

		String testDate = (String)params.get("TestDate");
		Date checkTime = DateUtil.stringToDate(testDate);
		String checkDate = DateUtil.dateToString(checkTime);
		params.put("checkDate", checkDate);
		params.put("checkTime", checkTime);

		return params;
	}


}
