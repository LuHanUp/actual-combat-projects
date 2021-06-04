package top.luhancc.wanxin.finance.consumer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.luhancc.wanxin.finance.common.domain.model.consumer.ConsumerDTO;
import top.luhancc.wanxin.finance.common.domain.model.consumer.ConsumerRegisterDTO;
import top.luhancc.wanxin.finance.consumer.mapper.entity.Consumer;

/**
 * @author luHan
 * @create 2021/6/4 14:23
 * @since 1.0.0
 */
public interface ConsumerService extends IService<Consumer> {

    /**
     * 注册用户信息
     *
     * @param consumerRegisterDTO
     * @return
     */
    ConsumerDTO register(ConsumerRegisterDTO consumerRegisterDTO);
}