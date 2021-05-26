package top.luhancc.gulimall.search.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.alibaba.nacos.common.utils.MapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.luhancc.common.to.search.ProductEsModule;
import top.luhancc.gulimall.search.config.ElasticSearchConfig;
import top.luhancc.gulimall.search.domain.constant.EsConstant;
import top.luhancc.gulimall.search.domain.vo.web.SearchParam;
import top.luhancc.gulimall.search.domain.vo.web.SearchResult;
import top.luhancc.gulimall.search.service.SearchService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author luHan
 * @create 2021/1/7 17:31
 * @since 1.0.0
 */
@Service
@Slf4j
public class SearchServiceImpl implements SearchService {
    @Autowired
    private RestHighLevelClient esClient;

    @Override
    public SearchResult search(SearchParam searchParam) {
        SearchRequest searchRequest = buildSearchRequest(searchParam);
        try {
            SearchResponse searchResponse = esClient.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);
            return buildSearchResult(searchResponse, searchParam);
        } catch (IOException e) {
            log.warn("ES查询失败:", e);
        }
        return new SearchResult(searchParam);
    }

    /**
     * 通过SearchParam查询条件构建DSL查询语句
     *
     * @param searchParam 查询条件
     * @return SearchRequest其中带查询的索引和DSL语句
     */
    private SearchRequest buildSearchRequest(SearchParam searchParam) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();// 构建DSL语句的Builder

        //================================1. 检索过滤条件====================================

        // 涉及到多个条件的组合查询,所以使用bool方式来进行复合查询
        BoolQueryBuilder bool = QueryBuilders.boolQuery();
        // sku标题的全文检索:使用must方式
        if (StringUtils.isNotEmpty(searchParam.getKeyword())) {
            bool.must(QueryBuilders.matchQuery("skuTitle", searchParam.getKeyword()));
        }
        /*
        过滤条件,使用filter方式：属性啊、品牌、分类啊、价格区间啊、库存等等
         */
        // 1.1 过滤分类
        if (searchParam.getCatalog3Id() != null) {
            bool.filter(QueryBuilders.termQuery("catalogId", searchParam.getCatalog3Id()));
        }
        // 1.2 过滤品牌
        if (CollectionUtils.isNotEmpty(searchParam.getBrandId())) {
            bool.filter(QueryBuilders.termsQuery("brandId", searchParam.getBrandId()));
        }
        // 1.3 过滤属性
        if (CollectionUtils.isNotEmpty(searchParam.getAttrs())) {
            List<String> attrs = searchParam.getAttrs();
            for (String attr : attrs) {
                String[] s = attr.split("_");
                if (s.length == 2) {
                    BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
                    String attrId = s[0];
                    String[] attrValues = s[1].split(":");
                    nestedBoolQuery.must(QueryBuilders.termQuery("attr.attrId", attrId));
                    nestedBoolQuery.must(QueryBuilders.termsQuery("attr.attrValue", attrValues));

                    bool.filter(QueryBuilders.nestedQuery("attrs", null, ScoreMode.None));
                }
            }
        }
        // 1.4 过滤价格区间
        if (StringUtils.isNotEmpty(searchParam.getSkuPrice())) {
            // skuPrice=100_500 价格在100到500之间
            // skuPrice=_500 价格在小于等于500
            // skuPrice=500_ 价格在大于等于500
            String skuPrice = searchParam.getSkuPrice();
            String[] skuPrices = skuPrice.split("_");
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            boolean isAppend = false;
            if (skuPrices.length == 2) {
                rangeQuery.gte(skuPrices[0]).lte(skuPrices[1]);
                isAppend = true;
            } else if (skuPrices.length == 1) {
                if (skuPrice.startsWith("_")) {
                    rangeQuery.lte(skuPrices[0]);
                    isAppend = true;
                } else if (skuPrice.endsWith("_")) {
                    rangeQuery.gte(skuPrices[0]);
                    isAppend = true;
                }
            }
            if (isAppend) {
                bool.filter(rangeQuery);
            } else {
                log.warn("价格区间参数[{}]的格式错误,请按照指定格式[{{minPrice}}_{{maxPrice}}/_{{maxPrice}}/{{minPrice}}_]进行拼接传递", skuPrice);
            }
        }
        // 1.5 过滤库存
        if (searchParam.getHasStock() != null) {
            bool.filter(QueryBuilders.termsQuery("hashStock", searchParam.getHasStock() == 1));
        }
        sourceBuilder.query(bool);

        //================================2. 排序、分页、高亮====================================

        // 2.1 排序
        if (StringUtils.isNotEmpty(searchParam.getSort())) {
            String[] s = searchParam.getSort().split("_");
            if (s.length == 2) {
                String sortField = s[0];
                String sortOrder = s[1];
                sourceBuilder.sort(sortField, SortOrder.fromString(sortOrder));
            }
        }
        // 2.2 分页
        if (searchParam.getSize() != null) {
            sourceBuilder.size(searchParam.getSize());
        } else {
            sourceBuilder.size(EsConstant.DEFAULT_PAGE_SIZE);
        }
        int pageNum;
        if (searchParam.getPageNum() != null) {
            pageNum = searchParam.getPageNum();
        } else {
            pageNum = EsConstant.DEFAULT_PAGE_NUM;
        }
        sourceBuilder.from((pageNum - 1) * sourceBuilder.size());
        // 2.3 高亮
        if (StringUtils.isNotEmpty(searchParam.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'");
            highlightBuilder.postTags("</b>");
            sourceBuilder.highlighter(highlightBuilder);
        }

        //================================3. 聚合分析====================================

        // 3. 聚合分析
        // 3.1 聚合品牌
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms(EsConstant.AggNameConstant.BRAND_AGG);
        brandAgg.field("brandId").size(1000);// TODO 这个size最好是数据库中所有品牌的数量
        // 3.1.1 子聚合品牌的名称
        TermsAggregationBuilder brandNameAgg = AggregationBuilders.terms(EsConstant.AggNameConstant.BRAND_NAME_AGG);
        brandNameAgg.field("brandName").size(1);// 因为一个品牌id只可能对应一个名称,所以这里size设置为1
        brandAgg.subAggregation(brandNameAgg);
        // 3.1.2 子聚合品牌的图片
        TermsAggregationBuilder brandImgAgg = AggregationBuilders.terms(EsConstant.AggNameConstant.BRAND_IMG_AGG);
        brandImgAgg.field("brandImg").size(1);// 因为一个品牌id只可能对应一个品牌logo,所以这里size设置为1
        brandAgg.subAggregation(brandImgAgg);
        sourceBuilder.aggregation(brandAgg);

        // 3.2 聚合分类
        TermsAggregationBuilder catalogAgg = AggregationBuilders.terms(EsConstant.AggNameConstant.CATALOG_AGG);
        catalogAgg.field("catalogId").size(100);// TODO 这个size最好是数据库中所有的分类数量
        // 3.2.1 子聚合分类的名称
        TermsAggregationBuilder catalogNameAgg = AggregationBuilders.terms(EsConstant.AggNameConstant.CATALOG_NAME_AGG);
        catalogNameAgg.field("catalogName").size(1);// size设置为1理由同上
        catalogAgg.subAggregation(catalogNameAgg);
        sourceBuilder.aggregation(catalogAgg);

        // 3.3 聚合属性
        NestedAggregationBuilder nestedAgg = AggregationBuilders.nested(EsConstant.AggNameConstant.ATTR_AGG, "attrs");
        // 3.3.1 因为是nested的聚合,所以先聚合属性的id
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms(EsConstant.AggNameConstant.ATTR_ID_AGG);
        attrIdAgg.field("attrs.attrId").size(1000);// TODO 这个size最好是数据库中所有的属性数量
        nestedAgg.subAggregation(attrIdAgg);
        // 3.3.2 再聚合属性的名称
        TermsAggregationBuilder attrNameAgg = AggregationBuilders.terms(EsConstant.AggNameConstant.ATTR_NAME_AGG);
        attrNameAgg.field("attrs.attrName").size(1);// size设置为1理由同上
        attrIdAgg.subAggregation(attrNameAgg);
        // 3.3.3 再聚合属性的值
        TermsAggregationBuilder attrValueAgg = AggregationBuilders.terms(EsConstant.AggNameConstant.ATTR_VALUE_AGG);
        attrValueAgg.field("attrs.attrValue").size(1000);// TODO 这个size最好是数据库中所有的属性值数量
        attrIdAgg.subAggregation(attrValueAgg);
        sourceBuilder.aggregation(nestedAgg);

        log.info("构建的DSL语句:{}", sourceBuilder.toString());
        return new SearchRequest(new String[]{EsConstant.IndexConstant.PRODUCT_INDEX}, sourceBuilder);
    }

    /**
     * 解析SearchResponse封装返回数据到SearchResult中
     *
     * @param searchResponse 从es中检索的结果
     * @param searchParam    检索条件
     * @return SearchResult
     */
    private SearchResult buildSearchResult(SearchResponse searchResponse, SearchParam searchParam) {
        SearchResult searchResult = new SearchResult(searchParam);
        SearchHits searchHits = searchResponse.getHits();
        //================================商品数据从hits中获取====================================
        // 1. 封装返回的查询到的商品
        SearchHit[] hits = searchHits.getHits();
        if (ArrayUtils.isNotEmpty(hits)) {
            List<ProductEsModule> products = new ArrayList<>();
            for (SearchHit hit : hits) {
                String sourceAsString = hit.getSourceAsString();
                ProductEsModule productEsModule = JSONObject.parseObject(sourceAsString, ProductEsModule.class);
                // 处理高亮
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                if (MapUtils.isNotEmpty(highlightFields)) {
                    HighlightField skuTitleField = highlightFields.get("skuTitle");
                    String skuTitleHighlightText = skuTitleField.getFragments()[0].string();
                    productEsModule.setSkuTitle(skuTitleHighlightText);
                }
                products.add(productEsModule);
            }
            searchResult.setProducts(products);
        }

        //================================以下数据从聚合数据从获取====================================
        Aggregations aggregations = searchResponse.getAggregations();
        // 2. 封装查询出来的商品涉及到的所有属性信息
        ParsedNested attrNested = aggregations.get(EsConstant.AggNameConstant.ATTR_AGG);
        ParsedLongTerms attrIdTerms = attrNested.getAggregations().get(EsConstant.AggNameConstant.ATTR_ID_AGG);
        List<? extends Terms.Bucket> attrIdBuckets = attrIdTerms.getBuckets();
        if (CollectionUtils.isNotEmpty(attrIdBuckets)) {
            List<SearchResult.AttrVo> attrVoList = new ArrayList<>();
            for (Terms.Bucket attrIdBucket : attrIdBuckets) {
                SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
                // 属性id
                attrVo.setAttrId(attrIdBucket.getKeyAsNumber().longValue());
                // 属性名称
                ParsedStringTerms attrNameTerms = attrIdBucket.getAggregations().get(EsConstant.AggNameConstant.ATTR_NAME_AGG);
                attrVo.setAttrName(attrNameTerms.getBuckets().get(0).getKeyAsString());
                // 属性值
                ParsedStringTerms attrValueTerms = attrIdBucket.getAggregations().get(EsConstant.AggNameConstant.ATTR_VALUE_AGG);
                List<? extends Terms.Bucket> attrValueBuckets = attrValueTerms.getBuckets();
                attrVo.setAttrValue(attrValueBuckets.stream().map(MultiBucketsAggregation.Bucket::getKeyAsString).collect(Collectors.toList()));
                attrVoList.add(attrVo);
            }
            searchResult.setAttrs(attrVoList);
        }
        // 3. 封装查询出来的商品涉及到的所有品牌信息
        ParsedLongTerms brandTerms = aggregations.get(EsConstant.AggNameConstant.BRAND_AGG);
        List<? extends Terms.Bucket> brandBuckets = brandTerms.getBuckets();
        if (CollectionUtils.isNotEmpty(brandBuckets)) {
            List<SearchResult.BrandVo> brandVoList = new ArrayList<>();
            for (Terms.Bucket brandBucket : brandBuckets) {
                SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
                // 品牌id
                brandVo.setBrandId(brandBucket.getKeyAsNumber().longValue());
                // 品牌的名称
                ParsedStringTerms brandNameTerms = brandBucket.getAggregations().get(EsConstant.AggNameConstant.BRAND_NAME_AGG);
                brandVo.setBrandName(brandNameTerms.getBuckets().get(0).getKeyAsString());
                // 品牌的图片
                ParsedStringTerms brandImgTerms = brandBucket.getAggregations().get(EsConstant.AggNameConstant.BRAND_IMG_AGG);
                brandVo.setBrandImg(brandImgTerms.getBuckets().get(0).getKeyAsString());
                brandVoList.add(brandVo);
            }
            searchResult.setBrands(brandVoList);
        }
        // 4. 封装查询出来的商品涉及到的所有分类信息
        ParsedLongTerms catalogTerms = aggregations.get(EsConstant.AggNameConstant.CATALOG_AGG);
        List<? extends Terms.Bucket> catalogBuckets = catalogTerms.getBuckets();
        if (CollectionUtils.isNotEmpty(catalogBuckets)) {
            List<SearchResult.CatalogVo> catalogVoList = new ArrayList<>();
            for (Terms.Bucket bucket : catalogBuckets) {
                SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
                // 分类id
                catalogVo.setCatalogId(bucket.getKeyAsNumber().longValue());
                // 分类名称
                ParsedStringTerms catalogNameTerms = bucket.getAggregations().get(EsConstant.AggNameConstant.CATALOG_NAME_AGG);
                catalogVo.setCatalogName(catalogNameTerms.getBuckets().get(0).getKeyAsString());
                catalogVoList.add(catalogVo);
            }
            searchResult.setCatalogs(catalogVoList);
        }

        //================================分页数据====================================
        // 5. 封装分页信息
        searchResult.setPageNum(searchParam.getPageNum());
        long total = searchHits.getTotalHits().value;
        searchResult.setTotal(total);
        int totalPage = total % searchParam.getSize() == 0 ? ((int) total / searchParam.getSize()) : ((int) total / searchParam.getSize()) + 1;
        searchResult.setTotalPage(totalPage);

        //================================面包屑导航数据====================================

        // 1. 处理属性的面包屑导航
        List<String> attrs = searchParam.getAttrs();
        List<SearchResult.NavVo> navs = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(attrs)) {
            List<SearchResult.NavVo> attrNavs = attrs.stream().map(attr -> {
                SearchResult.NavVo navVo = new SearchResult.NavVo();

                String[] s = attr.split("_");
                Long attrId = Long.parseLong(s[0]);
                String value = s[1];

                navVo.setNavName(attrId + "");//TODO 远程查询获取属性名称
                navVo.setNavValue(value);
                // 处理跳转链接
                String queryString = searchParam.get_queryString();
                try {
                    // 需要对attr进行编码处理(因为会存在中文,需要编码处理)
                    String newAttr = URLEncoder.encode(attr, "UTF-8");
                    newAttr = newAttr.replace("+", "%20");// 因为URLEncoder编码后会把空格编码为+号,但是前段是将空格编码为%20,所以这里需要特殊处理一下
                    queryString = queryString.replace("&attrs=" + newAttr, "");
                    navVo.setLink("http://search.gulimall.com/list.html?" + queryString);
                } catch (UnsupportedEncodingException e) {
                    log.warn("对attr编码失败,", e);
                }
                return navVo;
            }).collect(Collectors.toList());
            navs.addAll(attrNavs);
        }
        // 2. 处理品牌的面包屑导航
        List<Long> brandIdList = searchParam.getBrandId();
        if (CollectionUtils.isNotEmpty(brandIdList)) {
            List<SearchResult.NavVo> brandNavs = brandIdList.stream().map(brandId -> {
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                navVo.setNavName("品牌");
                navVo.setNavValue(brandIdList + "");// TODO 远程查询品牌的名称
                String queryString = searchParam.get_queryString();
                // 从请求参数中将当前这个品牌id的参数移除
                queryString = queryString.replace("&brandId=" + brandId, "");
                navVo.setLink("http://search.gulimall.com/list.html?" + queryString);
                return navVo;
            }).collect(Collectors.toList());
            navs.addAll(brandNavs);
        }
        // 3. TODO 处理分类的面包屑导航

        searchResult.setNavs(navs);
        return searchResult;
    }
}
