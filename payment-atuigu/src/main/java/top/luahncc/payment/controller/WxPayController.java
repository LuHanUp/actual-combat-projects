package top.luahncc.payment.controller;

import com.oracle.tools.packager.mac.MacAppBundler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.luahncc.payment.domain.vo.R;
import top.luahncc.payment.service.WxPayService;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 微信支付api
 * <p>
 * 微信扫码支付时序图：https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_4_4.shtml
 *
 * @author luHan
 * @create 2022/1/9 21:02
 * @since 1.0.0
 */
@RestController
@RequestMapping("api/wx-pay")
@Slf4j
public class WxPayController {
    @Resource
    private WxPayService wxPayService;

    /**
     * 生成支付二维码，对应扫码时序图中的1~4步骤
     *
     * @return
     */
    @PostMapping("native/{productId}")
    public R nativePay(@PathVariable(value = "productId") Long productId) {
        log.info("{}:发起支付请求", productId);
        Map<String, Object> data = wxPayService.nativePay(productId);
        return R.ok(data);
    }

}
