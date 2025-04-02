import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Loan } from '../model/Loan';
import { LoanPage } from '../model/LoanPage';
import { Pageable } from 'src/app/core/model/page/Pageable';

@Injectable({
  providedIn: 'root'
})
export class LoanService {
  deleteCategory(id: number) {
    throw new Error('Method not implemented.');
  }

  private apiUrl = 'http://localhost:8080/loan';

  constructor(private http: HttpClient) { }

  /**
   * Obtener préstamos con filtros y paginación
   */
  getLoans(pageable: Pageable, filters: any): Observable<LoanPage> {
    const payload = {
      pageable: pageable,
      filters: filters
    };

    return this.http.post<LoanPage>(this.apiUrl, payload).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Guardar un préstamo
   */
  saveLoan(dto: any): Observable<any> {
    if (dto.id) {
      // Actualización
      return this.http.put<any>(`${this.apiUrl}/${dto.id}`, dto).pipe(
        catchError(this.handleError)
      );
    } else {
      // Alta nuevo préstamo
      return this.http.post<any>(this.apiUrl, dto).pipe(
        catchError(this.handleError)
      );
    }
  }
  

  /**
   * Eliminar un préstamo
   */
  deleteLoan(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Manejo de errores
   */
  private handleError(error: HttpErrorResponse) {
    let errorMessage = '';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `An error occurred: ${error.error.message}`;
    } else {
      errorMessage = error.error || 'Something went wrong';
    }
    return throwError(() => new Error(errorMessage));
  }
}
