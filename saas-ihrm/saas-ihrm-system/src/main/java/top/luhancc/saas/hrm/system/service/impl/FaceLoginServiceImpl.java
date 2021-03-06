package top.luhancc.saas.hrm.system.service.impl;

import com.baidu.aip.util.Base64Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.luhancc.hrm.common.domain.Result;
import top.luhancc.hrm.common.domain.ResultCode;
import top.luhancc.hrm.common.exception.BaseBusinessException;
import top.luhancc.hrm.common.utils.IdWorker;
import top.luhancc.hrm.common.utils.QRCodeUtil;
import top.luhancc.saas.hrm.common.model.system.User;
import top.luhancc.saas.hrm.common.model.system.response.FaceLoginResult;
import top.luhancc.saas.hrm.common.model.system.response.QRCode;
import top.luhancc.saas.hrm.system.controller.SystemController;
import top.luhancc.saas.hrm.system.domain.param.SocialLoginParam;
import top.luhancc.saas.hrm.system.service.FaceLoginService;
import top.luhancc.saas.hrm.system.service.UserService;
import top.luhancc.saas.hrm.system.thirdservice.FaceService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author luHan
 * @create 2021/5/21 14:10
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FaceLoginServiceImpl implements FaceLoginService {
    @Value("${faceLogin.qrCodeUrl}")
    private String faceLoginUrl;

    private final IdWorker idWorker;
    private final SystemController systemController;
    private final UserService userService;
    private final RedisTemplate<String, Object> redisTemplate;

    private final static String CODE_CACHE_KEY = "QRCode:code_%s";

    @Autowired
    @Qualifier("baidu-face")
    private FaceService faceService;

    @Override
    public QRCode qrCode() {
        String code = idWorker.nextId() + "";
        String content = faceLoginUrl + "?code=" + code;
        try {
            String qrCode = QRCodeUtil.createQRCode(content);
            // ??????????????????????????????redis???
            FaceLoginResult faceLoginResult = new FaceLoginResult(-1);
            redisTemplate.boundValueOps(String.format(CODE_CACHE_KEY, code)).set(faceLoginResult, 10, TimeUnit.MINUTES);
            return new QRCode(code, qrCode);
        } catch (Exception e) {
            log.error("?????????????????????,", e);
            throw new BaseBusinessException(ResultCode.CREATE_QRCODE_ERROR);
        }
    }

    @Override
    public FaceLoginResult qrCodeCheck(String code) {
        FaceLoginResult faceLoginResult = (FaceLoginResult) redisTemplate.opsForValue().get(String.format(CODE_CACHE_KEY, code));
        return faceLoginResult == null ? new FaceLoginResult(0) : faceLoginResult;
    }

    @Override
    public FaceLoginResult loginByFace(String code, MultipartFile attachment) {
        try {
            FaceLoginResult faceLoginResult = (FaceLoginResult) redisTemplate.opsForValue().get(String.format(CODE_CACHE_KEY, code));
            if (faceLoginResult == null) {
                throw new BaseBusinessException(ResultCode.RELOGIN_QR_ERROR);
            } else {
                if (faceLoginResult.getState() == 0) {
                    throw new BaseBusinessException(ResultCode.RELOGIN_QR_ERROR);
                } else if (faceLoginResult.getState() == 1) {
                    return faceLoginResult;
                }
            }
            String image = Base64Util.encode(attachment.getBytes());
            String userId = faceService.faceSearch(image);
            if (StringUtils.isNotEmpty(userId)) {
                // ????????????,??????token
                User user = userService.findById(userId);
                SocialLoginParam loginParam = new SocialLoginParam();
                loginParam.setMobile(user.getMobile());
                Result<String> tokenRes = systemController.socialLogin(loginParam);
                if (tokenRes.isSuccess()) {
                    String token = tokenRes.getData();
                    if (StringUtils.isNotEmpty(token)) {
                        // ???????????????,?????????????????????
                        faceLoginResult.setUserId(userId);
                        faceLoginResult.setToken(token);
                        faceLoginResult.setState(1);
                        redisTemplate.boundValueOps(String.format(CODE_CACHE_KEY, code)).set(faceLoginResult, 1, TimeUnit.MILLISECONDS);
                        return faceLoginResult;
                    }
                }
            }
            throw new BaseBusinessException(ResultCode.LOGIN_ERROR);
        } catch (IOException e) {
            log.error("??????????????????,", e);
            throw new BaseBusinessException(ResultCode.LOGIN_ERROR);
        }
    }

    @Override
    public boolean checkFace(MultipartFile attachment) {
        try {
            String image = Base64Util.encode(attachment.getBytes());
            return faceService.faceCheck(image);
        } catch (IOException e) {
            log.error("??????????????????,", e);
            throw new BaseBusinessException(ResultCode.SERVER_ERROR);
        }
    }
}
