import { Component, OnInit, Input } from '@angular/core';
import { ChatService } from '../services/chat.service';


@Component({
  selector: 'app-chat-form',
  templateUrl: './chat-form.component.html',
  styleUrls: ['./chat-form.component.css']
})
export class ChatFormComponent implements OnInit {

  @Input() chatID: string;

  message: string;
  
  constructor(private chat: ChatService) { }

  ngOnInit() {
  }

  send() {
    this.chat.sendMessage(this.message, this.chatID);
    this.message = '';
  }

  handleSubmit(event) {
    if (event.keyCOde === 13) {
      this.send();
    }
  }

}
