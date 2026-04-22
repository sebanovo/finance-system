/**
 * Servicio de utilidad para gestionar el tenant slug en el almacenamiento local
 * Se utiliza después del login para guardar el tenant y en logout para limpiar
 */
export class TenantStorage {
    /**
     * Clave para almacenar el tenant slug en localStorage
     */
    private static readonly TENANT_SLUG_KEY = 'tenant_slug';

    /**
     * Obtiene el tenant slug actual del almacenamiento
     * @returns El tenant slug o null si no existe
     */
    static getTenantSlug(): string | null {
        return localStorage.getItem(this.TENANT_SLUG_KEY);
    }

    /**
     * Establece el tenant slug en el almacenamiento local
     * Se debe llamar después de un login exitoso
     * @param tenantSlug El slug del tenant
     */
    static setTenantSlug(tenantSlug: string): void {
        localStorage.setItem(this.TENANT_SLUG_KEY, tenantSlug);
    }

    /**
     * Limpia el tenant slug del almacenamiento
     * Se debe llamar en el logout
     */
    static clearTenantSlug(): void {
        localStorage.removeItem(this.TENANT_SLUG_KEY);
    }

    /**
     * Verifica si existe un tenant slug activo
     * @returns true si hay un tenant slug, false en caso contrario
     */
    static hasTenantSlug(): boolean {
        return this.getTenantSlug() !== null;
    }
}
