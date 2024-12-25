import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AppComponent } from './app.component'; // Ajoutez le bon composant

const routes: Routes = [
  { path: 'polynome', component: AppComponent },  // Route vers le composant de résolveur de polynômes
  { path: '', redirectTo: '/polynome', pathMatch: 'full' } // Redirige vers la route principale
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
