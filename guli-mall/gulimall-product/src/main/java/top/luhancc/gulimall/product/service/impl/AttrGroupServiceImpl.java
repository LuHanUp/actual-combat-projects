package top.luhancc.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.luhancc.common.utils.PageUtils;
import top.luhancc.common.utils.Query;

import top.luhancc.gulimall.product.dao.AttrGroupDao;
import top.luhancc.gulimall.product.entity.AttrGroupEntity;
import top.luhancc.gulimall.product.service.AttrGroupService;
import top.luhancc.gulimall.product.vo.web.SkuItemVo;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuItemVo.SpuItemGroupAttrVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {
        /*
         * 查询sql：
            SELECT pav.spu_id,
                   ag.attr_group_name,
                   ag.attr_group_id,
                   aar.attr_id,
                   attr.attr_name,
                   pav.attr_value
            FROM pms_attr_group ag
                     LEFT JOIN pms_attr_attrgroup_relation aar on ag.attr_group_id = aar.attr_group_id
                     LEFT JOIN pms_attr attr on aar.attr_id = attr.attr_id
                     LEFT JOIN pms_product_attr_value pav on aar.attr_id = pav.attr_id
            WHERE ag.catelog_id = 225 and spu_id = 13; -- {catalogId} and pav.spu_id = #{spuId}
         *
         */
        return null;
    }

}