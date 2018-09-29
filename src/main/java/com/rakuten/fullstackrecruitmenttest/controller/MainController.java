package com.rakuten.fullstackrecruitmenttest.controller;

import com.rakuten.fullstackrecruitmenttest.dto.EmployeeDTO;
import com.rakuten.fullstackrecruitmenttest.exception.EmployeeException;
import com.rakuten.fullstackrecruitmenttest.service.EmployeeService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.rakuten.fullstackrecruitmenttest.utils.Utils.LOG_ERROR_FILE_NAME;
import static com.rakuten.fullstackrecruitmenttest.utils.Utils.UNKNOWN_FILE_EXCEPTION_MESSAGE;

@RestController
public class MainController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping(value = "/employees")
    @CrossOrigin(origins = "*")
    public List<EmployeeDTO> get() {
        return employeeService.getEmployeeList();
    }

    @PostMapping(value = "/employees")
    @CrossOrigin(origins = "*")
    public List<EmployeeDTO> post(@RequestBody List<EmployeeDTO> employeeDTOs) throws IOException, EmployeeException {
        String errorMessage = employeeService.checkEmployeeInfoCompliance(employeeDTOs);
        if (StringUtils.isBlank(errorMessage)) {
            employeeService.save(employeeDTOs);
            return employeeDTOs;
        } else {
            throw new EmployeeException(errorMessage);
        }
    }

    @GetMapping(value = "/download")
    @CrossOrigin(origins = "*")
    public byte[] downloadLogError() throws Exception {
        File file = new File(LOG_ERROR_FILE_NAME);
        if (file.exists()) {
            InputStream targetStream = new FileInputStream(file);
            return IOUtils.toByteArray(targetStream);
        } else {
            throw new Exception(UNKNOWN_FILE_EXCEPTION_MESSAGE);
        }
    }

    @PutMapping(value = "/employees")
    @CrossOrigin(origins = "*")
    public List<EmployeeDTO> update(@RequestBody EmployeeDTO employeeDTO) throws IOException, EmployeeException {
        List<EmployeeDTO> employeeDTOs = new ArrayList<EmployeeDTO>();
        employeeDTOs.add(employeeDTO);
        String errorMessage = employeeService.checkEmployeeInfoCompliance(employeeDTOs);
        if (StringUtils.isBlank(errorMessage)) {
            employeeDTOs = employeeService.update(employeeDTO);
        } else {
            throw new EmployeeException(errorMessage);
        }
        return employeeDTOs;
    }
}
