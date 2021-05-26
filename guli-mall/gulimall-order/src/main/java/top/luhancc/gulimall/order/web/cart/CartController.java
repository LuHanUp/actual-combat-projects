package top.luhancc.gulimall.order.web.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import top.luhancc.gulimall.order.domain.cat.Cart;
import top.luhancc.gulimall.order.domain.cat.CartItem;
import top.luhancc.gulimall.order.service.cart.CartService;

/**
 * 购物车api
 *
 * @author luHan
 * @create 2021/1/12 19:42
 * @since 1.0.0
 */
@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    /**
     * 浏览器有一个cookie：user-key标识用户身份，一个月后过期
     * 如果第一次使用jd的购物车功能，都会给一个临时的用户身份
     * 浏览器保存后，每次访问都会带上这个cookie
     * <p>
     * 登录：session有
     * 没登录：按照cookie里面带来user-key来做
     * 第一次：如果没有临时用户，帮忙创建一个临时用户
     *
     * @return
     */
    @GetMapping("/cart.html")
    public String cartListPage(Model model) {
        Cart cart = cartService.getCart();
        model.addAttribute("cart", cart);
        return "cart/cartList";
    }

    /**
     * 添加商品到购物车
     *
     * @param skuId skuId
     * @param count 添加的数量
     * @return
     */
    @GetMapping("/addToCart")
    public String add2Cart(@RequestParam("skuId") Long skuId, @RequestParam("count") Integer count,
                           RedirectAttributes ra) {
        cartService.addToCart(skuId, count);
        // 因为是重定向,RedirectAttributes重定向传值
        ra.addAttribute("skuId", skuId);
        return "redirect:http://cart.gulimall.com/success.html";
    }

    /**
     * 重定向到添加商品成功页面
     *
     * @param skuId 商品id
     * @return
     */
    @GetMapping("/success.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId, Model model) {
        CartItem cartItem = cartService.getCartItem(skuId);
        model.addAttribute("item", cartItem);
        return "cart/success";
    }

    /**
     * 修改购物车商品的选中状态
     *
     * @param skuId 商品id
     * @param check true-选中 false-没有选中
     * @return
     */
    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId") Long skuId, @RequestParam("check") Boolean check) {
        cartService.checkItem(skuId, check);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    /**
     * 修改购物车商品的购买数量
     *
     * @param skuId 商品id
     * @param count 商品数量
     * @return
     */
    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId") Long skuId, @RequestParam("count") Integer count) {
        cartService.countItem(skuId, count);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    /**
     * 删除购物车中的指定商品
     *
     * @param skuId 商品id
     * @return
     */
    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId) {
        cartService.deleteItem(skuId);
        return "redirect:http://cart.gulimall.com/cart.html";
    }
}
