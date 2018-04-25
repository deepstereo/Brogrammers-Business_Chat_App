import { Component, OnInit } from '@angular/core';
import {NgbModal, NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import { ChatService } from '../services/chat.service';

@Component({
  selector: 'app-contact-list',
  templateUrl: './contact-list.component.html',
  styleUrls: ['./contact-list.component.css']
})
export class ContactListComponent implements OnInit {

  userContactList = [];
  userActiveChatList = [];
 
  constructor(public activeModal: NgbActiveModal, private chat: ChatService) {
      chat.getUser().valueChanges().subscribe( user => {
        const contactListObject = user.contactList;
        const contactListKeys = Object.keys(contactListObject);

        this.userContactList = [];
        for (let entity of contactListKeys) {
          chat.getUserByID(entity).valueChanges().subscribe( contact => {
            const user = {
              username: contact.username,
              key: entity
            };
            
            this.userContactList.push(user);
          });
        }
      });
   }

  ngOnInit() {
  }

  addChat(contact) {
    this.chat.addNewPersonalChat(contact.key);
  }
}
