package top.luhancc.gulimall.search.controller;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import top.luhancc.common.to.search.ProductEsModule;
import top.luhancc.gulimall.search.config.ElasticSearchConfig;

import java.io.IOException;
import java.util.List;

/**
 * 检索服务
 *
 * @author luHan
 * @create 2021/1/5 16:51
 * @since 1.0.0
 */
@RestController
@RequestMapping("/search")
public class SearchController {
    @Autowired
    private RestHighLevelClient esClient;

    /**
     * 将数据保存到ES中
     *
     * @param productEsModuleList 需要保存的数据
     * @throws IOException
     */
    @RequestMapping(value = "/saveData", method = RequestMethod.POST)
    public void saveData(List<ProductEsModule> productEsModuleList) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        for (ProductEsModule productEsModule : productEsModuleList) {
            IndexRequest indexRequest = new IndexRequest("product");
            indexRequest.id(productEsModule.getSkuId().toString());
            indexRequest.source(JSON.toJSONString(productEsModule), XContentType.JSON);

            bulkRequest.add(indexRequest);
        }
        BulkResponse bulkResponse = esClient.bulk(bulkRequest, ElasticSearchConfig.COMMON_OPTIONS);
        //TODO 如果批量保存存在错误,那么就进行处理
    }
}
