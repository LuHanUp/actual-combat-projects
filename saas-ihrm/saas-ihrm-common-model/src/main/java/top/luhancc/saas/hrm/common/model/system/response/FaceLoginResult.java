package top.luhancc.saas.hrm.common.model.system.response;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;


@Data
@ToString
public class FaceLoginResult implements Serializable {
    private static final long serialVersionUID = -1616426041373762391L;
    /**
     * 二维码使用状态
     * -1：未使用
     * 0：失败
     * 1：登录成功，返回token和用户id
     */
    private Integer state;
    /**
     * 登录信息
     */
    private String token;
    /**
     * 用户ID
     */
    private String userId;

    public FaceLoginResult(Integer state, String token, String userId) {
        this.state = state;
        this.token = token;
        this.userId = userId;
    }

    public FaceLoginResult(Integer state) {
        this.state = state;
    }
}

