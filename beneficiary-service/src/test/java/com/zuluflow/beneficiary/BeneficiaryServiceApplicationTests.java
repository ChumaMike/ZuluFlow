package com.zuluflow.beneficiary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zuluflow.beneficiary.domain.beneficiary.Beneficiary;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc // Allows us to fake HTTP requests
class BeneficiaryServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc; // The "Fake Browser"

    @Autowired
    private ObjectMapper objectMapper; // Converts Java Objects -> JSON

    @Test
    void shouldCreateBeneficiarySuccessfully() throws Exception {
        // 1. Prepare the Data (The "Given")
        // We use a raw Map or a Builder here. Let's use raw JSON for clarity.
        String jsonPayload = """
            {
                "clientId": "TEST-CLIENT-001",
                "name": "Integration Test Corp",
                "accountNumber": "9876543210",
                "bankCode": "250655",
                "accountType": "CHEQUE",
                "status": "ACTIVE" 
            }
        """;
        // Note: We send "ACTIVE" to test if the system forces it back to "PENDING"

        // 2. Perform the Request (The "When")
        mockMvc.perform(post("/api/v1/beneficiaries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                // 3. Verify the Output (The "Then")
                .andExpect(status().isCreated()) // Expect HTTP 201
                .andExpect(jsonPath("$.id").exists()) // Expect an ID was generated
                .andExpect(jsonPath("$.status").value("PENDING")) // CRITICAL: Security check
                .andExpect(jsonPath("$.name").value("Integration Test Corp"));
    }

}