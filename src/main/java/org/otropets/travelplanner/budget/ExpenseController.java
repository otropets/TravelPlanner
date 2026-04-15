package org.otropets.travelplanner.budget;

import jakarta.validation.Valid;
import org.otropets.travelplanner.budget.dto.CreateExpenseDTO;
import org.otropets.travelplanner.budget.dto.ExpenseResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService service;
    public ExpenseController(ExpenseService service)
    {
        this.service = service;
    }

    @PostMapping("/{tripId}")
    public ResponseEntity<ExpenseResponseDTO> createExpense(@PathVariable Long tripId, @Valid @RequestBody CreateExpenseDTO request){
        ExpenseResponseDTO response = service.createExpense(tripId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long expenseId)
    {
        service.deleteExpense(expenseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/trip/{tripId}/list")
    public ResponseEntity<List<ExpenseResponseDTO>> getExpenses(@PathVariable Long tripId){
        List<ExpenseResponseDTO> res = service.getExpensesList(tripId);
        return ResponseEntity.ok(res);
    }




}
