package top.luhancc.saas.hrm.system.thirdservice.baidu.faceservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 百度人脸识别的配置属性类
 *
 * @author luHan
 * @create 2021/5/21 13:47
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "baidu.face")
@Data
public class BaiduFaceProperties {
    private String appId;
    private String apiKey;
    private String secretKey;

    /**
     * 图片类型
     * BASE64:图片的base64值，base64编码后的图片数据，编码后的图片大小不超过2M；
     * URL:图片的 URL地址( 可能由于网络等原因导致下载图片时间过长)；
     * FACE_TOKEN: 人脸图片的唯一标识，调用人脸检测接口时，会为每个人脸图片赋予一个唯一的FACE_TOKEN，同一张图片多次检测得到的FACE_TOKEN是同一个
     */
    private String imageType;

    /**
     * 用户组id（由数字、字母、下划线组成），长度限制128B
     */
    private String groupId;
}
