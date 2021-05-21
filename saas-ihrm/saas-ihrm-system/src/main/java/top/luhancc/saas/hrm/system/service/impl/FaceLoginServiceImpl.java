package top.luhancc.saas.hrm.system.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.luhancc.hrm.common.domain.ResultCode;
import top.luhancc.hrm.common.exception.BaseBusinessException;
import top.luhancc.hrm.common.utils.IdWorker;
import top.luhancc.hrm.common.utils.QRCodeUtil;
import top.luhancc.saas.hrm.common.model.system.response.FaceLoginResult;
import top.luhancc.saas.hrm.common.model.system.response.QRCode;
import top.luhancc.saas.hrm.system.service.FaceLoginService;
import top.luhancc.saas.hrm.system.thirdservice.FaceService;

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
            // 将当前二维码状态存入redis中
            FaceLoginResult faceLoginResult = new FaceLoginResult(-1);
            redisTemplate.boundValueOps(String.format(CODE_CACHE_KEY, code)).set(faceLoginResult, 10, TimeUnit.MINUTES);
            return new QRCode(code, qrCode);
        } catch (Exception e) {
            log.error("生成二维码失败,", e);
            throw new BaseBusinessException(ResultCode.CREATE_QRCODE_ERROR);
        }
    }

    @Override
    public FaceLoginResult qrCodeCheck(String code) {
        return null;
    }

    @Override
    public void loginByFace(String code, MultipartFile attachment) {

    }

    @Override
    public void checkFace(MultipartFile attachment) {

    }
}
