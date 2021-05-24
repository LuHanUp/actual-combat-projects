package top.luhancc.hrm.common.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import top.luhancc.hrm.common.utils.IdWorker;
import top.luhancc.saas.hrm.common.model.system.PermissionApi;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 获取saas-ihrm所有请求路径,将其作为api权限保存
 *
 * @author luHan
 * @create 2021/5/21 22:36
 * @since 1.0.0
 */
@Component
@Slf4j
public class ScanAllRequestUrlRunner implements ApplicationRunner {
    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private PermissionApiFeign permissionApiFeign;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("开始保存所有的api权限列表");
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();

        List<PermissionApi> permissionApis = new ArrayList<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> handlerMethodEntry : handlerMethods.entrySet()) {
            RequestMappingInfo mappingInfo = handlerMethodEntry.getKey();
            Set<RequestMethod> methods = mappingInfo.getMethodsCondition().getMethods();
            Set<String> urlPatterns = mappingInfo.getPatternsCondition().getPatterns();
            for (String urlPattern : urlPatterns) {
                PermissionApi permissionApi = new PermissionApi();
                permissionApi.setId(idWorker.nextId() + "");
                permissionApi.setApiUrl(urlPattern);
                permissionApi.setApiMethod(String.join(",", methods.stream().map(Enum::name).collect(Collectors.toSet())));
                permissionApi.setApiLevel("2");

                permissionApis.add(permissionApi);
                log.debug("将要保存的api权限:{}", permissionApi);
            }
        }
        // 保存
        if (!CollectionUtils.isEmpty(permissionApis)) {
            CompletableFuture.runAsync(() -> {
                permissionApiFeign.saveApis(permissionApis);
            }).exceptionally(new Function<Throwable, Void>() {
                @Override
                public Void apply(Throwable throwable) {
                    return null;
                }
            }).get();
        }
    }
}
