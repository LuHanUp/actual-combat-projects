package top.luhancc.gulimall;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.luhancc.gulimall.product.ProductApplication;
import top.luhancc.gulimall.product.entity.BrandEntity;
import top.luhancc.gulimall.product.service.BrandService;

/**
 * @author luHan
 * @create 2020/12/7 16:24
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProductApplication.class)
public class ProductApplicationTest {
    @Autowired
    private BrandService brandService;

    @Test
    public void testContextLoads() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setName("这是测试添加的数据");
        brandService.save(brandEntity);
    }
}
