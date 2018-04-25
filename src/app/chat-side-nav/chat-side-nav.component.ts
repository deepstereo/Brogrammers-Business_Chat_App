import { Component, OnInit } from '@angular/core';
import {NgbModal, NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-chat-side-nav',
  templateUrl: './chat-side-nav.component.html',
  styleUrls: ['./chat-side-nav.component.css']
})
export class ChatSideNavComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

  showContactList() {
    this.modal.open(ContactListComponent);
  }

  showUserProfile() {
    this.modal.open(UserProfileComponent);
  }

}
