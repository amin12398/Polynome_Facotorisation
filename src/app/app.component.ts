import { Component } from '@angular/core';
import { PolynomialService } from './services/polynomial.service'; 

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  polynomial: string = ''; // Contient le polynôme à résoudre
  result: any = null; // Contient les résultats renvoyés par l'API
  error: string | null = null; // Contient l'erreur si elle survient

  constructor(private polynomialService: PolynomialService) {}

  // Méthode pour appeler le service et résoudre le polynôme
  solve(): void {
    this.error = null; // Réinitialiser l'erreur
    this.result = null; // Réinitialiser le résultat

    // Préparer le JSON à envoyer
    const polynomialJson = JSON.stringify({
      polynomial: this.polynomial
    });

    // Appel au service pour résoudre le polynôme
    this.polynomialService.solvePolynomial(polynomialJson).subscribe({
      next: (res) => {
        console.log('Réponse du backend:', res); // Log pour vérifier la réponse
        this.result = res; // Stocker la réponse dans result
      },
      error: (err) => {
        this.error = 'Erreur lors du traitement du polynôme.'; // Affichage d'un message d'erreur
        console.error(err); // Log l'erreur
      },
    });
  }
}
