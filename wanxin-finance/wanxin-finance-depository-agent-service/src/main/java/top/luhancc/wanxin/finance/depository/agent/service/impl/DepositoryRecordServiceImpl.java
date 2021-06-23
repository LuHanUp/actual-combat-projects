package top.luhancc.wanxin.finance.depository.agent.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.luhancc.wanxin.finance.common.cache.Cache;
import top.luhancc.wanxin.finance.common.domain.BusinessException;
import top.luhancc.wanxin.finance.common.domain.StatusCode;
import top.luhancc.wanxin.finance.common.domain.model.consumer.rquest.ConsumerRequest;
import top.luhancc.wanxin.finance.common.domain.model.consumer.rquest.GatewayRequest;
import top.luhancc.wanxin.finance.common.domain.model.depository.agent.*;
import top.luhancc.wanxin.finance.common.domain.model.repayment.LoanRequest;
import top.luhancc.wanxin.finance.common.domain.model.transaction.ProjectDTO;
import top.luhancc.wanxin.finance.common.util.EncryptUtil;
import top.luhancc.wanxin.finance.common.util.RSAUtil;
import top.luhancc.wanxin.finance.depository.agent.common.constant.DepositoryErrorCode;
import top.luhancc.wanxin.finance.depository.agent.common.constant.DepositoryRequestTypeCode;
import top.luhancc.wanxin.finance.depository.agent.mapper.DepositoryRecordMapper;
import top.luhancc.wanxin.finance.depository.agent.mapper.entity.DepositoryRecord;
import top.luhancc.wanxin.finance.depository.agent.service.ConfigService;
import top.luhancc.wanxin.finance.depository.agent.service.DepositoryRecordService;
import top.luhancc.wanxin.finance.depository.agent.service.OkHttpService;

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
    @Autowired
    private OkHttpService okHttpService;
    @Autowired
    private Cache cache;

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

    @Override
    public DepositoryResponseDTO<DepositoryBaseResponse> createProject(ProjectDTO projectDTO) {
        // 1. 保存交易记录
        DepositoryRecord depositoryRecord = saveDepositoryRecord(projectDTO.getRequestNo(),
                DepositoryRequestTypeCode.CREATE.getCode(), "Project", projectDTO.getId());

        // 2. 签名数据
        // ProjectDTO 转换为 ProjectRequestDataDTO
        ProjectRequestDataDTO projectRequestDataDTO = convertProjectDTOToProjectRequestDataDTO(projectDTO, depositoryRecord.getRequestNo());

        //转换为JSON
        String jsonString = JSON.toJSONString(projectRequestDataDTO);
        //base64编码
        String reqData = EncryptUtil.encodeUTF8StringBase64(jsonString);
        //3. 往银行存管系统发送数据(标的信息),根据结果修改状态并返回结果
        // url地址 发送哪些数据
        String url = configService.getDepositoryUrl() + "/service";
        // 怎么发 OKHttpClient 发送Http请求
        return sendHttpGet("CREATE_PROJECT", url, reqData, depositoryRecord);
    }

    @Override
    public DepositoryResponseDTO<DepositoryBaseResponse> userAutoPreTransaction(UserAutoPreTransactionRequest userAutoPreTransactionRequest) {
        DepositoryRecord depositoryRecord = new DepositoryRecord(userAutoPreTransactionRequest.getRequestNo(),
                userAutoPreTransactionRequest.getBizType(), "UserAutoPreTransactionRequest",
                userAutoPreTransactionRequest.getId());
        DepositoryResponseDTO<DepositoryBaseResponse> handleIdempotent = handleIdempotent(depositoryRecord);
        if (handleIdempotent != null) {
            return handleIdempotent;
        }
        depositoryRecord = getEntityByRequestNo(userAutoPreTransactionRequest.getRequestNo());

        // 进行encode后发送存管系统
        String string = JSON.toJSONString(userAutoPreTransactionRequest);
        String reqData = EncryptUtil.encodeUTF8StringBase64(string);
        String url = configService.getDepositoryUrl() + "/service";
        return sendHttpGet("USER_AUTO_PRE_TRANSACTION", url, reqData, depositoryRecord);
    }

    @Override
    public DepositoryResponseDTO<DepositoryBaseResponse> confirmLoan(LoanRequest loanRequest) {
        DepositoryRecord depositoryRecord = new DepositoryRecord(loanRequest.getRequestNo(),
                DepositoryRequestTypeCode.FULL_LOAN.getCode(), "LoanRequest", loanRequest.getId());
        DepositoryResponseDTO<DepositoryBaseResponse> handleIdempotent = handleIdempotent(depositoryRecord);
        if (handleIdempotent != null) {
            return handleIdempotent;
        }
        depositoryRecord = getEntityByRequestNo(loanRequest.getRequestNo());
        // 进行encode后发送存管系统
        String string = JSON.toJSONString(loanRequest);
        String reqData = EncryptUtil.encodeUTF8StringBase64(string);
        String url = configService.getDepositoryUrl() + "/service";
        return sendHttpGet("CONFIRM_LOAN", url, reqData, depositoryRecord);
    }

    @Override
    public DepositoryResponseDTO<DepositoryBaseResponse> modifyProjectStatus(ModifyProjectStatusDTO modifyProjectStatusDTO) {
        DepositoryRecord depositoryRecord = new DepositoryRecord(modifyProjectStatusDTO.getRequestNo(),
                DepositoryRequestTypeCode.MODIFY_STATUS.getCode(), "ModifyProjectStatusDTO",
                modifyProjectStatusDTO.getId());
        DepositoryResponseDTO<DepositoryBaseResponse> handleIdempotent = handleIdempotent(depositoryRecord);
        if (handleIdempotent != null) {
            return handleIdempotent;
        }
        depositoryRecord = getEntityByRequestNo(modifyProjectStatusDTO.getRequestNo());
        // 进行encode后发送存管系统
        String string = JSON.toJSONString(modifyProjectStatusDTO);
        String reqData = EncryptUtil.encodeUTF8StringBase64(string);
        String url = configService.getDepositoryUrl() + "/service";
        return sendHttpGet("MODIFY_PROJECT", url, reqData, depositoryRecord);
    }

    /**
     * 对数据进行签名后将数据发送给存管系统
     *
     * @param serviceName
     * @param url
     * @param reqData
     * @param depositoryRecord
     * @return
     */
    private DepositoryResponseDTO<DepositoryBaseResponse> sendHttpGet(String serviceName, String url, String reqData, DepositoryRecord depositoryRecord) {
        // 银行存管系统接收的4大参数: serviceName, platformNo, reqData, signature
        // signature会在okHttp拦截器(SignatureInterceptor)中处理
        // 平台编号
        String platformNo = configService.getP2pCode();
        // redData签名
        // 发送请求, 获取结果, 如果检验签名失败, 拦截器会在结果中放入: "signature", "false"
        String responseBody = okHttpService.doSyncGet(url + "?serviceName=" + serviceName + "&platformNo=" + platformNo + "&reqData=" + reqData);
        DepositoryResponseDTO<DepositoryBaseResponse> depositoryResponse = JSON.parseObject(responseBody,
                new TypeReference<DepositoryResponseDTO<DepositoryBaseResponse>>() {
                });
        depositoryRecord.setResponseData(responseBody);
        // 响应后, 根据结果更新数据库( 进行签名判断 )
        // 判断签名(signature)是为 false, 如果是说明验签失败!
        if (!"false".equals(depositoryResponse.getSignature())) {
            // 成功 - 设置数据同步状态
            depositoryRecord.setRequestStatus(StatusCode.STATUS_IN.getCode());
            // 设置消息确认时间
            depositoryRecord.setConfirmDate(LocalDateTime.now());
            // 更新数据库
            updateById(depositoryRecord);
        } else {
            // 失败 - 设置数据同步状态
            depositoryRecord.setRequestStatus(StatusCode.STATUS_FAIL.getCode());
            // 设置消息确认时间
            depositoryRecord.setConfirmDate(LocalDateTime.now());
            // 更新数据库
            updateById(depositoryRecord);
            // 抛业务异常
            throw new BusinessException(DepositoryErrorCode.E_160101);
        }
        return depositoryResponse;
    }

    private ProjectRequestDataDTO convertProjectDTOToProjectRequestDataDTO(ProjectDTO projectDTO, String requestNo) {
        if (projectDTO == null) {
            return null;
        }
        ProjectRequestDataDTO requestDataDTO = new ProjectRequestDataDTO();
        BeanUtils.copyProperties(projectDTO, requestDataDTO);
        requestDataDTO.setRequestNo(requestNo);
        return requestDataDTO;
    }

    private DepositoryRecord saveDepositoryRecord(String requestNo, String requestType, String objectType, Long objectId) {
        DepositoryRecord depositoryRecord = new DepositoryRecord();
        // 设置请求流水号
        depositoryRecord.setRequestNo(requestNo);
        // 设置请求类型
        depositoryRecord.setRequestType(requestType);
        // 设置关联业务实体类型
        depositoryRecord.setObjectType(objectType);
        // 设置关联业务实体标识
        depositoryRecord.setObjectId(objectId);
        // 设置请求时间
        depositoryRecord.setCreateDate(LocalDateTime.now());
        // 设置数据同步状态
        depositoryRecord.setRequestStatus(StatusCode.STATUS_OUT.getCode());
        this.save(depositoryRecord);
        return depositoryRecord;
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

    /**
     * 实现幂等性保存记录
     *
     * @param depositoryRecord
     * @return
     */
    private DepositoryResponseDTO<DepositoryBaseResponse> handleIdempotent(DepositoryRecord depositoryRecord) {
        // 根据requestNo进行查询
        String requestNo = depositoryRecord.getRequestNo();
        DepositoryRecordDTO depositoryRecordDTO = getByRequestNo(requestNo);

        //1. 交易记录不存在,保存交易记录
        if (null == depositoryRecordDTO) {
            saveDepositoryRecord(depositoryRecord.getRequestNo(), depositoryRecord.getRequestType(), depositoryRecord.getObjectType(), depositoryRecord.getObjectId());
            return null;
        }

        //2. 重复点击，重复请求，利用redis的原子性，争夺执行权
        if (StatusCode.STATUS_OUT.getCode() ==
                depositoryRecordDTO.getRequestStatus()) {
            //如果requestNo不存在则返回1,如果已经存在,则会返回（requestNo已存在个数+1）
            Long count = cache.incrBy(requestNo, 1L);
            if (count == 1) {
                cache.expire(requestNo, 10); //设置requestNo有效期5秒
                return null;
            }
            // 若count大于1，说明已有线程在执行该操作，直接返回“正在处理”
            if (count > 1) {
                throw new BusinessException(DepositoryErrorCode.E_160103);
            }
        }

        //3. 交易记录已经存在，并且状态是“已同步”
        return JSON.parseObject(depositoryRecordDTO.getResponseData(),
                new TypeReference<DepositoryResponseDTO<DepositoryBaseResponse>>() {
                });
    }

    private DepositoryRecordDTO getByRequestNo(String requestNo) {
        DepositoryRecord depositoryRecord = getEntityByRequestNo(requestNo);
        if (depositoryRecord == null) {
            return null;
        }
        DepositoryRecordDTO depositoryRecordDTO = new DepositoryRecordDTO();
        BeanUtils.copyProperties(depositoryRecord, depositoryRecordDTO);
        return depositoryRecordDTO;
    }

    private DepositoryRecord getEntityByRequestNo(String requestNo) {
        return getOne(new QueryWrapper<DepositoryRecord>().lambda()
                .eq(DepositoryRecord::getRequestNo, requestNo));
    }
}
