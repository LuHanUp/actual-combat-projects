package top.luhancc.gulimall.ware.service.impl;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.common.utils.Query;

import top.luhancc.common.utils.R;
import top.luhancc.gulimall.ware.dao.WareInfoDao;
import top.luhancc.gulimall.ware.domain.vo.MemberAddressVo;
import top.luhancc.gulimall.ware.entity.WareInfoEntity;
import top.luhancc.gulimall.ware.feign.MemberFeign;
import top.luhancc.gulimall.ware.service.WareInfoService;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {
    private MemberFeign memberFeign;

    @Override
    public PageUtils<WareInfoEntity> queryPage(Map<String, Object> params) {
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils<>(page);
    }

    @Override
    public BigDecimal getFare(Long addrId) {
        R r = memberFeign.info(addrId);
        MemberAddressVo memberAddressVo = r.get("memberReceiveAddress", MemberAddressVo.class);
        // 就拿用户手机号的最后一位当做运费,TODO 可以从物流api中获取运费
        String phone = memberAddressVo.getPhone();
        String fare = phone.substring(phone.length() - 1, phone.length());
        return new BigDecimal(fare);
    }

}