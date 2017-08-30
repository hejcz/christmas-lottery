import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';

const loginFormId = "login-form";

class App extends Component {

  handleLogin(e) {
      e.preventDefault();
      // wywala się bo nie znamy numeru sesji. Można zrobić endpoint session, który
      // zwróci sesję.
      fetch('/api/login',
          {method: 'POST',
              body: new FormData(document.getElementById(loginFormId)),
              credentials: 'include'})
          .then(resp => {
              return resp.json();
          })
          .then(json => console.log(json));
  }

  pingToApi() {
      fetch(document.getElementById("api-path").value, {method: 'POST', credentials: 'include'})
          .then(resp => {
              return resp.json();
          })
          .then(json => console.log(json));
  }

  render() {
    return (
      <div className="App">
        <div className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <h2>Welcome to React</h2>
        </div>
        <p className="App-intro">
          To get started, edit <code>src/App.js</code> and save to reload.
        </p>

        <form id={loginFormId}>
            <label htmlFor="username">User Name:</label>
            <input id="username" name="username" type="text"/>
            <label htmlFor="password">Password:</label>
            <input id="password" name="password" type="password"/>
            <input type="submit" onClick={this.handleLogin} value="Log In"/>
        </form>

        <input id="api-path" name="api" type="text"/>
        <button onClick={this.pingToApi} style={{width: 20, height: 20}} label="Click" />

      </div>
    );
  }
}

export default App;
