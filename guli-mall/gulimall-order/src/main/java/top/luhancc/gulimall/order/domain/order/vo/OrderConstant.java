package top.luhancc.gulimall.order.domain.order.vo;

/**
 * @author luHan
 * @create 2021/1/14 17:34
 * @since 1.0.0
 */
public class OrderConstant {
    public static final String USER_ORDER_TOKEN_KEY = "order:token:";

    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";

    public static final String ORDER_EVENT_EXCHANGE = "order-event-exchange";

    public static final String ORDER_RELEASE_QUEUE = "order.release.queue";

    public static final String ORDER_SECKILL_QUEUE = "order.seckill.queue";

    public static final String ORDER_CREATE_ORDER_TOPIC = "order.create.order";

    public static final String ORDER_RELEASE_ORDER_TOPIC = "order.release.order";

    /**
     * 订单自动取消时间 单位:毫秒
     */
    public static final int ORDER_AUTO_CANCEL_TIME = 1000 * 60 * 30;
}
