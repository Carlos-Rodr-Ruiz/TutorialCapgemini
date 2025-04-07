import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { LoginDialogComponent } from '../login/login-dialog/login-dialog.component';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  isAuthenticated = false;

  constructor(private dialog: MatDialog, private authService: AuthService) {}

  ngOnInit(): void {
    this.isAuthenticated = this.authService.isAuthenticated();
  }

  openLoginDialog(): void {
    const dialogRef = this.dialog.open(LoginDialogComponent, {
      width: '300px'
    });

    dialogRef.afterClosed().subscribe(() => {
      this.isAuthenticated = this.authService.isAuthenticated();
    });
  }

  logout(): void {
    this.authService.clearToken();
    this.isAuthenticated = false;
  }
}
