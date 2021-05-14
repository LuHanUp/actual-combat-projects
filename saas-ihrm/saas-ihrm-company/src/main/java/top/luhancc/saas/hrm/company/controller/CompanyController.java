package top.luhancc.saas.hrm.company.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import top.luhancc.hrm.common.controller.BaseController;
import top.luhancc.hrm.common.domain.Result;
import top.luhancc.saas.hrm.common.model.company.Company;
import top.luhancc.saas.hrm.company.service.CompanyService;

import java.util.List;

/**
 * @author luHan
 * @create 2021/4/23 21:21
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/company")
public class CompanyController extends BaseController<Company, CompanyService> {

    @Override
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Result<List<Company>> findAll() {
        List<Company> companies = service.findAll();
        return Result.success(companies);
    }
}
