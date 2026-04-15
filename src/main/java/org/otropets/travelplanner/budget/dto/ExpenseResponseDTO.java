package org.otropets.travelplanner.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseResponseDTO {
    private Long expenseId;
    private String name;
    private BigDecimal amount;
    private Long tripId;
    private LocalDateTime createdAt;
    private String createdByUsername;
    private String description;
}
