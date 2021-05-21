package top.luhancc.saas.hrm.system.service;

import org.springframework.web.multipart.MultipartFile;
import top.luhancc.saas.hrm.common.model.system.response.FaceLoginResult;

/**
 * 人脸登录service
 *
 * @author luHan
 * @create 2021/5/21 14:09
 * @since 1.0.0
 */
public interface FaceLoginService {

    /**
     * 获取刷脸登录二维码
     */
    String qrCode();

    /**
     * 检查二维码：登录页面轮询调用此方法，根据唯一标识code判断用户登录情况
     */
    FaceLoginResult qrCodeCheck(String code);

    /**
     * 人脸登录：根据落地页随机拍摄的面部头像进行登录
     * 根据拍摄的图片调用百度云AI进行检索查找
     */
    void loginByFace(String code, MultipartFile attachment);

    /**
     * 图像检测，判断图片中是否存在面部头像
     */
    void checkFace(MultipartFile attachment);
}
