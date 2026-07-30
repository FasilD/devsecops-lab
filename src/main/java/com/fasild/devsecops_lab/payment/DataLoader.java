package com.fasild.devsecops_lab;

import com.fasild.devsecops_lab.payment.Payment;
import com.fasild.devsecops_lab.payment.PaymentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadPayments(PaymentRepository repository) {
        return args -> {
            repository.save(
                    new Payment(
                            "merchant-a",
                            "Office equipment",
                            new BigDecimal("250.00")
                    )
            );

            repository.save(
                    new Payment(
                            "merchant-b",
                            "Software subscription",
                            new BigDecimal("95.00")
                    )
            );
        };
    }
}