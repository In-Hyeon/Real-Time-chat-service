package com.example.springbootpractice.domain.gift.controller;

import com.example.springbootpractice.domain.gift.dto.GiftOrderCreateRequest;
import com.example.springbootpractice.domain.gift.dto.GiftOrderResponse;
import com.example.springbootpractice.domain.gift.dto.GiftProductCreateRequest;
import com.example.springbootpractice.domain.gift.dto.GiftProductResponse;
import com.example.springbootpractice.domain.gift.entity.GiftProduct;
import com.example.springbootpractice.domain.gift.entity.Voucher;
import com.example.springbootpractice.domain.gift.service.GiftService;
import com.example.springbootpractice.global.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gifts")
@RequiredArgsConstructor
public class GiftController {

    private final GiftService giftService;

    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<GiftProductResponse> createProduct(@RequestBody GiftProductCreateRequest request) {
        GiftProduct product = giftService.createProduct(request.productName(), request.brandName(), request.price());
        return ApiResponse.success(GiftProductResponse.from(product));
    }

    @GetMapping("/products")
    public ApiResponse<List<GiftProductResponse>> findProducts() {
        List<GiftProductResponse> products =
                giftService.findAllProducts().stream().map(GiftProductResponse::from).toList();
        return ApiResponse.success(products);
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<GiftOrderResponse> order(@AuthenticationPrincipal Long userId,
                                                 @RequestBody GiftOrderCreateRequest request) {
        Voucher voucher = giftService.order(userId, request.receiverUserId(), request.productId());
        return ApiResponse.success(GiftOrderResponse.of(voucher.getOrder(), voucher));
    }

    @GetMapping("/orders/sent")
    public ApiResponse<List<GiftOrderResponse>> sentOrders(@AuthenticationPrincipal Long userId) {
        return ApiResponse.success(giftService.findSentOrders(userId));
    }

    @GetMapping("/orders/received")
    public ApiResponse<List<GiftOrderResponse>> receivedOrders(@AuthenticationPrincipal Long userId) {
        return ApiResponse.success(giftService.findReceivedOrders(userId));
    }

    @PatchMapping("/vouchers/{id}/use")
    public ApiResponse<GiftOrderResponse> useVoucher(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        Voucher voucher = giftService.useVoucher(userId, id);
        return ApiResponse.success(GiftOrderResponse.of(voucher.getOrder(), voucher));
    }
}
