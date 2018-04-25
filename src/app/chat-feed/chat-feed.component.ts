import { Component, OnInit, OnChanges, Input } from '@angular/core';

@Component({
  selector: 'app-chat-feed',
  templateUrl: './chat-feed.component.html',
  styleUrls: ['./chat-feed.component.css']
})
export class ChatFeedComponent implements OnInit {

  @Input() chatID: string;
    
  constructor() { }

  ngOnInit() {
  }

}
