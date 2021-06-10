package top.luhancc.wanxin.finance.depository.agent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.luhancc.wanxin.finance.common.domain.model.consumer.rquest.ConsumerRequest;
import top.luhancc.wanxin.finance.common.domain.model.consumer.rquest.GatewayRequest;
import top.luhancc.wanxin.finance.depository.agent.mapper.entity.DepositoryRecord;

/**
 * @author luHan
 * @create 2021/6/10 16:57
 * @since 1.0.0
 */
public interface DepositoryRecordService extends IService<DepositoryRecord> {

    /**
     * 生成开户请求数据
     * <p>
     * 保存交易记录
     * 对数据进行签名加密后返回
     *
     * @param consumerRequest 开户信息
     * @return
     */
    GatewayRequest createOpenAccountParam(ConsumerRequest consumerRequest);
}
