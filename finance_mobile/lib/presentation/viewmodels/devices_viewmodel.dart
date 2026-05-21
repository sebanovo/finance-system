import 'package:finance_mobile/domain/usecases/revoke_device_usecase.dart';
import 'package:flutter/material.dart';
import '../../../domain/usecases/get_devices_usecase.dart';
import '../../../domain/usecases/deactivate_device_usecase.dart';
import '../../../domain/entities/notification_device.dart';

class DevicesViewModel extends ChangeNotifier {
  final GetDevicesUseCase getDevicesUseCase;
  final DeactivateDeviceUseCase deactivateDeviceUseCase;
  final RevokeDeviceUseCase revokeDeviceUseCase;

  List<NotificationDevice> _devices = [];
  bool _loading = false;
  bool _deactivating = false;
  String? _errorMessage;
  String? _successMessage;

  DevicesViewModel({
    required this.getDevicesUseCase,
    required this.deactivateDeviceUseCase,
    required this.revokeDeviceUseCase,
  });

  List<NotificationDevice> get devices => _devices;
  bool get loading => _loading;
  bool get deactivating => _deactivating;
  String? get errorMessage => _errorMessage;
  String? get successMessage => _successMessage;

  void clearMessages() {
    _errorMessage = null;
    _successMessage = null;
    notifyListeners();
  }

  Future<void> loadDevices() async {
    _loading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      _devices = await getDevicesUseCase();
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _loading = false;
      notifyListeners();
    }
  }

  Future<void> deactivateDevice(String deviceId) async {
    _deactivating = true;
    _errorMessage = null;
    _successMessage = null;
    notifyListeners();

    try {
      await deactivateDeviceUseCase(deviceId);
      _successMessage = 'Dispositivo desactivado correctamente';
      await loadDevices();
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _deactivating = false;
      notifyListeners();
    }
  }

  Future<void> revokeDevice(String deviceId) async {
    _deactivating = true;
    _errorMessage = null;
    _successMessage = null;
    notifyListeners();

    try {
      await revokeDeviceUseCase(deviceId);
      _successMessage = 'Dispositivo revocado correctamente';
      await loadDevices();
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _deactivating = false;
      notifyListeners();
    }
  }
}
