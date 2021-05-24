package top.luhancc.hrm.common.request;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import top.luhancc.hrm.common.domain.Result;
import top.luhancc.saas.hrm.common.model.system.PermissionApi;

import java.util.List;

/**
 * @author luHan
 * @create 2021/5/24 09:48
 * @since 1.0.0
 */
@FeignClient(qualifier = "permissionApiFeign", name = "${feign.permission.name:saas-ihrm-system}", url = "${feign.permission.url:}", path = "")
public interface PermissionApiFeign {
    @RequestMapping(value = "/sys/permission/save/apis", method = RequestMethod.POST)
    public Result<Void> saveApis(@RequestBody List<PermissionApi> apis);
}
