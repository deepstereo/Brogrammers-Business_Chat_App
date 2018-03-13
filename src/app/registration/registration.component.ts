import { Component, OnInit } from '@angular/core';
import { NgIf } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
// import { moveIn, fallIn } from '../router.animations';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {

  public isRegistrationProcess = false;

  username: string
  email: string
  password: string
  errorMsg: string

  constructor(public af: AuthService,private router: Router) {

  }

  ngOnInit() {
  }

  showRegistrationForm(){
      this.isRegistrationProcess = true;
    
  }

hideRegistrationForm(){
  this.isRegistrationProcess = false;
}

signUP(){
  const email = this.email;
  const password = this.password;
  const username = this.username;
  this.af.signUp(email, password, username)
  .then(res => this.router.navigate(['chat']))
  .catch(error => this.errorMsg = error.message);
}

}
