import { Injectable } from '@angular/core';
import {
  HttpInterceptor,       //Interfaz para interceptar peticiones HTTP
  HttpRequest,            //Representa una petición HTTP
  HttpHandler,            //Encargado de enviar la petición al backend
  HttpEvent               //Representa un evento de respuesta HTTP
} from '@angular/common/http';

import { Observable } from 'rxjs';
import { AuthService } from './auth.service'; // Servicio donde tenemos guardado el token

/**
 * Clase que interviene en todas las peticiones y se encarga de añadir el 
 * token JWT a las cabeceras de cada petición HTTP si el usuario está autenticado.
 */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService) {}

  /**
   * Método que intercepta cada petición saliente del cliente.
   * Si hay un token en sessionStorage, lo añade como cabecera Authorization.
   *
   * @param req la petición HTTP original
   * @param next el siguiente paso el backend
   * @returns Un Observable (respuesta HTTP) de la petición interceptada,
   *          ya sea con o sin el token añadido
   */
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.getToken(); //Obtenemos el token si existe
    console.log('[INTERCEPTOR] Token en sesión:', token); 

    if (token) {
      //Si hay token, clonamos la petición y le añadimos la cabecera Authorization
      const authReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
      console.log('[INTERCEPTOR] Petición con token añadida:', authReq);
      return next.handle(authReq); // Enviamos la petición con token
    }

    //Si no hay token, enviamos la petición tal cual 
    console.log('[INTERCEPTOR] Petición sin token:', req);
    return next.handle(req);
  }
}
