import { Injectable } from '@angular/core';
import { Router } from "@angular/router";
import { AngularFireAuth } from 'angularfire2/auth';
import { AngularFireDatabase} from 'angularfire2/database';
import * as firebase from 'firebase/app';
import { Observable } from 'rxjs/Observable';

@Injectable()
export class AuthService {
  private user: Observable<firebase.User>;
  private authState: any;

  constructor(private afAuth: AngularFireAuth, private db: AngularFireDatabase, private router: Router) {
    this.user = afAuth.authState;
   }

   get currentUserID(): string {
    return this.authState !== null ? this.authState.uid : '';
  }

  authUser() {
    return this.user;
  }

  login(email: string, password: string) {
    console.log(email + ' ' + password);
    return this.afAuth.auth.signInWithEmailAndPassword(email, password)
       .then( res => {
         this.router.navigate(['chat'])
       .catch(error => {console.log(error.code + ' ' + error.message)});
       });
  }

  signUp(email: string, password: string, username: string) {
    console.log(email + ' ' + password);
     return this.afAuth.auth.createUserWithEmailAndPassword(email, password)
         .then((user) => {
           this.authState = user;
           this.setUserData(email, username);
         }).catch(error => console.log(error.message));
  }

  setUserData(email: string, username: string) {
    const path = 'users/' + this.currentUserID;
    const data = {
      email: email,
      username: username
    };

    this.db.object(path).update(data);
  }

}