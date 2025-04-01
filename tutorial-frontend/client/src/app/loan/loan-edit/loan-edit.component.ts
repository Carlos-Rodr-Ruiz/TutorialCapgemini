import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { DialogSuccessComponent } from 'src/app/core/dialog-success/dialog-success.component';
import { Loan } from '../model/Loan';
import { LoanService } from '../loan-service/loan.service';
import { Client } from 'src/app/client/model/Client';
import { ClientService } from 'src/app/client/client.service';
import { Game } from 'src/app/game/model/Game';
import { GameService } from 'src/app/game/game.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-loan-edit',
  templateUrl: './loan-edit.component.html',
  styleUrls: ['./loan-edit.component.scss'],
})
export class LoanEditComponent implements OnInit {

  loan: Loan;
  clients: Client[];
  games: Game[];
  errorMessage: string;
endLoanDateError: any;

  constructor(
    public dialogRef: MatDialogRef<LoanEditComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private loanService: LoanService,
    private clientService: ClientService,
    private gameService: GameService,
    private snackBar: MatSnackBar,
    public dialog: MatDialog
  ) { }

  ngOnInit(): void {
    if (this.data.loan != null) {
      this.loan = Object.assign({}, this.data.loan);
    } else {
      this.loan = new Loan();
    }

    this.clientService.getClients().subscribe(clients => {
      this.clients = clients;
    });

    this.gameService.getGames().subscribe(games => {
      this.games = games;
    });
  }

  onSave(): void {
    const startDate = this.formatDate(this.loan.startLoanDate);
    const endDate = this.formatDate(this.loan.endLoanDate);

    if (!this.loan.client || !this.loan.game || !startDate || !endDate) {
      this.errorMessage = "Todos los campos son obligatorios.";
      this.showSnackBar(this.errorMessage, 'error-snackbar');
      return;
    }

    if (startDate > endDate) {
      this.errorMessage = "La fecha de devolución no puede ser anterior a la fecha de préstamo.";
      this.showSnackBar(this.errorMessage, 'error-snackbar');
      return;
    }

    const loanPeriod = (new Date(endDate).getTime() - new Date(startDate).getTime()) / (1000 * 3600 * 24);
    if (loanPeriod > 14) {
      this.errorMessage = "El periodo de préstamo máximo es de 14 días.";
      this.showSnackBar(this.errorMessage, 'error-snackbar');
      return;
    }

    const formattedLoan: any = {
      id: this.loan.id,
      gameId: this.loan.game?.id,
      clientId: this.loan.client?.id,
      startDate: new Date(startDate),
      endDate: new Date(endDate)
    };

    this.loanService.saveLoan(formattedLoan).subscribe({
      next: () => {
        this.dialogRef.close();
        this.dialog.open(DialogSuccessComponent, {
          data: { title: "Operación de guardado", description: "Préstamo guardado con éxito :)" }
        });
      },
      error: (error) => {
        console.error('Error:', error);
        let errorMessage = error.message || "Error desconocido";
        const startFormatted = this.formatDate(this.loan.startLoanDate, 'dd-MM-yyyy');
        const endFormatted = this.formatDate(this.loan.endLoanDate, 'dd-MM-yyyy');
        errorMessage = errorMessage.replace(this.formatDate(this.loan.startLoanDate), startFormatted);
        errorMessage = errorMessage.replace(this.formatDate(this.loan.endLoanDate), endFormatted);
        this.errorMessage = errorMessage;
        this.showSnackBar(this.errorMessage, 'error-snackbar');
      }
    });
  }

  onClose(): void {
    this.dialogRef.close();
  }

  private formatDate(date: Date, format: string = 'yyyy-MM-dd'): string {
    const d = new Date(date);
    const month = '' + (d.getMonth() + 1);
    const day = '' + d.getDate();
    const year = d.getFullYear();

    if (format === 'dd-MM-yyyy') {
      return [day, month, year].join('-');
    }

    return [year, month.padStart(2, '0'), day.padStart(2, '0')].join('-');
  }

  private showSnackBar(message: string, panelClass: string): void {
    this.snackBar.open(message, 'Cerrar', {
      panelClass: [panelClass],
      verticalPosition: 'top'
    });
  }
}
