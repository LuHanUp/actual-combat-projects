package top.luhancc.saas.hrm.system.thirdservice.baidu.faceservice.impl;

import com.baidu.aip.face.AipFace;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import top.luhancc.saas.hrm.system.thirdservice.FaceService;
import top.luhancc.saas.hrm.system.thirdservice.baidu.faceservice.config.BaiduFaceProperties;

import java.util.HashMap;

/**
 * 百度人脸识别服务
 * <p>
 * 文档参考:https://cloud.baidu.com/doc/FACE/s/8k37c1rqz
 *
 * @author luHan
 * @create 2021/5/21 13:46
 * @since 1.0.0
 */
@Service("baidu-face")
@Slf4j
public class BaiduFaceServiceImpl implements FaceService {
    private final AipFace aipFace;
    private final BaiduFaceProperties faceProperties;
    private HashMap<String, String> options = new HashMap<String, String>();

    public BaiduFaceServiceImpl(AipFace aipFace, BaiduFaceProperties faceProperties) {
        this.aipFace = aipFace;
        this.faceProperties = faceProperties;
        options.put("quality_control", "NORMAL");
        options.put("liveness_control", "LOW");
    }

    @Override
    public boolean faceExists(String userId) {
        JSONObject res = aipFace.getUser(userId, faceProperties.getGroupId(), null);
        log.info("调用百度云人脸获取用户服务返回结果:{}", res);
        int errorCode = res.getInt("error_code");
        return errorCode == 0;
    }

    /**
     * 人脸注册 ：将用户照片存入人脸库中
     */
    @Override
    public boolean faceRegister(String userId, String image) {
        // 人脸注册
        JSONObject res = aipFace.addUser(image, faceProperties.getImageType(), faceProperties.getGroupId(), userId, options);
        log.info("调用百度云人脸注册服务返回结果:{}", res);
        int errorCode = res.getInt("error_code");
        return errorCode == 0;
    }

    /**
     * 人脸更新 ：更新人脸库中的用户照片
     */
    @Override
    public boolean faceUpdate(String userId, String image) {
        // 人脸更新
        JSONObject res = aipFace.updateUser(image, faceProperties.getImageType(), faceProperties.getGroupId(), userId, options);
        log.info("调用百度云人脸更新服务返回结果:{}", res);
        int errorCode = res.getInt("error_code");
        return errorCode == 0;
    }

    /**
     * 人脸检测：判断上传图片中是否具有面部头像
     */
    @Override
    public boolean faceCheck(String image) {
        JSONObject res = aipFace.detect(image, faceProperties.getImageType(), options);
        log.info("调用百度云人脸检测服务返回结果:{}", res);
        if (res.has("error_code") && res.getInt("error_code") == 0) {
            JSONObject resultObject = res.getJSONObject("result");
            int faceNum = resultObject.getInt("face_num");
            return faceNum == 1;
        } else {
            return false;
        }
    }

    /**
     * 人脸查找：查找人脸库中最相似的人脸并返回数据
     * 处理：用户的匹配得分（score）大于80分，即可认为是同一个用户
     */
    @Override
    public String faceSearch(String image) {
        JSONObject res = aipFace.search(image, faceProperties.getImageType(), faceProperties.getGroupId(), options);
        log.info("调用百度云人脸查找服务返回结果:{}", res);
        if (res.has("error_code") && res.getInt("error_code") == 0) {
            JSONObject result = res.getJSONObject("result");
            JSONArray userList = result.getJSONArray("user_list");
            if (userList.length() > 0) {
                JSONObject user = userList.getJSONObject(0);
                double score = user.getDouble("score");
                if (score > 80) {
                    // 当分数是大于80分的才认为是同一个用户
                    return user.getString("user_id");
                }
            }
        }
        return null;
    }
}
