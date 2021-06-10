package top.luhancc.wanxin.finance.consumer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.luhancc.wanxin.finance.common.domain.model.consumer.BankCardDTO;
import top.luhancc.wanxin.finance.consumer.mapper.entity.BankCard;

/**
 * @author luHan
 * @create 2021/6/10 15:48
 * @since 1.0.0
 */
public interface BankCardService extends IService<BankCard> {

    BankCardDTO getByConsumerId(Long consumerId);

    BankCardDTO getByCardNumber(String cardNumber);
}
