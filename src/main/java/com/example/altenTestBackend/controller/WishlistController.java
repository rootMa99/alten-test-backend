package com.example.altenTestBackend.controller;
import com.example.altenTestBackend.model.Wishlist;
import com.example.altenTestBackend.service.WishlistService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {
    @Autowired
    private WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<Wishlist> getWishlist() {
        Wishlist wishlist = wishlistService.getCurrentUserWishlist();
        return ResponseEntity.ok(wishlist);
    }

    @PostMapping("/add")
    public ResponseEntity<Wishlist> addToWishlist(@RequestBody WishlistItemRequest request) {
        Wishlist updatedWishlist = wishlistService.addToWishlist(request.getProductId());
        return ResponseEntity.ok(updatedWishlist);
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Wishlist> removeFromWishlist(@PathVariable Long productId) {
        Wishlist updatedWishlist = wishlistService.removeFromWishlist(productId);
        return ResponseEntity.ok(updatedWishlist);
    }

    @Data
    public static class WishlistItemRequest {
        private Long productId;

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }
    }
}