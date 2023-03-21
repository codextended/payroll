package dev.ona.payroll.resource;

import dev.ona.payroll.domain.Employee;
import dev.ona.payroll.service.PayrollService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/payroll")
@RequiredArgsConstructor
public class PayrollResource {

    private final PayrollService payrollService;

    @GetMapping(value = "/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello from Payroll Backend");
    }

    @PostMapping(value = "")
    public ResponseEntity<Employee> addEmployee(@RequestBody Employee employee) {
        return ResponseEntity.ok(payrollService.addEmployee(employee));
    }

    @GetMapping(value = "")
    public ResponseEntity<List<Employee>> getEmployees() {
        return ResponseEntity.ok(payrollService.getEmployees());
    }

    @GetMapping(value = "/csv")
    public void getPayrollFile(HttpServletResponse response) throws Exception {
        payrollService.getPayrollFile(response);
    }
}
