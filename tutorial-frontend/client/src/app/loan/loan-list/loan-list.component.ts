import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { LoanService } from '../loan-service/loan.service';
import { Loan } from '../model/Loan';
import { ClientService } from 'src/app/client/client.service';
import { GameService } from 'src/app/game/game.service';
import { Client } from 'src/app/client/model/Client';
import { Game } from 'src/app/game/model/Game';
import { LoanEditComponent } from '../loan-edit/loan-edit.component';
import { MatDialog } from '@angular/material/dialog';
import { DialogConfirmationComponent } from 'src/app/core/dialog-confirmation/dialog-confirmation.component';
import { DialogSuccessComponent } from 'src/app/core/dialog-success/dialog-success.component';

@Component({
  selector: 'app-loan-list',
  templateUrl: './loan-list.component.html',
  styleUrls: ['./loan-list.component.scss']
})
export class LoanListComponent implements OnInit {
  
  
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  loans: Loan[] = [];
  displayedColumns: string[] = ['id', 'gameTitle', 'clientName', 'startDate', 'endDate', 'action'];

  // Paginación
  pageNumber = 0;
  pageSize = 5;
  totalElements = 0;

  // Filtros
  filterClient: Client | null = null;
  filterGame: Game | null = null;
  filterDate: Date | null = null;

  // Datos para combos
  clients: Client[] = [];
  games: Game[] = [];

  constructor(
    private loanService: LoanService,
    private clientService: ClientService,
    private gameService: GameService,
    private dialog: MatDialog
  ) {}

  createLoan(): void {
    const dialogRef = this.dialog.open(LoanEditComponent, {
      data: { loan: null }
    });
  
    dialogRef.afterClosed().subscribe(() => {
      this.loadPage({
        pageIndex: this.pageNumber,
        pageSize: this.pageSize,
        length: this.totalElements
      });
    });
  }
  

  ngOnInit(): void {
    this.loadClients();
    this.loadGames();
    this.loadPage(); // carga inicial
  }

  loadClients(): void {
    this.clientService.getClients().subscribe(data => this.clients = data);
  }

  loadGames(): void {
    this.gameService.getGames().subscribe(data => this.games = data);
  }

  loadPage(event?: PageEvent): void {
    if (event) {
      this.pageNumber = event.pageIndex;
      this.pageSize = event.pageSize;
    }

    const pageable = {
      pageNumber: this.pageNumber,
      pageSize: this.pageSize,
      sort: [{ property: 'startDate', direction: 'ASC' }]
    };

    const filters = {
      clientId: this.filterClient?.id || null,
      gameId: this.filterGame?.id || null,
      date: this.filterDate ? this.filterDate.toISOString().split('T')[0] : null
    };

    this.loanService.getLoans(pageable, filters).subscribe(data => {
      this.loans = data.content;
      this.totalElements = data.totalElements;
    });
  }

  onSearch(): void {
    this.pageNumber = 0;
    if (this.paginator) this.paginator.firstPage();
    this.loadPage();
  }

  clearFilters(): void {
    this.filterClient = null;
    this.filterGame = null;
    this.filterDate = null;
    this.onSearch();
  }
  deleteLoan(loan: Loan): void {
    const dialogRef = this.dialog.open(DialogConfirmationComponent, {
      data: {
        title: "Eliminar préstamo",
        description: `¿Estás seguro de que deseas eliminar el préstamo del juego "${loan['gameTitle']}" para el cliente "${loan['clientName']}"?`
      }
    });
  
    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.loanService.deleteLoan(loan.id).subscribe(() => {
          this.loadPage();
          this.dialog.open(DialogSuccessComponent, {
            data: {
              title: 'Préstamo eliminado',
              description: 'El préstamo fue eliminado correctamente.'
            }
          });
        });
      }
    });
    
  }
  
}  

