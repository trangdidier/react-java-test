package com.dt.java.react.beans;


import com.dt.java.react.utils.Designation;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class Employee {

    private int id;

    private String name;

    private String department;

    private Designation designation;

    private float salary;

    private Date joigningDate;

    public Employee(int id,String name, String department, Designation designation, float salary, Date joigningDate) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.designation = designation;
        this.salary = salary;
        this.joigningDate = joigningDate;
    }
}
