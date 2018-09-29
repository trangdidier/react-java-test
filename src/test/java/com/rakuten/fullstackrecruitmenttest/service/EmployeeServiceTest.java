package com.rakuten.fullstackrecruitmenttest.service;


import com.rakuten.fullstackrecruitmenttest.dto.EmployeeDTO;
import com.rakuten.fullstackrecruitmenttest.beans.Employee;
import com.rakuten.fullstackrecruitmenttest.exception.EmployeeException;
import com.rakuten.fullstackrecruitmenttest.utils.Designation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.rakuten.fullstackrecruitmenttest.utils.Utils.DATA_JSON_FILE_NAME;
import static com.rakuten.fullstackrecruitmenttest.utils.Utils.DATE_FORMAT;
import static com.rakuten.fullstackrecruitmenttest.utils.Utils.LOG_ERROR_FILE_NAME;
import static org.junit.Assert.assertTrue;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration
@SpringBootTest
public class EmployeeServiceTest {

    @Autowired
    private EmployeeService employeeService;

    EmployeeDTO employeeDTO;

    @Test
    public void should_not_throw_exception_when_data_is_correct() throws EmployeeException{
        //given
        EmployeeDTO employeeDTO = new EmployeeDTO("1","John", "IT", "Developer", "1000", "2018-01-01");

        //when
        employeeService.verify(employeeDTO);

        //Should not throw any exception
    }

    @Test(expected = EmployeeException.class)
    public void should_throw_exception_when_name_has_numbers() throws EmployeeException{
        //given
        EmployeeDTO employeeDTO = new EmployeeDTO("1","John1", "IT", "Developer", "1000", "2018-01-01");

        //when
        employeeService.verify(employeeDTO);
    }

    @Test(expected = EmployeeException.class)
    public void should_throw_exception_when_department_has_invalid_character() throws EmployeeException{
        //given
        EmployeeDTO employeeDTO = new EmployeeDTO("1","John", "IT;", "Developer", "1000", "2018-01-01");

        //when
        employeeService.verify(employeeDTO);
    }

    @Test(expected = EmployeeException.class)
    public void should_throw_exception_when_designation_is_not_listed() throws EmployeeException{
        //given
        EmployeeDTO employeeDTO = new EmployeeDTO("1","John", "IT", "Business developer", "1000", "2018-01-01");

        //when
        employeeService.verify(employeeDTO);
    }

    @Test(expected = EmployeeException.class)
    public void should_throw_exception_when_salary_has_alpha_character() throws EmployeeException{
        //given
        EmployeeDTO employeeDTO = new EmployeeDTO("1","John", "IT", "Developer", "1000O", "2018-01-01");

        //when
        employeeService.verify(employeeDTO);
    }

    @Test(expected = EmployeeException.class)
    public void should_throw_exception_when_date_is_not_valid() throws EmployeeException{
        //given
        EmployeeDTO employeeDTO = new EmployeeDTO("1","John", "IT", "Developer", "1000", "2018-0101");

        //when
        employeeService.verify(employeeDTO);
    }

    @Test
    public void should_create_a_file_error_when_data_is_invalid() throws EmployeeException{
        //given
        List<EmployeeDTO> employeeDTOList = new ArrayList<EmployeeDTO>();
        employeeDTOList.add(new EmployeeDTO("1","John1", "IT", "Developer", "1000", "2018-0101"));
        employeeDTOList.add(new EmployeeDTO("2","John", "IT", "Developer", "1000O", "2018-01-01"));

        String exceptionMessage;
        //when
        exceptionMessage = employeeService.checkEmployeeInfoCompliance(employeeDTOList);

        //then
        System.out.println(exceptionMessage);
        File file = new File(LOG_ERROR_FILE_NAME);
        assertTrue(file.exists());
        assertTrue(!exceptionMessage.isEmpty());
        file.delete();
    }

    @Test
    public void should_list_employee_when_file_contains_data() throws EmployeeException{
        //given
        List<EmployeeDTO> expectedEmployeeDTOs = new ArrayList<>();
        expectedEmployeeDTOs.add(new EmployeeDTO("1","JohnDOE", "IT", "Developer", "1000.0", "2018-01-01"));
        expectedEmployeeDTOs.add(new EmployeeDTO("2","John", "IT", "Developer", "10000.0", "2018-01-01"));
        employeeService.save(expectedEmployeeDTOs);
        //when
        List<EmployeeDTO> givenEmployeeDTO = employeeService.getEmployeeList();
        //then
        assertTrue(expectedEmployeeDTOs.containsAll(givenEmployeeDTO));
        File file = new File(DATA_JSON_FILE_NAME);
        file.delete();
    }

    @Test
    public void should_create_a_data_file() throws Exception{
        //given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        String date = "2018-01-01";
        Date joigningDate = Date.from(LocalDate.parse(date, formatter).atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<Employee> expectedEmployeeList = new ArrayList<Employee>();
        expectedEmployeeList.add(new Employee(1,"John", "IT", Designation.DEVELOPER, 1000, joigningDate));
        //when
        employeeService.createOrUpdateDataFile(expectedEmployeeList);
        List<Employee> givenEmployeeList = employeeService.getDataFromFile();
        //then
        assertTrue(expectedEmployeeList.containsAll(givenEmployeeList));
        File file = new File(DATA_JSON_FILE_NAME);
        file.delete();
    }

}
