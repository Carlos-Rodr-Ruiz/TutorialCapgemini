import { Component } from '@angular/core';
import { MatDialogRef, MatDialog } from '@angular/material/dialog';
import { DialogSuccessComponent } from 'src/app/core/dialog-success/dialog-success.component';

@Component({
  selector: 'app-login-dialog',
  templateUrl: './login-dialog.component.html',
  styleUrls: ['./login-dialog.component.scss']
})
export class LoginDialogComponent {
  username: string = '';
  password: string = '';

  constructor(
    public dialogRef: MatDialogRef<LoginDialogComponent>,
    private dialog: MatDialog
  ) {}

  onLogin(): void {
    if (this.username === 'admin' && this.password === 'admin') {
      this.dialogRef.close();
      this.dialog.open(DialogSuccessComponent, {
        data: { message: 'Inicio de sesión exitoso.' }
      });
    } else {
      this.dialog.open(DialogSuccessComponent, {
        data: { message: '❌ Usuario o contraseña incorrectos.' }
      });
      
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
