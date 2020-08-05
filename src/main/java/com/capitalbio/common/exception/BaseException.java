package com.capitalbio.common.exception;

import com.capitalbio.common.util.redis.DataDictUtil;
import lombok.Data;
import com.toolkit.redisClient.template.JedisTemplate;
import com.toolkit.redisClient.util.RedisUtils;
@Data
public class BaseException extends Exception {
  private static JedisTemplate template = RedisUtils.getTemplate();
  /** serialVersionUID */
  private static final long serialVersionUID = 1L;
  
  private String debugInfo;

  /**
   * 构造一个基本异常.
   * 
   * @param message 信息描述
   */
  public BaseException(String message) {
    super(template.hget(DataDictUtil.getRedisBucket() +"DebugInfo", message));
    this.setDebugInfo(message);
  }
  

  /**
   * 构造一个基本异常.
   *
   * @param message 信息描述
   * @param cause 根异常类（可以存入任何异常）
   */
  public BaseException(String message, Throwable cause) {
    super(template.hget(DataDictUtil.getRedisBucket() +"DebugInfo", message), cause);
    System.out.println(cause.getStackTrace());
    this.setDebugInfo(message);
  }


}
