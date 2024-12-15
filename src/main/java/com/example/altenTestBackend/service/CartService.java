package com.example.altenTestBackend.service;

import com.example.altenTestBackend.model.Cart;
import com.example.altenTestBackend.model.CartItem;
import com.example.altenTestBackend.model.Product;
import com.example.altenTestBackend.model.User;
import com.example.altenTestBackend.repository.CartRepository;
import com.example.altenTestBackend.repository.ProductRepository;
import com.example.altenTestBackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public Cart getCurrentUserCart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        User user = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUserId(user.getId());
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);
        }

        return cart;
    }

    @Transactional
    public Cart addToCart(Long productId, int quantity) {
        Cart cart = getCurrentUserCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if product already in cart
        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(item.getQuantity() + quantity);
                return cartRepository.save(cart);
            }
        }

        // If not in cart, add new item
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);

        cart.getItems().add(cartItem);

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeFromCart(Long productId) {
        Cart cart = getCurrentUserCart();

        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateCartItemQuantity(Long productId, int quantity) {
        Cart cart = getCurrentUserCart();

        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(quantity);
                break;
            }
        }

        return cartRepository.save(cart);
    }
}