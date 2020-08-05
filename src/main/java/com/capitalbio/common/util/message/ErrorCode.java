package com.capitalbio.common.util.message;

/**
 * 错误代码.
 */
public enum ErrorCode {

  UNKNOWN("FAIL", "FAIL"), SUCCESS("SUCCESS", "SUCCESS");



  private String moduleCode;
  private String message;

  ErrorCode(String moduleCode, String message) {
    this.moduleCode = moduleCode;
  }

  public String getModuleCode() {
    return moduleCode;
  }

  public String getMessage() {
    return message;
  }


}
