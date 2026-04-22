export type ApiFieldError = {
  field?: string;
  message: string;
};

export type ApiErrorResponse = {
  success: false;
  message: string;
  errors: ApiFieldError[];
  timestamp: string;
};
