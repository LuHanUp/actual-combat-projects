package top.luhancc.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 通用entity
 *
 * @author luHan
 * @create 2020/12/7 18:20
 * @since 1.0.0
 */
@Data
public class BaseEntity {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
}
