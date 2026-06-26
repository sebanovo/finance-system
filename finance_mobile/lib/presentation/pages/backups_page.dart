import 'dart:async';
import 'dart:typed_data';
import 'package:file_picker/file_picker.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

import '../../core/di/injection_container.dart' as di;
import '../../core/utils/backup_file_saver.dart';
import '../../domain/entities/backup_record.dart';
import '../viewmodels/backups_viewmodel.dart';

const _green = Color(0xFF166534);
const _surface = Color(0xFFFFFFFF);
const _surfaceVariant = Color(0xFFF9FAFB);
const _outline = Color(0xFFE5E7EB);
const _ink = Color(0xFF111827);

class BackupsPage extends StatefulWidget {
  const BackupsPage({super.key});

  @override
  State<BackupsPage> createState() => _BackupsPageState();
}

class _BackupsPageState extends State<BackupsPage> {
  late BackupsViewModel _viewModel;
  String? _lastShownMessage;

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<BackupsViewModel>();
    _viewModel.addListener(_onViewModelChanged);

    WidgetsBinding.instance.addPostFrameCallback((_) {
      _viewModel.loadBackups();
    });
  }

  @override
  void dispose() {
    _viewModel.removeListener(_onViewModelChanged);
    super.dispose();
  }

  void _onViewModelChanged() {
    if (!mounted) return;

    final errorMessage = _viewModel.errorMessage;
    final successMessage = _viewModel.successMessage;

    if (errorMessage != null && errorMessage != _lastShownMessage) {
      _lastShownMessage = errorMessage;
      WidgetsBinding.instance.addPostFrameCallback((_) {
        if (!mounted) return;
        _showSnackBar(errorMessage, isError: true);
        _viewModel.clearMessages();
      });
    } else if (successMessage != null && successMessage != _lastShownMessage) {
      _lastShownMessage = successMessage;
      WidgetsBinding.instance.addPostFrameCallback((_) {
        if (!mounted) return;
        _showSnackBar(successMessage, isError: false);
        _viewModel.clearMessages();
      });
    }

    setState(() {});
  }

  void _showSnackBar(String message, {required bool isError}) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: isError ? Colors.red.shade700 : Colors.green.shade700,
      ),
    );
  }

  Future<void> _openCreateBackupSheet() async {
    if (!_viewModel.canCreateBackup) {
      _showSnackBar('No tienes permisos para crear respaldos', isError: true);
      return;
    }

    await showModalBottomSheet<void>(
      context: context,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(28)),
      ),
      builder: (sheetContext) {
        var reason = '';

        return DraggableScrollableSheet(
          initialChildSize: 0.52,
          minChildSize: 0.36,
          maxChildSize: 0.88,
          expand: false,
          builder: (context, scrollController) {
            return Padding(
              padding: EdgeInsets.only(
                left: 16,
                right: 16,
                top: 20,
                bottom: MediaQuery.of(context).viewInsets.bottom + 20,
              ),
              child: SingleChildScrollView(
                controller: scrollController,
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      'Nuevo respaldo',
                      style: Theme.of(context).textTheme.titleLarge?.copyWith(
                        fontWeight: FontWeight.bold,
                        color: _ink,
                      ),
                    ),
                    const SizedBox(height: 8),
                    const Text(
                      'Se generará un respaldo del tenant actual. Puedes agregar un motivo opcional.',
                      style: TextStyle(color: Color(0xFF6B7280)),
                    ),
                    const SizedBox(height: 16),
                    TextFormField(
                      initialValue: reason,
                      decoration: const InputDecoration(
                        labelText: 'Motivo',
                        hintText: 'Ej. copia antes de actualización',
                      ),
                      maxLines: 3,
                      onChanged: (value) => reason = value,
                    ),
                    const SizedBox(height: 20),
                    Row(
                      children: [
                        Expanded(
                          child: OutlinedButton(
                            onPressed: () => Navigator.of(context).pop(),
                            child: const Text('Cancelar'),
                          ),
                        ),
                        const SizedBox(width: 12),
                        Expanded(
                          child: FilledButton(
                            onPressed: _viewModel.saving
                                ? null
                                : () {
                                    Navigator.of(context).pop();
                                    WidgetsBinding.instance
                                        .addPostFrameCallback((_) {
                                          if (!mounted) return;
                                          unawaited(
                                            _viewModel.createBackup(
                                              reason: reason.trim(),
                                            ),
                                          );
                                        });
                                  },
                            child: _viewModel.saving
                                ? const SizedBox(
                                    height: 18,
                                    width: 18,
                                    child: CircularProgressIndicator(
                                      strokeWidth: 2,
                                    ),
                                  )
                                : const Text('Crear respaldo'),
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            );
          },
        );
      },
    );
  }

  Future<void> _openRestoreFromFileSheet() async {
    if (!_viewModel.canRestoreBackup) {
      _showSnackBar(
        'No tienes permisos para restaurar respaldos',
        isError: true,
      );
      return;
    }

    await showModalBottomSheet<void>(
      context: context,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(28)),
      ),
      builder: (_) => _RestoreBackupSheet(viewModel: _viewModel),
    );
  }

  Future<void> _downloadBackup(BackupRecord backup) async {
    try {
      final bytes = await _viewModel.downloadBackup(backup.id);
      final savedPath = await saveBackupBytes(bytes, backup.displayFileName);
      if (!mounted) return;

      _showSnackBar(
        kIsWeb
            ? 'Descarga iniciada para ${backup.displayFileName}'
            : 'Respaldo guardado en $savedPath',
        isError: false,
      );
    } catch (e) {
      if (!mounted) return;
      _showSnackBar(e.toString(), isError: true);
    }
  }

  String _statusLabel(String status) {
    switch (status.toUpperCase()) {
      case 'COMPLETED':
        return 'Completado';
      case 'RESTORED':
        return 'Restaurado';
      case 'RESTORED_WITH_WARNINGS':
        return 'Restaurado con avisos';
      case 'RUNNING':
        return 'En proceso';
      case 'PENDING':
        return 'Pendiente';
      case 'RESTORING':
        return 'Restaurando';
      case 'FAILED':
        return 'Fallido';
      case 'RESTORE_FAILED':
        return 'Restauración fallida';
      default:
        return status;
    }
  }

  Color _statusColor(String status) {
    switch (status.toUpperCase()) {
      case 'COMPLETED':
      case 'RESTORED':
        return _green;
      case 'RESTORED_WITH_WARNINGS':
      case 'PENDING':
        return const Color(0xFF374151);
      case 'RUNNING':
      case 'RESTORING':
        return const Color(0xFF111827);
      default:
        return const Color(0xFFB91C1C);
    }
  }

  String _formatBytes(int? bytes) {
    if (bytes == null || bytes <= 0) return 'N/A';
    const units = ['B', 'KB', 'MB', 'GB', 'TB'];
    double value = bytes.toDouble();
    var unitIndex = 0;
    while (value >= 1024 && unitIndex < units.length - 1) {
      value /= 1024;
      unitIndex++;
    }
    return '${value.toStringAsFixed(value >= 10 || unitIndex == 0 ? 0 : 1)} ${units[unitIndex]}';
  }

  String _formatDate(DateTime? date) {
    if (date == null) return 'Sin fecha';
    final local = date.toLocal();
    return '${local.day.toString().padLeft(2, '0')}/${local.month.toString().padLeft(2, '0')}/${local.year} ${local.hour.toString().padLeft(2, '0')}:${local.minute.toString().padLeft(2, '0')}';
  }

  @override
  Widget build(BuildContext context) {
    final canOperate =
        _viewModel.canCreateBackup || _viewModel.canRestoreBackup;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Respaldos'),
        backgroundColor: _surface,
        foregroundColor: _green,
        actions: [
          IconButton(
            onPressed: _viewModel.loading ? null : _viewModel.refresh,
            icon: const Icon(Icons.refresh),
            tooltip: 'Actualizar',
          ),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: _viewModel.refresh,
        color: _green,
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            Container(
              padding: const EdgeInsets.all(20),
              decoration: BoxDecoration(
                color: _surface,
                borderRadius: BorderRadius.circular(24),
                border: Border.all(color: _outline),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text(
                    'Respaldos del tenant',
                    style: TextStyle(
                      fontSize: 22,
                      fontWeight: FontWeight.w900,
                      color: _ink,
                    ),
                  ),
                  const SizedBox(height: 8),
                  const Text(
                    'Genera respaldos, descarga archivos e importa backups desde un archivo local.',
                    style: TextStyle(color: Color(0xFF6B7280)),
                  ),
                  if (canOperate) ...[
                    const SizedBox(height: 16),
                    Wrap(
                      spacing: 12,
                      runSpacing: 12,
                      children: [
                        FilledButton.icon(
                          onPressed: _viewModel.saving
                              ? null
                              : _openCreateBackupSheet,
                          icon: const Icon(Icons.add),
                          label: const Text('Nuevo respaldo'),
                        ),
                        OutlinedButton.icon(
                          onPressed: _viewModel.saving
                              ? null
                              : _openRestoreFromFileSheet,
                          icon: const Icon(Icons.upload_file),
                          label: const Text('Restaurar archivo'),
                        ),
                      ],
                    ),
                  ],
                ],
              ),
            ),
            const SizedBox(height: 16),
            Row(
              children: [
                Expanded(
                  child: _StatCard(
                    label: 'Total',
                    value: _viewModel.totalElements.toString(),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: _StatCard(
                    label: 'Página',
                    value:
                        '${_viewModel.page + 1}/${(_viewModel.totalPages == 0 ? 1 : _viewModel.totalPages)}',
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),
            if (_viewModel.loading)
              const Padding(
                padding: EdgeInsets.symmetric(vertical: 48),
                child: Center(child: CircularProgressIndicator()),
              )
            else if (_viewModel.backups.isEmpty)
              _EmptyState(onRetry: _viewModel.refresh)
            else
              ..._viewModel.backups.map(
                (backup) => Padding(
                  padding: const EdgeInsets.only(bottom: 12),
                    child: Card(
                      elevation: 0,
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(20),
                        side: const BorderSide(color: Color(0xFFE5E7EB)),
                      ),
                    child: Padding(
                      padding: const EdgeInsets.all(16),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Container(
                                padding: const EdgeInsets.all(10),
                                decoration: BoxDecoration(
                                  color: _surfaceVariant,
                                  borderRadius: BorderRadius.circular(14),
                                ),
                                child: const Icon(
                                  Icons.archive,
                                  color: Color(0xFF166534),
                                ),
                              ),
                              const SizedBox(width: 12),
                              Expanded(
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Text(
                                      backup.displayFileName,
                                      style: const TextStyle(
                                        fontSize: 16,
                                        fontWeight: FontWeight.w800,
                                        color: Color(0xFF111827),
                                      ),
                                    ),
                                    const SizedBox(height: 4),
                                    Text(
                                      '${backup.operationType} • ${backup.scope}',
                                      style: const TextStyle(
                                        color: Color(0xFF6B7280),
                                        fontSize: 12,
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                              Container(
                                padding: const EdgeInsets.symmetric(
                                  horizontal: 10,
                                  vertical: 6,
                                ),
                                decoration: BoxDecoration(
                                  color: _statusColor(
                                    backup.status,
                                  ).withValues(alpha: 0.10),
                                  borderRadius: BorderRadius.circular(999),
                                  border: Border.all(
                                    color: _statusColor(
                                      backup.status,
                                    ).withValues(alpha: 0.25),
                                  ),
                                ),
                                child: Text(
                                  _statusLabel(backup.status),
                                  style: TextStyle(
                                    color: _statusColor(backup.status),
                                    fontSize: 11,
                                    fontWeight: FontWeight.w800,
                                  ),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: 12),
                          Wrap(
                            spacing: 12,
                            runSpacing: 8,
                            children: [
                              _InfoChip(
                                label: 'Tenant',
                                value: backup.tenantSlug ?? 'GLOBAL',
                              ),
                              _InfoChip(
                                label: 'Archivo',
                                value: backup.format ?? 'N/A',
                              ),
                              _InfoChip(
                                label: 'Tamaño',
                                value: _formatBytes(backup.sizeBytes),
                              ),
                              _InfoChip(
                                label: 'Fecha',
                                value: _formatDate(backup.createdAt),
                              ),
                            ],
                          ),
                          if (backup.reason != null &&
                              backup.reason!.trim().isNotEmpty) ...[
                            const SizedBox(height: 10),
                            Text(
                              'Motivo: ${backup.reason}',
                              style: const TextStyle(color: Color(0xFF6B7280)),
                            ),
                          ],
                          if (backup.failureReason != null &&
                              backup.failureReason!.trim().isNotEmpty) ...[
                            const SizedBox(height: 10),
                            Text(
                              'Fallo: ${backup.failureReason}',
                              style: const TextStyle(color: Color(0xFFB91C1C)),
                            ),
                          ],
                          const SizedBox(height: 12),
                          Row(
                            mainAxisAlignment: MainAxisAlignment.end,
                            children: [
                              IconButton(
                                onPressed:
                                    (_viewModel.downloading &&
                                        _viewModel.downloadingBackupId ==
                                            backup.id)
                                    ? null
                                    : () => _downloadBackup(backup),
                                icon:
                                    (_viewModel.downloading &&
                                        _viewModel.downloadingBackupId ==
                                            backup.id)
                                    ? const SizedBox(
                                        height: 18,
                                        width: 18,
                                        child: CircularProgressIndicator(
                                          strokeWidth: 2,
                                        ),
                                      )
                                    : const Icon(Icons.download),
                                color: const Color(0xFF166534),
                                tooltip: 'Descargar',
                              ),
                            ],
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
              ),
            const SizedBox(height: 16),
            if (_viewModel.totalPages > 1)
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  OutlinedButton(
                    onPressed: _viewModel.hasPreviousPage
                        ? () =>
                              _viewModel.loadBackups(page: _viewModel.page - 1)
                        : null,
                    child: const Text('Anterior'),
                  ),
                  Text(
                    'Página ${_viewModel.page + 1} de ${_viewModel.totalPages}',
                    style: const TextStyle(
                      fontWeight: FontWeight.w600,
                      color: Color(0xFF111827),
                    ),
                  ),
                  OutlinedButton(
                    onPressed: _viewModel.hasNextPage
                        ? () =>
                              _viewModel.loadBackups(page: _viewModel.page + 1)
                        : null,
                    child: const Text('Siguiente'),
                  ),
                ],
              ),
          ],
        ),
      ),
    );
  }
}

class _StatCard extends StatelessWidget {
  final String label;
  final String value;

  const _StatCard({required this.label, required this.value});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: _surface,
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: _outline),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            label,
            style: const TextStyle(
              color: Color(0xFF6B7280),
              fontSize: 12,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            value,
            style: const TextStyle(
              fontSize: 24,
              fontWeight: FontWeight.w900,
              color: _ink,
            ),
          ),
        ],
      ),
    );
  }
}

class _InfoChip extends StatelessWidget {
  final String label;
  final String value;

  const _InfoChip({required this.label, required this.value});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
      decoration: BoxDecoration(
        color: _surfaceVariant,
        borderRadius: BorderRadius.circular(999),
        border: Border.all(color: _outline),
      ),
      child: Text(
        '$label: $value',
        style: const TextStyle(fontSize: 12, color: Color(0xFF4F6650)),
      ),
    );
  }
}

class _EmptyState extends StatelessWidget {
  final VoidCallback onRetry;

  const _EmptyState({required this.onRetry});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(32),
      decoration: BoxDecoration(
        color: _surface,
        borderRadius: BorderRadius.circular(24),
        border: Border.all(color: _outline),
      ),
      child: Column(
        children: [
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: _surfaceVariant,
              borderRadius: BorderRadius.circular(20),
            ),
            child: const Icon(Icons.backup, color: Color(0xFF166534), size: 34),
          ),
          const SizedBox(height: 16),
          const Text(
            'Aún no hay respaldos',
            style: TextStyle(
              fontSize: 16,
              fontWeight: FontWeight.w800,
              color: _ink,
            ),
          ),
          const SizedBox(height: 8),
          const Text(
            'Crea un respaldo para empezar a construir el historial.',
            style: TextStyle(color: Color(0xFF6B7280)),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 16),
          OutlinedButton(onPressed: onRetry, child: const Text('Recargar')),
        ],
      ),
    );
  }
}

class _RestoreBackupSheet extends StatefulWidget {
  final BackupsViewModel viewModel;

  const _RestoreBackupSheet({required this.viewModel});

  @override
  State<_RestoreBackupSheet> createState() => _RestoreBackupSheetState();
}

class _RestoreBackupSheetState extends State<_RestoreBackupSheet> {
  final _confirmationController =
      TextEditingController(text: 'RESTORE_TENANT_BACKUP');
  final _reasonController = TextEditingController();
  PlatformFile? _selectedFile;
  Uint8List? _fileBytes;
  bool _submitting = false;

  @override
  void dispose() {
    _confirmationController.dispose();
    _reasonController.dispose();
    super.dispose();
  }

  Future<void> _pickBackupFile() async {
    FocusScope.of(context).unfocus();
    final result = await FilePicker.platform.pickFiles(
      type: FileType.any,
      withData: true,
    );
    if (!mounted || result == null || result.files.isEmpty) {
      return;
    }
    final picked = result.files.first;
    setState(() {
      _selectedFile = picked;
      _fileBytes = picked.bytes;
    });
  }

  Future<void> _restore() async {
    if (_selectedFile == null || _fileBytes == null || _submitting) return;
    final backupBytes = _fileBytes!;
    final backupFileName = _selectedFile!.name;
    final confirmation = _confirmationController.text.trim();
    final restoreReason = _reasonController.text.trim();

    setState(() => _submitting = true);
    Navigator.of(context).pop();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      widget.viewModel.restoreBackupFromFile(
        fileBytes: backupBytes,
        fileName: backupFileName,
        confirmationText: confirmation,
        reason: restoreReason,
      );
    });
  }

  @override
  Widget build(BuildContext context) {
    return DraggableScrollableSheet(
      initialChildSize: 0.68,
      minChildSize: 0.48,
      maxChildSize: 0.92,
      expand: false,
      builder: (context, scrollController) {
        return AnimatedPadding(
          duration: const Duration(milliseconds: 180),
          padding: EdgeInsets.only(
            left: 16,
            right: 16,
            top: 20,
            bottom: MediaQuery.of(context).viewInsets.bottom + 20,
          ),
          child: SingleChildScrollView(
            controller: scrollController,
            child: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'Restaurar desde archivo',
                  style: Theme.of(context).textTheme.titleLarge?.copyWith(
                        fontWeight: FontWeight.bold,
                        color: const Color(0xFF111827),
                      ),
                ),
                const SizedBox(height: 8),
                const Text(
                  'Selecciona el archivo del respaldo y confirma la operación.',
                  style: TextStyle(color: Color(0xFF6B7280)),
                ),
                const SizedBox(height: 16),
                OutlinedButton.icon(
                  onPressed: _pickBackupFile,
                  icon: const Icon(Icons.attach_file),
                  label: Text(
                    _selectedFile == null ? 'Seleccionar archivo' : _selectedFile!.name,
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
                const SizedBox(height: 12),
                TextFormField(
                  controller: _confirmationController,
                  decoration: const InputDecoration(
                    labelText: 'Confirmación',
                    hintText: 'RESTORE_TENANT_BACKUP',
                  ),
                ),
                const SizedBox(height: 12),
                TextFormField(
                  controller: _reasonController,
                  decoration: const InputDecoration(
                    labelText: 'Motivo',
                    hintText: 'Opcional',
                  ),
                  maxLines: 3,
                ),
                const SizedBox(height: 20),
                Row(
                  children: [
                    Expanded(
                      child: OutlinedButton(
                        onPressed: () => Navigator.of(context).pop(),
                        child: const Text('Cancelar'),
                      ),
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      child: FilledButton(
                        onPressed: _selectedFile == null || _fileBytes == null || _submitting
                            ? null
                            : _restore,
                        child: _submitting
                            ? const SizedBox(
                                height: 18,
                                width: 18,
                                child: CircularProgressIndicator(strokeWidth: 2),
                              )
                            : const Text('Restaurar'),
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        );
      },
    );
  }
}
