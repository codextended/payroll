package dev.ona.payroll.repository;

import dev.ona.payroll.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
}
