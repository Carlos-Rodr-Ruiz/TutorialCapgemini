import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { DialogConfirmationComponent } from 'src/app/core/dialog-confirmation/dialog-confirmation.component';
import { PageEvent } from '@angular/material/paginator';
import { Pageable } from 'src/app/core/model/page/Pageable';
import { Loan } from '../model/Loan';
import { LoanService } from '../loan-service/loan.service';
import { LoanEditComponent } from '../loan-edit/loan-edit.component';
import { Client } from 'src/app/client/model/Client';
import { Game } from 'src/app/game/model/Game';
import { GameService } from 'src/app/game/game.service';
import { ClientService } from 'src/app/client/client.service';

@Component({
  selector: 'app-loan-list',
  templateUrl: './loan-list.component.html',
  styleUrls: ['./loan-list.component.scss']
})
export class LoanListComponent implements OnInit {
[x: string]: any;

  loan: Loan = new Loan();
  clients: Client[];
  games: Game[];

  filterClientName: string = '';
  filterGameTitle: string = '';
  pageNumber: number = 0;
  pageSize: number = 5;
  totalElements: number = 0;
  filterGame: Game | null = null;
  filterClient: Client | null = null;
  filterStartDate: Date | null = null;

  dataSource = new MatTableDataSource<Loan>();
  displayedColumns: string[] = ['id', 'gameTitle', 'clientName', 'startDate', 'endDate', 'action'];

  constructor(
    private loanService: LoanService,
    private gameService: GameService,
    private clientService: ClientService,
    public dialog: MatDialog,
  ) { }

  ngOnInit(): void {
    this.loadPage();
    this.loadGames();
    this.loadClients();
  }

  loadGames() {
    this.gameService.getGames().subscribe(games => {
      this.games = games;
    });
  }

  loadClients() {
    this.clientService.getClients().subscribe(clients => {
      this.clients = clients;
    });
  }

  loadPage(event?: PageEvent) {
    let pageable: Pageable = {
      pageNumber: this.pageNumber,
      pageSize: this.pageSize,
      sort: [
        { property: 'startDate', direction: 'ASC' },
        { property: 'id', direction: 'ASC' }
      ]
    };

    if (event != null) {
      pageable.pageSize = event.pageSize;
      pageable.pageNumber = event.pageIndex;
    }

    const filters = {
      gameId: this.filterGame ? this.filterGame.id : null,
      clientId: this.filterClient ? this.filterClient.id : null,
      date: this.filterStartDate ? this.formatDate(this.filterStartDate, 'yyyy-MM-dd') : null
    };

    this.loanService.getLoans(pageable, filters).subscribe(data => {
      this.dataSource.data = data.content;
      this.pageNumber = data.pageable.pageNumber;
      this.pageSize = data.pageable.pageSize;
      this.totalElements = data.totalElements;
    });
  }

  createLoan() {
    const dialogRef = this.dialog.open(LoanEditComponent, { data: {} });
    dialogRef.afterClosed().subscribe(() => this.loadPage());
  }

  deleteLoan(loan: Loan) {
    const dialogRef = this.dialog.open(DialogConfirmationComponent, {
      data: {
        title: "Eliminar préstamo",
        description: "Atención, si borra el préstamo se perderán sus datos. ¿Desea continuar?"
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loanService.deleteLoan(loan.id).subscribe(() => this.loadPage());
      }
    });
  }

  onCleanFilter() {
    this.filterGame = null;
    this.filterClient = null;
    this.filterStartDate = null;
    this.loadPage();
  }

  onSearch() {
    this.pageNumber = 0;
    this.loadPage();
  }

  private formatDate(date: Date, format: string = 'yyyy-MM-dd'): string {
    const d = new Date(date);
    const month = ('0' + (d.getMonth() + 1)).slice(-2);
    const day = ('0' + d.getDate()).slice(-2);
    const year = d.getFullYear();
    return [year, month, day].join('-');
  }
}
