import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Client } from '../model/Client';

@Component({
  selector: 'app-client-edit',
  templateUrl: './client-edit.component.html',
  styleUrls: ['./client-edit.component.scss']
})
export class ClientEditComponent {

  client: Client;

  constructor(
    public dialogRef: MatDialogRef<ClientEditComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.client = data.client || { id: null, name: '' };
  }

  onSave(): void {
    if (!this.client.name?.trim()) return;
    this.dialogRef.close(this.client);
  }

  onClose(): void {
    this.dialogRef.close();
  }
}
