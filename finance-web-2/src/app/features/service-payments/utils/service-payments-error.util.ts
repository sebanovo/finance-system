export function resolveServicePaymentError(error: unknown, fallback = 'No se pudo completar la operación'): string {
  const err = error as any;

  const message = err?.error?.message || err?.message || fallback;

  const mapped: Record<string, string> = {
    'Service provider not found': 'Proveedor de servicio no encontrado.',
    'Service provider is inactive': 'El proveedor de servicio está inactivo.',
    'Service customer not found': 'Código de servicio no encontrado.',
    'Service bill not found': 'Deuda de servicio no encontrada.',
    'Service bill is not pending': 'La deuda ya no está pendiente.',
    'Service bill has already been paid': 'La deuda ya fue pagada.',
    'Source account not found': 'Cuenta origen no encontrada.',
    'Source account does not belong to the current user': 'La cuenta no pertenece al usuario actual.',
    'Insufficient available balance': 'Saldo insuficiente para completar el pago.',
    'Idempotency key has already been used for another payment': 'El pago ya fue procesado anteriormente.'
  };

  return mapped[message] ?? message;
}
