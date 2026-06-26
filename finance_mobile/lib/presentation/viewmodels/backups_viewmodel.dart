import 'package:flutter/foundation.dart';

import '../../core/network/api_client.dart';
import '../../domain/entities/backup_record.dart';
import '../../domain/usecases/create_backup_usecase.dart';
import '../../domain/usecases/download_backup_usecase.dart';
import '../../domain/usecases/get_backups_usecase.dart';
import '../../domain/usecases/restore_backup_from_file_usecase.dart';

class BackupsViewModel extends ChangeNotifier {
  final GetBackupsUseCase getBackupsUseCase;
  final CreateBackupUseCase createBackupUseCase;
  final RestoreBackupFromFileUseCase restoreBackupFromFileUseCase;
  final DownloadBackupUseCase downloadBackupUseCase;
  final ApiClient apiClient;

  BackupsViewModel({
    required this.getBackupsUseCase,
    required this.createBackupUseCase,
    required this.restoreBackupFromFileUseCase,
    required this.downloadBackupUseCase,
    required this.apiClient,
  });

  List<BackupRecord> _backups = [];
  bool _loading = false;
  bool _saving = false;
  bool _downloading = false;
  String? _errorMessage;
  String? _successMessage;
  int _page = 0;
  final int _size = 10;
  int _totalPages = 0;
  int _totalElements = 0;
  String? _downloadingBackupId;

  List<BackupRecord> get backups => _backups;
  bool get loading => _loading;
  bool get saving => _saving;
  bool get downloading => _downloading;
  String? get errorMessage => _errorMessage;
  String? get successMessage => _successMessage;
  int get page => _page;
  int get size => _size;
  int get totalPages => _totalPages;
  int get totalElements => _totalElements;
  String? get downloadingBackupId => _downloadingBackupId;

  bool get isOwnerAdmin => apiClient.isOwnerAdmin;
  bool get canReadBackups =>
      isOwnerAdmin ||
      apiClient.hasPermission('backups.read') ||
      apiClient.hasAnyPermissionPrefix('backups.');
  bool get canCreateBackup =>
      isOwnerAdmin ||
      apiClient.hasPermission('backups.create') ||
      apiClient.hasAnyPermissionPrefix('backups.');
  bool get canRestoreBackup =>
      isOwnerAdmin ||
      apiClient.hasPermission('backups.restore') ||
      apiClient.hasAnyPermissionPrefix('backups.');
  bool get canDownloadBackup =>
      isOwnerAdmin ||
      apiClient.hasPermission('backups.download') ||
      apiClient.hasAnyPermissionPrefix('backups.');

  bool get hasBackups => _backups.isNotEmpty;
  bool get hasPreviousPage => _page > 0;
  bool get hasNextPage => _page + 1 < _totalPages;

  void clearMessages() {
    _errorMessage = null;
    _successMessage = null;
  }

  Future<void> loadBackups({int? page}) async {
    if (!canReadBackups) {
      _errorMessage = 'No tienes permisos para consultar respaldos';
      notifyListeners();
      return;
    }

    _loading = true;
    if (page != null) {
      _page = page;
    }
    _errorMessage = null;
    notifyListeners();

    try {
      final result = await getBackupsUseCase(page: _page, size: _size);
      _backups = result.items;
      _totalPages = result.totalPages;
      _totalElements = result.totalElements;
      _page = result.number;
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _loading = false;
      notifyListeners();
    }
  }

  Future<void> refresh() => loadBackups(page: _page);

  Future<void> createBackup({String? reason}) async {
    if (!canCreateBackup) {
      _errorMessage = 'No tienes permisos para crear respaldos';
      notifyListeners();
      return;
    }

    _saving = true;
    _errorMessage = null;
    _successMessage = null;
    notifyListeners();

    try {
      final created = await createBackupUseCase(reason: reason);
      _successMessage = 'Respaldo solicitado: ${created.displayFileName}';
      await refresh();
    } catch (e) {
      final message = e.toString();
      if (_looksLikeAccepted(message)) {
        _errorMessage = null;
        // _successMessage = _acceptedMessage(message, 'Respaldo solicitado');
        await refresh();
      } else {
        _errorMessage = message;
      }
    } finally {
      _saving = false;
      notifyListeners();
    }
  }

  Future<void> restoreBackupFromFile({
    required Uint8List fileBytes,
    required String fileName,
    required String confirmationText,
    String? reason,
  }) async {
    if (!canRestoreBackup) {
      _errorMessage = 'No tienes permisos para restaurar respaldos';
      notifyListeners();
      return;
    }

    _saving = true;
    _errorMessage = null;
    _successMessage = null;
    notifyListeners();

    try {
      await restoreBackupFromFileUseCase(
        fileBytes: fileBytes,
        fileName: fileName,
        confirmationText: confirmationText,
        reason: reason,
      );
      _successMessage = null;
      await refresh();
    } catch (e) {
      final message = e.toString();
      if (_looksLikeAccepted(message)) {
        _errorMessage = null;
        _successMessage = null;
        await refresh();
      } else {
        _errorMessage = message;
      }
    } finally {
      _saving = false;
      notifyListeners();
    }
  }

  Future<Uint8List> downloadBackup(String backupId) async {
    if (!canDownloadBackup) {
      throw Exception('No tienes permisos para descargar respaldos');
    }

    _downloading = true;
    _downloadingBackupId = backupId;
    notifyListeners();

    try {
      return await downloadBackupUseCase(backupId);
    } finally {
      _downloading = false;
      _downloadingBackupId = null;
      notifyListeners();
    }
  }

  bool _looksLikeAccepted(String message) {
    final normalized = message.toLowerCase();
    return normalized.contains('accepted') ||
        normalized.contains('job accepted') ||
        normalized.contains('tenant default job accepted');
  }
}
