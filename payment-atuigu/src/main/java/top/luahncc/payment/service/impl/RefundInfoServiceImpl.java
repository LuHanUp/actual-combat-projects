package top.luahncc.payment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.luahncc.payment.entity.RefundInfo;
import top.luahncc.payment.mapper.RefundInfoMapper;
import top.luahncc.payment.service.RefundInfoService;

@Service
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo> implements RefundInfoService {

}
