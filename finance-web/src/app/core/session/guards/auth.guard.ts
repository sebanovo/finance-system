// import { inject } from "@angular/core";
// import { CanActivateFn, Router } from "@angular/router";

// import { SessionStore } from "../store/session.store";

// export const authMatchGuard: CanActivateFn = (_route, state) => {
//   const sessionStore = inject(SessionStore);
//   const router = inject(Router);

//   if (sessionStore.isAuthenticated() && sessionStore.initialized()) {
//     return true;
//   }
//   console.log("authMatchGuard");

//   return router.createUrlTree(["/auth/login"], {
//     queryParams: {
//       returnUrl: state.url,
//     },
//   });
// };

import { inject } from "@angular/core";
import { CanActivateFn, Router, UrlTree } from "@angular/router";

import { SessionStore } from "../store/session.store";

export const authGuard: CanActivateFn = (_route, state): boolean | UrlTree => {
  const sessionStore = inject(SessionStore);
  const router = inject(Router);

  if (!sessionStore.initialized()) {
    console.log(
      "authGuard: session not initialized, redirecting to /auth/login"
    );
    return router.createUrlTree(["/auth/login"], {
      queryParams: {
        returnUrl: state.url,
      },
    });
  }

  if (sessionStore.isAuthenticated()) {
    console.log("authGuard: user is authenticated, allowing access to route");
    return true;
  }

  return router.createUrlTree(["/auth/login"], {
    queryParams: {
      returnUrl: state.url,
    },
  });
};
