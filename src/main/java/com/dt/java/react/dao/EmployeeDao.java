package com.dt.java.react.dao;

import com.dt.java.react.beans.Employee;
import com.dt.java.react.dto.EmployeeDTO;
import com.dt.java.react.utils.Designation;
import com.dt.java.react.utils.Utils;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeDao {

    public Employee toEmployee(EmployeeDTO employeeDTO) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Utils.DATE_FORMAT);
        Employee employee = new Employee();
        employee.setId(Integer.valueOf(employeeDTO.getId()));
        employee.setName(employeeDTO.getName());
        employee.setDepartment(employeeDTO.getDepartment());
        employee.setDesignation(Designation.get(employeeDTO.getDesignation()));
        employee.setSalary(Float.valueOf(employeeDTO.getSalary()));
        employee.setJoigningDate(
                Date.from(LocalDate.parse(employeeDTO.getJoigningDate(), formatter)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()));
        return employee;
    }

    public EmployeeDTO toEmployeeDTO(Employee employee) {
        final DateFormat formatter = new SimpleDateFormat(Utils.DATE_FORMAT);
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setId(String.valueOf(employee.getId()));
        employeeDTO.setName(employee.getName());
        employeeDTO.setDepartment(employee.getDepartment());
        employeeDTO.setDesignation(employee.getDesignation().getName());
        employeeDTO.setSalary(String.valueOf(employee.getSalary()));
        employeeDTO.setJoigningDate(formatter.format(employee.getJoigningDate()));
        return employeeDTO;
    }

    public List<Employee> convertToEmployee(List<EmployeeDTO> employeeDTOs) {
        return employeeDTOs.stream().map(employeeDTO -> toEmployee(employeeDTO))
                .collect(Collectors.toList());
    }

    public List<EmployeeDTO> convertToEmployeeDTO(List<Employee> employees) {
        return employees.stream().map(employee -> toEmployeeDTO(employee))
                .collect(Collectors.toList());
    }
}
