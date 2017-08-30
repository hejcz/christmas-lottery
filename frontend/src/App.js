import React, {Component} from 'react';
import './App.css';
import $ from 'jquery';
import Login from './Login';
import Page from './Page'
import 'jquery/dist/jquery.min';
import 'bootstrap/dist/css/bootstrap.css';

//sadly only fix for fckng ES6
window.jQuery = window.$ = $;
require('bootstrap');

class App extends Component {

  constructor() {
    super();
    this.state = {loggedIn: false};
  }

  pingToApi() {
    fetch($("#api-path").val(), {method: 'POST', credentials: 'include'})
      .then(resp => {
        return resp.json();
      })
      .then(json => console.log(json));
  }
  loggedInSuccessfully() {
    this.setState ({loggedIn: true});
  }
  render() {
    const loggedIn = this.state.loggedIn;
    let page = null;
    if (loggedIn) {
      page = <Page/>;
    } else {
      page = <Login action={this.loggedInSuccessfully.bind(this)}/>;
    }

    return(
      <div className="mainApp">
        {page}
      </div>
    );

  }
}
export default App;
