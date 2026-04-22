/**
 * Generic API Response wrapper.
 * All backend endpoints return this structure.
 * Example: { success: true, message: "...", data: T, timestamp: "..." }
 */
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}
