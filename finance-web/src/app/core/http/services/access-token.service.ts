import { Injectable } from "@angular/core";
import { TokenData } from "../models/token.data";

@Injectable({
  providedIn: "root",
})
export class AccessTokenService {
  private readonly sessionKey = "session";

  getTokens(): TokenData | null {
    const raw = localStorage.getItem(this.sessionKey);

    if (!raw) {
      return null;
    }

    try {
      const parsed = JSON.parse(raw) as TokenData | null;

      if (!parsed || typeof parsed !== "object") {
        return null;
      }

      return parsed;
    } catch {
      return null;
    }
  }

  setTokens(session: TokenData): void {
    localStorage.setItem(this.sessionKey, JSON.stringify(session));
  }

  getAccessToken(): string | null {
    const session = this.getTokens();

    if (!session?.accessToken) {
      return null;
    }

    const token = session.accessToken.trim();
    return token.length > 0 ? token : null;
  }

  clearTokens(): void {
    localStorage.removeItem(this.sessionKey);
  }
}
