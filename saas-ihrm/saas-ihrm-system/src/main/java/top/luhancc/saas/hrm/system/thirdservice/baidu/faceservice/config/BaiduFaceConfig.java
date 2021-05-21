package top.luhancc.saas.hrm.system.thirdservice.baidu.faceservice.config;

import com.baidu.aip.face.AipFace;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 百度人脸识别配置类
 *
 * @author luHan
 * @create 2021/5/21 13:48
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties({BaiduFaceProperties.class})
@RequiredArgsConstructor
public class BaiduFaceConfig {
    private final BaiduFaceProperties faceProperties;

    @Bean
    public AipFace aipFace() {
        return new AipFace(faceProperties.getAppId(), faceProperties.getApiKey(), faceProperties.getSecretKey());
    }
}
