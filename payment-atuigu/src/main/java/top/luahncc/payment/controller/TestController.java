package top.luahncc.payment.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.luahncc.payment.config.WxPayConfig;
import top.luahncc.payment.domain.vo.R;

import javax.annotation.Resource;
import java.security.PrivateKey;

/**
 * @author luHan
 * @create 2022/1/9 19:44
 * @since 1.0.0
 */
@RestController
@RequestMapping("api/test")
public class TestController {
    @Resource
    private WxPayConfig wxPayConfig;

    @RequestMapping("/getPrivateKey")
    public R getPrivateKey() {
        PrivateKey privateKey = wxPayConfig.getPrivateKey();
        return R.ok(privateKey.toString());
    }
}
