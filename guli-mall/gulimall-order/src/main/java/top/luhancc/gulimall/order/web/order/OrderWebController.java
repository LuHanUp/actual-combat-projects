package top.luhancc.gulimall.order.web.order;

import com.alipay.api.AlipayApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import top.luhancc.gulimall.order.config.AlipayTemplate;
import top.luhancc.gulimall.order.domain.order.vo.OrderConfirmVo;
import top.luhancc.gulimall.order.domain.order.vo.OrderSubmitVo;
import top.luhancc.gulimall.order.domain.order.vo.PayVo;
import top.luhancc.gulimall.order.domain.order.vo.SubmitOrderResultVo;
import top.luhancc.gulimall.order.service.OrderService;

/**
 * @author luHan
 * @create 2021/1/14 13:28
 * @since 1.0.0
 */
@Controller
@RequestMapping("/web/order")
public class OrderWebController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private AlipayTemplate alipayTemplate;

    @GetMapping("/list.html")
    public String orderListPage() {
        return "order/list";
    }

    /**
     * 去结算，进入到订单确认页
     *
     * @return
     */
    @GetMapping("/toTrade")
    public String toTrade(Model model) {
        OrderConfirmVo orderConfirmVo = orderService.confirm();
        model.addAttribute("orderConfirmData", orderConfirmVo);
        return "order/confirm";
    }

    /**
     * 提交订单，进入到支付页
     *
     * @param orderSubmitVo
     * @return
     */
    @PostMapping("/submitOrder")
    public String submitOrder(@RequestBody OrderSubmitVo orderSubmitVo, Model model) {
        SubmitOrderResultVo submitOrderResultVo = orderService.submitOrder(orderSubmitVo);
        if (submitOrderResultVo.getCode() == 0) {
            model.addAttribute("submitOrderResult", submitOrderResultVo);
            return "order/pay";// 下单成功到支付页
        } else {
            return "redirect:http://order.gulimall.com/toTrade";
        }
    }

    /**
     * 支付请求
     *
     * @param orderSn 订单号
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/payOrder", produces = "text/html")
    public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {
        PayVo payVo = orderService.getOrderPay(orderSn);
        return alipayTemplate.pay(payVo);
    }
}
