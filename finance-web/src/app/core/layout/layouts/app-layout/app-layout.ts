import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AppHeader } from '../../components/app-header/app-header';
import { AppSidebar } from '../../components/app-sidebar/app-sidebar';

@Component({
  selector: 'app-app-layout',
  standalone: true,
  imports: [RouterOutlet, AppHeader, AppSidebar],
  templateUrl: './app-layout.html',
  styleUrl: './app-layout.css',
})
export class AppLayout {}
