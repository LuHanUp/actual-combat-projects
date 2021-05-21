package top.luhancc.saas.hrm.system.thirdservice;

/**
 * 人脸服务
 *
 * @author luHan
 * @create 2021/5/21 13:45
 * @since 1.0.0
 */
public interface FaceService {

    /**
     * 用户是否已经注册了人脸照片
     *
     * @param userId 用户id
     * @return
     */
    public boolean faceExists(String userId);

    /**
     * 人脸注册 ：将用户照片存入人脸库中
     *
     * @param userId 用户id
     * @param image  人脸图片
     * @return 注册成功还是失败
     */
    public boolean faceRegister(String userId, String image);

    /**
     * 人脸更新 ：更新人脸库中的用户照片
     *
     * @param userId 用户id
     * @param image  人脸图片
     * @return 更新成功还是失败
     */
    public boolean faceUpdate(String userId, String image);

    /**
     * 人脸检测：判断上传图片中是否具有面部头像
     *
     * @param image 人脸图片
     * @return true具有面部头像 false不具有面部头像
     */
    public boolean faceCheck(String image);

    /**
     * 人脸查找：查找人脸库中最相似的人脸并返回数据
     * 处理：用户的匹配得分（score）大于80分，即可认为是同一个用户
     *
     * @param image 人脸图片
     * @return 返回和image是同一个人的userId
     */
    public String faceSearch(String image);
}
