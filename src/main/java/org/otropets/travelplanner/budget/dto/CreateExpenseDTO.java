package org.otropets.travelplanner.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateExpenseDTO {
    private String name;
    private BigDecimal amount;
    private String description;
}
