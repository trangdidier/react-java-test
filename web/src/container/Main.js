import React, {Component} from 'react';
import axios from 'axios';
import csvParser from 'papaparse';
import DataTabComponent from "./dataTab/DataTabComponent";
import FileUploadComponent from "./fileUpload/FileUploadComponent";
import './Main.css'
import fileDownload from 'js-file-download';

class Main extends Component {


    constructor(props) {
        super(props);
        this.maxEmployeeByPage = 5;
        this.state = {
            data: [],
            error: ""
        };
        this.pageList = [1];
        this.currentPage = 1;
        this.maxPage = 1;
        this.employeeList = [];
        this.initData = this.initData.bind(this);
        this.onParseFile = this.onParseFile.bind(this);
        this.parseData = this.parseData.bind(this);
        this.loadData = this.loadData.bind(this);
        this.renderError = this.renderError.bind(this);
        this.initData();
    }

    initData() {
        axios.get("http://localhost:8080/employees").then(res => {
            this.setState({
                data: res.data,
                error: ""
            });
            this.loadData(1);
        }, err => {
            this.setState((state) => {
                return {
                    data: state.data,
                    error: err.response && err.response.data ? err.response.data.message : ""
                }
            });
        });
    }


    loadData(page) {

        if (this.state.data) {
            this.maxPage = Math.ceil(this.state.data.length / this.maxEmployeeByPage);
        } else {
            this.maxPage = 1;
        }
        if (page.target && Number.isInteger(page.target.text)) {
            this.currentPage = parseInt(page.target.text);
        } else {
            this.currentPage = page;
        }
        if (this.currentPage > this.maxPage) {
            this.currentPage = 1;
        }
        this.pageList = [];
        for(let nb = 1; nb <= this.maxPage; nb++) {
            this.pageList.push(nb);
        }
        let maxIndex = this.state.data.length;
        let minIndex = 0;
        if ((this.currentPage*this.maxEmployeeByPage) <= this.state.data.length ) {
            maxIndex = this.currentPage*this.maxEmployeeByPage;
        }
        if ((maxIndex - this.maxEmployeeByPage - 1) > 0) {
            minIndex = maxIndex - this.maxEmployeeByPage -1;
        }
        this.employeeList = this.state.data.slice(minIndex, maxIndex);
    }

    onParseFile(file) {
        csvParser.parse(file, {
            skipEmptyLines: true,
            complete: (result) => {
                this.setState((state) => {
                    return {
                        data: state.data,
                        error: ""
                    }
                });
                let fileData = this.parseData(result.data);
                if (!this.state.error) {
                    axios.post("http://localhost:8080/employees", fileData).then(res => {
                        this.setState((state) => {
                            return {
                                data: res.data,
                                error: ""
                            }
                        });
                        this.loadData(1);
                    }, err => {
                        this.setState((state) => {
                            return {
                                data: state.data,
                                error: err.response && err.response.data ? err.response.data.message : ""
                            }
                        });
                    });
                }
            },
            error: (error) => {
                this.setState((state) => {
                    return {
                        data: state.data,
                        error: error
                    }
                });
                console.error('error parsing csv', error);
            }
        });
    }

    parseData(data = []) {
        let filteredData = data.filter((entry, index) => {
            if (entry.length !== 5) {
                this.setState((state) => {
                    return {
                        data: state.data,
                        error: state.error + "\n data missing in line" + (index + 1)
                    }
                });
            }
            return entry.length === 5;
        })

        return filteredData.map((entry, index) => {
            return {
                id: index + 1,
                name: entry[0],
                department: entry[1],
                designation: entry[2],
                salary: entry[3],
                joigningDate: entry[4]
            }
        })
    }

    onDownloadFileError() {
        axios.get("http://localhost:8080/download").then(res => {
            fileDownload(res.data, "error.log");
        });
    }

    onUpdate(employee) {
        if (employee) {
            axios.put("http://localhost:8080/employees", employee).then(res => {
                this.setState({
                    data: res.data,
                    error: ""
                });
            }, err => {
                this.setState((state) => {
                    return {
                        data: state.data,
                        error: err.response && err.response.data ? err.response.data.message : ""
                    }
                });
            });
        }
    }

    renderError() {
        if (this.state.error) {
            return (
                <div className="error-panel">
                    ERROR:
                    {this.state.error}
                </div>
            )
        }
    }

    renderPagination() {
        this.pageList.map(page => {
            if (page == this.currentPage) {
                return <a className="current-page pagination">{page}</a>
            } else {
                return <a className="pagination"
                          onClick={this.loadData.bind(this)}>{page}</a>
            }
        })
    }

    render() {
        return (
            <div className="Main">
                <h1>Welcome to Rakuten employees</h1>
                <div>
                    <div className="">
                        <FileUploadComponent parsedFile={this.onParseFile}/>
                        <button onClick={this.onDownloadFileError} download> Download File Error</button>
                    </div>
                    {this.renderError}
                    <DataTabComponent employeeData={this.employeeList}
                                      update={this.onUpdate.bind(this)}/>
                    {this.renderPagination}
                </div>
            </div>
        );
    }
}

export default Main;
