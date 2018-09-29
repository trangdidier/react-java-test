import React, { Component } from 'react';
import UpdateModalComponent from "../updateModal/UpdateModalComponent";
import "./DataTabComponent.css"

class DataTabComponent extends Component {

    constructor(props) {
        super(props);
        this.state = {
            designations: ["Developer","Senior developer","Manager","Team lead","VP", "CEO"],
        };
        this.onUpdateData = this.onUpdateData.bind(this);
        this.employeeList = [];
    }

    componentWillReceiveProps(nextProps) {
        this.employeeList = nextProps.employeeData;
    }

    onUpdateData(employee) {
        this.props.update(employee)
    }

    render() {
        return (
            <div className="data-tab">
                <div className="data-tab-entry">
                    <label className="flex-1"><strong>Name</strong></label>
                    <label className="flex-1"><strong>Department</strong></label>
                    <label className="flex-1"><strong>Designation</strong></label>
                    <label className="flex-1"><strong>Salary</strong></label>
                    <label className="flex-1"><strong>Joigning date</strong></label>
                    <label className="flex-1"><strong>Actions</strong></label>
                </div>
                {

                    this.employeeList.map((data, index) =>{
                        return (
                            <div className="data-tab-entry"
                                key={"data-" + index}>
                                <label className="flex-1">{data.name}</label>
                                <label className="flex-1">{data.department}</label>
                                <label className="flex-1">{data.designation}</label>
                                <label className="flex-1">{data.salary}</label>
                                <label className="flex-1">{data.joigningDate}</label>
                                <UpdateModalComponent
                                    className="flex-1"
                                    update={this.onUpdateData}
                                    employee={data}/>
                            </div>
                        )
                    })
                }
            </div>
        );
    }
}

export default DataTabComponent;