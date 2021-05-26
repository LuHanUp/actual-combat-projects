package top.luhancc.gulimall.thirdparty.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.luhancc.common.utils.R;
import top.luhancc.gulimall.thirdparty.config.OssConfiguration;
import top.luhancc.gulimall.thirdparty.domain.vo.OssPolicyVo;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author luHan
 * @create 2020/12/10 14:14
 * @since 1.0.0
 */
@RestController
@RequestMapping("oss")
@Slf4j
public class OssController {
    @Autowired
    private OSS ossClient;
    @Autowired
    private OssConfiguration ossConfiguration;

    @RequestMapping("/policy")
    public R policy() {
        String host = "https://" + ossConfiguration.getBucketName() + "." + ossConfiguration.getEndpoint(); // host的格式为 bucketname.endpoint
        // callbackUrl为 上传回调服务器的URL，请将下面的IP和Port配置为您自己的真实信息。
//        String callbackUrl = "http://88.88.88.88:8888";
        String dir = new SimpleDateFormat("yyyy-MM-dd").format(new Date());// 用户上传文件时指定的前缀。
        try {
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            // PostObject请求最大可支持的文件大小为5 GB，即CONTENT_LENGTH_RANGE为5*1024*1024*1024。
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            OssPolicyVo ossPolicyVo = new OssPolicyVo();
            ossPolicyVo.setAccessId(ossConfiguration.getAccessId());
            ossPolicyVo.setPolicy(encodedPolicy);
            ossPolicyVo.setSignature(postSignature);
            ossPolicyVo.setDir(dir);
            ossPolicyVo.setHost(host);
            ossPolicyVo.setExpire(String.valueOf(expireEndTime / 1000));

            return R.ok().put("data", ossPolicyVo);
        } catch (Exception e) {
            log.error("获取上传文件签名失败,", e);
            return R.error(e.getMessage());
        } finally {
            ossClient.shutdown();
        }
    }
}
