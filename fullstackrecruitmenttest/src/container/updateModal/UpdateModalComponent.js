import React, { Component } from 'react';
import Modal from 'react-responsive-modal';
import './UpdateModalComponent.css';

class UpdateModalComponent extends Component {

    constructor(props) {
        super(props);
        this.employee = this.props.employee;
        this.update = this.props.update;
        this.state = {
            open: false,
            designations: ["Developer","Senior developer","Manager","Team lead","VP", "CEO"]
        };
        this.onUpdateData = this.onUpdateData.bind(this);
        this.handleChange = this.handleChange.bind(this);
    }

    onOpenModal = () => {
        this.setState({ open: true });
    };

    onCloseModal = () => {
        this.setState({ open: false });
    };

    componentWillReceiveProps(nextProps) {
        this.employee = nextProps.employee;
        this.update = nextProps.update
    }

    handleChange(event, property){
        this.employee[property] = event.target.value;
    }

    onUpdateData() {
        if (this.employee) {
            this.update(this.employee);
            this.onCloseModal();
        }
    }

    render(){
        let open = this.state.open;
        return (
                    <div>
                        <button onClick={this.onOpenModal}>Update employee information</button>
                        <Modal open={open} onClose={this.onCloseModal} center>
                            <div className="update-modal">
                                <div className="update-modal-info">
                                    <label className="flex-1">Name:</label>
                                    <input type="text"
                                           className="flex-1"
                                           key="name"
                                           defaultValue={this.employee.name}
                                           onChange={(e) => this.handleChange(e, "name")} />
                                </div>
                                <div className="update-modal-info">
                                    <label className="flex-1">Department:</label>
                                    <input type="text"
                                           className="flex-1"
                                           key="department"
                                           defaultValue={this.employee.department}
                                           onChange={(e) => this.handleChange(e, "department")}/>
                                </div>
                                <div className="update-modal-info">
                                    <label className="flex-1">Designation:</label>
                                    <select defaultValue={this.employee.designation}
                                        key="designation"
                                        className="flex-1"
                                        onChange={(e) => this.handleChange(e, "designation")}>
                                        {this.state.designations.map((designation,ind) =>
                                        <option value={designation}
                                                key={"designation_"+ ind}>
                                            {designation}
                                        </option>
                                    )}
                                </select>
                                </div>
                                <div className="update-modal-info">
                                    <label className="flex-1">Salary:</label>
                                    <input type="text"
                                       key="salary"
                                       className="flex-1"
                                       defaultValue={this.employee.salary}
                                       onChange={(e) => this.handleChange(e, "salary")}/>
                                </div>
                                <div className="update-modal-info">
                                    <label className="flex-1">Joigning date: </label>
                                    <input type="text"
                                           className="flex-1"
                                           key="joigningDate"
                                           defaultValue={this.employee.joigningDate}
                                           onChange={(e) => this.handleChange(e, "joigningDate")}/>
                                </div>
                                <div className="update-modal-footer">
                                    <button
                                        className=""
                                        onClick={this.onUpdateData}> Save </button>
                                </div>
                            </div>
                        </Modal>
                    </div>
            );
    }
}

export default UpdateModalComponent;