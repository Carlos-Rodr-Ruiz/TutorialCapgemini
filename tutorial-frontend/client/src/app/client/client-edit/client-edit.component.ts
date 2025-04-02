import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ClientService } from '../client.service';
import { Client } from '../model/Client';

@Component({
  selector: 'app-client-edit',
  templateUrl: './client-edit.component.html',
  styleUrls: ['./client-edit.component.scss']
})
export class ClientEditComponent implements OnInit {
  client: Client = { id: null, name: '' };

  constructor(
    public dialogRef: MatDialogRef<ClientEditComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private clientService: ClientService
  ) { }

  ngOnInit(): void {
    if (this.data.client) {
      this.client = { ...this.data.client };
    }
  }

  onSave(): void {
    this.clientService.saveClient(this.client).subscribe(result => {
      this.dialogRef.close(result);
    });
  }

  onClose(): void {
    this.dialogRef.close();
  }
}
