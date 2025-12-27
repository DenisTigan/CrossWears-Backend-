package com.example.ecom.project.service;


import com.example.ecom.project.model.Cart;
import com.example.ecom.project.model.CartItem;
import com.example.ecom.project.model.Product;
import com.example.ecom.project.model.User;
import com.example.ecom.project.repo.CartRepository;
import com.example.ecom.project.repo.ProductsRepository;
import com.example.ecom.project.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductsRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    public Cart getCartByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilizator negăsit"));
        return cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(new Cart(user)));
    }

    @Transactional
    public Cart addToCart(String email, int productId, int quantity) {
        Cart cart = getCartByUser(email);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produs negăsit"));

        // Verificăm dacă produsul există deja în coș folosind getProductId()
        java.util.Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getProductId() == product.getProductId())
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem(cart, product, quantity);
            cart.getItems().add(newItem);
        }

        cart.recalculateTotal();
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeFromCart(String email, Long itemId) {
        Cart cart = getCartByUser(email);
        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        cart.recalculateTotal();
        return cartRepository.save(cart);
    }
}
