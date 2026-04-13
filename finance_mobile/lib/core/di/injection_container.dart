import 'package:finance_mobile/core/network/api_client.dart';
import 'package:finance_mobile/domain/repositories/auth_repository.dart';
import 'package:finance_mobile/domain/repositories/permission_repository.dart';
import 'package:finance_mobile/domain/repositories/role_repository.dart';
import 'package:finance_mobile/domain/usecases/forgot_password_usecase.dart';
import 'package:finance_mobile/domain/usecases/get_permissions_usecase.dart';
import 'package:finance_mobile/domain/usecases/get_roles_usecase.dart';
import 'package:finance_mobile/domain/usecases/login_usecase.dart';
import 'package:finance_mobile/domain/usecases/reset_password_usecase.dart';
import 'package:finance_mobile/domain/usecases/signup_usecase.dart';
import 'package:finance_mobile/infrastructure/datasources/auth_remote_datasource.dart';
import 'package:finance_mobile/infrastructure/datasources/permission_remote_datasource.dart';
import 'package:finance_mobile/infrastructure/datasources/role_remote_datasource.dart';
import 'package:finance_mobile/infrastructure/repositories/auth_repository_impl.dart';
import 'package:finance_mobile/infrastructure/repositories/permission_repository_impl.dart';
import 'package:finance_mobile/infrastructure/repositories/role_repository_impl.dart';
import 'package:finance_mobile/presentation/viewmodels/forgot_password_viewmodel.dart';
import 'package:finance_mobile/presentation/viewmodels/login_viewmodel.dart';
import 'package:finance_mobile/presentation/viewmodels/permissions_viewmodel.dart';
import 'package:finance_mobile/presentation/viewmodels/reset_password_viewmodel.dart';
import 'package:finance_mobile/presentation/viewmodels/roles_viewmodel.dart';
import 'package:finance_mobile/presentation/viewmodels/signup_viewmodel.dart';
import 'package:get_it/get_it.dart';

// service locator
final sl = GetIt.instance;

Future<void> init() async {
  sl.registerLazySingleton(() => ApiClient());
  initPermissionModule();
  initRoleModule();
  initAuthModule();
}

void initPermissionModule() {
  // Permission Features
  sl.registerLazySingleton<PermissionRemoteDataSource>(
    () => PermissionRemoteDataSourceImpl(sl()),
  );
  sl.registerLazySingleton<PermissionRepository>(
    () => PermissionRepositoryImpl(sl()),
  );
  sl.registerLazySingleton(() => GetPermissionsUsecase(sl()));
  sl.registerFactory(() => PermissionsViewModel(getPermissionsUseCase: sl()));
}

void initRoleModule() {
  // Roles Features
  sl.registerLazySingleton<RoleRemoteDataSource>(
    () => RoleRemoteDataSourceImpl(sl()),
  );
  sl.registerLazySingleton<RoleRepository>(() => RoleRepositoryImpl(sl()));
  sl.registerLazySingleton(() => GetRolesUsecase(sl()));
  sl.registerFactory(() => RolesViewModel(sl()));
}

void initAuthModule() {
  // Auth Feature
  sl.registerLazySingleton<AuthRemoteDataSource>(
    () => AuthRemoteDataSourceImpl(sl()),
  );
  sl.registerLazySingleton<AuthRepository>(() => AuthRepositoryImpl(sl()));
  sl.registerLazySingleton(() => LoginUseCase(sl()));
  sl.registerFactory(() => LoginViewModel(loginUseCase: sl()));
  // Reset Features
  sl.registerLazySingleton(() => ResetPasswordUseCase(sl()));
  sl.registerFactory(() => ResetPasswordViewModel(resetPasswordUseCase: sl()));
  // SignUp(Tenant) Features
  sl.registerLazySingleton(() => SignupUseCase(sl()));
  sl.registerFactory(() => SignupViewModel(signupUseCase: sl()));
  // Forgot Password FEatures
  sl.registerLazySingleton(() => ForgotPasswordUseCase(sl()));
  sl.registerFactory(
    () => ForgotPasswordViewModel(forgotPasswordUseCase: sl()),
  );
}
