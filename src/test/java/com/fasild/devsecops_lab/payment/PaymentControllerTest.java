package com.fasild.devsecops_lab.payment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnAllPayments() throws Exception {
        mockMvc.perform(get("/api/payments")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldReturnNotFoundForUnknownPayment() throws Exception {
        mockMvc.perform(get("/api/payments/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectPaymentWithNegativeAmount() throws Exception {
        String requestBody = """
        {
          "merchantId": "merchant-test",
          "description": "Invalid payment",
          "amount": -10.00
        }
        """;

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.fields.amount").exists());
    }

    @Test
    void shouldCreateValidPayment() throws Exception {
        String requestBody = """
        {
          "merchantId": "merchant-test",
          "description": "Security assessment",
          "amount": 450.00
        }
        """;

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.merchantId").value("merchant-test"))
                .andExpect(jsonPath("$.description").value("Security assessment"))
                .andExpect(jsonPath("$.amount").value(450.00));
    }

    @Test
    void shouldUpdateExistingPayment() throws Exception {
        String requestBody = """
        {
          "merchantId": "merchant-a",
          "description": "Updated equipment purchase",
          "amount": 300.00
        }
        """;

        mockMvc.perform(put("/api/payments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description")
                        .value("Updated equipment purchase"))
                .andExpect(jsonPath("$.amount").value(300.00));
    }

    @Test
    void shouldDeleteExistingPayment() throws Exception {
        mockMvc.perform(delete("/api/payments/2"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/payments/2"))
                .andExpect(status().isNotFound());
    }
}