package top.luahncc.payment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.luahncc.payment.entity.Product;
import top.luahncc.payment.mapper.ProductMapper;
import top.luahncc.payment.service.ProductService;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

}
