import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: []
})
export class Message { 
  $key?: string;
  content?: string;
  isMultimedia?: boolean;
  senderId?: string;
  timeSent?: Date = new Date();
}
