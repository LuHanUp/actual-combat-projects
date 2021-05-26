package top.luhancc.gulimall.search.config;

import lombok.Data;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * elasticSearch config
 * <p>
 * 参照文档：https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.x/index.html
 *
 * @author luHan
 * @create 2021/1/4 19:01
 * @since 1.0.0
 */
@Configuration
@ConfigurationProperties(prefix = "elasticsearch")
@Data
public class ElasticSearchConfig {
    private List<String> address;

    public static final RequestOptions COMMON_OPTIONS;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
//        builder.addHeader("Authorization", "Bearer " + TOKEN);
//        builder.setHttpAsyncResponseConsumerFactory(
//                new HttpAsyncResponseConsumerFactory
//                        .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }

    @Bean
    public RestHighLevelClient esClient() {
        HttpHost[] httpHosts = new HttpHost[address.size()];
        for (int i = 0; i < address.size(); i++) {
            String s = address.get(i);
            String[] url = s.split(":");
            String host = url[0];
            Integer port = Integer.parseInt(url[1]);
            httpHosts[i] = new HttpHost(host, port, "http");
        }
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(httpHosts));
        return client;
    }
}
