package top.luhancc.saas.hrm.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.luhancc.saas.hrm.common.model.system.response.FaceLoginResult;
import top.luhancc.saas.hrm.system.service.FaceLoginService;
import top.luhancc.saas.hrm.system.thirdservice.FaceService;

/**
 * @author luHan
 * @create 2021/5/21 14:10
 * @since 1.0.0
 */
@Service
public class FaceLoginServiceImpl implements FaceLoginService {
    @Value("${faceLogin.qrCodeUrl}")
    private String faceLoginUrl;

    @Autowired
    @Qualifier("baidu-face")
    private FaceService faceService;

    @Override
    public String qrCode() {
        return null;
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
