package top.luhancc.saas.hrm.common.model;

import lombok.Data;

import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * @author luHan
 * @create 2021/4/23 19:22
 * @since 1.0.0
 */
@Data
public class BaseDo {

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
}
