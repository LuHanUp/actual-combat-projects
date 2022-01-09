package top.luahncc.payment.service.impl;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import top.luahncc.payment.config.WxPayConfig;
import top.luahncc.payment.domain.enums.OrderStatus;
import top.luahncc.payment.domain.enums.wxpay.WxApiType;
import top.luahncc.payment.domain.enums.wxpay.WxNotifyType;
import top.luahncc.payment.entity.OrderInfo;
import top.luahncc.payment.service.WxPayService;
import top.luahncc.payment.utils.OrderNoUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author luHan
 * @create 2022/1/9 21:03
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WxPayServiceImpl implements WxPayService {
    private final WxPayConfig wxPayConfig;
    private final CloseableHttpClient httpClient;

    @SneakyThrows
    @Override
    public Map<String, Object> nativePay(Long productId) {
        // TODO 临时创建订单对象
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setTitle("test");
        orderInfo.setOrderNo(OrderNoUtils.getOrderNo());
        orderInfo.setUserId(123456L);
        orderInfo.setProductId(productId);
        orderInfo.setTotalFee(1);
        orderInfo.setOrderStatus(OrderStatus.NOTPAY.getType());
        orderInfo.setCreateTime(new Date());
        orderInfo.setUpdateTime(new Date());

        // TODO 保存订单信息

        // 调用native下单接口生成支付二维码
        HttpPost httpPost = new HttpPost(wxPayConfig.getDomain().concat(WxApiType.NATIVE_PAY.getType()));
        // 请求body参数
        String reqdata = "";/*"{"
                + "\"time_expire\":\"2018-06-08T10:34:56+08:00\","
                + "\"amount\": {"
                + "\"total\":100,"
                + "\"currency\":\"CNY\""
                + "},"
                + "\"mchid\":\"1230000109\","
                + "\"description\":\"Image形象店-深圳腾大-QQ公仔\","
                + "\"notify_url\":\"https://www.weixin.qq.com/wxpay/pay.php\","
                + "\"out_trade_no\":\"1217752501201407033233368018\","
                + "\"goods_tag\":\"WXG\","
                + "\"appid\":\"wxd678efh567hg6787\","
                + "\"attach\":\"自定义数据说明\","
                + "\"detail\": {"
                + "\"invoice_id\":\"wx123\","
                + "\"goods_detail\": ["
                + "{"
                + "\"goods_name\":\"iPhoneX 256G\","
                + "\"wechatpay_goods_id\":\"1001\","
                + "\"quantity\":1,"
                + "\"merchant_goods_id\":\"商品编码\","
                + "\"unit_price\":828800"
                + "},"
                + "{"
                + "\"goods_name\":\"iPhoneX 256G\","
                + "\"wechatpay_goods_id\":\"1001\","
                + "\"quantity\":1,"
                + "\"merchant_goods_id\":\"商品编码\","
                + "\"unit_price\":828800"
                + "}"
                + "],"
                + "\"cost_price\":608800"
                + "},"
                + "\"scene_info\": {"
                + "\"store_info\": {"
                + "\"address\":\"广东省深圳市南山区科技中一道10000号\","
                + "\"area_code\":\"440305\","
                + "\"name\":\"腾讯大厦分店\","
                + "\"id\":\"0001\""
                + "},"
                + "\"device_id\":\"013467007045764\","
                + "\"payer_client_ip\":\"14.23.150.211\""
                + "}"
                + "}";*/
        Map<String, Object> paramMap = new HashMap<>(10);
        paramMap.put("appid", wxPayConfig.getAppid());
        paramMap.put("mchid", wxPayConfig.getMchId());
        paramMap.put("description", orderInfo.getTitle());
        paramMap.put("out_trade_no", orderInfo.getOrderNo());
        paramMap.put("notify_url", wxPayConfig.getNotifyDomain().concat(WxNotifyType.NATIVE_NOTIFY.getType()));
        Map<String, Object> amountMap = new HashMap<>(2);
        amountMap.put("total", orderInfo.getTotalFee());
        amountMap.put("currency", "CNY");
        paramMap.put("amount", amountMap);
        StringEntity entity = new StringEntity((reqdata = new Gson().toJson(paramMap)), "utf-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");

        //完成签名并执行请求
        CloseableHttpResponse response = httpClient.execute(httpPost);

        try {
            int statusCode = response.getStatusLine().getStatusCode();
            String bodyAsString = EntityUtils.toString(response.getEntity());
            if (statusCode == 200) { //处理成功
                log.info("success,return body = " + bodyAsString);
            } else if (statusCode == 204) { //处理成功，无返回Body
                log.info("success");
            } else {
                log.error("failed,resp code = " + statusCode + ",return body = " + bodyAsString);
                throw new RuntimeException("request failed");
            }
            HashMap<String, Object> resultMap = new Gson().fromJson(bodyAsString, HashMap.class);
            resultMap.put("orderNo", orderInfo.getOrderNo());
            resultMap.put("codeUrl", resultMap.get("code_url"));
            return resultMap;
        } finally {
            response.close();
        }
    }
}
