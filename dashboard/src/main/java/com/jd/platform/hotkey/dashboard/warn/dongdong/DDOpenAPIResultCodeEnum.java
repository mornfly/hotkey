package com.jd.platform.hotkey.dashboard.warn.dongdong;

public enum DDOpenAPIResultCodeEnum {

    MSG_SUCCESS(230001,"消息发送成功"),
    MSG_COMM_EXCEEDED_FREQUENT(230002,"消息发送过快，请稍后再试"),
    MSG_COMM_SYS_BUSY(230003,"系统繁忙，请稍后再试"),
    MSG_COMM_VALIDATE_PASS(230004,"消息校验通过"),
    MSG_COMM_SEND_EXCEPTION(230005,"消息发送处理异常"),
    MSG_COMM_SEND_ACK_NULL(230006,"消息发送回执为空"),
    MSG_COMM_SEND_ACK_FAILED(230007,"消息发送回执校验失败"),
    MSG_HEADER_FROM_INVALID(230011,"消息发送字段无效"),
    MSG_HEADER_TO_INVALID(230012,"消息接收字段无效"),
    MSG_HEADER_VER_ILLEGAL(230013,"消息版本号不支持"),
    MSG_HEADER_TYPE_ILLEGAL(230014,"消息类型不支持"),
    MSG_HEADER_AID_INVALID(230015,"消息AID无效"),
    MSG_HEADER_AID_SUC(230016,"消息AID校验通过"),
    MSG_HEADER_EXCEPTION(230017,"消息头部异常"),
    MSG_HEADER_CLIENT_ILLEGAL(230018,"消息客户端类型不支持"),
    MSG_HEADER_APP_ILLEGAL(230019,"消息投递app不匹配"),
    MSG_BODY_NULL(230021,"消息体为空"),
    MSG_BODY_TYPE_ILLEGAL(230022,"消息体类型非法"),
    MSG_BODY_SUBTYPE_ILLEGAL(230023,"消息体子类型非法"),
    MSG_BODY_FORMAT_ILLEGAL(230024,"消息体格式非法"),
    MSG_SIGN_SUCCESS(230031,"获取访问签名信息成功"),
    MSG_SIGN_SUC_VER_EXPIRING(230032,"获取访问签名信息成功，但APP本地版本号即将过期"),
    MSG_SIGN_FAILED_VER_EXPIRED(230033,"获取访问签名信息失败，APP本地版本号已过期"),
    MSG_SIGN_FAILED_ILLEGAL(230034,"获取访问签名信息失败，APP签名凭证非法"),
    MSG_SIGN_FAILED_NOT_WHITE_LIST(230035,"获取访问签名信息失败，非白名单IP地址"),
    MSG_SIGN_FAILED_EXCEEDED_FREQUENT(230036,"获取访问签名信息太频繁，请稍后再试"),
    MSG_SIGN_FAILED_NULL(230037,"签名信息为空"),
    MSG_SIGN_FAILED_ACCID_DUPLICATED(230038,"签名信息accessId重复"),
    MSG_SIGN_FAILED_VER_ILLEGAL(230039,"签名信息版本非法"),
    MSG_SIGN_FAILED_TOKEN_ILLEGAL(230040,"签名信息accessToken非法"),
    MSG_SIGN_FAILED_EXCEPTION(230041,"获取签名信息异常"),
    MSG_SIGN_FAILED_ACCID_INVALID(230042,"签名信息accessId无效"),
    MSG_SIGN_FAILED_ASPID_INVALID(230043,"签名信息aspId无效"),
    MSG_SIGN_FAILED_HOST_INVALID(230044,"访问主机无效"),
    MSG_SIGN_FAILED_ASPINFO_NOT_READY(230045,"asp信息未完成配置"),
    MSG_SIGN_FAILED_ASPINFO_SECRET_NULL(230046,"asp配置凭证为空"),
    MSG_SIGN_FAILED_ASPINFO_ACCESSTOKEN_NULL(230047,"asp配置accessToken为空"),
    MSG_SIGN_FAILED_HTTP_FORBIDDEN(230048,"禁止非https访问"),
    MSG_NOTICE_SUC(230070,"通知消息发送成功"),
    MSG_NOTICE_ILLEGAL(230071,"通知消息非法"),
    MSG_NOTICE_EXCEED_MAX(230072,"通知接受者超出范围"),
    MSG_NOTICE_RECEIVE_EMPTY(230073,"通知接受者列表为空"),
    MSG_NOTICE_SEND_FAILED(230074,"发送通知消息失败"),
    MSG_NOTICE_SEND_EXCEPTION(230075,"发送通知消息失败"),
    MSG_DATA_REQ_SUC(230100,"请求数据服务操作成功"),
    MSG_DATA_APP_ILLEGAL(230101,"请求数据app不合法"),
    MSG_DATA_REQ_PARAM_ILLEGAL(230102,"请求数据参数不合法"),
    MSG_DATA_RESP_FAILED(230103,"请求数据服务返回失败"),
    MSG_DATA_RESP_NULL(230104,"请求数据服务范围空值"),
    MSG_DATA_REQ_EXCEPTION(230105,"请求数据处理异常"),
    MSG_DATA_REQ_VER_ILLEGAL(230106,"请求数据版本号非法"),
    MSG_DATA_REQ_LIST_EXCEEDED(230107,"请求数据列表长度超出范围"),
    MSG_DATA_RESP_UNSUPPORTED(230108,"请求返回数据不支持"),
    MSG_DATA_RESP_APP_ILLEGAL(230109,"新增appid格式不合法"),
    MSG_DATA_RESP_APP_EXISTED(230110,"新增appid已存在"),
    MSG_SOA_REQUEST_SUC(230200,"SOA请求成功"),
    MSG_SOA_APPID_EXEPTION(230201,"appId异常"),
    MSG_SOA_DATATYPE_EXEPTION(230202,"dataType异常"),
    MSG_SOA_ACTION_EXEPTION(230203,"action异常"),
    MSG_SOA_PARAM_NULL(230204,"param参数空"),
    MSG_SOA_REQUEST_NULL(230205,"soa request空"),
    MSG_SOA_REQUEST_EXCEPTION(230206,"soa request异常"),
    MSG_SOA_RESULT_NULL(230207,"请求返回为空"),
    MSG_SOA_RESULT_INVALID(230208,"请求返回校验不通过"),
    MSG_SOA_REQUEST_FAILED(230209,"请求返回操作失败"),
    MSG_SOA_SERVICE_NOT_EXIST(230210,"请求SOA服务不存在"),
    MSG_SOA_REQ_PASS(230211,"SOA请求校验通过"),
    MSG_SOA_REQ_HTTP_CHARSET_EXCEPTION(230212,"SOA HTTP请求不支持的字符集"),
    MSG_SOA_REQ_HTTP_IO_EXCEPTION(230213,"SOA HTTP请求IO异常"),
    MSG_SOA_REQ_HTTP_PARSE_EXCEPTION(230214,"SOA HTTP请求解析异常"),
    MSG_SOA_REQ_HTTP_ENCODING_EXCEPTION(230215,"SOA HTTP请求编码异常"),
    MSG_SOA_RESULT_AES_DECODE_EXCEPTION(230216,"SOA请求AES解析异常"),
    MSG_SOA_REQ_HTTP_URL_NULL(230217,"SOA HTTP请求URL为空"),
    MSG_SOA_REQ_HTTP_RESP_FAILED(230218,"SOA HTTP请求返回校验失败"),
    MSG_CHAT_MORE_OPERATE_NEEDED(230301,"需要更多的操作"),
    MSG_CHAT_WAITER_MISSED(230302,"没有配置客服"),
    MSG_CHAT_SHUNT_SUCCESS(230303,"分流成功"),
    MSG_CHAT_RISK_NO_WAITER_SERVICE(230304,"客服暂时不能为您提供服务"),
    MSG_CHAT_RISK_CAPTCHA_INPUT(230305,"请输入验证码"),
    MSG_CHAT_RISK_CAPTCHA_MISMATCH(230306,"验证码错误，请重新输入"),
    MSG_CHAT_RISK_CAPTCHA_MISMATCH_EXCEEDED(230307,"验证码错误次数超过限制"),
    MSG_CHAT_RISK_CAPTCHA_MATCHED(230308,"验证码回答正确"),
    MSG_CHAT_RISK_CAPTCHA_VOICE_INPUT(230309,"请输入语音验证码"),
    MSG_CHAT_RISK_CAPTCHA_VOICE_MISMATCH(230310,"风控语音验证码错误，请重新输入验证码"),
    MSG_CHAT_RISK_PHONE_NOT_BOUND(230311,"用户未绑定电话"),
    MSG_CHAT_RISK_LANG_FAILED(230312,"语言验证失败，重新验证"),
    MSG_CHAT_RISK_CAPTCHA_VOICE_PRESS(230313,"为了保护您的账号安全，请点击按钮进行语音验证"),
    MSG_CHAT_WAITER_OFFLINE(230314,"分配不在工作时间或客服不在线"),
    MSG_CHAT_SEND_FAILED(230315,"消息发送失败"),
    MSG_CHAT_RISK_NETWORK_EXCEPTION(230316,"网络异常，请重新输入验证码"),
    MSG_CHAT_REPEAT_CUSTOMER(230317,"消息重复发送 顾客消息"),
    MSG_CHAT_REPEAT_WAITER(230318,"消息重复发送 客服消息"),
    MSG_CHAT_LEAVE_SUCCESS(230319,"留言成功"),
    MSG_CHAT_LEAVE_FAILED(230320,"留言失败"),
    MSG_CHAT_RISK_TIPS(230321,"风险提示"),
    MSG_CHAT_ACK_UNKNOWN(230322,"未知返回chat_message类型"),
    MSG_CHAT_ACK_CODE_NOT_SUPPORT(230323,"不支持的返回code"),
    MSG_INVITE_FAILED(230351,"邀评失败"),
    MSG_INVITE_EVALUATED(230352,"已评价"),
    MSG_INVITE_DUPLICATED(230353,"已邀评"),
    MSG_INVITE_CUSTOMER_OFFLINE(230354,"顾客已经离线"),
    MSG_INVITE_UNKNOWN(230355,"未知邀评状态");

    private int code;
    private String description;

    DDOpenAPIResultCodeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * Getter for property 'code'.
     *
     * @return Value for property 'code'.
     */
    public int getCode() {
        return code;
    }

    /**
     * Setter for property 'code'.
     *
     * @param code Value to set for property 'code'.
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * Getter for property 'description'.
     *
     * @return Value for property 'description'.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for property 'description'.
     *
     * @param description Value to set for property 'description'.
     */
    public void setDescription(String description) {
        this.description = description;
    }
}