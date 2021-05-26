package top.luhancc.gulimall.product.vo.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 3级分类
 * <p>类描述</p>
 *
 * @author luHan
 * @create 2021/1/5 18:33
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Category3Vo {
    private String catalog2Id;
    private String id;
    private String name;
}
