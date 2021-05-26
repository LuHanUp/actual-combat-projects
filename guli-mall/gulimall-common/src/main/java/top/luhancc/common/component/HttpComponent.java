package top.luhancc.common.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * http 请求的组件类
 *
 * @author luHan
 * @create 2021/1/12 11:22
 * @since 1.0.0
 */
@Component
public class HttpComponent {
    @Autowired
    private RestTemplate restTemplate;

    /**
     * post请求
     *
     * @param url          请求地址
     * @param uriVariable  请求参数
     * @param responseType 请求结果响应类型
     * @return {@code ResponseEntity<T>}
     */
    public <T> ResponseEntity<T> post(String url, MultiValueMap<String, String> uriVariable, Class<T> responseType) {
        HttpEntity<Object> entity = new HttpEntity<>(uriVariable, null);
        return restTemplate.postForEntity(url, entity, responseType);
    }

    /**
     * get请求
     *
     * @param url          请求地址
     * @param uriVariable  请求参数
     * @param responseType 请求结果响应类型
     * @return {@code ResponseEntity<T>}
     */
    public <T> ResponseEntity<T> get(String url, Map<String, String> uriVariable, Class<T> responseType) {
        StringBuffer sb = new StringBuffer();
        if (!CollectionUtils.isEmpty(uriVariable)) {
            sb.append("?");
            uriVariable.forEach((key, value) -> {
                sb.append(key).append("=").append(value).append("&");
            });
            sb.deleteCharAt(sb.length() - 1);
            url += sb.toString();
        }
        return restTemplate.getForEntity(url, responseType);
    }
}
