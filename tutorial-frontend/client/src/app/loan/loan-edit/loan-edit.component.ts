import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { DialogSuccessComponent } from 'src/app/core/dialog-success/dialog-success.component';
import { Loan } from '../model/Loan';
import { LoanService } from '../loan-service/loan.service';
import { ClientService } from 'src/app/client/client.service';
import { GameService } from 'src/app/game/game.service';
import { Client } from 'src/app/client/model/Client';
import { Game } from 'src/app/game/model/Game';

@Component({
  selector: 'app-loan-edit',
  templateUrl: './loan-edit.component.html',
  styleUrls: ['./loan-edit.component.scss']
})
export class LoanEditComponent implements OnInit {

  loan: Loan;
  clients: Client[] = [];
  games: Game[] = [];
  errorMessage: string = '';
  endLoanDateError: string = '';

  constructor(
    public dialogRef: MatDialogRef<LoanEditComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private loanService: LoanService,
    private clientService: ClientService,
    private gameService: GameService,
    private snackBar: MatSnackBar,
    public dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loan = this.data.loan ? Object.assign({}, this.data.loan) : new Loan();

    this.clientService.getClients().subscribe(data => this.clients = data);
    this.gameService.getGames().subscribe(data => this.games = data);
  }

  onSave(): void {
    const { client, game, startLoanDate, endLoanDate } = this.loan;

    if (!client || !game || !startLoanDate || !endLoanDate) {
      this.showError('Todos los campos son obligatorios.');
      return;
    }

    if (startLoanDate > endLoanDate) {
      this.showError('La fecha de devolución no puede ser anterior a la fecha de préstamo.');
      return;
    }

    const diffDays = (new Date(endLoanDate).getTime() - new Date(startLoanDate).getTime()) / (1000 * 3600 * 24);
    if (diffDays > 14) {
      this.showError('El periodo de préstamo máximo es de 14 días.');
      return;
    }

    const dto = {
      id: this.loan.id,
      clientId: client.id,
      gameId: game.id,
      startDate: startLoanDate,
      endDate: endLoanDate
    };
    
    this.loanService.saveLoan(dto).subscribe({
      next: () => {
        this.dialogRef.close(true);
        this.dialog.open(DialogSuccessComponent, {
          data: {
            title: 'Operación de guardado',
            description: 'Préstamo guardado con éxito :)'
          }
        });
      },
      error: (error) => {
        const backendMessage = error?.error?.message || 'Error desconocido';
        this.showError(backendMessage);
      }
    });
  }

  onClose(): void {
    this.dialogRef.close();
  }

  private showError(message: string): void {
    this.errorMessage = message;
    this.snackBar.open(message, 'Cerrar', {
      panelClass: ['error-snackbar'],
      verticalPosition: 'top'
    });
  }
}
