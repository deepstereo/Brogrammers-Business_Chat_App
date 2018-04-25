import { Injectable } from '@angular/core';
import { AngularFireDatabase, AngularFireList } from 'angularfire2/database';
import { AngularFireAuth } from 'angularfire2/auth';
import * as firebase from 'firebase/app';
import { Observable } from 'rxjs/Observable';
import { AuthService } from '../services/auth.service';

import { Message } from '../models/message.module';
import { User } from '../models/user.module';
@Injectable()
export class ChatService {
  chatMessages: AngularFireList<Message[]>;
  chatMessage: Message;
  userName: Observable<string>;
  contactList = {};
  activeChats = {};
  user: firebase.User;

  constructor(
    private db: AngularFireDatabase,
    private dbAuth: AngularFireAuth) 
    {
    this.dbAuth.authState.subscribe(auth => {
       if (auth !== undefined && auth !== null) {
         this.user = auth;
      }
      if ( this.user != undefined) {
    this.getUser().snapshotChanges().subscribe( action => {

      this.userName = action.payload.val().username;
      const contactList = action.payload.val().contactList;
      const activeChats = action.payload.val().activePersonalChats;

      if (contactList != undefined) {
        this.contactList = contactList;
      };

      if (activeChats != undefined) {
        this.activeChats = activeChats;
      };

    });
    console.log(this.userName); }
    });
  }

  getUser() {
    const userID = this.user.uid;
    const path = 'users_angular/' + userID;
    return this.db.object(path);
  }



  getUserByID(id: string) {
    const userID = id;
    const path = 'users_angular/' + userID;
    return this.db.object(path);
  }

  getUsers() {
    return this.db.list("/users_angular");
  }

  sendMessage(msg: string, id: string) {

    const timestamp = new Date().valueOf();
    this.chatMessages = this.getMessages(id);
    let  messageCheck: boolean = true;

    const path = 'messages-angular/' + id;
    let refM = firebase.database().ref(path);    
    refM.once('value', action => { 
      console.log(action); 
      if (action.val() != null) {
        console.log("We haz messages");     
      } else {
        console.log("it's a first message, add me to your active chats list");
      }
    });
    
    this.chatMessages.push({
      senderId: this.user.uid,
      isMultimedia: false,
      content: msg,
      timeSent: timestamp,
    });
  }

  addToActiveChats(id: string) {

  }

  getMessages(id: string): AngularFireList<Message[]> {
    const path = 'messages-angular/' + id;
    return this.db.list(path, ref => ref.limitToLast(20).orderByKey()
  );
  }

  getActiveChats() {
    const userID = this.user.uid;
    const path = 'users_angular/' + userID + '/activePersonalChats';
    return this.db.object(path);
  }

  addUserToContactList(id: string) {
    const contactUserId = id;
    if (!this.contactList[contactUserId]) {
      console.log("User does not exist, We will add it to the list");
    } else {
      console.log("User exists, ignore adding to the list");
    }

    let cL=Object.keys(this.contactList);
    let myUsers = [];
    for (let ent of cL) {
      this.getUserByID(ent).valueChanges().subscribe( res => {
        //console.log(res.key);
        myUsers.push(res.key);
      });
    }

   
    const path = 'users_angular/' + this.user.uid + '/contactList';
    const contactsList = this.db.object(path);
    this.contactList[contactUserId]= true;
    contactsList.update(this.contactList);
  }

  addNewPersonalChat(id: string) {
    const userId = id;
    const path = 'users_angular/' + this.user.uid + '/activePersonalChats';
    const chatList = this.db.object(path);

    if (!this.activeChats[userId]) {
      this.activeChats[userId] = true;
    }

    chatList.update(this.activeChats);
    console.log("chat list:");
    console.log(this.activeChats);
  }
  
}
