package com.capitalbio.common.util.message;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

/**
 * .
 * @ClassName: Message
 * @Description: rest接口返回消息
 * @version V1.0
 */
@JsonInclude(Include.NON_NULL)
@Data
public class Messages {
  
  static final int SUCCESS = 0;
  static final int FAILURE = 1;
  /** SYSTEM_MAINTENANCE_MODE
   * 系统维护模式 状态码为3*/  
  public static final int SYSTEM_MAINTENANCE_MODE = 3;

  private int code;
  private JSONObject data;
  private String msg;
  private String debugInfo;

}
