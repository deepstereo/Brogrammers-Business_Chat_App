import { Component, OnInit, Input } from '@angular/core';
import { ChatService } from '../services/chat.service';
import { Message } from '../models/message.module';

@Component({
  selector: 'app-chat-message',
  templateUrl: './chat-message.component.html',
  styleUrls: ['./chat-message.component.css']
})
export class ChatMessageComponent implements OnInit {

  @Input() chatMessage: Message;  

  userName: string;
  content: string;
  timeSent: Date;

  constructor(private chatService: ChatService) { }

  ngOnInit(chatMessage = this.chatMessage) {
    this.chatService.getUserByID(chatMessage.senderId).valueChanges().subscribe( res => {
      this.userName = res.userName;
      this.content = chatMessage.content;
      this.timeSent = chatMessage.timeSent;
    })
  }

}
