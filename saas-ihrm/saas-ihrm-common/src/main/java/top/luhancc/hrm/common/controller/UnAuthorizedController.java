package top.luhancc.hrm.common.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.luhancc.hrm.common.domain.Result;
import top.luhancc.hrm.common.domain.ResultCode;
import top.luhancc.hrm.common.exception.BaseBusinessException;

/**
 * 类功能简述
 * <p>类描述</p>
 *
 * @author luHan
 * @create 2021/5/25 15:53
 * @since 1.0.0
 */
@RestController
@RequestMapping("/sys")
public class UnAuthorizedController {
    @RequestMapping(value = "/autherror", method = RequestMethod.GET)
    public Result<String> authError(@RequestParam("code") Integer code) {
        if (1 == code) {
            throw new BaseBusinessException(ResultCode.UNAUTHENTICATED);
        } else if (2 == code) {
            throw new BaseBusinessException(ResultCode.UNAUTHORISE);
        } else {
            return Result.success();
        }
    }
}
