package top.luhancc.hrm.common.service;

import java.util.List;

/**
 * CRUDService接口
 *
 * @param <T> 数据实体类
 * @author luHan
 * @create 2021/5/13 15:48
 * @since 1.0.0
 */
public interface CRUDService<T> {
    public void save(T entity);

    public void update(T entity);

    public T findById(String id);

    public List<T> findAll(String companyId);

    public void deleteById(String id);
}
