package com.capitalbio.common.util.message;

import org.apache.commons.lang3.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.capitalbio.common.util.PropertyUtils;
import com.capitalbio.common.util.redis.DataDictUtil;
import com.toolkit.redisClient.template.JedisTemplate;
import com.toolkit.redisClient.util.RedisUtils;

/**
 * @ClassName: MessageUtil
 * @Description: rest接口返回消息工具类
 * @version V1.0
 */
public class MessageUtil {
  /** 初始化redis. */
  static JedisTemplate template = RedisUtils.getTemplate();
 
  /** redis中 消息码的key */
  private final static String MSG = DataDictUtil.getRedisBucket()+"Msg";
  /** redis中 错误码的key */
  private final static String DEBUGINFO = DataDictUtil.getRedisBucket()+"DebugInfo";

  /**
   * 1.接口访问成功，没有返回值和向用户展示的提示信息
   * 
   * @return Message
   */
  public static Messages getSuccessMessage() {
    Messages message = new Messages();
    message.setCode(Messages.SUCCESS);
    return message;
  }

  /**
   * 2.接口访问成功，没有返回值，但有向用户展示的提示信息
   * 
   * @param msgFieldName 消息码在redis中的fieldName
   * @return Message
   */
  public static Messages getSuccessMessage(String msgFieldName) {
    Messages message = new Messages();
    message.setCode(Messages.SUCCESS);
    message.setMsg(template.hget(MSG, msgFieldName));
    return message;
  }

  /**
   * 3. 接口访问成功，有返回值，且有向用户展示的提示信息
   * 
   * @param msgFieldName 消息码在redis中的fieldName
   * @param data 返回数据体
   * @return Message
   */
  public static Messages getSuccessMessage(String msgFieldName, JSONObject data) {
    Messages message = new Messages();
    message.setCode(Messages.SUCCESS);
    message.setMsg(template.hget(MSG, msgFieldName));
    message.setData(data);
    return message;
  }

  /**
   * 4. 接口访问失败。有向用户展示的提示信息
   * 
   * @param msgFieldName 消息码在redis中的fieldName
   * @return Message
   */
  public static Messages getErrorMessage(String msgFieldName) {
    Messages message = new Messages();
    message.setCode(Messages.FAILURE);
    message.setMsg(template.hget(MSG, msgFieldName));
    return message;
  }

  /**
   * 5. 接口访问失败。有向用户展示的提示信息和给开发人员的调试信息.
   * 
   * @param msgFieldName 消息码在redis中的fieldName
   * @param debugInfoFieldName 错误码在redis中的fieldName
   * @return Message
   */
  public static Messages getErrorMessage(String msgFieldName, String debugInfoFieldName) {
    Messages message = new Messages();
    message.setCode(Messages.FAILURE);
    message.setMsg(template.hget(MSG, msgFieldName));
    /** 联调环境中需要返回debug_info 错误代码信息. **/
    String apiType = PropertyUtils.getProperty("api.type");
    if (StringUtils.isNotEmpty(apiType) && apiType.equals("2")) {
      message.setDebugInfo(template.hget(DEBUGINFO, debugInfoFieldName));
    }
    return message;
  }

  /**
   * 6. 接口访问失败。只有给开发人员的调试信息.
   * 
   * @param debugInfoFieldName 错误码在redis中的fieldName
   * @return Message
   */
  public static Messages getErrorDebugInfoMessage(String debugInfoFieldName) {
    Messages message = new Messages();
    message.setCode(Messages.FAILURE);
    /** 联调环境中需要返回debug_info 错误代码信息. **/
    String apiType = PropertyUtils.getProperty("api.type");
    if (StringUtils.isNotEmpty(apiType) && apiType.equals("2")) {
      message.setDebugInfo(template.hget(DEBUGINFO, debugInfoFieldName));
    }
    return message;
  }

  /**
   * 7. 接口访问失败。有向用户展示的提示信息
   * 
   * @param msgFieldName 消息码在redis中的fieldName
   * @return Message
   */
  public static Messages getErrorMessageByMsg(String msgFieldName) {
    Messages message = new Messages();
    message.setCode(Messages.FAILURE);
    message.setMsg(template.hget(MSG, msgFieldName));
    return message;
  }
}
