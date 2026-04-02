import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';
import { environment } from './environments/environment';

async function enableMocking() {

  if (!environment.enableMocks) {
    return;
  }
  const { worker } = await import('./mocks/browser');

  await worker.start({ onUnhandledRequest: 'bypass' });
}

enableMocking().then(() => {
  bootstrapApplication(App, appConfig).catch((err) => console.error(err));
});

// bootstrapApplication(App, appConfig)
//   .catch((err) => console.error(err));
