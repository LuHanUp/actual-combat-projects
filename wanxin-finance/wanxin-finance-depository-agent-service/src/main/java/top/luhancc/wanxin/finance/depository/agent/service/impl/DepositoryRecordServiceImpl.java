package top.luhancc.wanxin.finance.depository.agent.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.luhancc.wanxin.finance.common.domain.StatusCode;
import top.luhancc.wanxin.finance.common.domain.model.consumer.rquest.ConsumerRequest;
import top.luhancc.wanxin.finance.common.domain.model.consumer.rquest.GatewayRequest;
import top.luhancc.wanxin.finance.common.util.EncryptUtil;
import top.luhancc.wanxin.finance.common.util.RSAUtil;
import top.luhancc.wanxin.finance.depository.agent.common.constant.DepositoryRequestTypeCode;
import top.luhancc.wanxin.finance.depository.agent.mapper.DepositoryRecordMapper;
import top.luhancc.wanxin.finance.depository.agent.mapper.entity.DepositoryRecord;
import top.luhancc.wanxin.finance.depository.agent.service.ConfigService;
import top.luhancc.wanxin.finance.depository.agent.service.DepositoryRecordService;

import java.time.LocalDateTime;

/**
 * @author luHan
 * @create 2021/6/10 16:57
 * @since 1.0.0
 */
@Service
public class DepositoryRecordServiceImpl extends ServiceImpl<DepositoryRecordMapper, DepositoryRecord> implements DepositoryRecordService {
    @Autowired
    private ConfigService configService;

    @Override
    public GatewayRequest createOpenAccountParam(ConsumerRequest consumerRequest) {
        // 保存交易记录
        DepositoryRecord depositoryRecord = getByConsumerRequest(consumerRequest);
        this.save(depositoryRecord);

        // 签名请求数据
        String jsonString = JSON.toJSONString(consumerRequest);
        String sign = RSAUtil.sign(jsonString, configService.getP2pPrivateKey(), "UTF-8");
        GatewayRequest gatewayRequest = new GatewayRequest();
        gatewayRequest.setServiceName("PERSONAL_REGISTER");
        gatewayRequest.setPlatformNo(configService.getP2pCode());
        gatewayRequest.setReqData(EncryptUtil.encodeURL(EncryptUtil.encodeUTF8StringBase64(jsonString)));
        gatewayRequest.setSignature(EncryptUtil.encodeURL(sign));
        gatewayRequest.setDepositoryUrl(configService.getDepositoryUrl() + "/gateway");

        return gatewayRequest;
    }

    @Override
    public boolean modifyRequestStatus(String requestNo, Integer status) {
        LambdaUpdateWrapper<DepositoryRecord> updateWrapper = Wrappers.<DepositoryRecord>lambdaUpdate()
                .eq(DepositoryRecord::getRequestNo, requestNo)
                .set(DepositoryRecord::getConfirmDate, LocalDateTime.now())
                .set(DepositoryRecord::getRequestStatus, status);
        return this.update(updateWrapper);
    }

    private DepositoryRecord getByConsumerRequest(ConsumerRequest consumerRequest) {
        DepositoryRecord depositoryRecord = new DepositoryRecord();
        depositoryRecord.setRequestNo(consumerRequest.getRequestNo());
        depositoryRecord.setRequestType(DepositoryRequestTypeCode.CONSUMER_CREATE.getCode());
        depositoryRecord.setObjectType("CONSUMER");
        depositoryRecord.setObjectId(consumerRequest.getId());
        depositoryRecord.setCreateDate(LocalDateTime.now());
        depositoryRecord.setIsSyn(1);
        depositoryRecord.setRequestStatus(StatusCode.STATUS_OUT.getCode());
        return depositoryRecord;
    }
}
