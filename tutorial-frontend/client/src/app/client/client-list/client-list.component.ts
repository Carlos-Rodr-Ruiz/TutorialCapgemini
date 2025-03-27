import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Client } from '../model/Client';
import { ClientService } from '../client.service';
import { MatDialog } from '@angular/material/dialog';
import { ClientEditComponent } from '../client-edit/client-edit.component';
import { DialogConfirmationComponent } from 'src/app/core/dialog-confirmation/dialog-confirmation.component';

@Component({
  selector: 'app-client-list',
  templateUrl: './client-list.component.html',
  styleUrls: ['./client-list.component.scss']
})
export class ClientListComponent implements OnInit {

  dataSource = new MatTableDataSource<Client>();
  displayedColumns: string[] = ['id', 'name', 'action'];

  constructor(
    private clientService: ClientService,
    public dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.clientService.getClients().subscribe(
      clients => this.dataSource.data = clients
    );
  }

  createClient(): void {
    const dialogRef = this.dialog.open(ClientEditComponent, {
      data: {}
    });

    dialogRef.afterClosed().subscribe(() => this.ngOnInit());
  }

  editClient(client: Client): void {
    const dialogRef = this.dialog.open(ClientEditComponent, {
      data: { client }
    });
  
    dialogRef.afterClosed().subscribe((updatedClient: Client) => {
      if (updatedClient) {
        this.clientService.saveClient(updatedClient).subscribe(() => {
          this.ngOnInit();
        });
      }
    });
  }
  

  deleteClient(client: Client): void {
    const dialogRef = this.dialog.open(DialogConfirmationComponent, {
      data: {
        title: 'Eliminar cliente',
        description: `¿Estás seguro de que quieres eliminar al cliente "${client.name}"?`
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.clientService.deleteClient(client.id).subscribe(() => {
          this.ngOnInit();
        });
      }
    });
  }
}
