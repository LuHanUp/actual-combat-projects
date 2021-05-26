package top.luhancc.gulimall.order.listener;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import top.luhancc.gulimall.order.config.AlipayTemplate;
import top.luhancc.gulimall.order.domain.order.vo.PayAsyncVo;
import top.luhancc.gulimall.order.service.OrderService;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 支付成功后支付宝回调的监听器
 *
 * @author luHan
 * @create 2021/1/19 19:01
 * @since 1.0.0
 */
@RestController
@Slf4j
public class OrderPayedListener {
    @Autowired
    private OrderService orderService;
    @Autowired
    private AlipayTemplate alipayTemplate;

    @PostMapping("/payed/notify")
    public String handleAlipayed(PayAsyncVo payAsyncVo, HttpServletRequest request) throws UnsupportedEncodingException, AlipayApiException {
        log.info("收到支付宝通知:{}", payAsyncVo);
        // 只要收到了支付宝给我们的异步通知，告诉我们订单支付成功，返回success，支付宝就不会通知了

        // 1. 验签
        boolean verifyResult = verify(request);
        if (verifyResult) {
            log.warn("支付宝验签成功");
            return orderService.handlePayResult(payAsyncVo);
        }
        log.warn("支付宝验签失败");
        return "FAIL";
    }

    /**
     * 验证支付宝签名
     *
     * @param request
     * @return
     * @throws AlipayApiException
     * @throws UnsupportedEncodingException
     */
    private boolean verify(HttpServletRequest request) throws AlipayApiException, UnsupportedEncodingException {
        //获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = iter.next();
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
//            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "UTF-8");
            params.put(name, valueStr);
        }
        //计算得出通知验证结果
        //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
        return AlipaySignature.rsaCheckV1(params, alipayTemplate.getAlipay_public_key(), alipayTemplate.getCharset(), alipayTemplate.getSign_type());
    }
}
