package top.luhancc.hrm.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.luhancc.hrm.common.context.UserContext;
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
    protected String companyName;

    public static final String ENTITY_TYPE_FLAG = "#{T}";

    @Autowired
    protected Service service;

    @ModelAttribute
    public void init(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        this.companyId = UserContext.getCurrentUser().getCompanyId();
        this.companyName = UserContext.getCurrentUser().getCompanyName();
    }

    @RequestMapping(value = "", method = RequestMethod.POST, name = ENTITY_TYPE_FLAG + "_SAVE")
    public Result<Void> save(@RequestBody T entity) {
        service.save(entity);
        return Result.success();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, name = ENTITY_TYPE_FLAG + "_UPDATE")
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

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, name = ENTITY_TYPE_FLAG + "_DELETE")
    public Result<Void> delete(@PathVariable("id") String id) {
        service.deleteById(id);
        return Result.success();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, name = ENTITY_TYPE_FLAG + "_FIND_BY_ID")
    public Result<?> findById(@PathVariable("id") String id) {
        T data = service.findById(id);
        return Result.success(data);
    }

    @RequestMapping(value = "", method = RequestMethod.GET, name = ENTITY_TYPE_FLAG + "_FIND_ALL")
    public Result<?> findAll() {
        List<T> list = service.findAll(companyId);
        return Result.success(list);
    }
}
