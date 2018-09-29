package com.rakuten.fullstackrecruitmenttest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.rakuten.fullstackrecruitmenttest.dao.EmployeeDao;
import com.rakuten.fullstackrecruitmenttest.dto.EmployeeDTO;
import com.rakuten.fullstackrecruitmenttest.beans.Employee;
import com.rakuten.fullstackrecruitmenttest.exception.EmployeeException;
import com.rakuten.fullstackrecruitmenttest.utils.Designation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.rakuten.fullstackrecruitmenttest.utils.Utils.*;

@Service
public class EmployeeService {

    @Autowired
    EmployeeDao employeeDao;

    public String checkEmployeeInfoCompliance(List<EmployeeDTO> employeesDTOList){
        String exceptionMessage = "";
        int line = 0;
        for(EmployeeDTO employeeDTO : employeesDTOList) {
            try {
                line++;
                employeeDTO.setId(String.valueOf(line));
                verify(employeeDTO);
            } catch (EmployeeException e) {
                try {
                    createOrUpdateErrorFile(e.getMessage());
                    exceptionMessage += "\n" + e.getMessage();
                } catch (IOException e1) {
                    System.out.println(UNKNOWN_FILE_EXCEPTION_MESSAGE);
                }
            }
        }
        return exceptionMessage;
    }

    public void createOrUpdateErrorFile(String message) throws IOException {
        File file = new File(LOG_ERROR_FILE_NAME);
        FileWriter writer = new FileWriter(file, true);
        BufferedWriter bw = new BufferedWriter(writer);
        bw.write(message);
        bw.newLine();
        bw.close();
    }

    public void createOrUpdateDataFile(List<Employee> employees) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(DATA_JSON_FILE_NAME), employees);
    }

    public List<Employee> getDataFromFile() throws IOException{
        List<Employee> employeeList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        File file = new File(DATA_JSON_FILE_NAME);
        if (file.exists()) {
            employeeList = objectMapper.readValue(new File(DATA_JSON_FILE_NAME),
                    typeFactory.constructCollectionType(List.class, Employee.class));
        }

        return employeeList;
    }

    public List<EmployeeDTO> getEmployeeList() {
        List<EmployeeDTO> employeeDTOs = null;
        try {
            List<Employee> employees = getDataFromFile();
            employeeDTOs = employeeDao.convertToEmployeeDTO(employees);
        } catch (IOException e) {
            System.out.println(UNKNOWN_FILE_EXCEPTION_MESSAGE);
        }
        return employeeDTOs;
    }

    public void save(List<EmployeeDTO> employeeDTOs) {
        List<Employee> employees = employeeDao.convertToEmployee(employeeDTOs);
        try {
            createOrUpdateDataFile(employees);
        } catch (IOException e) {
            try {
                createOrUpdateErrorFile(e.getMessage());
            } catch (IOException e1) {
                System.out.println(UNKNOWN_FILE_EXCEPTION_MESSAGE);
            }
        }
    }

    public List<EmployeeDTO> update(EmployeeDTO employeeDTO) {
        List<EmployeeDTO> employeeDTOs = getEmployeeList();

        //replace the updated employee into the whole list.
        employeeDTOs = employeeDTOs.stream().map(employee -> {
            if (employee.getId().equals(employeeDTO.getId())) {
                return employeeDTO;
            }
            return employee;
        }).collect(Collectors.toList());

        save(employeeDTOs);
        return employeeDTOs;
    }


    public void verify(EmployeeDTO employeeDTO) throws EmployeeException {
        if (!StringUtils.isAlpha(employeeDTO.getName())) {
            throw new EmployeeException("Not an alphabet name:\"" + employeeDTO.getName() + "\" at Line:" + employeeDTO.getId());
        } else if (!employeeDTO.getDepartment().matches(DEPARTMENT_PATTERN)) {
            throw new EmployeeException("Not an alphanumeric department :\"" + employeeDTO.getDepartment() + "\"at Line:" + employeeDTO.getId());
        } else if (!Designation.exist(employeeDTO.getDesignation())) {
            throw new EmployeeException("Not a valid designation :\"" + employeeDTO.getDesignation() + "\" at Line:" + employeeDTO.getId());
        } else if (!NumberUtils.isCreatable(employeeDTO.getSalary())) {
            throw new EmployeeException("Not an numeric salary :\"" + employeeDTO.getSalary() + "\" at Line:" + employeeDTO.getId());
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            try {
                LocalDate.parse(employeeDTO.getJoigningDate(), formatter);
            } catch (DateTimeParseException e) {
                throw new EmployeeException("Not a valid date :\"" + employeeDTO.getJoigningDate() + "\" at Line:" + employeeDTO.getId());
            }
        }
    }
}
