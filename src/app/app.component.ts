import { Component } from '@angular/core';
import { PolynomialService } from './services/polynomial.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  polynomial: string = '';
  method: string = 'newton'; // Par défaut : Newton
  result: any = null;
  error: string | null = null;

  // Données pour le graphique
  chartData: any = null;
  chartOptions: any = {
    responsive: true,
  };

  constructor(private polynomialService: PolynomialService) {}

  solve(): void {
    this.error = null;
    this.result = null;

    const payload = {
      polynomial: this.polynomial,
      method: this.method,
    };

    this.polynomialService.solvePolynomial(payload).subscribe({
      next: (res) => {
        this.result = res;

        // Mise à jour du graphique
        this.updateChart(res);
      },
      error: (err) => {
        this.error = 'Erreur lors du traitement du polynôme.';
        console.error(err);
      },
    });
  }

  // Méthode pour mettre à jour le graphique
  updateChart(data: any): void {
    const xValues = data.graph.x; // Points x
    const yValues = data.graph.y; // Points y
    this.chartData = {
      labels: xValues,
      datasets: [
        {
          label: 'Polynôme',
          data: yValues,
          borderColor: 'blue',
          fill: false,
        },
      ],
    };
  }
}
