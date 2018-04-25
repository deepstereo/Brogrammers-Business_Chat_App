import { Component, OnInit, OnChanges, Input } from '@angular/core';
import { ChatService } from '../services/chat.service';
import { Observable } from 'rxjs/Observable';
import { Message } from '../models/message.module';
import { AngularFireList } from 'angularfire2/database';

@Component({
  selector: 'app-chat-feed',
  templateUrl: './chat-feed.component.html',
  styleUrls: ['./chat-feed.component.css']
})
export class ChatFeedComponent implements OnInit {

  @Input() chatID: string;

  feed: AngularFireList<Message[]>;
    
  constructor(private chat: ChatService) { }

  ngOnInit() {
    this.feed = null;
    this.feed = this.chat.getMessages(this.chatID).valueChanges();
  }

  ngOnChanges() {
    this.feed = null;
    this.feed = this.chat.getMessages(this.chatID).valueChanges();
  }

}
