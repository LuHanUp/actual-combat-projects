package top.luhancc.wanxin.finance.consumer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import top.luhancc.wanxin.finance.common.domain.model.consumer.BankCardDTO;
import top.luhancc.wanxin.finance.consumer.mapper.BankCardMapper;
import top.luhancc.wanxin.finance.consumer.mapper.entity.BankCard;
import top.luhancc.wanxin.finance.consumer.service.BankCardService;

/**
 * @author luHan
 * @create 2021/6/10 15:48
 * @since 1.0.0
 */
@Service
public class BankCardServiceImpl extends ServiceImpl<BankCardMapper, BankCard> implements BankCardService {

    @Override
    public BankCardDTO getByConsumerId(Long consumerId) {
        LambdaQueryWrapper<BankCard> queryWrapper = Wrappers.lambdaQuery(BankCard.class).eq(BankCard::getConsumerId, consumerId);
        BankCard bankCard = this.getOne(queryWrapper);
        if (bankCard == null) return null;
        BankCardDTO bankCardDTO = new BankCardDTO();
        BeanUtils.copyProperties(bankCard, bankCardDTO);
        return bankCardDTO;
    }

    @Override
    public BankCardDTO getByCardNumber(String cardNumber) {
        LambdaQueryWrapper<BankCard> queryWrapper = Wrappers.lambdaQuery(BankCard.class).eq(BankCard::getCardNumber, cardNumber);
        BankCard bankCard = this.getOne(queryWrapper);
        if (bankCard == null) return null;
        BankCardDTO bankCardDTO = new BankCardDTO();
        BeanUtils.copyProperties(bankCard, bankCardDTO);
        return bankCardDTO;
    }
}
