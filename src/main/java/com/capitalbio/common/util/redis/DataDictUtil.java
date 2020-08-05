package com.capitalbio.common.util.redis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import com.capitalbio.common.exception.BaseException;
import com.capitalbio.common.model.DataDict;
import com.capitalbio.common.util.CommUtil;
import com.google.common.collect.Lists;
import com.toolkit.redisClient.template.JedisTemplate;
import com.toolkit.redisClient.util.RedisUtils;

/**  
* <p>Title: DataDictUtil</p>  
* <p>Description: 数据字典工具类</p>  
* @author guohuiyang  
* @date 2018年7月6日 下午2:04:47
*/
@Slf4j
public class DataDictUtil {
  static String REDIS_BUCKET=CommUtil.getConfigByString("redis.bucket", "chms");
  static JedisTemplate template = RedisUtils.getTemplate();
  /**  
   * <p>Title: getDictData</p>  
   * <p>Description: </p>  
   * @param dictKey 字典key，例如：dict:disease
   * @param orderKey 字典排序key，例如：dict:past_medical_history_order
   * @return  
   */  
  public static List<DataDict> getDictData(String dictKey, String orderKey) throws BaseException{
    Map<String, String> map = template.hgetAll(dictKey);
    List<String> lrange = template.lrange(orderKey, 0, template.llen(orderKey).intValue());
    /**将非HashSet集合转换成HashSet是为了快速处理集合之间的，交集、并集、差集*/
    HashSet<String> orderKeyHashSet=new HashSet<String>(map.keySet());
    HashSet<String> dictKeyHashSet=new HashSet<String>(lrange);
    orderKeyHashSet.removeAll(dictKeyHashSet);
    if(orderKeyHashSet.size()!=0){
      /**
       * debug_info字段待讨论，大概内容：字典key与字典排序key不一致
       * 
       * */
      log.error("Redis Err: "+dictKey + "和"+orderKey+"数据不一致");
      throw new BaseException("Redis_Data_Inconsistent");
    }else {
      ArrayList<DataDict> list = Lists.newArrayList();
      for (String code : lrange) {
        DataDict d = new DataDict();
        d.setCode(code);
        String name = map.get(code);
        if (null != name) {
          d.setName(name);
        } else {
          throw new BaseException("Redis_Key_Not_Exist");
        }
        list.add(d);
      }
      return list;
    }
  }
  
  /**  
   * <p>Title: getRedisBucket</p>  
   * <p>Description: 获取Redis 桶, 例如：healthdatavault，默认已添加分割符：（分号）</p>  
   * @return  healthdatavault：
   */  
  public static String getRedisBucket() {
    return REDIS_BUCKET+":";
  }
}
