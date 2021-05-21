package top.luhancc.hrm.common.domain;

/**
 * 返回码枚举类
 *
 * @author luHan
 */
public enum ResultCode {

    SUCCESS(true, 10000, "操作成功！"),
    // ---系统错误返回码-----
    FAIL(false, 10001, "操作失败"),
    UNAUTHENTICATED(false, 10002, "您还未登录"),
    UNAUTHORISE(false, 10003, "权限不足"),
    ARGUMENT_ERROR(false, 99998, "参数错误"),
    SERVER_ERROR(false, 99999, "抱歉，系统繁忙，请稍后重试！"),

    // ---用户操作返回码 2000x----
    LOGIN_ERROR(false, 20001, "用户名或密码错误"),
    RELOGIN_ERROR(false, 20002, "token过期,请重新登录"),
    RELOGIN_QR_ERROR(false, 20002, "二维码登录失效,请重新获取二维码"),
    USER_HEAD_IMG_ERROR(false, 20003, "上传用户头像失败"),
    CREATE_QRCODE_ERROR(false, 20004, "生成二维码失败"),
    // ---企业操作返回码 3000x----
    // ---权限操作返回码 4000x----
    // ---其他操作返回码----

    ;
    // 操作是否成功
    boolean success;
    // 操作代码
    int code;
    // 提示信息
    String message;

    ResultCode(boolean success, int code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public boolean success() {
        return success;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }

}
