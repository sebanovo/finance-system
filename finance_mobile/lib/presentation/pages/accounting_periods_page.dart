import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import '../../core/di/injection_container.dart' as di;
import '../../core/network/api_client.dart';
import '../../domain/entities/accounting_period.dart';
import '../viewmodels/accounting_periods_viewmodel.dart';

class AccountingPeriodsPage extends StatefulWidget {
  const AccountingPeriodsPage({super.key});

  @override
  State<AccountingPeriodsPage> createState() => _AccountingPeriodsPageState();
}

class _AccountingPeriodsPageState extends State<AccountingPeriodsPage> {
  late AccountingPeriodsViewModel _viewModel;
  late ApiClient _apiClient;

  final _searchController = TextEditingController();

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<AccountingPeriodsViewModel>();
    _apiClient = di.sl<ApiClient>();
    _viewModel.addListener(_onChanged);
    WidgetsBinding.instance.addPostFrameCallback(
      (_) => _viewModel.loadPeriods(),
    );
  }

  @override
  void dispose() {
    _viewModel.removeListener(_onChanged);
    _searchController.dispose();
    super.dispose();
  }

  void _onChanged() {
    if (!mounted) return;
    if (_viewModel.errorMessage != null) {
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(SnackBar(content: Text(_viewModel.errorMessage!)));
      _viewModel.clearError();
    }
    setState(() {});
  }

  List<AccountingPeriod> get _filteredPeriods {
    final term = _searchController.text.trim().toLowerCase();
    if (term.isEmpty) return _viewModel.items;
    return _viewModel.items.where((item) {
      return item.periodCode.toLowerCase().contains(term) ||
          item.periodType.toLowerCase().contains(term) ||
          item.status.toLowerCase().contains(term) ||
          (item.description ?? '').toLowerCase().contains(term);
    }).toList();
  }

  @override
  Widget build(BuildContext context) {
    if (!_apiClient.isOwnerAdmin) {
      return const Scaffold(
        body: Center(
          child: Text('No tienes permisos para ver períodos contables'),
        ),
      );
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text('Períodos contables'),
        backgroundColor: Colors.white,
        foregroundColor: const Color(0xFF2E7D32),
        elevation: 0,
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _viewModel.loading
                ? null
                : () => _viewModel.loadPeriods(page: _viewModel.page),
          ),
          IconButton(
            icon: const Icon(Icons.notifications),
            onPressed: () => context.push('/notifications'),
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: _viewModel.saving ? null : _showCreateDialog,
        backgroundColor: const Color(0xFF2E7D32),
        foregroundColor: Colors.white,
        icon: const Icon(Icons.add),
        label: const Text('Nuevo período'),
      ),
      body: RefreshIndicator(
        onRefresh: () => _viewModel.loadPeriods(page: 0),
        color: const Color(0xFF2E7D32),
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            _buildHero(),
            const SizedBox(height: 16),
            _buildStats(),
            const SizedBox(height: 16),
            _buildSearch(),
            const SizedBox(height: 16),
            if (_viewModel.loading && _viewModel.items.isEmpty)
              const Padding(
                padding: EdgeInsets.symmetric(vertical: 48),
                child: Center(child: CircularProgressIndicator()),
              )
            else if (_filteredPeriods.isEmpty)
              _emptyState()
            else
              ..._filteredPeriods.map(_periodCard),
            const SizedBox(height: 16),
            if (_viewModel.totalPages > 1) _pagination(),
          ],
        ),
      ),
    );
  }

  Widget _buildHero() {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        gradient: const LinearGradient(
          colors: [Color(0xFFEAF6EB), Colors.white],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(24),
        border: Border.all(color: const Color(0xFFC8E6C9)),
      ),
      child: const Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'Contabilidad',
            style: TextStyle(
              fontSize: 12,
              fontWeight: FontWeight.w700,
              color: Color(0xFF4E7B54),
            ),
          ),
          SizedBox(height: 6),
          Text(
            'Períodos contables',
            style: TextStyle(
              fontSize: 26,
              fontWeight: FontWeight.w900,
              color: Color(0xFF1B5E20),
            ),
          ),
          SizedBox(height: 8),
          Text(
            'Gestiona los ciclos contables, crea nuevos períodos y cierra los abiertos cuando corresponda.',
            style: TextStyle(color: Color(0xFF5F765B)),
          ),
        ],
      ),
    );
  }

  Widget _buildStats() {
    final openCount = _viewModel.items
        .where((e) => e.status.toUpperCase() == 'OPEN')
        .length;
    final closedCount = _viewModel.items
        .where((e) => e.status.toUpperCase() == 'CLOSED')
        .length;
    final archivedCount = _viewModel.items
        .where((e) => e.status.toUpperCase() == 'ARCHIVED')
        .length;
    return Wrap(
      spacing: 12,
      runSpacing: 12,
      children: [
        _statCard('En página', '${_filteredPeriods.length}'),
        _statCard('Abiertos', '$openCount'),
        _statCard('Cerrados', '$closedCount'),
        _statCard('Archivados', '$archivedCount'),
      ],
    );
  }

  Widget _statCard(String label, String value) {
    return Container(
      width: 160,
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(18),
        border: Border.all(color: const Color(0xFFDDEED8)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            label,
            style: const TextStyle(fontSize: 11, color: Color(0xFF6B7D6C)),
          ),
          const SizedBox(height: 6),
          Text(
            value,
            style: const TextStyle(
              fontSize: 22,
              fontWeight: FontWeight.w900,
              color: Color(0xFF1B5E20),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildSearch() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(24),
        border: Border.all(color: const Color(0xFFC8E6C9)),
      ),
      child: TextField(
        controller: _searchController,
        onChanged: (_) => setState(() {}),
        decoration: InputDecoration(
          labelText: 'Buscar período',
          helperText: 'Filtra por código, tipo, estado o descripción',
          prefixIcon: const Icon(Icons.search),
          border: OutlineInputBorder(borderRadius: BorderRadius.circular(18)),
        ),
      ),
    );
  }

  Widget _periodCard(AccountingPeriod period) {
    final status = period.status.toUpperCase();
    final canClose = status == 'OPEN';
    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        period.periodCode,
                        style: const TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.w900,
                          color: Color(0xFF1B5E20),
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        _periodTypeLabel(period.periodType),
                        style: const TextStyle(color: Colors.black54),
                      ),
                    ],
                  ),
                ),
                _statusChip(
                  _periodStatusLabel(status),
                  _periodStatusColor(status),
                ),
              ],
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                Expanded(
                  child: _miniInfo('Inicio', _formatDate(period.startDate)),
                ),
                Expanded(child: _miniInfo('Fin', _formatDate(period.endDate))),
              ],
            ),
            const SizedBox(height: 8),
            Text(
              period.description?.trim().isNotEmpty == true
                  ? period.description!.trim()
                  : 'Sin descripción',
              style: const TextStyle(color: Colors.black54),
            ),
            if (period.closedAt != null) ...[
              const SizedBox(height: 8),
              Text(
                'Cerrado: ${_formatDateTime(period.closedAt!)}',
                style: const TextStyle(fontSize: 12, color: Colors.black45),
              ),
            ],
            const SizedBox(height: 12),
            Align(
              alignment: Alignment.centerRight,
              child: Wrap(
                spacing: 8,
                children: [
                  if (canClose)
                    TextButton.icon(
                      onPressed: _viewModel.saving
                          ? null
                          : () => _showCloseDialog(period),
                      icon: const Icon(Icons.lock, size: 18),
                      label: const Text('Cerrar'),
                    ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _statusChip(String label, Color color) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
      decoration: BoxDecoration(
        color: color.withValues(alpha: 0.12),
        borderRadius: BorderRadius.circular(999),
      ),
      child: Text(
        label,
        style: TextStyle(
          color: color,
          fontWeight: FontWeight.w700,
          fontSize: 11,
        ),
      ),
    );
  }

  Widget _miniInfo(String label, String value) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          label,
          style: const TextStyle(fontSize: 11, color: Colors.black54),
        ),
        const SizedBox(height: 4),
        Text(value, style: const TextStyle(fontWeight: FontWeight.w700)),
      ],
    );
  }

  Widget _emptyState() {
    return Container(
      padding: const EdgeInsets.all(32),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(24),
        border: Border.all(color: const Color(0xFFC8E6C9)),
      ),
      child: const Column(
        children: [
          Icon(Icons.event_note, size: 40, color: Color(0xFF2E7D32)),
          SizedBox(height: 12),
          Text('No hay períodos contables registrados'),
        ],
      ),
    );
  }

  Widget _pagination() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        OutlinedButton(
          onPressed: _viewModel.hasPreviousPage
              ? () => _viewModel.loadPeriods(page: _viewModel.page - 1)
              : null,
          child: const Text('Anterior'),
        ),
        Text('Página ${_viewModel.page + 1} de ${_viewModel.totalPages}'),
        OutlinedButton(
          onPressed: _viewModel.hasNextPage
              ? () => _viewModel.loadPeriods(page: _viewModel.page + 1)
              : null,
          child: const Text('Siguiente'),
        ),
      ],
    );
  }

  Future<void> _showCreateDialog() async {
    final formKey = GlobalKey<FormState>();
    final codeController = TextEditingController();
    final descriptionController = TextEditingController();
    var periodType = 'MONTHLY';
    var startDate = DateTime.now();
    var endDate = DateTime.now().add(const Duration(days: 30));

    try {
      final result = await showDialog<Map<String, dynamic>>(
        context: context,
        builder: (dialogContext) {
          return AlertDialog(
            title: const Text('Nuevo período contable'),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(24),
            ),
            content: SizedBox(
              width: 420,
              child: Form(
                key: formKey,
                child: StatefulBuilder(
                  builder: (context, setLocalState) {
                    return SingleChildScrollView(
                      child: Column(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          TextFormField(
                            controller: codeController,
                            textCapitalization: TextCapitalization.characters,
                            decoration: const InputDecoration(
                              labelText: 'Código',
                              helperText: 'Ejemplo: 2026-06',
                            ),
                            validator: (value) {
                              if ((value ?? '').trim().isEmpty) {
                                return 'Ingresa un código';
                              }
                              return null;
                            },
                          ),
                          const SizedBox(height: 12),
                          DropdownButtonFormField<String>(
                            initialValue: periodType,
                            decoration: const InputDecoration(
                              labelText: 'Tipo',
                            ),
                            items: const [
                              DropdownMenuItem(
                                value: 'DAILY',
                                child: Text('Diario'),
                              ),
                              DropdownMenuItem(
                                value: 'MONTHLY',
                                child: Text('Mensual'),
                              ),
                              DropdownMenuItem(
                                value: 'ANNUAL',
                                child: Text('Anual'),
                              ),
                              DropdownMenuItem(
                                value: 'CUSTOM',
                                child: Text('Personalizado'),
                              ),
                            ],
                            onChanged: (value) {
                              if (value == null) return;
                              setLocalState(() => periodType = value);
                            },
                          ),
                          const SizedBox(height: 12),
                          _DatePickerField(
                            label: 'Fecha de inicio',
                            value: startDate,
                            onTap: () async {
                              final picked = await showDatePicker(
                                context: dialogContext,
                                initialDate: startDate,
                                firstDate: DateTime(2000),
                                lastDate: DateTime(2100),
                              );
                              if (picked != null) {
                                setLocalState(() => startDate = picked);
                              }
                            },
                          ),
                          const SizedBox(height: 12),
                          _DatePickerField(
                            label: 'Fecha de fin',
                            value: endDate,
                            onTap: () async {
                              final picked = await showDatePicker(
                                context: dialogContext,
                                initialDate: endDate,
                                firstDate: DateTime(2000),
                                lastDate: DateTime(2100),
                              );
                              if (picked != null) {
                                setLocalState(() => endDate = picked);
                              }
                            },
                          ),
                          const SizedBox(height: 12),
                          TextFormField(
                            controller: descriptionController,
                            decoration: const InputDecoration(
                              labelText: 'Descripción',
                            ),
                            maxLines: 3,
                          ),
                        ],
                      ),
                    );
                  },
                ),
              ),
            ),
            actions: [
              TextButton(
                onPressed: () => Navigator.pop(dialogContext, null),
                child: const Text('Cancelar'),
              ),
              FilledButton(
                onPressed: _viewModel.saving
                    ? null
                    : () async {
                        if (!formKey.currentState!.validate()) return;
                        if (startDate.isAfter(endDate)) {
                          ScaffoldMessenger.of(dialogContext).showSnackBar(
                            const SnackBar(
                              content: Text(
                                'La fecha de inicio no puede ser mayor a la fecha de fin',
                              ),
                            ),
                          );
                          return;
                        }
                        Navigator.pop(dialogContext, {
                          'periodCode': codeController.text
                              .trim()
                              .toUpperCase(),
                          'periodType': periodType,
                          'startDate': startDate
                              .toIso8601String()
                              .split('T')
                              .first,
                          'endDate': endDate.toIso8601String().split('T').first,
                          'description': descriptionController.text.trim(),
                        });
                      },
                child: const Text('Crear'),
              ),
            ],
          );
        },
      );

      if (result == null) return;
      final ok = await _viewModel.createPeriod(
        periodCode: result['periodCode'] as String,
        periodType: result['periodType'] as String,
        startDate: result['startDate'] as String,
        endDate: result['endDate'] as String,
        description: result['description'] as String?,
      );
      if (!mounted) return;
      if (ok) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Período creado exitosamente')),
        );
      }
    } finally {
      codeController.dispose();
      descriptionController.dispose();
    }
  }

  Future<void> _showCloseDialog(AccountingPeriod period) async {
    final formKey = GlobalKey<FormState>();
    final reasonController = TextEditingController();
    try {
      final result = await showDialog<String>(
        context: context,
        builder: (dialogContext) {
          return AlertDialog(
            title: Text('Cerrar ${period.periodCode}'),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(24),
            ),
            content: SizedBox(
              width: 420,
              child: Form(
                key: formKey,
                child: TextFormField(
                  controller: reasonController,
                  maxLines: 3,
                  decoration: const InputDecoration(
                    labelText: 'Motivo',
                    helperText: 'Opcional',
                  ),
                ),
              ),
            ),
            actions: [
              TextButton(
                onPressed: () => Navigator.pop(dialogContext, null),
                child: const Text('Cancelar'),
              ),
              FilledButton(
                onPressed: _viewModel.saving
                    ? null
                    : () {
                        Navigator.pop(
                          dialogContext,
                          reasonController.text.trim(),
                        );
                      },
                child: const Text('Cerrar período'),
              ),
            ],
          );
        },
      );

      if (result == null) return;
      final ok = await _viewModel.closePeriod(
        period.id,
        reason: result.isEmpty ? null : result,
      );
      if (!mounted) return;
      if (ok) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Período cerrado exitosamente')),
        );
      }
    } finally {
      reasonController.dispose();
    }
  }

  String _periodStatusLabel(String status) {
    switch (status.toUpperCase()) {
      case 'OPEN':
        return 'Abierto';
      case 'CLOSED':
        return 'Cerrado';
      case 'ARCHIVED':
        return 'Archivado';
      default:
        return status;
    }
  }

  Color _periodStatusColor(String status) {
    switch (status.toUpperCase()) {
      case 'OPEN':
        return Colors.green;
      case 'CLOSED':
        return Colors.grey;
      case 'ARCHIVED':
        return Colors.orange;
      default:
        return Colors.grey;
    }
  }

  String _periodTypeLabel(String type) {
    switch (type.toUpperCase()) {
      case 'DAILY':
        return 'Diario';
      case 'MONTHLY':
        return 'Mensual';
      case 'ANNUAL':
        return 'Anual';
      case 'CUSTOM':
        return 'Personalizado';
      default:
        return type;
    }
  }

  String _formatDate(DateTime date) {
    final local = date.toLocal();
    return '${local.day.toString().padLeft(2, '0')}/${local.month.toString().padLeft(2, '0')}/${local.year}';
  }

  String _formatDateTime(DateTime date) {
    final local = date.toLocal();
    return '${_formatDate(local)} ${local.hour.toString().padLeft(2, '0')}:${local.minute.toString().padLeft(2, '0')}';
  }
}

class _DatePickerField extends StatelessWidget {
  final String label;
  final DateTime value;
  final VoidCallback onTap;

  const _DatePickerField({
    required this.label,
    required this.value,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final local = value.toLocal();
    final formatted =
        '${local.day.toString().padLeft(2, '0')}/${local.month.toString().padLeft(2, '0')}/${local.year}';
    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(12),
      child: InputDecorator(
        decoration: InputDecoration(
          labelText: label,
          suffixIcon: const Icon(Icons.date_range),
        ),
        child: Text(formatted),
      ),
    );
  }
}
