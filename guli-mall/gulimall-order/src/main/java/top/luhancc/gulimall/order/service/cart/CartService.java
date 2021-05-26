package top.luhancc.gulimall.order.service.cart;

import top.luhancc.gulimall.order.domain.cat.Cart;
import top.luhancc.gulimall.order.domain.cat.CartItem;

import java.util.List;

/**
 * @author luHan
 * @create 2021/1/13 10:35
 * @since 1.0.0
 */
public interface CartService {
    CartItem addToCart(Long skuId, Integer count);

    CartItem getCartItem(Long skuId);

    Cart getCart();

    void checkItem(Long skuId, Boolean check);

    void countItem(Long skuId, Integer count);

    void deleteItem(Long skuId);

    List<CartItem> getUserCartItems(Long userId);
}
