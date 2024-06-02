package com.kodilla.ecommercee.controller;

import com.kodilla.ecommercee.dto.request.AddProductToCartRequest;
import com.kodilla.ecommercee.dto.request.CreateCartRequest;
import com.kodilla.ecommercee.dto.request.RemoveProductFromCartRequest;
import com.kodilla.ecommercee.dto.response.CartItemResponse;
import com.kodilla.ecommercee.dto.response.CartResponse;
import com.kodilla.ecommercee.dto.response.OrderResponse;
import com.kodilla.ecommercee.dto.response.ProductResponse;
import com.kodilla.ecommercee.exception.CartNotFoundException;
import com.kodilla.ecommercee.exception.ProductNotFoundException;
import com.kodilla.ecommercee.exception.UserNotFoundException;
import com.kodilla.ecommercee.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("shop/v1/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping("{cartId}")
    public ResponseEntity<List<CartItemResponse>> getAllProductsFromCart(@PathVariable Long cartId) throws CartNotFoundException {
        return ResponseEntity.ok(cartService.getCart(cartId).productResponses());
    }

    @PostMapping
    public ResponseEntity<CartResponse> createCart(@RequestBody CreateCartRequest createCartRequest) throws UserNotFoundException {
        return ResponseEntity.ok(cartService.createEmptyCart(createCartRequest));
    }

    @PutMapping("/item")
    public ResponseEntity<CartResponse> addProductToCart(@RequestBody AddProductToCartRequest addProductToCartRequest) throws CartNotFoundException, ProductNotFoundException {
        return ResponseEntity.ok(cartService.addProductToCart(addProductToCartRequest));
    }

    @PostMapping("/{cartId}")
    public ResponseEntity<OrderResponse> createOrderFromCart(@PathVariable Long cartId) throws CartNotFoundException {
        return ResponseEntity.ok(cartService.createOrderFromCart(cartId));
    }

    @DeleteMapping
    public ResponseEntity<ProductResponse> deleteProductFromCart(@RequestBody RemoveProductFromCartRequest removeProductFromCartRequest) throws CartNotFoundException, ProductNotFoundException {
        return ResponseEntity.ok(cartService.deleteProductFromCart(removeProductFromCartRequest));
    }

}