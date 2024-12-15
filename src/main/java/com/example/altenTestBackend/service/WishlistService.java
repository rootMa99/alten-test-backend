package com.example.altenTestBackend.service;

import com.example.altenTestBackend.model.Product;
import com.example.altenTestBackend.model.User;
import com.example.altenTestBackend.model.Wishlist;
import com.example.altenTestBackend.model.WishlistItem;
import com.example.altenTestBackend.repository.ProductRepository;
import com.example.altenTestBackend.repository.UserRepository;
import com.example.altenTestBackend.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WishlistService {
    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public Wishlist getCurrentUserWishlist() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        User user = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wishlist wishlist = wishlistRepository.findByUserId(user.getId());
        if (wishlist == null) {
            wishlist = new Wishlist();
            wishlist.setUser(user);
            wishlistRepository.save(wishlist);
        }

        return wishlist;
    }

    @Transactional
    public Wishlist addToWishlist(Long productId) {
        Wishlist wishlist = getCurrentUserWishlist();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        WishlistItem wishlistItem = new WishlistItem();
        wishlistItem.setProduct(product);

        wishlist.getItems().add(wishlistItem);

        return wishlistRepository.save(wishlist);
    }

    @Transactional
    public Wishlist removeFromWishlist(Long productId) {
        Wishlist wishlist = getCurrentUserWishlist();

        wishlist.getItems().removeIf(item -> item.getProduct().getId().equals(productId));

        return wishlistRepository.save(wishlist);
    }
}