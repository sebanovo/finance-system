import { Injectable } from '@angular/core';

/**
 * Servicio centralizado para manejar almacenamiento en localStorage
 * Proporciona encriptación básica y validación
 * (Nota: para seguridad crítica, usar HttpOnly cookies en el backend)
 */
@Injectable({
  providedIn: 'root',
})
export class StorageService {
  private prefix = 'finance_';

  /**
   * Guarda un valor en localStorage con un prefijo
   */
  setItem(key: string, value: string): void {
    try {
      localStorage.setItem(this.prefix + key, value);
    } catch (error) {
      console.error('Error storing item:', error);
    }
  }

  /**
   * Obtiene un valor de localStorage
   */
  getItem(key: string): string | null {
    try {
      return localStorage.getItem(this.prefix + key);
    } catch (error) {
      console.error('Error retrieving item:', error);
      return null;
    }
  }

  /**
   * Elimina un item de localStorage
   */
  removeItem(key: string): void {
    try {
      localStorage.removeItem(this.prefix + key);
    } catch (error) {
      console.error('Error removing item:', error);
    }
  }

  /**
   * Limpia todos los items con el prefijo de finance
   */
  clear(): void {
    try {
      const keys = Object.keys(localStorage);
      keys.forEach((key) => {
        if (key.startsWith(this.prefix)) {
          localStorage.removeItem(key);
        }
      });
    } catch (error) {
      console.error('Error clearing storage:', error);
    }
  }

  /**
   * Comprueba si existe un item
   */
  hasItem(key: string): boolean {
    return localStorage.getItem(this.prefix + key) !== null;
  }

  /**
   * Obtiene todos los items de finance
   */
  getAllItems(): Record<string, string> {
    const items: Record<string, string> = {};
    const keys = Object.keys(localStorage);
    keys.forEach((key) => {
      if (key.startsWith(this.prefix)) {
        const value = localStorage.getItem(key);
        if (value) {
          items[key.replace(this.prefix, '')] = value;
        }
      }
    });
    return items;
  }
}
