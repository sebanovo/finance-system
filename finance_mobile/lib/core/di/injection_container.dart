import 'package:finance_mobile/core/network/api_client.dart';
import 'package:finance_mobile/domain/repositories/permission_repository.dart';
import 'package:finance_mobile/domain/usecases/get_permissions_usecase.dart';
import 'package:finance_mobile/infrastructure/datasources/permission_remote_datasource.dart';
import 'package:finance_mobile/infrastructure/repositories/permission_repository_impl.dart';
import 'package:finance_mobile/presentation/viewmodels/permissions_viewmodel.dart';
import 'package:get_it/get_it.dart';

// service locator
final sl = GetIt.instance;

Future<void> init() async {
  sl.registerLazySingleton(() => ApiClient());
  sl.registerLazySingleton<PermissionRemoteDataSource>(
    () => PermissionRemoteDataSourceImpl(sl()),
  );
  sl.registerLazySingleton<PermissionRepository>(
    () => PermissionRepositoryImpl(sl()),
  );
  sl.registerLazySingleton(() => GetPermissionsUsecase(sl()));
  // sl.registerFactory(() => PermissionsViewModel(getPermissionsUseCase: sl()));
  sl.registerFactory(() => PermissionsViewModel(sl()));
}
