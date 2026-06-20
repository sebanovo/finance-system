import 'package:flutter/material.dart';

import 'package:finance_mobile/core/network/api_client.dart';
import 'package:finance_mobile/domain/entities/user_info.dart';
import 'package:finance_mobile/domain/usecases/get_profile_usecase.dart';
import 'package:finance_mobile/domain/usecases/logout_usecase.dart';
import 'package:finance_mobile/domain/usecases/remove_profile_photo_usecase.dart';
import 'package:finance_mobile/domain/usecases/update_profile_usecase.dart';

class ProfileViewModel extends ChangeNotifier {
  final GetProfileUseCase getProfileUseCase;
  final UpdateProfileUseCase updateProfileUseCase;
  final RemoveProfilePhotoUseCase removeProfilePhotoUseCase;
  final LogoutUseCase logoutUseCase;
  final ApiClient apiClient;

  ProfileViewModel({
    required this.getProfileUseCase,
    required this.updateProfileUseCase,
    required this.removeProfilePhotoUseCase,
    required this.logoutUseCase,
    required this.apiClient,
  });

  UserInfo? _profile;
  bool _loading = false;
  bool _saving = false;
  String? _errorMessage;

  UserInfo? get profile => _profile;
  bool get loading => _loading;
  bool get saving => _saving;
  String? get errorMessage => _errorMessage;

  Future<void> loadProfile() async {
    _loading = true;
    notifyListeners();
    try {
      _profile = await getProfileUseCase();
      _errorMessage = null;
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _loading = false;
      notifyListeners();
    }
  }

  Future<bool> updateProfile({
    String? firstName,
    String? lastName,
    String? photoPath,
    String? photoName,
  }) async {
    _saving = true;
    notifyListeners();
    try {
      _profile = await updateProfileUseCase(
        firstName: firstName,
        lastName: lastName,
        photoPath: photoPath,
        photoName: photoName,
      );
      _errorMessage = null;
      return true;
    } catch (e) {
      _errorMessage = e.toString();
      return false;
    } finally {
      _saving = false;
      notifyListeners();
    }
  }

  Future<bool> removeProfilePhoto() async {
    _saving = true;
    notifyListeners();
    try {
      _profile = await removeProfilePhotoUseCase();
      _errorMessage = null;
      return true;
    } catch (e) {
      _errorMessage = e.toString();
      return false;
    } finally {
      _saving = false;
      notifyListeners();
    }
  }

  Future<void> logout() async {
    await logoutUseCase();
    apiClient.clearSession();
  }

  void clearError() {
    _errorMessage = null;
    notifyListeners();
  }
}
