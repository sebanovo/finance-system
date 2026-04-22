export type UserResponse<T> = {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
};
