package top.luhancc.wanxin.finance.consumer.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.dromara.hmily.annotation.Hmily;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.luhancc.wanxin.finance.common.domain.BusinessException;
import top.luhancc.wanxin.finance.common.domain.CodePrefixCode;
import top.luhancc.wanxin.finance.common.domain.RestResponse;
import top.luhancc.wanxin.finance.common.domain.StatusCode;
import top.luhancc.wanxin.finance.common.domain.model.account.AccountDTO;
import top.luhancc.wanxin.finance.common.domain.model.account.AccountRegisterDTO;
import top.luhancc.wanxin.finance.common.domain.model.consumer.*;
import top.luhancc.wanxin.finance.common.domain.model.consumer.rquest.ConsumerRequest;
import top.luhancc.wanxin.finance.common.domain.model.consumer.rquest.GatewayRequest;
import top.luhancc.wanxin.finance.common.domain.model.depository.agent.DepositoryConsumerResponse;
import top.luhancc.wanxin.finance.common.domain.model.depository.agent.DepositoryReturnCode;
import top.luhancc.wanxin.finance.common.util.CodeNoUtil;
import top.luhancc.wanxin.finance.common.util.IDCardUtil;
import top.luhancc.wanxin.finance.consumer.domain.ConsumerErrorCode;
import top.luhancc.wanxin.finance.consumer.feign.account.AccountFeign;
import top.luhancc.wanxin.finance.consumer.feign.depository.agent.DepositoryAgentFeign;
import top.luhancc.wanxin.finance.consumer.mapper.ConsumerMapper;
import top.luhancc.wanxin.finance.consumer.mapper.entity.BankCard;
import top.luhancc.wanxin.finance.consumer.mapper.entity.Consumer;
import top.luhancc.wanxin.finance.consumer.service.BankCardService;
import top.luhancc.wanxin.finance.consumer.service.ConsumerService;

import java.io.IOException;
import java.util.Map;

/**
 * @author luHan
 * @create 2021/6/4 14:24
 * @since 1.0.0
 */
@Service
@Slf4j
public class ConsumerServiceImpl extends ServiceImpl<ConsumerMapper, Consumer> implements ConsumerService {
    @Autowired
    private AccountFeign accountFeign;
    @Autowired
    private BankCardService bankCardService;
    @Autowired
    private DepositoryAgentFeign depositoryAgentFeign;

    @Value("${depository.url}")
    private String depositoryURL;

    private OkHttpClient okHttpClient = new OkHttpClient().newBuilder().build();

    @Override
    @Hmily(confirmMethod = "registerConfirm", cancelMethod = "registerCancel")
    @Transactional
    public ConsumerDTO register(ConsumerRegisterDTO consumerRegisterDTO) {
        checkMobile(consumerRegisterDTO.getMobile());

        Consumer consumer = new Consumer();
        BeanUtils.copyProperties(consumerRegisterDTO, consumer);
        consumer.setUserNo(CodeNoUtil.getNo(CodePrefixCode.CODE_CONSUMER_PREFIX));
        consumer.setUsername(CodeNoUtil.getNo(CodePrefixCode.CODE_NO_PREFIX));
        consumer.setIsBindCard(0);
        this.save(consumer);

        // ???????????????????????????????????????
        AccountRegisterDTO accountRegisterDTO = new AccountRegisterDTO();
        accountRegisterDTO.setUsername(consumerRegisterDTO.getUsername());
        accountRegisterDTO.setMobile(consumerRegisterDTO.getMobile());
        accountRegisterDTO.setPassword(consumerRegisterDTO.getPassword());
        RestResponse<AccountDTO> restResponse = accountFeign.register(accountRegisterDTO);
        if (!restResponse.isSuccessful()) {
            throw new BusinessException(restResponse.getMsg());
        }

        ConsumerDTO consumerDTO = new ConsumerDTO();
        BeanUtils.copyProperties(consumer, consumerDTO);
        return consumerDTO;
    }

    /**
     * ?????????Hmily confirm??????
     *
     * @param consumerRegisterDTO
     */
    public void registerConfirm(ConsumerRegisterDTO consumerRegisterDTO) {
        log.info("execute registerConfirm");
    }

    /**
     * ?????????Hmily cancel??????
     *
     * @param consumerRegisterDTO
     */
    public void registerCancel(ConsumerRegisterDTO consumerRegisterDTO) {
        log.info("execute registerCancel");
        remove(Wrappers.<Consumer>lambdaQuery().eq(Consumer::getMobile,
                consumerRegisterDTO.getMobile()));
    }

    @Override
    public Consumer getByMobile(String mobile) {
        LambdaQueryWrapper<Consumer> queryWrapper = Wrappers.lambdaQuery(Consumer.class).eq(Consumer::getMobile, mobile);
        return this.getOne(queryWrapper);
    }

    @Override
    public BorrowerDTO getBorrower(Long id) {
        Consumer consumer = this.getById(id);
        if (consumer == null) {
            throw new BusinessException(ConsumerErrorCode.E_140101);
        }
        BorrowerDTO borrowerDTO = new BorrowerDTO();
        BeanUtils.copyProperties(consumer, borrowerDTO);
        Map<String, String> cardInfoMap = IDCardUtil.getInfo(borrowerDTO.getIdNumber());
        borrowerDTO.setBirthday(cardInfoMap.get("birthday"));
        borrowerDTO.setGender(cardInfoMap.get("gender"));
        borrowerDTO.setAge(Integer.parseInt(cardInfoMap.get("age")));
        return borrowerDTO;
    }

    @Override
    public BalanceDetailsDTO getBalance(String userNo) {
        RestResponse<BalanceDetailsDTO> restResponse = getBalanceFromDepository(userNo);
        if (restResponse.isSuccessful()) {
            return restResponse.getResult();
        }
        throw new BusinessException(restResponse.getMsg());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GatewayRequest createOpenAccountParam(ConsumerRequest consumerRequest) {
        Consumer consumer = getByMobile(consumerRequest.getMobile());
        if (consumer == null) {
            throw new BusinessException(ConsumerErrorCode.E_140101);
        }

        // ?????????????????????
        if (consumer.getIsBindCard() != null && 1 == consumer.getIsBindCard()) {
            throw new BusinessException(ConsumerErrorCode.E_140105);
        }

        // ?????????????????????????????????
        BankCardDTO bankCardDTO = bankCardService.getByCardNumber(consumerRequest.getCardNumber());
        if (bankCardDTO != null && bankCardDTO.getStatus() == StatusCode.STATUS_IN.getCode()) {
            throw new BusinessException(ConsumerErrorCode.E_140151);
        }

        consumerRequest.setId(consumer.getId());
        // ???????????????????????????????????????
        consumerRequest.setUserNo(CodeNoUtil.getNo(CodePrefixCode.CODE_CONSUMER_PREFIX));
        consumerRequest.setRequestNo(CodeNoUtil.getNo(CodePrefixCode.CODE_REQUEST_PREFIX));
        LambdaUpdateWrapper<Consumer> updateWrapper = Wrappers.lambdaUpdate(Consumer.class)
                .eq(Consumer::getId, consumer.getId())
                .set(Consumer::getUserNo, consumerRequest.getUserNo())
                .set(Consumer::getRequestNo, consumerRequest.getRequestNo())
                .set(Consumer::getFullname, consumerRequest.getFullname())
                .set(Consumer::getIdNumber, consumerRequest.getIdNumber())
                .set(Consumer::getAuthList, "ALL");
        this.update(updateWrapper);

        // ?????????????????????????????????
        BankCard bankCard = new BankCard();
        bankCard.setConsumerId(consumer.getId());
        bankCard.setBankCode(consumerRequest.getBankCode());
        bankCard.setCardNumber(consumerRequest.getCardNumber());
        bankCard.setMobile(consumer.getMobile());
        bankCard.setStatus(StatusCode.STATUS_OUT.getCode());

        // ??????????????????????????????????????????????????????????????????????????????
        if (bankCardDTO != null) {
            bankCard.setId(bankCardDTO.getId());
        }
        bankCardService.saveOrUpdate(bankCard);

        RestResponse<GatewayRequest> restResponse = depositoryAgentFeign.createOpenAccountParam(consumerRequest);
        if (restResponse.isSuccessful()) {
            return restResponse.getResult();
        }
        throw new BusinessException(ConsumerErrorCode.E_140121);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean modifyResult(DepositoryConsumerResponse response) {
        StatusCode statusCode = DepositoryReturnCode.RETURN_CODE_00000.getCode().equals(response.getRespCode()) ? StatusCode.STATUS_IN : StatusCode.STATUS_FAIL;
        Consumer consumer = getByRequestNo(response.getRequestNo());
        if (consumer != null) {
            // ????????????????????????????????????
            LambdaUpdateWrapper<Consumer> updateWrapper = Wrappers.<Consumer>lambdaUpdate()
                    .eq(Consumer::getRequestNo, response.getRequestNo())
                    .set(Consumer::getIsBindCard, statusCode.getCode())
                    .set(Consumer::getStatus, statusCode.getCode());
            this.update(updateWrapper);
            // ?????????????????????
            return bankCardService.update(Wrappers.<BankCard>lambdaUpdate()
                    .eq(BankCard::getConsumerId, consumer.getId())
                    .set(BankCard::getStatus, statusCode.getCode())
                    .set(BankCard::getBankCode, response.getBankCode())
                    .set(BankCard::getBankName, response.getBankName()));
        }
        return false;
    }

    private Consumer getByRequestNo(String requestNo) {
        LambdaQueryWrapper<Consumer> queryWrapper = Wrappers.lambdaQuery(Consumer.class).eq(Consumer::getRequestNo, requestNo);
        return this.getOne(queryWrapper);
    }

    private void checkMobile(String mobile) {
        LambdaQueryWrapper<Consumer> queryWrapper = Wrappers.lambdaQuery(Consumer.class).eq(Consumer::getMobile, mobile);
        int count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ConsumerErrorCode.E_140107);
        }
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param userNo ????????????
     * @return
     */
    private RestResponse<BalanceDetailsDTO> getBalanceFromDepository(String userNo) {
        String url = depositoryURL + "/balance-details/" + userNo;
        BalanceDetailsDTO balanceDetailsDTO;
        Request request = new Request.Builder().url(url).build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                balanceDetailsDTO = JSON.parseObject(responseBody,
                        BalanceDetailsDTO.class);
                return RestResponse.success(balanceDetailsDTO);
            }
        } catch (IOException e) {
            log.warn("??????????????????{}?????????????????? ", url, e);
        }
        return RestResponse.validfail("????????????");
    }
}
