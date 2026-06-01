import 'package:finance_mobile/core/network/api_client.dart';
import 'package:finance_mobile/core/services/notification_service.dart';
import 'package:finance_mobile/domain/repositories/account_repository.dart';
import 'package:finance_mobile/domain/repositories/auth_repository.dart';
import 'package:finance_mobile/domain/repositories/notification_repository.dart';
import 'package:finance_mobile/domain/repositories/permission_repository.dart';
import 'package:finance_mobile/domain/repositories/role_repository.dart';
import 'package:finance_mobile/domain/repositories/subscription_repository.dart';
import 'package:finance_mobile/domain/repositories/transaction_repository.dart';
import 'package:finance_mobile/domain/repositories/user_repository.dart';
import 'package:finance_mobile/domain/usecases/archive_notification_usecase.dart';
import 'package:finance_mobile/domain/usecases/assign_role_usecase.dart';
import 'package:finance_mobile/domain/usecases/change_password_usecase.dart';
import 'package:finance_mobile/domain/usecases/create_account_usecase.dart';
import 'package:finance_mobile/domain/usecases/create_deposit_usecase.dart';
import 'package:finance_mobile/domain/usecases/create_hold_usecase.dart';
import 'package:finance_mobile/domain/usecases/create_payment_usecase.dart';
import 'package:finance_mobile/domain/usecases/create_release_usecase.dart';
import 'package:finance_mobile/domain/usecases/create_transfer_usecase.dart';
import 'package:finance_mobile/domain/usecases/create_user_usecase.dart';
import 'package:finance_mobile/domain/usecases/create_withdrawal_usecase.dart';
import 'package:finance_mobile/domain/usecases/deactivate_device_usecase.dart';
import 'package:finance_mobile/domain/usecases/forgot_password_usecase.dart';
import 'package:finance_mobile/domain/usecases/get_account_balance_usecase.dart';
import 'package:finance_mobile/domain/usecases/get_account_by_id_usecase.dart';
import 'package:finance_mobile/domain/usecases/get_account_transactions_usecase.dart';
import 'package:finance_mobile/domain/usecases/get_accounts_usecase.dart';
import 'package:finance_mobile/domain/usecases/get_available_roles_usecase.dart';
import 'package:finance_mobile/domain/usecases/get_devices_usecase.dart';
import 'package:finance_mobile/domain/usecases/get_notifications_usecase.dart';
import 'package:finance_mobile/domain/usecases/get_permissions_usecase.dart';
import 'package:finance_mobile/domain/usecases/create_role_usecase.dart';
import 'package:finance_mobile/domain/usecases/get_roles_usecase.dart';
import 'package:finance_mobile/domain/usecases/activate_role_usecase.dart';
import 'package:finance_mobile/domain/usecases/deactivate_role_usecase.dart';
import 'package:finance_mobile/domain/usecases/get_role_usecase.dart';
import 'package:finance_mobile/domain/usecases/get_transaction_by_id_usecase.dart';
import 'package:finance_mobile/domain/usecases/get_transactions_usecase.dart';
import 'package:finance_mobile/domain/usecases/get_unread_count_usecase.dart';
import 'package:finance_mobile/domain/usecases/mark_all_notifications_as_read_usecase.dart';
import 'package:finance_mobile/domain/usecases/mark_notification_as_read_usecase.dart';
import 'package:finance_mobile/domain/usecases/open_notification_usecase.dart';
import 'package:finance_mobile/domain/usecases/register_device_usecase.dart';
import 'package:finance_mobile/domain/usecases/revoke_device_usecase.dart';
import 'package:finance_mobile/domain/usecases/update_account_alias_usecase.dart';
import 'package:finance_mobile/domain/usecases/update_role_usecase.dart';
import 'package:finance_mobile/domain/usecases/get_subscription_usecase.dart';
import 'package:finance_mobile/domain/usecases/get_user_info_usecase.dart';
import 'package:finance_mobile/domain/usecases/get_user_roles_usecase.dart';
import 'package:finance_mobile/domain/usecases/get_users_usecase.dart';
import 'package:finance_mobile/domain/usecases/login_usecase.dart';
import 'package:finance_mobile/domain/usecases/logout_usecase.dart';
import 'package:finance_mobile/domain/usecases/reset_password_usecase.dart';
import 'package:finance_mobile/domain/usecases/signup_usecase.dart';
import 'package:finance_mobile/infrastructure/datasources/account_remote_datasource.dart';
import 'package:finance_mobile/infrastructure/datasources/auth_remote_datasource.dart';
import 'package:finance_mobile/infrastructure/datasources/notification_remote_datasource.dart';
import 'package:finance_mobile/infrastructure/datasources/permission_remote_datasource.dart';
import 'package:finance_mobile/infrastructure/datasources/role_remote_datasource.dart';
import 'package:finance_mobile/infrastructure/datasources/subscription_remote_datasource.dart';
import 'package:finance_mobile/infrastructure/datasources/transaction_remote_datasource.dart';
import 'package:finance_mobile/infrastructure/datasources/user_remote_datasource.dart';
import 'package:finance_mobile/infrastructure/repositories/account_repository_impl.dart';
import 'package:finance_mobile/infrastructure/repositories/auth_repository_impl.dart';
import 'package:finance_mobile/infrastructure/repositories/notification_repository_impl.dart';
import 'package:finance_mobile/infrastructure/repositories/permission_repository_impl.dart';
import 'package:finance_mobile/infrastructure/repositories/role_repository_impl.dart';
import 'package:finance_mobile/infrastructure/repositories/subscription_repository_impl.dart';
import 'package:finance_mobile/infrastructure/repositories/transaction_repository_impl.dart';
import 'package:finance_mobile/infrastructure/repositories/user_repository_impl.dart';
import 'package:finance_mobile/presentation/viewmodels/accounts_viewmodel.dart';
import 'package:finance_mobile/presentation/viewmodels/devices_viewmodel.dart';
import 'package:finance_mobile/presentation/viewmodels/forgot_password_viewmodel.dart';
import 'package:finance_mobile/presentation/viewmodels/home_viewmodel.dart';
import 'package:finance_mobile/presentation/viewmodels/login_viewmodel.dart';
import 'package:finance_mobile/presentation/viewmodels/notifications_viewmodel.dart';
import 'package:finance_mobile/presentation/viewmodels/permissions_viewmodel.dart';
import 'package:finance_mobile/presentation/viewmodels/reset_password_viewmodel.dart';
import 'package:finance_mobile/presentation/viewmodels/roles_viewmodel.dart';
import 'package:finance_mobile/presentation/viewmodels/signup_viewmodel.dart';
import 'package:finance_mobile/presentation/viewmodels/transactions_viewmodel.dart';
import 'package:finance_mobile/presentation/viewmodels/users_viewmodel.dart';
import 'package:get_it/get_it.dart';

// service locator
final sl = GetIt.instance;

Future<void> init() async {
  sl.registerLazySingleton(() => ApiClient());
  initPermissionModule();
  initRoleModule();
  initAuthModule();
  initUserModule();
  initSubscriptionModule();
  initHomeModule();
  initAccountsModule();
  initTransactionModule();
  initNotifationsModule();
  initDevicesNotifications();
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
  sl.registerLazySingleton(() => CreateRoleUseCase(sl()));
  sl.registerLazySingleton(() => ActivateRoleUseCase(sl()));
  sl.registerLazySingleton(() => DeactivateRoleUseCase(sl()));
  sl.registerLazySingleton(() => GetRoleUseCase(sl()));
  sl.registerLazySingleton(() => UpdateRoleUseCase(sl()));
  sl.registerFactory(
    () => RolesViewModel(
      getRolesUseCase: sl(),
      createRoleUseCase: sl(),
      activateRoleUseCase: sl(),
      deactivateRoleUseCase: sl(),
      updateRoleUseCase: sl(),
    ),
  );
}

void initAuthModule() {
  // Auth Feature
  sl.registerLazySingleton<AuthRemoteDataSource>(
    () => AuthRemoteDataSourceImpl(sl()),
  );
  sl.registerLazySingleton<AuthRepository>(() => AuthRepositoryImpl(sl(), sl()));
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

  sl.registerLazySingleton(() => ChangePasswordUseCase(sl()));
  sl.registerLazySingleton(() => LogoutUseCase(sl()));

  sl.registerFactory(
    () => ForgotPasswordViewModel(forgotPasswordUseCase: sl()),
  );
}

void initUserModule() {
  // Users Features
  sl.registerLazySingleton<UserRemoteDataSource>(
    () => UserRemoteDataSourceImpl(sl()),
  );
  sl.registerLazySingleton<UserRepository>(() => UserRepositoryImpl(sl()));
  sl.registerLazySingleton(() => GetUsersUseCase(sl()));
  sl.registerLazySingleton(() => GetUserRolesUseCase(sl()));
  sl.registerLazySingleton(() => AssignRoleUseCase(sl()));
  sl.registerLazySingleton(() => CreateUserUseCase(sl()));
  sl.registerLazySingleton(() => GetAvailableRolesUseCase(sl()));
  sl.registerLazySingleton(() => GetUserInfoUseCase(sl()));
  sl.registerFactory(
    () => UsersViewModel(
      getUsersUseCase: sl(),
      getUserRolesUseCase: sl(),
      assignRoleUseCase: sl(),
      createUserUseCase: sl(),
      getAvailableRolesUseCase: sl(),
    ),
  );
}

void initSubscriptionModule() {
  sl.registerLazySingleton<SubscriptionRemoteDataSource>(
    () => SubscriptionRemoteDataSourceImpl(sl()),
  );
  sl.registerLazySingleton<SubscriptionRepository>(
    () => SubscriptionRepositoryImpl(sl()),
  );
  sl.registerLazySingleton(() => GetSubscriptionUseCase(sl()));
}

void initHomeModule() {
  sl.registerFactory(
    () => HomeViewModel(
      getSubscriptionUseCase: sl(),
      getUserInfoUseCase: sl(),
      changePasswordUseCase: sl(),
      logoutUseCase: sl(),
    ),
  );
}

void initAccountsModule() {
  // Accounts feature
  sl.registerLazySingleton<AccountRemoteDataSource>(
    () => AccountRemoteDataSourceImpl(sl()),
  );
  sl.registerLazySingleton<AccountRepository>(
    () => AccountRepositoryImpl(sl()),
  );
  sl.registerLazySingleton(() => GetAccountsUseCase(sl()));
  sl.registerLazySingleton(() => GetAccountByIdUseCase(sl()));
  sl.registerLazySingleton(() => GetAccountBalanceUseCase(sl()));
  sl.registerLazySingleton(() => UpdateAccountAliasUseCase(sl()));
  sl.registerLazySingleton(() => GetAccountTransactionsUseCase(sl()));
  sl.registerLazySingleton(() => CreateAccountUseCase(sl()));
  sl.registerFactory(
    () => AccountsViewModel(
      getAccountsUseCase: sl(),
      getAccountByIdUseCase: sl(),
      getAccountBalanceUseCase: sl(),
      updateAccountAliasUseCase: sl(),
      getAccountTransactionsUseCase: sl(),
      createAccountUseCase: sl(),
    ),
  );
}

void initTransactionModule() {
  // Transactions feature
  sl.registerLazySingleton<TransactionRemoteDataSource>(
    () => TransactionRemoteDataSourceImpl(sl()),
  );
  sl.registerLazySingleton<TransactionRepository>(
    () => TransactionRepositoryImpl(sl()),
  );
  sl.registerLazySingleton(() => GetTransactionsUseCase(sl()));
  sl.registerLazySingleton(() => GetTransactionByIdUseCase(sl()));
  sl.registerLazySingleton(() => CreateDepositUseCase(sl()));
  sl.registerLazySingleton(() => CreateHoldUseCase(sl()));
  sl.registerLazySingleton(() => CreatePaymentUseCase(sl()));
  sl.registerLazySingleton(() => CreateReleaseUseCase(sl()));
  sl.registerLazySingleton(() => CreateTransferUseCase(sl()));
  sl.registerLazySingleton(() => CreateWithdrawalUseCase(sl()));
  sl.registerFactory(
    () => TransactionsViewModel(
      getTransactionsUseCase: sl(),
      getTransactionByIdUseCase: sl(),
      createDepositUseCase: sl(),
      createHoldUseCase: sl(),
      createPaymentUseCase: sl(),
      createReleaseUseCase: sl(),
      createTransferUseCase: sl(),
      createWithdrawalUseCase: sl(),
    ),
  );
}

void initNotifationsModule() {
  // Notifications
  sl.registerLazySingleton<NotificationRemoteDataSource>(
    () => NotificationRemoteDataSourceImpl(sl()),
  );
  sl.registerLazySingleton<NotificationRepository>(
    () => NotificationRepositoryImpl(sl()),
  );
  sl.registerLazySingleton(() => GetNotificationsUseCase(sl()));
  sl.registerLazySingleton(() => GetUnreadCountUseCase(sl()));
  sl.registerLazySingleton(() => MarkNotificationAsReadUseCase(sl()));
  sl.registerLazySingleton(() => MarkAllNotificationsAsReadUseCase(sl()));
  sl.registerLazySingleton(() => ArchiveNotificationUseCase(sl()));
  sl.registerLazySingleton(() => OpenNotificationUseCase(sl()));
  sl.registerLazySingleton(() => RegisterDeviceUseCase(sl()));
  sl.registerLazySingleton(() => GetDevicesUseCase(sl()));
  sl.registerLazySingleton(() => DeactivateDeviceUseCase(sl()));
  sl.registerFactory(
    () => NotificationsViewModel(
      getNotificationsUseCase: sl(),
      getUnreadCountUseCase: sl(),
      markAsReadUseCase: sl(),
      markAllAsReadUseCase: sl(),
      archiveUseCase: sl(),
      registerDeviceUseCase: sl(),
    ),
  );
  sl.registerLazySingleton(() => NotificationService());
}

void initDevicesNotifications() {
  sl.registerFactory(
    () => DevicesViewModel(
      getDevicesUseCase: sl(),
      deactivateDeviceUseCase: sl(),
      revokeDeviceUseCase: sl(),
    ),
  );
  sl.registerLazySingleton(() => RevokeDeviceUseCase(sl()));
}
