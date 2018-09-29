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
        this.state = {
            data: [],
            error: ""
        };
        this.initData = this.initData.bind(this);
        this.onParseFile = this.onParseFile.bind(this);
        this.parseData = this.parseData.bind(this);
        this.renderError = this.renderError.bind(this);
    }

    componentWillMount() {
        this.initData();
    }

    initData() {
        axios.get("http://localhost:8080/employees").then(res => {
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

    render() {
        return (
            <div className="Main">
                <h1>Welcome to Rakuten employees</h1>
                <div>
                    <div className="">
                        <FileUploadComponent parsedFile={this.onParseFile}/>
                        <button onClick={this.onDownloadFileError} download> Download File Error</button>
                    </div>
                    {this.renderError()}
                    <DataTabComponent employeeData={this.state.data}
                                      update={this.onUpdate.bind(this)}/>
                </div>
            </div>
        );
    }
}

export default Main;
