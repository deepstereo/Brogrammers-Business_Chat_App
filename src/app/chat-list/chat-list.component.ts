import { Component, OnInit } from '@angular/core';
import { User } from '../models/user.module';
import { ChatService } from '../services/chat.service';
import { ChatComponent } from '../chat/chat.component';
import { Md5 } from 'ts-md5/dist/md5';

@Component({
  selector: 'app-chat-list',
  templateUrl: './chat-list.component.html',
  styleUrls: ['./chat-list.component.css']
})
export class ChatListComponent implements OnInit {

  chats= [];
  
  constructor(private chat: ChatService, private app: ChatComponent) { }

  ngOnInit() {
    this.chat.getActiveChats().valueChanges().subscribe( action => {
      const chatListObject = action;

      if (chatListObject != undefined) {
        const chatListKeys = Object.keys(chatListObject);
        
        this.chats = [];

        for (let ent of chatListKeys) {
        this.chat.getUserByID(ent).valueChanges().subscribe( action => {
         const chatItem = {
            username: action.username,
            key: ent
          }
        if ( this.chats.indexOf(chatItem) == -1) { 
        this.chats.push(chatItem); }
        });
        }
      }
    });
  }

  openChat(id: string) {
    let toSort = [id, this.chat.user.uid];
    toSort.sort();
    const chatStr = Md5.hashStr(toSort[0] + toSort[1]).toString();
    this.app.activeChatID = chatStr;
    console.log(toSort);
  }

}
