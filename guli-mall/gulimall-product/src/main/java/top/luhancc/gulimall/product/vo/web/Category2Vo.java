package top.luhancc.gulimall.product.vo.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 2级分类
 *
 * @author luHan
 * @create 2021/1/5 18:33
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Category2Vo {
    private String catalog1Id;
    private List<Category3Vo> catalog3List;
    private String id;
    private String name;
}
