package top.luhancc.gulimall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeCloseModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import top.luhancc.gulimall.order.domain.order.vo.PayVo;

/**
 * @author luhan
 */
@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private String app_id = "2016100100639422";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private String merchant_private_key = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC5JvMPOe81XIjGqgEbbBEsfe8/V4yLyzNo+48b5WQuf7buF60zzS9UDsDyjwfRsUbPRIyMDvW8wc/ahI0pFfFIGfVmJXstDbHI7bae+jk97VpEoLf1RlSCy4pTbpJsqs3uEXCl+CSMUtrKnBGGHn4Q1g1xXBpGfjcymh1nLfpllqdbujeiaVhDSCeJkCpCk6ewauMN5meEs6NHtvbVlmRM1EtoVvVFgIxL+A2SKomLZN+C7uLAnO0tQqH6FPuBz55Ue8JmYgwIT4QPSHg8hlGethR+TAr15NKGyOAuTzGslgpHrizIJQMnBy7TZUGuIuZVAwXjQmHL8FvAPwZs0cavAgMBAAECggEARhbZghUTWabq3BG1E5VoNuiBbgANY0oL7xzzgsLmlregHyd1WJu+49ol7mx4sEekn4e6kLaFDexsa7Mr4La+vOWewQlFRLvfZkmejTX5/1hfiIUyvjXefhjhB05fPaW3CP0vIJT7+ObQjkxgUd4vKN5u/k+4XqEe8hfnM4zzMgsb8HT4S+HqIA+48rM/kTaZ8u5FT9ZGLEn7c4dD7uaNm8ViZZCsuXeDLjYg8sQiLGR8FWeXt09UtfBur36cCQrOcCvLuLLiusOq+LmtF8qQFguS0e+X3VXdQfgWNe9lfP2q7j2zGPLrggSZGrlBKaZohUagiQCtC+ZNFzRp8UCNKQKBgQD++9y5K758VolIh+riwoSWBFns14SsITcce8rCnAsU6nE0hGsZLeBCNBPrCz6Wphcf4OEXD/JtzPTwFj5XLhJX8Eyg3Ja/k2DrIv7tW480oNftamAnsTbIJ0IRTkJlAJHG/XI7nKFoAS/9USfW9CbnpQoXe/MKo0wUbcgnNzpkMwKBgQC549gUQ3vbUPQQivTTDsblvUabXUHQ00oXO/+b5XtfYAt1yw1crID8V/+3KR9jioBg0HtO58nrD46f94i4CihJYlBhIM3VKhCUe1nrnO/PDBc09jLsuJgYt9JB8LgOjb/5PjIlYGiDrvTJW3/R3T+M75DbgNYn0gBNVTGRr/e3lQKBgHXxNhWIwGFu/a1JAULGSuHBPX8p6TpdWsqYEB+YYh/YmWh7X7aT28mDoTr1RowUY1nhgPxT6gUT6rAq+ccFjoKSqj82CZyvpQLeeyB7W3Ayfe5ZaAS0GezXC95JXritXMC4duBQAPDEq9f2dAEfiedYFaTrNm5TPlustAo/p4i1AoGAHWniDQLInGFo3R0/tA3Ihx22CGtvOJ3WQWrq/T4IkYTfC1p/2MQq/MLn4ZGON+/4dFlIyal/l8Q0azd92LEywrndwiCwsSQgz33dMpz4jSh6m5TBZhenaXHcwTZXyWXAlK20vZlNsFiEvz/NL+X0Ylad8z2lpVNjXNk4KLdRWSkCgYBdfYi6d6V68T8EtTahHHatjoZtye+qC8xVkdwPS0H9c/XhWqliNX6BEuzGbFsf0+BPxfqQet2dz3U/n2rsU8pLrGlGc94/P58cYflQISdNNvqADg5cG1VolnxL+/h7gDBqBe4se+woNcZujSNraOCrdLQQYlP33CAHaEiA4htSMg==";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAidrQXhWrYNPUL5qs7OrEtKHQSf6aLn3A6n2mvzr6wtxlIbThzmjbCIb3LoIrf1gutFu0WxDo3S4WuRynCT9BNxhJITpLZ0KCtT8Bj/oGbmPlbZAf2VikIzKjy5YiUQQepGHVjwPBay/Q5OsCKS2lBp9P50N6xxchYkSbPkdY9aoKsPrNiqo+yPHaJzxocdFc8DWzUfinV3c/hLfcjQRHAURvjpf9pnfdkOt9lmfKnYgc87u0/8tTqr6ybm0+ul8yZ7Rb27DAP8FctV+6rVH3fdgzxc/9ORquPQgPlwaOTGb8hMFeqnIp7zL/Q3uPr2tk2pBZEi+4Ou4XXW19K5T+lwIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private String notify_url = "http://qy36945696.zicp.vip/payed/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private String return_url = "http://order.gulimall.com/web/order/list.html";

    // 签名方式
    private String sign_type = "RSA2";

    // 字符编码格式
    private String charset = "utf-8";

    // 支付超时时间
    private String timeout = "30m";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
                + "\"total_amount\":\"" + total_amount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"timeout_express\":\"" + timeout + "\"," // 订单的支付最晚超时时间
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应：" + result);

        return result;

    }

    /**
     * 收单
     *
     * @param outTradeNo 商户订单号
     * @param tradeNo    支付宝交易号
     */
    public void close(String outTradeNo, String tradeNo) throws AlipayApiException {
        //商户订单号和支付宝交易号不能同时为空。 trade_no、  out_trade_no如果同时存在优先取trade_no
        //商户订单号，和支付宝交易号二选一
//        String out_trade_no = new String(request.getParameter("WIDout_trade_no").getBytes("ISO-8859-1"), "UTF-8");
        //支付宝交易号，和商户订单号二选一
//        String trade_no = new String(request.getParameter("WIDtrade_no").getBytes("ISO-8859-1"), "UTF-8");
        /**********************/
        // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签
        AlipayClient client = new DefaultAlipayClient(gatewayUrl, app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);
        AlipayTradeCloseRequest alipayRequest = new AlipayTradeCloseRequest();

        AlipayTradeCloseModel model = new AlipayTradeCloseModel();
        model.setOutTradeNo(outTradeNo);
//        model.setTradeNo(tradeNo);
        alipayRequest.setBizModel(model);

        AlipayTradeCloseResponse alipayResponse = client.execute(alipayRequest);
        System.out.println(alipayResponse.getBody());
    }

    /**
     * 查询支付订单信息
     *
     * @param outTradeNo 商户订单号
     * @param tradeNo    支付宝交易号
     * @throws AlipayApiException
     */
    public void query(String outTradeNo, String tradeNo) throws AlipayApiException {
        //商户订单号和支付宝交易号不能同时为空。 trade_no、  out_trade_no如果同时存在优先取trade_no
        //商户订单号，和支付宝交易号二选一
//        String out_trade_no = new String(request.getParameter("WIDout_trade_no").getBytes("ISO-8859-1"), "UTF-8");
        //支付宝交易号，和商户订单号二选一
//        String trade_no = new String(request.getParameter("WIDtrade_no").getBytes("ISO-8859-1"), "UTF-8");
        /**********************/
        // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签
        AlipayClient client = new DefaultAlipayClient(gatewayUrl, app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);
        AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();

        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        model.setOutTradeNo(outTradeNo);
//        model.setTradeNo(tradeNo);
        alipayRequest.setBizModel(model);

        AlipayTradeQueryResponse alipayResponse = client.execute(alipayRequest);
        System.out.println(alipayResponse.getBody());
    }
}
