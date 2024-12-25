import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PolynomialService {
  private apiUrl = 'http://localhost:8080/api/solve'; // URL de l'API backend

  constructor(private http: HttpClient) {}

  solvePolynomial(polynomialJson: string): Observable<any> {
    // Envoie le JSON au backend et retourne un Observable avec la réponse
    return this.http.post<any>(this.apiUrl, polynomialJson, {
      headers: new HttpHeaders().set('Content-Type', 'application/json'),
    });
  }
}
