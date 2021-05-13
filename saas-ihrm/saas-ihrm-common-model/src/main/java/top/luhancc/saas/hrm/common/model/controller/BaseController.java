package top.luhancc.saas.hrm.common.model.controller;

import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 公用的Controller
 *
 * @author luHan
 * @create 2021/5/13 11:12
 * @since 1.0.0
 */
public class BaseController {
    public HttpServletRequest request;
    public HttpServletResponse response;
    protected String companyId;

    @ModelAttribute
    public void init(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        // TODO 后续需要动态获取
        this.companyId = "1";
    }
}
