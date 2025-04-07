import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root' //Servicio disponible en toda la aplicación
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/auth/login';  //URL del backend para login
  private tokenKey = 'authToken';  // Clave para guardar el token en sessionStorage

  constructor(private http: HttpClient) {}

  /**
   * Realiza la petición de login al backend y guarda el token si es correcto.
   */
  login(username: string, password: string): Observable<any> {
    const body = { username, password };  // Prepara el body con las credenciales

    return this.http.post<any>(this.apiUrl, body).pipe(
      tap((response) => {
        // Si la respuesta contiene un token, lo guardamos en sessionStorage
        if (response.token) {
          this.saveToken(response.token);
        }
      })
    );
  }

  /**
   * Guarda el token JWT en sessionStorage
   */
  saveToken(token: string): void {
    sessionStorage.setItem(this.tokenKey, token);
  }

  /**
   * Obtiene el token JWT desde sessionStorage
   */
  getToken(): string | null {
    return sessionStorage.getItem(this.tokenKey);
  }

  /**
   * Elimina el token (logout)
   */
  clearToken(): void {
    sessionStorage.removeItem(this.tokenKey);
  }

  /**
   * Comprueba si el usuario está autenticado
   */
  isAuthenticated(): boolean {
    return this.getToken() !== null;
  }
}
