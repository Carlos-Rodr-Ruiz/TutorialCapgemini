import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
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
    @Inject(MAT_DIALOG_DATA) public data: any
  ) { }

  ngOnInit(): void {
    // Si hay un cliente recibido en los datos del diálogo, lo asignamos
    if (this.data.client) {
      this.client = { ...this.data.client };
    }
  }

  onSave(): void {
    if (this.client.id) {
      // Editar cliente existente
      this.dialogRef.close(this.client);
    } else {
      // Crear nuevo cliente
      this.client.id = null; // Asegurarse de que el id esté vacío al crear un nuevo cliente
      this.dialogRef.close(this.client);
    }
  }

  onClose(): void {
    this.dialogRef.close();
  }
}
