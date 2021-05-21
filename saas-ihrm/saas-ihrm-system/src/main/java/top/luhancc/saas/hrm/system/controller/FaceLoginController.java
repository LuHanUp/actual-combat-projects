package top.luhancc.saas.hrm.system.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.luhancc.hrm.common.domain.Result;
import top.luhancc.saas.hrm.common.model.system.response.FaceLoginResult;
import top.luhancc.saas.hrm.system.service.FaceLoginService;

/**
 * 人脸登录controller
 *
 * @author luHan
 * @create 2021/5/21 13:37
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/sys/faceLogin")
@Slf4j
public class FaceLoginController {
    private final FaceLoginService faceLoginService;

    /**
     * 获取刷脸登录二维码
     */
    @RequestMapping(value = "/qrcode", method = RequestMethod.GET)
    public Result<String> qrCode() throws Exception {
        String qrCode = faceLoginService.qrCode();
        return Result.success(qrCode);
    }

    /**
     * 检查二维码：登录页面轮询调用此方法，根据唯一标识code判断用户登录情况
     */
    @RequestMapping(value = "/qrcode/{code}", method = RequestMethod.GET)
    public Result<FaceLoginResult> qrCodeCheck(@PathVariable(name = "code") String code) throws Exception {
        FaceLoginResult faceLoginResult = faceLoginService.qrCodeCheck(code);
        return Result.success(faceLoginResult);
    }

    /**
     * 人脸登录：根据落地页随机拍摄的面部头像进行登录
     * 根据拍摄的图片调用百度云AI进行检索查找
     */
    @RequestMapping(value = "/{code}", method = RequestMethod.POST)
    public Result<Void> loginByFace(@PathVariable(name = "code") String code, @RequestParam(name = "file") MultipartFile attachment) throws Exception {
        faceLoginService.loginByFace(code, attachment);
        return Result.success();
    }


    /**
     * 图像检测，判断图片中是否存在面部头像
     */
    @RequestMapping(value = "/checkFace", method = RequestMethod.POST)
    public Result<Void> checkFace(@RequestParam(name = "file") MultipartFile attachment) throws Exception {
        faceLoginService.checkFace(attachment);
        return Result.success();
    }
}
