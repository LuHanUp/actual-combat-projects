package top.luahncc.payment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.luahncc.payment.entity.PaymentInfo;
import top.luahncc.payment.mapper.PaymentInfoMapper;
import top.luahncc.payment.service.PaymentInfoService;

@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {

}
