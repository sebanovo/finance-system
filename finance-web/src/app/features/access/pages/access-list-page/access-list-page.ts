import { Component, inject, OnInit } from "@angular/core";
import { AccessStore } from "../../store/access.store";

@Component({
  selector: "app-access-list-page",
  imports: [],
  templateUrl: "./access-list-page.html",
  styleUrl: "./access-list-page.css",
})
export class AccessListPage implements OnInit {
  readonly store = inject(AccessStore);

  ngOnInit(): void {
    console.log("init", this.store.permissions);
    void this.store.loadPermisssions();
  }
}
