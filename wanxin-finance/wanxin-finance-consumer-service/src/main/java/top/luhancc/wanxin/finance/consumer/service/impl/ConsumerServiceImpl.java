package top.luhancc.wanxin.finance.consumer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import top.luhancc.wanxin.finance.common.domain.BusinessException;
import top.luhancc.wanxin.finance.common.domain.CodePrefixCode;
import top.luhancc.wanxin.finance.common.domain.model.consumer.ConsumerDTO;
import top.luhancc.wanxin.finance.common.domain.model.consumer.ConsumerRegisterDTO;
import top.luhancc.wanxin.finance.common.util.CodeNoUtil;
import top.luhancc.wanxin.finance.consumer.domain.ConsumerErrorCode;
import top.luhancc.wanxin.finance.consumer.mapper.ConsumerMapper;
import top.luhancc.wanxin.finance.consumer.mapper.entity.Consumer;
import top.luhancc.wanxin.finance.consumer.service.ConsumerService;

/**
 * @author luHan
 * @create 2021/6/4 14:24
 * @since 1.0.0
 */
@Service
public class ConsumerServiceImpl extends ServiceImpl<ConsumerMapper, Consumer> implements ConsumerService {

    @Override
    public ConsumerDTO register(ConsumerRegisterDTO consumerRegisterDTO) {
        checkMobile(consumerRegisterDTO.getMobile());

        Consumer consumer = new Consumer();
        BeanUtils.copyProperties(consumerRegisterDTO, consumer);
        consumer.setUserNo(CodeNoUtil.getNo(CodePrefixCode.CODE_CONSUMER_PREFIX));
        consumer.setUsername(CodeNoUtil.getNo(CodePrefixCode.CODE_NO_PREFIX));
        consumer.setIsBindCard(0);
        this.save(consumer);

        ConsumerDTO consumerDTO = new ConsumerDTO();
        BeanUtils.copyProperties(consumer, consumerDTO);
        return consumerDTO;
    }

    private void checkMobile(String mobile) {
        LambdaQueryWrapper<Consumer> queryWrapper = Wrappers.lambdaQuery(Consumer.class).eq(Consumer::getMobile, mobile);
        int count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ConsumerErrorCode.E_140107);
        }
    }
}
