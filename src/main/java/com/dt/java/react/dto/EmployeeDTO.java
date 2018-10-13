package com.dt.java.react.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmployeeDTO {

    private String id;

    private String name;

    private String department;

    private String designation;

    private String salary;

    private String joigningDate;

    public EmployeeDTO(String id,String name, String department, String designation, String salary, String joigningDate) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.designation = designation;
        this.salary = salary;
        this.joigningDate = joigningDate;
    }
}
