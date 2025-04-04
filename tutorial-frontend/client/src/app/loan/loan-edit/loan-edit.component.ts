import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
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
      this.showDialog('Campos obligatorios', 'Todos los campos son obligatorios.');
      return;
    }

    if (startLoanDate > endLoanDate) {
      this.showDialog('Fechas inválidas', 'La fecha de devolución no puede ser anterior a la fecha de préstamo.');
      return;
    }

    const diffDays = (new Date(endLoanDate).getTime() - new Date(startLoanDate).getTime()) / (1000 * 3600 * 24);
    if (diffDays > 30) {
      this.showDialog('Fechas inválidas', 'La duración del préstamo no puede superar los 30 días.');
      return;
    }

    const dto = {
      clientId: this.loan.client.id,
      gameId: this.loan.game.id,
      startDate: this.loan.startLoanDate,
      endDate: this.loan.endLoanDate
    };
    console.log('📦 Enviando DTO:', dto);

    this.loanService.saveLoan(dto).subscribe({
      next: () => {
        this.dialogRef.close(true);
        this.showDialog('¡Éxito!', 'Préstamo guardado correctamente.');
      },
      error: (error) => {
        const mensaje = error?.error?.message || 'Error al guardar el préstamo.';
        this.showDialog('Error', mensaje);
      }
    });
  }

  onClose(): void {
    this.dialogRef.close();
  }

  private showDialog(title: string, description: string): void {
    this.dialog.open(DialogSuccessComponent, {
      data: { title, description }
    });
  }
}
