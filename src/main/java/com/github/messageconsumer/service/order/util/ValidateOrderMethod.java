package com.github.messageconsumer.service.order.util;


import com.github.messageconsumer.entity.Cart;
import com.github.messageconsumer.entity.Order;
import com.github.messageconsumer.entity.Product;
import com.github.messageconsumer.entity.User;
import com.github.messageconsumer.repository.CartRepository;
import com.github.messageconsumer.repository.OrderRepository;
import com.github.messageconsumer.repository.ProductRepository;
import com.github.messageconsumer.repository.UserRepository;
import com.github.messageconsumer.service.cart.exception.CartErrorCode;
import com.github.messageconsumer.service.cart.exception.CartException;
import com.github.messageconsumer.service.order.exception.OrderErrorCode;
import com.github.messageconsumer.service.order.exception.OrderException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class ValidateOrderMethod {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    public User validateUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new OrderException(OrderErrorCode.USER_NOT_FOUND));
    }

    public Order validateOrder(Long orderId, Long userId) {
        return orderRepository.findByIdAndUsersId(orderId, userId)
                .orElseThrow(() -> new OrderException(OrderErrorCode.THIS_ORDER_DOES_NOT_EXIST));
    }

    public Product validateProduct(Long productId){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new OrderException(OrderErrorCode.THIS_PRODUCT_DOES_NOT_EXIST));

        Integer stock = product.getLeftAmount();
        if (stock == null || stock <= 0) {
            throw new OrderException(OrderErrorCode.OUT_OF_STOCK);
        }

        return product;
    }

    public boolean validateStock(Integer inputQuantity, Product product){
        return inputQuantity > 0 && inputQuantity <= product.getLeftAmount();
    }

    public Cart validateCart(Long cartId, Long userId){
        return cartRepository.findByIdAndUsersId(cartId, userId).orElseThrow(()->new CartException(CartErrorCode.THIS_CART_DOES_NOT_EXIST));
    }


    private boolean existsInCart(Long userId, Long productId){
        return cartRepository.existsByUsersIdAndProductsId(userId, productId);
    }
}
