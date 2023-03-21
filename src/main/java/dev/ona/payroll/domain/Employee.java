package dev.ona.payroll.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    @Id
    @GeneratedValue
    private UUID id;
    private String firstname;
    private String lastname;
    private LocalDate entryDate;
    private String function;

    @OneToOne
    @JoinColumn(
            name = "category_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "category_id_fk"
            )
    )
    private Category category;
    private BigDecimal actualSalary;
}
