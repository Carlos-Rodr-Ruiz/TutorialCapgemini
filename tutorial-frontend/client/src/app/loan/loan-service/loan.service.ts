import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Pageable } from 'src/app/core/model/page/Pageable';
import { LoanPage } from '../model/LoanPage';
import { Loan } from '../model/Loan';


@Injectable({
  providedIn: 'root'
})
export class LoanService {

  private apiUrl = 'http://localhost:8080/loan';

  constructor(private http: HttpClient) { }

  getLoans(pageable: Pageable, filters: any): Observable<LoanPage> {
    const payload = {
      pageable: pageable,
      ...filters
    };

    return this.http.post<LoanPage>(this.apiUrl, payload).pipe(
      catchError(this.handleError)
    );
  }


  getAllLoans(): Observable<Loan[]> {
    return this.http.get<Loan[]>(this.apiUrl);
  }

  saveLoan(loan: Loan): Observable<Loan> {
    const url = loan.id ? `${this.apiUrl}/${loan.id}` : this.apiUrl;
    return this.http.put<Loan>(url, loan).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = '';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `An error occurred: ${error.error.message}`;
    } else {
      errorMessage = error.error || 'Something went wrong';
    }
    return throwError(() => new Error(errorMessage));
  }

  deleteLoan(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
