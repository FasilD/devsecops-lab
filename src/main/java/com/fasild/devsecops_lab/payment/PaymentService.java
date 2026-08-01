package com.fasild.devsecops_lab.payment;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository repository;

    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }

    public List<Payment> getAllPayments() {
        return repository.findAll();
    }

    public Payment getPaymentById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
    }

    public Payment createPayment(Payment payment) {
        payment.setId(null);
        return repository.save(payment);
    }

    public List<Payment> getPaymentsByMerchantId(String merchantId) {
        return repository.findByMerchantId(merchantId);
    }

    public Payment updatePayment(Long id, Payment updatedPayment) {
        Payment existingPayment = getPaymentById(id);

        existingPayment.setMerchantId(updatedPayment.getMerchantId());
        existingPayment.setDescription(updatedPayment.getDescription());
        existingPayment.setAmount(updatedPayment.getAmount());

        return repository.save(existingPayment);
    }

    public void deletePayment(Long id) {
        Payment payment = getPaymentById(id);
        repository.delete(payment);
    }
}