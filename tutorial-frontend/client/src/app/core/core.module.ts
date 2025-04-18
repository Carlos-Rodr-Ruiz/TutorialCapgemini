import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from './header/header.component';

import { MatToolbarModule } from '@angular/material/toolbar'; 
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';

import { MatDialogModule } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input'; // ✅ FALTA ESTE

import { LoginDialogComponent } from './login/login-dialog/login-dialog.component';

@NgModule({
  declarations: [
    HeaderComponent,
    LoginDialogComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule
  ],
  exports: [HeaderComponent]
})
export class CoreModule { }
