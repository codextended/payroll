package dev.ona.payroll.service;

import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import dev.ona.payroll.domain.Employee;
import dev.ona.payroll.dto.EmployeeResponse;
import dev.ona.payroll.repository.EmployeeRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class PayrollService {

    private final EmployeeRepository employeeRepository;

    @Transactional
    public Employee addEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Transactional
    public List<Employee> getEmployees() {
        return employeeRepository.findAll();
    }

    @Transactional
    public void getPayrollFile(HttpServletResponse response) throws Exception{

        List<Employee> employees = employeeRepository.findAll();
        List<EmployeeResponse> employeeResponses = new ArrayList<>();

        for (Employee employee : employees) {
            EmployeeResponse employeeResponse = EmployeeResponse.builder()
                    .firstname(employee.getFirstname())
                    .lastname(employee.getLastname())
                    .actualSalary(employee.getActualSalary())
                    .entryDate(employee.getEntryDate())
                    .function(employee.getFunction())
                    .category(employee.getCategory().getName())
                    .monthOfService(calculateMonthsOfService(employee.getEntryDate()))
                    .mosPercentage(calculateMosPercentage(employee.getEntryDate(), employee.getCategory().getName()))
                    .newSalary(calculateNewSalary(employee.getActualSalary(), employee.getEntryDate(), employee.getCategory().getName()))
                    .build();
            employeeResponses.add(employeeResponse);
        }

        // Set file name and content type
        String filename = "PayrollAdjustment.csv";

        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename +"\"");

        String[] header = {"firstname", "lastname", "function", "category", "entryDate", "actualSalary", "monthOfService", "mosPercentage", "newSalary"};

        ColumnPositionMappingStrategy<EmployeeResponse> mappingStrategy = new ColumnPositionMappingStrategy<>();
        mappingStrategy.setType(EmployeeResponse.class);
        mappingStrategy.generateHeader(EmployeeResponse.builder().build());
        mappingStrategy.setColumnMapping(header);

        // create csv writer
        StatefulBeanToCsv<EmployeeResponse> writer = new StatefulBeanToCsvBuilder<EmployeeResponse>(response.getWriter())
                .withMappingStrategy(mappingStrategy)
                .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .withOrderedResults(true)
                .build();

        // write all employees to csv file
        writer.write(employeeResponses);
    }

    private BigDecimal calculateNewSalary(BigDecimal actualSalary, LocalDate entryDate, String name) {
        int percentage = calculateMosPercentage(entryDate, name);
        BigDecimal newSalary = actualSalary.multiply(BigDecimal.valueOf((double)percentage/100 + 1));

        return newSalary;
    }

    private int calculateMosPercentage(LocalDate entryDate, String name) {
        int monthsOfService = calculateMonthsOfService(entryDate);
        int finalPercentage = 0;

        switch (name) {
            case "B" -> {
                if (monthsOfService < 61) {
                    finalPercentage = 5;
                } else if (monthsOfService < 121) {
                    finalPercentage = 6;
                } else {
                    finalPercentage = 7;
                }
            }
            case "C", "D", "E" -> {
                if (monthsOfService < 61) {
                    finalPercentage = 10;
                } else if (monthsOfService < 121) {
                    finalPercentage = 12;
                } else {
                    finalPercentage = 15;
                }
            }
            case "F", "A" -> {
                if (monthsOfService < 61) {
                    finalPercentage = 3;
                } else if (monthsOfService < 121) {
                    finalPercentage = 4;
                } else {
                    finalPercentage = 5;
                }
            }
            case "H" -> {
                finalPercentage = 4;
            }
        }
        return finalPercentage;
    }

    private int calculateMonthsOfService(LocalDate entryDate) {

        long monthsBetween = ChronoUnit.MONTHS.between(
                entryDate.withDayOfMonth(1),
                LocalDate.now().withDayOfMonth(1)
        );
        return (int) monthsBetween;
    }
}
