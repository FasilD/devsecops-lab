package com.fasild.devsecops_lab.payment;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @GetMapping
    public List<Payment> getAllPayments() {
        return service.getAllPayments();
    }

    @GetMapping("/{id}")
    public Payment getPaymentById(@PathVariable Long id) {
        return service.getPaymentById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Payment createPayment(@Valid @RequestBody Payment payment) {
        return service.createPayment(payment);
    }

    @GetMapping("/merchant/{merchantId}")
    public List<Payment> getPaymentsByMerchantId(
            @PathVariable String merchantId) {

        return service.getPaymentsByMerchantId(merchantId);
    }

    @PutMapping("/{id}")
    public Payment updatePayment(
            @PathVariable Long id,
            @Valid @RequestBody Payment payment) {

        return service.updatePayment(id, payment);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePayment(@PathVariable Long id) {
        service.deletePayment(id);
    }
}