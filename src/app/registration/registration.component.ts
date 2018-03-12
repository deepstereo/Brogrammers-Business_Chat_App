import { Component, OnInit } from '@angular/core';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {

  public isRegistrationProcess = false;
  constructor() { }

  ngOnInit() {
  }

  showRegistrationForm(){
      this.isRegistrationProcess = true;
    
  }

hideRegistrationForm(){
  this.isRegistrationProcess = false;
}

  
}
