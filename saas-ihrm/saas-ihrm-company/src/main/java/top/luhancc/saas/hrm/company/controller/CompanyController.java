package top.luhancc.saas.hrm.company.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.luhancc.hrm.common.domain.Result;
import top.luhancc.saas.hrm.common.model.company.Company;
import top.luhancc.saas.hrm.common.model.validate.group.UpdateGroup;
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
public class CompanyController {
    private final CompanyService companyService;

    @RequestMapping(method = RequestMethod.POST)
    public Result<Void> save(@Validated @RequestBody Company company) {
        companyService.add(company);
        return Result.success();
    }

    @RequestMapping(method = RequestMethod.PUT)
    public Result<Void> update(@Validated(value = UpdateGroup.class) @RequestBody Company company) {
        companyService.update(company);
        return Result.success();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result<Void> delete(@PathVariable("id") String id) {
        companyService.deleteById(id);
        return Result.success();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result<Company> findById(@PathVariable("id") String id) {
        Company company = companyService.findById(id);
        return Result.success(company);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Result<List<Company>> findAll() {
        List<Company> companies = companyService.findAll();
        return Result.success(companies);
    }
}
