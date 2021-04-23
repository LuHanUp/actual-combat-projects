package top.luhancc.hrm.common.mapping;

import java.util.List;

/**
 * 通用的Mapper类
 *
 * @param <B> bo普通的业务实体类
 * @param <D> entity对应数据库的实体类
 * @author luHan
 * @create 2021/4/23 19:45
 * @since 1.0.0
 */
public interface BaseMapping<B, D> {

    /**
     * 将Bo转化为Do
     *
     * @param bo
     * @return
     */
    D toDo(B bo);

    /**
     * 将Do转化为Bo
     *
     * @param entity
     * @return
     */
    B toBo(D entity);

    /**
     * 将List<B>转换为List<D>
     *
     * @param bos
     * @return
     */
    List<D> toListDo(List<B> bos);

    /**
     * 将List<D>转换为List<B>
     *
     * @param entities
     * @return
     */
    List<B> toListBo(List<D> entities);
}
