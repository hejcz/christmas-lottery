import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';

fetch('/api')
    .then(resp => resp.json())
    .then(json => console.log(json));

ReactDOM.render(<App />, document.getElementById('root'));
