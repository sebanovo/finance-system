export type AccessResponse<T> = {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
};
