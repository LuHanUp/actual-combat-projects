package top.luhancc.wanxin.finance.depository.agent.controller;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.luhancc.wanxin.finance.common.domain.model.depository.agent.DepositoryConsumerResponse;
import top.luhancc.wanxin.finance.common.util.EncryptUtil;
import top.luhancc.wanxin.finance.depository.agent.message.GatewayMessageProducer;
import top.luhancc.wanxin.finance.depository.agent.service.DepositoryRecordService;

/**
 * 银行存管系统通知服务
 *
 * @author luHan
 * @create 2021/6/11 10:59
 * @since 1.0.0
 */
@Api(value = "银行存管系统通知服务", tags = "depository-agent")
@RestController
public class DepositoryNotifyController {
    @Autowired
    private DepositoryRecordService depositoryRecordService;
    @Autowired
    private GatewayMessageProducer gatewayMessageProducer;

    @ApiOperation(value = "接受银行存管系统开户回调结果", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "serviceName", value = "请求的存管接口名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "platformNo", value = "平台编号，平台与存管系统签约时获取", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "signature", value = "对reqData参数的签名", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "reqData", value = "业务数据报文，json格式", required = true, dataType = "String", paramType = "query")
    })
    @RequestMapping(value = "/gateway", method = RequestMethod.GET, params = "serviceName=PERSONAL_REGISTER")
    public String receiveDepositoryCreateConsumerResult(@RequestParam("serviceName") String serviceName,
                                                        @RequestParam("platformNo") String platformNo,
                                                        @RequestParam("signature") String signature,
                                                        @RequestParam("reqData") String reqData) {
        // 1.更新数据
        DepositoryConsumerResponse depositoryConsumerResponse = JSON.parseObject(EncryptUtil.decodeUTF8StringBase64(reqData), DepositoryConsumerResponse.class);
        depositoryRecordService.modifyRequestStatus(depositoryConsumerResponse.getRequestNo(), depositoryConsumerResponse.getStatus());
        // 2.给用户中心发送消息
        gatewayMessageProducer.personalRegister(depositoryConsumerResponse);
        // 3.给银行存管系统返回结果
        return "OK";
    }
}
