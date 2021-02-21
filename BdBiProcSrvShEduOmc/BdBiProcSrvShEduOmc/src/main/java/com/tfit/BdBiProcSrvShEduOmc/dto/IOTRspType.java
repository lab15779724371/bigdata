package com.tfit.BdBiProcSrvShEduOmc.dto;

public enum IOTRspType {
    Success("成功", 1000),
    System_ERR("系统错误", 2001),
    AUTHCODE_CHKERR("授权码验证失败", 2003),
    USERACCOUNT_NOTEXIST_ERR("用户不存在", 2004),
    USERACCOUNTPASSWORD_ERR("用户名或密码错误", 2006),
    PASSWORDERRFIVETIMES_ERR("密码输入错误超过5次，请15分钟之后再试！", 2008),
    Account_Exist("用户账号已存在", 2005),
    OldPassword_Err("密码错误", 2007),
    NewConfPassword_Err("新密码不能为空", 2009),
    JsonFormParse_Err("Json格式数据解析失败", 2011),
    QueryDataRecord_Err("查询数据记录失败", 2013),
    Account_NonExist("用户账号不存在", 2015),
    Param_VisitIllegal("访问接口参数非法", 2017),
    UserEnable_Err("启禁用参数错误", 2019),
    SET_WARN_RULE_ERR("保存预警规则失败",2040),
    /**
     * http请求方式异常
     */
    HTTP_REQUEST_EXCEPTION("http请求方式异常", 2021),
    UNKNOW_EXCEPTION("未知异常", 2023),
    DATABASE_OPS_EXCEPTION("数据库操作异常", 2025),
    RoleName_Exist("角色已存在", 2027),
    RoleName_NoExist("角色不存在", 2029),
    Param_VisitFrmErr("访问接口参数格式错误", 2031),
    AdminAccount_NonExist("管理员账号不存在", 2033),
    Account_Forbid("账号已被禁用", 2035),
    Mail_Send_Fail("邮件发送失败", 2037),
    
    /**
     * 失败
     */
    Fail("失败", 3001),
    ;

    private String msg;
    private Integer code;

    private IOTRspType(String msg, int code) {
        this.code = code;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
