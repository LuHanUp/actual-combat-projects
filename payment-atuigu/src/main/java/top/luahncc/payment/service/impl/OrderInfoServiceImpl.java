package top.luahncc.payment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.luahncc.payment.entity.OrderInfo;
import top.luahncc.payment.mapper.OrderInfoMapper;
import top.luahncc.payment.service.OrderInfoService;

@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

}
