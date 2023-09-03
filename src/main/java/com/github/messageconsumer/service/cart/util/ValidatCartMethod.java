package com.github.messageconsumer.service.cart.util;

import com.github.messageconsumer.entity.Cart;
import com.github.messageconsumer.entity.Product;
import com.github.messageconsumer.entity.User;
import com.github.messageconsumer.repository.CartRepository;
import com.github.messageconsumer.repository.ProductRepository;
import com.github.messageconsumer.repository.UserRepository;
import com.github.messageconsumer.service.cart.exception.CartErrorCode;
import com.github.messageconsumer.service.cart.exception.CartException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class ValidatCartMethod {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;


    public User validateUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new CartException(CartErrorCode.USER_NOT_FOUND));
    }

    public void validateStock(Integer inputQuantity, Product product){
        if (inputQuantity <= 0 || inputQuantity > product.getLeftAmount()) {
            throw new CartException(CartErrorCode.INVALID_QUANTITY);
        }
    }

    public Product validateProduct(Long productId){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CartException(CartErrorCode.THIS_PRODUCT_DOES_NOT_EXIST));

        Long stock = product.getLeftAmount();
        if (stock == null || stock <= 0) {
            throw new CartException(CartErrorCode.OUT_OF_STOCK);
        }

        return product;
    }

    public Cart validateCart(Long cartId, Long userId){
        Cart cart = cartRepository.findByIdAndUsersId(cartId, userId);

        if (cart == null) {
            throw new CartException(CartErrorCode.THIS_CART_DOES_NOT_EXIST);
        }
        return cart;
    }

    private boolean existsInCart(Long userId, Long productId){
        return cartRepository.existsByUsersIdAndProductsId(userId, productId);
    }
}
