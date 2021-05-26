package top.luhancc.gulimall.order.service.cart.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.luhancc.common.to.product.ProductInfo;
import top.luhancc.common.utils.R;
import top.luhancc.gulimall.order.domain.cat.Cart;
import top.luhancc.gulimall.order.domain.cat.CartItem;
import top.luhancc.gulimall.order.domain.cat.to.UserInfoTo;
import top.luhancc.gulimall.order.feign.ProductFeign;
import top.luhancc.gulimall.order.interceptor.CartInterceptor;
import top.luhancc.gulimall.order.service.cart.CartService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author luHan
 * @create 2021/1/13 10:35
 * @since 1.0.0
 */
@Service
@Slf4j
public class CartServiceImpl implements CartService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ProductFeign productFeign;

    private final String CART_PREFIX = "gulimall:cart:";

    @Override
    public CartItem addToCart(Long skuId, Integer count) {
        BoundHashOperations<String, String, String> operations = getCartOps();
        String s = operations.get(skuId.toString());
        if (StringUtils.isNotEmpty(s)) {
            // 更新叠加数量即可
            CartItem cartItem = JSON.parseObject(s, CartItem.class);
            cartItem.setCount(cartItem.getCount() + count);
            operations.put(skuId.toString(), JSON.toJSONString(cartItem));
            return cartItem;
        }
        CartItem cartItem = new CartItem();
        CompletableFuture<Void> itemInfoFuture = CompletableFuture.runAsync(() -> {
            // 查询sku的基本信息
            R r = productFeign.getSkuInfoById(skuId);
            if (r.isSuccess()) {
                ProductInfo skuInfo = r.get("skuInfo", ProductInfo.class);
                cartItem.setSkuId(skuId);
                cartItem.setCheck(true);
                cartItem.setTitle(skuInfo.getSkuTitle());
                cartItem.setImage(skuInfo.getSkuDefaultImg());
                cartItem.setPrice(skuInfo.getPrice());
                cartItem.setCount(count);
            } else {
                log.error("远程获取商品信息失败:{}", r);
            }
        });
        CompletableFuture<Void> skuAttrValuesFuture = CompletableFuture.runAsync(() -> {
            List<String> skuSaleAttrValues = productFeign.getSkuSaleAttrValues(skuId);
            cartItem.setAttr(skuSaleAttrValues);
        });
        CompletableFuture.allOf(itemInfoFuture, skuAttrValuesFuture).join();
        operations.put(skuId.toString(), JSON.toJSONString(cartItem));
        return cartItem;
    }

    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, String, String> operations = getCartOps();
        String s = operations.get(skuId.toString());
        return JSON.parseObject(s, CartItem.class);
    }

    @Override
    public Cart getCart() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        Cart cart = new Cart();
        if (userInfoTo.getUserId() != null) {
            // 看临时购物车中的数据有没有,如果有就进行合并
            String tempCartKey = cartKey(userInfoTo.getUserKey());
            List<CartItem> tempCartItemList = getCartItems(tempCartKey);
            if (!CollectionUtils.isEmpty(tempCartItemList)) {
                for (CartItem cartItem : tempCartItemList) {
                    addToCart(cartItem.getSkuId(), cartItem.getCount());
                }
                // 清空临时购物车中的数据
                redisTemplate.opsForHash().delete(tempCartKey);
            }
            // 在获取登录后的购物车,这样就包含了临时购物车中的数据
            String cartKey = cartKey(userInfoTo.getUserId());
            List<CartItem> cartItemList = getCartItems(cartKey);
            cart.setItems(cartItemList);
        } else {
            // 临时购物车中的数据
            String cartKey = cartKey(userInfoTo.getUserKey());
            List<CartItem> cartItemList = getCartItems(cartKey);
            cart.setItems(cartItemList);
        }
        return cart;
    }

    @Override
    public void checkItem(Long skuId, Boolean check) {
        CartItem cartItem = this.getCartItem(skuId);
        cartItem.setCheck(check);
        BoundHashOperations<String, String, String> operations = getCartOps();
        operations.put(skuId.toString(), JSON.toJSONString(cartItem));
    }

    @Override
    public void countItem(Long skuId, Integer count) {
        CartItem cartItem = this.getCartItem(skuId);
        cartItem.setCount(count);
        BoundHashOperations<String, String, String> operations = getCartOps();
        operations.put(skuId.toString(), JSON.toJSONString(cartItem));
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, String, String> operations = getCartOps();
        operations.delete(skuId.toString());
    }

    @Override
    public List<CartItem> getUserCartItems(Long userId) {
        String cartKey = cartKey(userId.toString());
        List<CartItem> cartItems = getCartItems(cartKey);
        return cartItems.stream().filter(CartItem::getCheck).collect(Collectors.toList());
    }

    private BoundHashOperations<String, String, String> getCartOps() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey = "";
        if (userInfoTo.getUserId() != null) {
            cartKey = cartKey(userInfoTo.getUserId());
        } else {
            cartKey = cartKey(userInfoTo.getUserKey());
        }
        return redisTemplate.boundHashOps(cartKey);
    }

    private String cartKey(Object token) {
        return CART_PREFIX + token.toString();
    }

    /**
     * 获取购物车中的商品项
     *
     * @param cartKey
     * @return
     */
    private List<CartItem> getCartItems(String cartKey) {
        List<Object> values = redisTemplate.opsForHash().values(cartKey);
        if (!CollectionUtils.isEmpty(values)) {
            return values.stream()
                    .map(value -> JSON.parseObject(value.toString(), CartItem.class))
                    .map(cartItem -> {
                        BigDecimal price = productFeign.getPriceBySkuId(cartItem.getSkuId());
                        cartItem.setPrice(price);
                        return cartItem;
                    })
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
