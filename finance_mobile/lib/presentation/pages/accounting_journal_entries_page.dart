import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import '../../core/di/injection_container.dart' as di;
import '../../core/network/api_client.dart';
import '../../domain/entities/journal_entry.dart';
import '../viewmodels/journal_entries_viewmodel.dart';

class AccountingJournalEntriesPage extends StatefulWidget {
  const AccountingJournalEntriesPage({super.key});

  @override
  State<AccountingJournalEntriesPage> createState() => _AccountingJournalEntriesPageState();
}

class _AccountingJournalEntriesPageState extends State<AccountingJournalEntriesPage> {
  late JournalEntriesViewModel _viewModel;
  late ApiClient _apiClient;

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<JournalEntriesViewModel>();
    _apiClient = di.sl<ApiClient>();
    _viewModel.addListener(_onChanged);
    WidgetsBinding.instance.addPostFrameCallback((_) => _viewModel.loadEntries());
  }

  @override
  void dispose() {
    _viewModel.removeListener(_onChanged);
    super.dispose();
  }

  void _onChanged() {
    if (!mounted) return;
    if (_viewModel.errorMessage != null) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(_viewModel.errorMessage!)),
      );
      _viewModel.clearError();
    }
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    if (!_apiClient.isOwnerAdmin) {
      return const Scaffold(
        body: Center(child: Text('No tienes permisos para ver asientos contables')),
      );
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text('Asientos contables'),
        backgroundColor: Colors.white,
        foregroundColor: const Color(0xFF2E7D32),
        elevation: 0,
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _viewModel.loading ? null : () => _viewModel.loadEntries(page: _viewModel.page),
          ),
          IconButton(
            icon: const Icon(Icons.notifications),
            onPressed: () => context.push('/notifications'),
          ),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: () => _viewModel.loadEntries(page: 0),
        color: const Color(0xFF2E7D32),
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            _buildHero(),
            const SizedBox(height: 16),
            _buildStats(),
            const SizedBox(height: 16),
            if (_viewModel.loading && _viewModel.items.isEmpty)
              const Padding(
                padding: EdgeInsets.symmetric(vertical: 48),
                child: Center(child: CircularProgressIndicator()),
              )
            else if (_viewModel.items.isEmpty)
              _emptyState()
            else
              ..._viewModel.items.map(_entryCard),
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
            style: TextStyle(fontSize: 12, fontWeight: FontWeight.w700, color: Color(0xFF4E7B54)),
          ),
          SizedBox(height: 6),
          Text(
            'Asientos contables',
            style: TextStyle(fontSize: 26, fontWeight: FontWeight.w900, color: Color(0xFF1B5E20)),
          ),
          SizedBox(height: 8),
          Text(
            'Consulta los asientos generados por el sistema, su balance y sus líneas contables.',
            style: TextStyle(color: Color(0xFF5F765B)),
          ),
        ],
      ),
    );
  }

  Widget _buildStats() {
    final balanced = _viewModel.items.where((entry) => entry.balanced).length;
    final posted = _viewModel.items.where((entry) => entry.status.toUpperCase() == 'POSTED').length;
    final draft = _viewModel.items.where((entry) => entry.status.toUpperCase() != 'POSTED').length;
    return Wrap(
      spacing: 12,
      runSpacing: 12,
      children: [
        _statCard('En página', '${_viewModel.items.length}'),
        _statCard('Balanceados', '$balanced'),
        _statCard('Posteados', '$posted'),
        _statCard('Borradores', '$draft'),
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
          Text(label, style: const TextStyle(fontSize: 11, color: Color(0xFF6B7D6C))),
          const SizedBox(height: 6),
          Text(value, style: const TextStyle(fontSize: 22, fontWeight: FontWeight.w900, color: Color(0xFF1B5E20))),
        ],
      ),
    );
  }

  Widget _entryCard(JournalEntry entry) {
    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
      child: InkWell(
        borderRadius: BorderRadius.circular(20),
        onTap: _viewModel.loadingDetail ? null : () => _openEntryDetail(entry.id),
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
                          entry.entryNumber,
                          style: const TextStyle(fontSize: 18, fontWeight: FontWeight.w900, color: Color(0xFF1B5E20)),
                        ),
                        const SizedBox(height: 4),
                        Text(
                          entry.reference ?? 'Sin referencia',
                          style: const TextStyle(color: Colors.black54),
                        ),
                      ],
                    ),
                  ),
                  _statusChip(_entryStatusLabel(entry.status), _entryStatusColor(entry.status)),
                ],
              ),
              const SizedBox(height: 12),
              Wrap(
                spacing: 12,
                runSpacing: 12,
                children: [
                  _miniInfo('Tipo', _entryTypeLabel(entry.entryType)),
                  _miniInfo('Débitos', _formatAmount(entry.totalDebits)),
                  _miniInfo('Créditos', _formatAmount(entry.totalCredits)),
                  _miniInfo('Publicado', entry.postedAt != null ? _formatDateTime(entry.postedAt!) : 'Pendiente'),
                  _miniInfo('Líneas', '${entry.lines.length}'),
                ],
              ),
              const SizedBox(height: 8),
              Text(
                entry.description?.trim().isNotEmpty == true ? entry.description!.trim() : 'Sin descripción',
                style: const TextStyle(color: Colors.black54),
              ),
            ],
          ),
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
        style: TextStyle(color: color, fontWeight: FontWeight.w700, fontSize: 11),
      ),
    );
  }

  Widget _miniInfo(String label, String value) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      mainAxisSize: MainAxisSize.min,
      children: [
        Text(label, style: const TextStyle(fontSize: 11, color: Colors.black54)),
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
          Icon(Icons.receipt_long, size: 40, color: Color(0xFF2E7D32)),
          SizedBox(height: 12),
          Text('No hay asientos contables registrados'),
        ],
      ),
    );
  }

  Widget _pagination() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        OutlinedButton(
          onPressed: _viewModel.hasPreviousPage ? () => _viewModel.loadEntries(page: _viewModel.page - 1) : null,
          child: const Text('Anterior'),
        ),
        Text('Página ${_viewModel.page + 1} de ${_viewModel.totalPages}'),
        OutlinedButton(
          onPressed: _viewModel.hasNextPage ? () => _viewModel.loadEntries(page: _viewModel.page + 1) : null,
          child: const Text('Siguiente'),
        ),
      ],
    );
  }

  Future<void> _openEntryDetail(String id) async {
    final entry = await _viewModel.loadEntryById(id);
    if (!mounted || entry == null) {
      return;
    }

    await showModalBottomSheet<void>(
      context: context,
      isScrollControlled: true,
      showDragHandle: true,
      backgroundColor: Colors.white,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(24)),
      ),
      builder: (sheetContext) {
        return SafeArea(
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: DraggableScrollableSheet(
              expand: false,
              initialChildSize: 0.85,
              minChildSize: 0.55,
              maxChildSize: 0.95,
              builder: (context, scrollController) {
                return ListView(
                  controller: scrollController,
                  children: [
                    Text(
                      entry.entryNumber,
                      style: const TextStyle(fontSize: 22, fontWeight: FontWeight.w900, color: Color(0xFF1B5E20)),
                    ),
                    const SizedBox(height: 4),
                    Text(entry.reference ?? 'Sin referencia', style: const TextStyle(color: Colors.black54)),
                    const SizedBox(height: 12),
                    Wrap(
                      spacing: 8,
                      runSpacing: 8,
                      children: [
                        _statusChip(_entryStatusLabel(entry.status), _entryStatusColor(entry.status)),
                        _statusChip(entry.balanced ? 'Balanceado' : 'Desbalanceado', entry.balanced ? Colors.green : Colors.red),
                      ],
                    ),
                    const SizedBox(height: 16),
                    Row(
                      children: [
                        Expanded(child: _miniInfo('Tipo', _entryTypeLabel(entry.entryType))),
                        Expanded(child: _miniInfo('Débitos', _formatAmount(entry.totalDebits))),
                        Expanded(child: _miniInfo('Créditos', _formatAmount(entry.totalCredits))),
                      ],
                    ),
                    const SizedBox(height: 12),
                    Text(
                      entry.description?.trim().isNotEmpty == true ? entry.description!.trim() : 'Sin descripción',
                      style: const TextStyle(color: Colors.black87),
                    ),
                    const SizedBox(height: 16),
                    const Text(
                      'Líneas contables',
                      style: TextStyle(fontSize: 16, fontWeight: FontWeight.w800, color: Color(0xFF1B5E20)),
                    ),
                    const SizedBox(height: 8),
                    ...entry.lines.map(
                      (line) => Container(
                        margin: const EdgeInsets.only(bottom: 10),
                        padding: const EdgeInsets.all(14),
                        decoration: BoxDecoration(
                          color: const Color(0xFFF7FBF3),
                          borderRadius: BorderRadius.circular(18),
                          border: Border.all(color: const Color(0xFFC8E6C9)),
                        ),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Row(
                              children: [
                                Text(
                                  '#${line.lineNo}',
                                  style: const TextStyle(fontWeight: FontWeight.w800, color: Color(0xFF2E7D32)),
                                ),
                                const SizedBox(width: 8),
                                _statusChip(line.lineType, Colors.teal),
                              ],
                            ),
                            const SizedBox(height: 8),
                            Text(
                              '${line.accountCode} · ${line.accountName}',
                              style: const TextStyle(fontWeight: FontWeight.w700),
                            ),
                            const SizedBox(height: 4),
                            Text(
                              '${_formatAmount(line.amount)} ${line.currency}',
                              style: const TextStyle(color: Colors.black87),
                            ),
                            if (line.description != null && line.description!.trim().isNotEmpty) ...[
                              const SizedBox(height: 4),
                              Text(line.description!, style: const TextStyle(color: Colors.black54)),
                            ],
                          ],
                        ),
                      ),
                    ),
                  ],
                );
              },
            ),
          ),
        );
      },
    );
    _viewModel.clearSelectedEntry();
  }

  String _entryStatusLabel(String status) {
    switch (status.toUpperCase()) {
      case 'POSTED':
        return 'Posteado';
      case 'DRAFT':
        return 'Borrador';
      case 'CANCELLED':
        return 'Cancelado';
      default:
        return status;
    }
  }

  Color _entryStatusColor(String status) {
    switch (status.toUpperCase()) {
      case 'POSTED':
        return Colors.green;
      case 'DRAFT':
        return Colors.grey;
      case 'CANCELLED':
        return Colors.red;
      default:
        return Colors.grey;
    }
  }

  String _entryTypeLabel(String type) {
    switch (type.toUpperCase()) {
      case 'AUTOMATED':
        return 'Automático';
      case 'MANUAL':
        return 'Manual';
      case 'SYSTEM':
        return 'Sistema';
      default:
        return type;
    }
  }

  String _formatAmount(double value) {
    final fixed = value.toStringAsFixed(2);
    final parts = fixed.split('.');
    final intPart = parts[0];
    final decimals = parts.length > 1 ? parts[1] : '00';
    final negative = intPart.startsWith('-');
    final digits = negative ? intPart.substring(1) : intPart;
    final buffer = StringBuffer();
    for (var i = 0; i < digits.length; i++) {
      buffer.write(digits[i]);
      final remaining = digits.length - i - 1;
      if (remaining > 0 && remaining % 3 == 0) {
        buffer.write(',');
      }
    }
    return '${negative ? '-' : ''}${buffer.toString()}.$decimals';
  }

  String _formatDateTime(DateTime date) {
    final local = date.toLocal();
    return '${local.day.toString().padLeft(2, '0')}/${local.month.toString().padLeft(2, '0')}/${local.year} ${local.hour.toString().padLeft(2, '0')}:${local.minute.toString().padLeft(2, '0')}';
  }
}
