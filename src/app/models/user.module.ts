import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: []
})
export class User { 
  uid?: string;
  email?: string;
  userName?: string;
  password?: string;
  status?: string;
  lastOnline?: Date;
  avatarURL?: string;
  avatar?: boolean;
}
