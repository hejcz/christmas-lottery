import React, { Component} from 'react';

import $ from 'jquery';
import './Login.css';


class Login extends Component {

  handleLogin(e) {

    e.preventDefault();
    // wywala się bo nie znamy numeru sesjcodei. Można zrobić endpoint session, który
    // zwróci sesję.
    fetch('/api/login',
      {
        method: 'POST',
        body: new FormData($("#login-form")[0]),
        credentials: 'include'
      }).then(response => {
        if(response.ok) {
          console.log("ale haxxor");
          this.props.action();
        } else {
          console.log("chuja a nie haslo znasz");
        }
    });
  }

  render() {
    return (
      <form id="login-form" className="container text-center">

        <div className="row form-group">

          <div className="col-md-4 col-md-offset-4">
            <label htmlFor="username">User Name:</label>
            <input id="username" name="username" className="form-control" type="text"/>
          </div>
        </div>
        <div className="row form-group">
          <div className="col-md-4 col-md-offset-4">
            <label htmlFor="password">Password:</label>
            <input id="password" className="form-control" name="password" type="password"/>
          </div>
        </div>
        <div className="row form-group">
          <div className="col-md-2 col-md-offset-5">
            <input className="btn btn-default" type="submit" onClick={this.handleLogin.bind(this)} value="Log In"/>
          </div>
        </div>

      </form>


    );
  }

} export default Login;