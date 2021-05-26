package top.luhancc.gulimall.search;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.luhancc.gulimall.search.config.ElasticSearchConfig;

import java.io.IOException;

/**
 * @author luHan
 * @create 2021/1/4 19:19
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class SearchTest {
    @Autowired
    private RestHighLevelClient esClient;

    @Test
    public void testInsertData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");
        User user = new User();
        user.setUserName("zhansan");
        user.setGender("F");
        user.setAge(23);
        indexRequest.source(JSON.toJSONString(user), XContentType.JSON);

        IndexResponse indexResponse = esClient.index(indexRequest, ElasticSearchConfig.COMMON_OPTIONS);
        System.out.println(indexResponse);
    }

    @Test
    public void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest("bank");
        // 构建DSl
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource()
                .query(QueryBuilders.matchQuery("address", "mill"))
                // 计算年龄分布
                .aggregation(AggregationBuilders.terms("ageTerm").field("age"))
                // 计算平均薪资
                .aggregation(AggregationBuilders.avg("balanceAvg").field("balance"));

        System.out.println("检索条件:" + searchSourceBuilder);
        searchRequest.source(searchSourceBuilder);
        // 执行检索
        SearchResponse searchResponse = esClient.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);
        System.out.println("检索结果:" + searchResponse);

        // 获取检索到的数据
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            String source = hit.getSourceAsString();
            Account account = JSON.parseObject(source, Account.class);
            System.out.println("数据:" + account);
        }

        // 获取分析的结果
        Aggregations aggregations = searchResponse.getAggregations();
        Terms ageTerm = aggregations.get("ageTerm");
        for (Terms.Bucket bucket : ageTerm.getBuckets()) {
            System.out.println("年龄分布:" + bucket.getKeyAsString());
        }
        Avg balanceAvg = aggregations.get("balanceAvg");
        System.out.println("平均薪资:" + balanceAvg.getValue());
    }

    @After
    public void clean() throws IOException {
        esClient.close();
    }

    @Data
    public static class User {
        private String userName;
        private String gender;
        private Integer age;
    }

    @Data
    public static class Account {
        private Integer accountNumber;
        private Integer balance;
        private String firstname;
        private String lastname;
        private Integer age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;
    }
}
