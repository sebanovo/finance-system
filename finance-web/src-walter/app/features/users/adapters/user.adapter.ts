import { CreateUserRequest } from "../models/user-request.type";
import { UserDto } from "../models/user.dto";
import {
  UpdateUserRequest,
  User,
  UserUpsertFormValue,
} from "../models/user.model";

export function toUser(dto: UserDto): User {
  return {
    id: dto.id,
    email: dto.email,
    firstName: dto.firstName,
    lastName: dto.lastName,
    isActive: dto.active,
    status: dto.status,
    createdAt: new Date(dto.createdAt).toDateString(),
    updatedAt: new Date(dto.updatedAt).toDateString(),
  };
}

export function toUsers(dtos: UserDto[]): User[] {
  return dtos.map(toUser);
}

export function toCreateUserRequest(
  formValue: UserUpsertFormValue
): CreateUserRequest {
  return {
    email: formValue.email.trim(),
    password: formValue.password,
    firstName: formValue.firstName.trim(),
    lastName: formValue.lastName.trim(),
  };
}

export function toUpdateUserRequest(
  formValue: UserUpsertFormValue
): UpdateUserRequest {
  return {
    email: formValue.email.trim(),
    firstName: formValue.firstName.trim(),
    lastName: formValue.lastName.trim(),
  };
}
