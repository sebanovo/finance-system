import { CreateUserRequest, UserFormValue } from "../models/user-request.type";
import { UserDto } from "../models/user.dto";
import { User } from "../models/user.model";

export function toUser(dto: UserDto): User {
  return {
    id: dto.id,
    email: dto.email,
    fullName: `${dto.firstName} ${dto.lastName}`,
    isActive: dto.active,
    status: dto.status,
    createdAt: new Date(dto.createdAt),
    updatedAt: new Date(dto.updatedAt),
  };
}

export function toUsers(dtos: UserDto[]): User[] {
  return dtos.map(toUser);
}

export function toCreateUserRequest(
  formValue: UserFormValue
): CreateUserRequest {
  return {
    email: formValue.email.trim(),
    password: formValue.password,
    firstName: formValue.firstName.trim(),
    lastName: formValue.lastName.trim(),
  };
}
