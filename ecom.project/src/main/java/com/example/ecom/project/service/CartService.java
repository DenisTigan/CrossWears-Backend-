package com.example.ecom.project.service;


import com.example.ecom.project.dto.CartRequest;
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
    public Cart addToCart(String email, CartRequest req) {
        Cart cart = getCartByUser(email);
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Produs negăsit"));

        // Filtrare avansată: ID + Culoare + Mărime
        java.util.Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getProductId() == product.getProductId()
                        && item.getColor().equals(req.getColor())
                        && item.getSize().equals(req.getSize()))
                .findFirst();

        if (existingItem.isPresent()) {
            // Dacă rândul există deja, adunăm cantitatea
            existingItem.get().setQuantity(existingItem.get().getQuantity() + req.getQuantity());
        } else {
            // Dacă este o combinație nouă (ex: altă mărime), creăm un CartItem nou
            CartItem newItem = new CartItem(cart, product, req.getColor(), req.getSize(), req.getQuantity());
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
