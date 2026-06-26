import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import '../../core/di/injection_container.dart' as di;
import '../../core/network/api_client.dart';
import '../../domain/entities/fx_exchange_rate.dart';
import '../viewmodels/fx_rates_viewmodel.dart';

class FxRatesPage extends StatefulWidget {
  const FxRatesPage({super.key});

  @override
  State<FxRatesPage> createState() => _FxRatesPageState();
}

class _FxRatesPageState extends State<FxRatesPage> {
  late FxRatesViewModel _viewModel;
  late ApiClient _apiClient;

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<FxRatesViewModel>();
    _apiClient = di.sl<ApiClient>();
    _viewModel.addListener(_onChanged);
    WidgetsBinding.instance.addPostFrameCallback((_) => _viewModel.loadRates());
  }

  @override
  void dispose() {
    _viewModel.removeListener(_onChanged);
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

  bool get _canOperate => _apiClient.isOwnerAdmin;

  @override
  Widget build(BuildContext context) {
    if (!_apiClient.isOwnerAdmin) {
      return const Scaffold(
        body: Center(
          child: Text('No tienes permisos para ver tipos de cambio'),
        ),
      );
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text('Tipos de Cambio'),
        backgroundColor: Colors.white,
        foregroundColor: const Color(0xFF2E7D32),
        elevation: 0,
        actions: [
          IconButton(
            icon: const Icon(Icons.notifications),
            onPressed: () => context.push('/notifications'),
          ),
        ],
      ),
      floatingActionButton: _canOperate
          ? FloatingActionButton.extended(
              onPressed: _viewModel.saving ? null : () => _showRateDialog(),
              backgroundColor: const Color(0xFF2E7D32),
              foregroundColor: Colors.white,
              icon: const Icon(Icons.add),
              label: const Text('Nueva Tasa'),
            )
          : null,
      body: RefreshIndicator(
        onRefresh: () => _viewModel.loadRates(page: 0),
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
              ..._viewModel.items.map(_rateCard),
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
            'Divisas',
            style: TextStyle(
              fontSize: 12,
              fontWeight: FontWeight.w700,
              color: Color(0xFF4E7B54),
            ),
          ),
          SizedBox(height: 6),
          Text(
            'Tipos de Cambio',
            style: TextStyle(
              fontSize: 26,
              fontWeight: FontWeight.w900,
              color: Color(0xFF1B5E20),
            ),
          ),
          SizedBox(height: 8),
          Text(
            'Gestiona las tasas de cambio que usa el sistema para conversiones y cálculos financieros.',
            style: TextStyle(color: Color(0xFF5F765B)),
          ),
        ],
      ),
    );
  }

  Widget _buildStats() {
    final active = _viewModel.items.where((e) => e.active).length;
    final inactive = _viewModel.items.length - active;
    final pairs = _viewModel.items
        .map((e) => '${e.sourceCurrency}-${e.targetCurrency}')
        .toSet()
        .length;
    return Row(
      children: [
        Expanded(child: _statCard('Registros', '${_viewModel.totalElements}')),
        const SizedBox(width: 12),
        Expanded(child: _statCard('Activas', '$active')),
        const SizedBox(width: 12),
        Expanded(child: _statCard('Inactivas', '$inactive')),
        const SizedBox(width: 12),
        Expanded(child: _statCard('Pares', '$pairs')),
      ],
    );
  }

  Widget _statCard(String label, String value) {
    return Container(
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

  Widget _rateCard(FxExchangeRate rate) {
    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        '${rate.sourceCurrency} → ${rate.targetCurrency}',
                        style: const TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.w800,
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        rate.description ?? 'Sin descripción',
                        style: const TextStyle(color: Colors.black54),
                      ),
                    ],
                  ),
                ),
                _badge(
                  rate.active ? 'Activa' : 'Inactiva',
                  rate.active ? Colors.green : Colors.grey,
                ),
              ],
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                Expanded(
                  child: _miniInfo('Tasa', rate.rate.toStringAsFixed(6)),
                ),
                Expanded(
                  child: _miniInfo('Actualizado', _formatDate(rate.updatedAt)),
                ),
              ],
            ),
            const SizedBox(height: 8),
            Align(
              alignment: Alignment.centerRight,
              child: Wrap(
                spacing: 8,
                children: [
                  TextButton(
                    onPressed: _viewModel.saving
                        ? null
                        : () => _showRateDialog(rate: rate),
                    child: const Text('Editar'),
                  ),
                  TextButton(
                    onPressed: _viewModel.saving
                        ? null
                        : () => _deleteRate(rate),
                    child: const Text(
                      'Eliminar',
                      style: TextStyle(color: Colors.red),
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _badge(String label, Color color) {
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
          Icon(Icons.currency_exchange, size: 40, color: Color(0xFF2E7D32)),
          SizedBox(height: 12),
          Text('No hay tipos de cambio registrados'),
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
              ? () => _viewModel.loadRates(page: _viewModel.page - 1)
              : null,
          child: const Text('Anterior'),
        ),
        Text('Página ${_viewModel.page + 1} de ${_viewModel.totalPages}'),
        OutlinedButton(
          onPressed: _viewModel.hasNextPage
              ? () => _viewModel.loadRates(page: _viewModel.page + 1)
              : null,
          child: const Text('Siguiente'),
        ),
      ],
    );
  }

  Future<void> _showRateDialog({FxExchangeRate? rate}) async {
    final result = await showDialog<bool>(
      context: context,
      builder: (_) => FxRateDialog(viewModel: _viewModel, initialRate: rate),
    );
    if (result == true && mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(
            rate == null
                ? 'Tasa creada exitosamente'
                : 'Tasa actualizada exitosamente',
          ),
        ),
      );
    }
  }

  Future<void> _deleteRate(FxExchangeRate rate) async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Eliminar tasa'),
        content: Text(
          '¿Deseas eliminar la tasa ${rate.sourceCurrency} → ${rate.targetCurrency}?',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('Cancelar'),
          ),
          FilledButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text('Eliminar'),
          ),
        ],
      ),
    );
    if (confirm == true) {
      final ok = await _viewModel.deleteRate(rate.id);
      if (ok && mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Tasa eliminada exitosamente')),
        );
      }
    }
  }

  String _formatDate(DateTime? date) {
    if (date == null) return 'Sin fecha';
    final local = date.toLocal();
    return '${local.day.toString().padLeft(2, '0')}/${local.month.toString().padLeft(2, '0')}/${local.year}';
  }
}

class FxRateDialog extends StatefulWidget {
  final FxRatesViewModel viewModel;
  final FxExchangeRate? initialRate;

  const FxRateDialog({
    super.key,
    required this.viewModel,
    required this.initialRate,
  });

  @override
  State<FxRateDialog> createState() => _FxRateDialogState();
}

class _FxRateDialogState extends State<FxRateDialog> {
  final _formKey = GlobalKey<FormState>();
  late String _sourceCurrency;
  late String _targetCurrency;
  late String _rateText;
  late String _descriptionText;
  late bool _active;
  bool _submitting = false;

  @override
  void initState() {
    super.initState();
    final rate = widget.initialRate;
    _sourceCurrency = rate?.sourceCurrency ?? 'BOB';
    _targetCurrency = rate?.targetCurrency ?? 'USD';
    _rateText = rate?.rate.toString() ?? '1.000000';
    _descriptionText = rate?.description ?? '';
    _active = rate?.active ?? true;
  }

  @override
  Widget build(BuildContext context) {
    final isEditing = widget.initialRate != null;
    return AlertDialog(
      title: Text(isEditing ? 'Editar Tasa de Cambio' : 'Nueva Tasa de Cambio'),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
      content: SizedBox(
        width: 420,
        child: Form(
          key: _formKey,
          child: SingleChildScrollView(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                DropdownButtonFormField<String>(
                  initialValue: _sourceCurrency,
                  decoration: _inputDecoration('Moneda origen'),
                  items: const ['BOB', 'USD', 'EUR', 'USDT']
                      .map(
                        (value) =>
                            DropdownMenuItem(value: value, child: Text(value)),
                      )
                      .toList(),
                  onChanged: _submitting
                      ? null
                      : (value) => setState(
                          () => _sourceCurrency = value ?? _sourceCurrency,
                        ),
                ),
                const SizedBox(height: 12),
                DropdownButtonFormField<String>(
                  initialValue: _targetCurrency,
                  decoration: _inputDecoration('Moneda destino'),
                  items: const ['BOB', 'USD', 'EUR', 'USDT']
                      .map(
                        (value) =>
                            DropdownMenuItem(value: value, child: Text(value)),
                      )
                      .toList(),
                  onChanged: _submitting
                      ? null
                      : (value) => setState(
                          () => _targetCurrency = value ?? _targetCurrency,
                        ),
                ),
                const SizedBox(height: 12),
                TextFormField(
                  initialValue: _rateText,
                  enabled: !_submitting,
                  keyboardType: const TextInputType.numberWithOptions(
                    decimal: true,
                  ),
                  decoration: _inputDecoration('Tasa'),
                  onChanged: (value) => _rateText = value,
                  validator: (value) {
                    if (double.tryParse((value ?? '').replaceAll(',', '.')) ==
                        null) {
                      return 'Ingresa una tasa válida';
                    }
                    return null;
                  },
                ),
                const SizedBox(height: 12),
                TextFormField(
                  initialValue: _descriptionText,
                  enabled: !_submitting,
                  decoration: _inputDecoration('Descripción'),
                  maxLines: 3,
                  onChanged: (value) => _descriptionText = value,
                ),
                const SizedBox(height: 12),
                SwitchListTile(
                  contentPadding: EdgeInsets.zero,
                  value: _active,
                  onChanged: _submitting
                      ? null
                      : (value) => setState(() => _active = value),
                  title: const Text('Activa'),
                ),
              ],
            ),
          ),
        ),
      ),
      actions: [
        TextButton(
          onPressed: _submitting ? null : () => Navigator.pop(context, false),
          child: const Text('Cancelar'),
        ),
        FilledButton(
          onPressed: _submitting ? null : _submit,
          child: Text(isEditing ? 'Guardar' : 'Crear'),
        ),
      ],
    );
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;
    final parsedRate = double.parse(_rateText.replaceAll(',', '.'));
    if (_sourceCurrency.isEmpty || _targetCurrency.isEmpty) return;
    if (_sourceCurrency == _targetCurrency && parsedRate != 1) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('La tasa para la misma moneda debe ser 1'),
        ),
      );
      return;
    }

    setState(() => _submitting = true);
    final rate = widget.initialRate;
    final success = rate == null
        ? await widget.viewModel.createRate(
            sourceCurrency: _sourceCurrency,
            targetCurrency: _targetCurrency,
            rate: parsedRate,
            active: _active,
            description: _descriptionText.trim(),
          )
        : await widget.viewModel.updateRate(
            rate.id,
            sourceCurrency: _sourceCurrency,
            targetCurrency: _targetCurrency,
            rate: parsedRate,
            active: _active,
            description: _descriptionText.trim(),
          );

    if (!mounted) return;
    if (success) {
      Navigator.pop(context, true);
      return;
    }
    setState(() => _submitting = false);
  }

  InputDecoration _inputDecoration(String label) {
    return InputDecoration(
      labelText: label,
      border: OutlineInputBorder(borderRadius: BorderRadius.circular(14)),
    );
  }
}
