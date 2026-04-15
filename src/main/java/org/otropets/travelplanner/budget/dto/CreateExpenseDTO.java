package org.otropets.travelplanner.budget.dto;

import lombok.Data;


import java.math.BigDecimal;

@Data
public class CreateExpenseDTO {
    private String name;
    private BigDecimal amount;
    private String description;
}
