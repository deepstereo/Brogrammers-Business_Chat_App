import { Component, OnInit } from '@angular/core';
import {NgbModal, NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import { ChatService } from '../services/chat.service';
import { User} from '../models/user.module';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css']
})
export class UserProfileComponent implements OnInit {

  user = {};
  username: string;
  email: string;
  status: string;
  updateSuccess = false;

  constructor(public activeModal: NgbActiveModal, private chat: ChatService) { 
    chat.getUser().valueChanges().subscribe(action => {
      this.user = action;
      this.username = action.username;
      this.email = action.email;
      this.status = action.status;
    });
  }

  ngOnInit() {
  }

  update(){
    this.user.username = this.username;
    this.user.status = this.status;

    if (this.user.email != this.email) {
      this.chat.user.updateEmail(this.email);
      this.user.email = this.email;            
    } else {
      this.user.email = this.email;      
    }
    
    const userRef = this.chat.getUser();
    userRef.update(this.user);

    this.updateSuccess = true;

  }

}
