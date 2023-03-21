package dev.ona.payroll.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public class EmployeeResponse {
    private String firstname;
    private String lastname;
    private LocalDate entryDate;
    private String function;
    private BigDecimal actualSalary;
    private BigDecimal newSalary;
    private String category;
    private int monthOfService;
    private int mosPercentage;

}
