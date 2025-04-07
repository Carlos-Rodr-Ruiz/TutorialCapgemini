
import { Component } from '@angular/core';
import { MatDialogRef, MatDialog } from '@angular/material/dialog';
import { AuthService } from 'src/app/core/auth/auth.service'; // Servicio que maneja el login y el token
import { DialogSuccessComponent } from 'src/app/core/dialog-success/dialog-success.component'; 


@Component({
  selector: 'app-login-dialog',
  templateUrl: './login-dialog.component.html',
  styleUrls: ['./login-dialog.component.scss']
})
export class LoginDialogComponent {
  username: string = '';
  password: string = '';

  //  Dependencias necesarias
  constructor(
    public dialogRef: MatDialogRef<LoginDialogComponent>, // Para poder cerrar el modal de login
    private dialog: MatDialog, // Para abrir diálogos (como mensajes de éxito o error)
    private authService: AuthService // Servicio donde se hace la llamada al backend
  ) {}


  onLogin(): void {
    this.authService.login(this.username, this.password).subscribe({
      next: () => {
        this.dialogRef.close(); 
        this.dialog.open(DialogSuccessComponent, {
          data: { message: '✅ Inicio de sesión exitoso.' } 
        });
      },
      error: () => {
        this.dialog.open(DialogSuccessComponent, {
          data: { message: '❌ Usuario o contraseña incorrectos.' }
        });
      }
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
