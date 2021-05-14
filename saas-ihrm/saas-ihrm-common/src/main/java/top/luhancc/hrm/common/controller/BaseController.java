package top.luhancc.hrm.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.luhancc.hrm.common.domain.Result;
import top.luhancc.hrm.common.service.CRUDService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 公用的Controller
 *
 * @author luHan
 * @create 2021/5/13 11:12
 * @since 1.0.0
 */
public class BaseController<T, Service extends CRUDService<T>> {
    public HttpServletRequest request;
    public HttpServletResponse response;
    protected String companyId;

    @Autowired
    protected Service service;

    @ModelAttribute
    public void init(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        // TODO 后续需要动态获取
        this.companyId = "1";
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public Result<Void> save(@RequestBody T entity) {
        service.save(entity);
        return Result.success();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Result<Void> update(@PathVariable("id") String id, @RequestBody T entity) {
        Method setIdMethod = null;
        try {
            setIdMethod = entity.getClass().getMethod("setId", String.class);
            setIdMethod.setAccessible(true);
            setIdMethod.invoke(entity, id);
            service.update(entity);
            return Result.success();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result<Void> delete(@PathVariable("id") String id) {
        service.deleteById(id);
        return Result.success();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result<T> findById(@PathVariable("id") String id) {
        T data = service.findById(id);
        return Result.success(data);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Result<?> findAll() {
        List<T> list = service.findAll(companyId);
        return Result.success(list);
    }
}
