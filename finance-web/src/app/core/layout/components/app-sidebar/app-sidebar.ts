import { Component } from "@angular/core";
import { RouterLink, RouterLinkActive } from "@angular/router";
import { APP_NAVIGATION } from "../../constants/app-navigation.constants";

@Component({
  selector: "app-app-sidebar",
  imports: [RouterLink, RouterLinkActive],
  templateUrl: "./app-sidebar.html",
  styleUrl: "./app-sidebar.css",
})
export class AppSidebar {
  readonly navigationItems = APP_NAVIGATION;
}
