export type User = {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  isActive: boolean;
  status: string;
  createdAt: string; //Date
  updatedAt: string; //Date
};

export type UpdateUserRequest = {
  email: string;
  firstName: string;
  lastName: string;
};

export type UserUpsertFormValue = {
  email: string;
  firstName: string;
  lastName: string;
  password: string;
};
