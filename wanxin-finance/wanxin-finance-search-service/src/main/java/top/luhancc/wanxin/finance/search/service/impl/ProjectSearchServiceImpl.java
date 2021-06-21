package top.luhancc.wanxin.finance.search.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.luhancc.wanxin.finance.common.domain.model.PageVO;
import top.luhancc.wanxin.finance.common.domain.model.search.ProjectQueryParamsDTO;
import top.luhancc.wanxin.finance.common.domain.model.transaction.ProjectDTO;
import top.luhancc.wanxin.finance.search.service.ProjectIndexService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author luHan
 * @create 2021/6/21 10:33
 * @since 1.0.0
 */
@Service
@Slf4j
public class ProjectSearchServiceImpl implements ProjectIndexService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Value("${es.index}")
    private String projectIndex;

    @Override
    public PageVO<ProjectDTO> queryProjectIndex(ProjectQueryParamsDTO paramsDTO, Integer pageNo,
                                                Integer pageSize, String sortBy, String order) {
        SearchRequest searchRequest = new SearchRequest(projectIndex);

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        // 封装查询条件
        if (StringUtils.isNotBlank(paramsDTO.getName())) {
            queryBuilder.must(QueryBuilders.termQuery("name", paramsDTO.getName()));
        }
        if (paramsDTO.getStartPeriod() != null) {// 起始期限，使用范围检索的>=
            queryBuilder.must(QueryBuilders.rangeQuery("period").gte(paramsDTO.getStartPeriod()));
        }
        if (paramsDTO.getEndPeriod() != null) {// 截止期限，使用范围检索的<=
            queryBuilder.must(QueryBuilders.rangeQuery("period").lte(paramsDTO.getStartPeriod()));
        }

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);

        // 设置排序
        if (StringUtils.isNotBlank(sortBy) && StringUtils.isNotBlank(order)) {
            if ("desc".equalsIgnoreCase(order)) {
                searchSourceBuilder.sort(sortBy, SortOrder.DESC);
            } else {
                searchSourceBuilder.sort(sortBy, SortOrder.ASC);
            }
        } else {
            searchSourceBuilder.sort("createdate", SortOrder.DESC);
        }
        // 设置分页
        searchSourceBuilder.from((pageNo - 1) * pageSize);
        searchSourceBuilder.size(pageSize);

        searchRequest.source(searchSourceBuilder);

        List<ProjectDTO> record = new ArrayList<>();
        PageVO<ProjectDTO> pageVO = new PageVO<>();

        // 执行搜索
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("检索执行失败", e);
        }
        if (searchResponse != null) {
            SearchHits searchHits = searchResponse.getHits();
            long total = searchHits.getTotalHits().value;
            pageVO.setTotal(total);
            // 封装返回DTO数据
            SearchHit[] hits = searchHits.getHits();
            for (SearchHit hit : hits) {
                ProjectDTO projectDTO = new ProjectDTO();
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                Double amount = (Double) sourceAsMap.get("amount");
                Double annualRate = (Double) sourceAsMap.get("annualrate");
                String projectStatus = (String) sourceAsMap.get("projectstatus");
                Integer period = Integer.parseInt(sourceAsMap.get("period").toString());
                String name = (String) sourceAsMap.get("name");
                String description = (String) sourceAsMap.get("description");
                projectDTO.setAmount(new BigDecimal(amount));
                projectDTO.setAnnualRate(new BigDecimal(annualRate));
                projectDTO.setProjectStatus(projectStatus);
                projectDTO.setPeriod(period);
                projectDTO.setName(name);
                projectDTO.setDescription(description);
                record.add(projectDTO);
            }
            pageVO.setContent(record);
        } else {
            pageVO.setContent(record);
            pageVO.setTotal(0L);
        }
        pageVO.setPageNo(pageNo);
        pageVO.setPageSize(pageSize);
        return pageVO;
    }
}
