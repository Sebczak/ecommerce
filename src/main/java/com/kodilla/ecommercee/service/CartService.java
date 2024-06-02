package com.kodilla.ecommercee.service;

import com.kodilla.ecommercee.dto.request.AddProductToCartRequest;
import com.kodilla.ecommercee.dto.request.CreateCartRequest;
import com.kodilla.ecommercee.dto.request.RemoveProductFromCartRequest;
import com.kodilla.ecommercee.dto.response.CartResponse;
import com.kodilla.ecommercee.dto.response.OrderResponse;
import com.kodilla.ecommercee.dto.response.ProductResponse;
import com.kodilla.ecommercee.entity.*;
import com.kodilla.ecommercee.exception.CartNotFoundException;
import com.kodilla.ecommercee.exception.ProductNotFoundException;
import com.kodilla.ecommercee.exception.UserNotFoundException;
import com.kodilla.ecommercee.mapper.CartMapper;
import com.kodilla.ecommercee.mapper.OrderMapper;
import com.kodilla.ecommercee.mapper.ProductMapper;
import com.kodilla.ecommercee.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;

    private final CartMapper cartMapper;
    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;

    public List<CartResponse> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        return cartMapper.mapToCartListResponse(carts);
    }

    public CartResponse getCart(Long id) throws CartNotFoundException {
        Cart cart = cartRepository.findById(id).orElseThrow(() -> new CartNotFoundException(id));
        return cartMapper.mapToCartResponse(cart);
    }

    public CartResponse createEmptyCart(CreateCartRequest createCartRequest) throws UserNotFoundException {
        User user = userRepository.findById(createCartRequest.userId()).orElseThrow(() -> new UserNotFoundException(createCartRequest.userId()));
        Cart cart = new Cart(user);
        cartRepository.save(cart);

        return cartMapper.mapToCartResponse(cart);
    }

    public CartResponse addProductToCart(AddProductToCartRequest addProductToCartRequest) throws CartNotFoundException, ProductNotFoundException {

        Cart cart = cartRepository.findById(addProductToCartRequest.cartId()).orElseThrow(() -> new CartNotFoundException(addProductToCartRequest.cartId()));
        Product product = productRepository.findById(addProductToCartRequest.productId()).orElseThrow(() -> new ProductNotFoundException(addProductToCartRequest.productId()));

        Optional<CartItem> optionalCartItem = cart.getCartItems().stream().filter(ci -> ci.getProduct().equals(product)).findFirst();

        if (optionalCartItem.isPresent()) {
            CartItem cartItem = optionalCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + addProductToCartRequest.quantity());
            cartItemRepository.save(cartItem);
        } else {
            cart.getCartItems().add(new CartItem(product, addProductToCartRequest.quantity(), cart));
        }

        cart.setTotalProductPrice(cart.getTotalProductPrice().add(product.getPrice().multiply(new BigDecimal(addProductToCartRequest.quantity()))));
        cartRepository.save(cart);

        product.setQuantity(product.getQuantity() - addProductToCartRequest.quantity());
        productRepository.save(product);

        return cartMapper.mapToCartResponse(cart);
    }

    public ProductResponse deleteProductFromCart(RemoveProductFromCartRequest removeProductFromCartRequest) throws CartNotFoundException, ProductNotFoundException {
        Cart cart = cartRepository.findById(removeProductFromCartRequest.cartId()).orElseThrow(() -> new CartNotFoundException(removeProductFromCartRequest.cartId()));

        CartItem cartItemToRemove = cart.getCartItems().stream()
                .filter(ci -> ci.getProduct().getId().equals(removeProductFromCartRequest.productId()))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException(removeProductFromCartRequest.productId()));

        cart.getCartItems().remove(cartItemToRemove);
        Product productToRemoveFromCart = cartItemToRemove.getProduct();

        productToRemoveFromCart.setQuantity(productToRemoveFromCart.getQuantity() - cartItemToRemove.getQuantity());
        cart.setTotalProductPrice(cart.getTotalProductPrice().subtract(productToRemoveFromCart.getPrice().multiply(new BigDecimal(cartItemToRemove.getQuantity()))));

        cartRepository.save(cart);
        productRepository.save(productToRemoveFromCart);

        return productMapper.mapToProductResponse(cartItemToRemove.getProduct());
    }

    public OrderResponse createOrderFromCart(Long cartId) throws CartNotFoundException{
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new CartNotFoundException(cartId));
        User user = cart.getUser();
        Order order = new Order(
                cart.getTotalProductPrice(),
                user.getAddress(),
                OrderStatus.UNPAID,
                user,
                cart
        );

        orderRepository.save(order);
        return orderMapper.toOrderResponse(order);
    }


}
