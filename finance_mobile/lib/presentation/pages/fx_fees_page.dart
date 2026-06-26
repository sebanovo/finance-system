import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import '../../core/di/injection_container.dart' as di;
import '../../core/network/api_client.dart';
import '../../domain/entities/operation_fee.dart';
import '../viewmodels/fx_fees_viewmodel.dart';

class FxFeesPage extends StatefulWidget {
  const FxFeesPage({super.key});

  @override
  State<FxFeesPage> createState() => _FxFeesPageState();
}

class _FxFeesPageState extends State<FxFeesPage> {
  late FxFeesViewModel _viewModel;
  late ApiClient _apiClient;

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<FxFeesViewModel>();
    _apiClient = di.sl<ApiClient>();
    _viewModel.addListener(_onChanged);
    WidgetsBinding.instance.addPostFrameCallback((_) => _viewModel.loadFees());
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
        body: Center(child: Text('No tienes permisos para ver comisiones')),
      );
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text('Comisiones Operativas'),
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
      floatingActionButton: FloatingActionButton.extended(
        onPressed: _viewModel.saving ? null : () => _showFeeDialog(),
        backgroundColor: const Color(0xFF2E7D32),
        foregroundColor: Colors.white,
        icon: const Icon(Icons.add),
        label: const Text('Nueva Comisión'),
      ),
      body: RefreshIndicator(
        onRefresh: () => _viewModel.loadFees(page: 0),
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
              ..._viewModel.items.map(_feeCard),
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
          Text('Tarifas', style: TextStyle(fontSize: 12, fontWeight: FontWeight.w700, color: Color(0xFF4E7B54))),
          SizedBox(height: 6),
          Text('Comisiones Operativas', style: TextStyle(fontSize: 26, fontWeight: FontWeight.w900, color: Color(0xFF1B5E20))),
          SizedBox(height: 8),
          Text('Define tarifas fijas o porcentuales para las operaciones del sistema.',
              style: TextStyle(color: Color(0xFF5F765B))),
        ],
      ),
    );
  }

  Widget _buildStats() {
    final active = _viewModel.items.where((e) => e.active).length;
    final percentage = _viewModel.items.where((e) => e.feeType == 'PERCENTAGE').length;
    final fixed = _viewModel.items.where((e) => e.feeType == 'FIXED').length;
    final none = _viewModel.items.where((e) => e.feeType == 'NONE').length;
    return Row(
      children: [
        Expanded(child: _statCard('Registros', '${_viewModel.totalElements}')),
        const SizedBox(width: 12),
        Expanded(child: _statCard('Activas', '$active')),
        const SizedBox(width: 12),
        Expanded(child: _statCard('Porcentaje', '$percentage')),
        const SizedBox(width: 12),
        Expanded(child: _statCard('Fijas', '$fixed')),
        const SizedBox(width: 12),
        Expanded(child: _statCard('Sin comisión', '$none')),
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
          Text(label, style: const TextStyle(fontSize: 11, color: Color(0xFF6B7D6C))),
          const SizedBox(height: 6),
          Text(value, style: const TextStyle(fontSize: 22, fontWeight: FontWeight.w900, color: Color(0xFF1B5E20))),
        ],
      ),
    );
  }

  Widget _feeCard(OperationFee fee) {
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
                      Text(_operationLabel(fee.operationCode), style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w800)),
                      const SizedBox(height: 4),
                      Text(fee.description ?? 'Sin descripción', style: const TextStyle(color: Colors.black54)),
                    ],
                  ),
                ),
                _badge(fee.active ? 'Activa' : 'Inactiva', fee.active ? Colors.green : Colors.grey),
              ],
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                Expanded(child: _miniInfo('Tipo', _feeTypeLabel(fee.feeType))),
                Expanded(child: _miniInfo('Modo', _calculationModeLabel(fee.calculationMode))),
                Expanded(child: _miniInfo('Valor', _feeValueLabel(fee))),
              ],
            ),
            const SizedBox(height: 8),
            Align(
              alignment: Alignment.centerRight,
              child: Wrap(
                spacing: 8,
                children: [
                  TextButton(
                    onPressed: _viewModel.saving ? null : () => _showFeeDialog(fee: fee),
                    child: const Text('Editar'),
                  ),
                  TextButton(
                    onPressed: _viewModel.saving ? null : () => _deleteFee(fee),
                    child: const Text('Eliminar', style: TextStyle(color: Colors.red)),
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
        color: color.withOpacity(0.12),
        borderRadius: BorderRadius.circular(999),
      ),
      child: Text(label, style: TextStyle(color: color, fontWeight: FontWeight.w700, fontSize: 11)),
    );
  }

  Widget _miniInfo(String label, String value) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
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
          Icon(Icons.payments_outlined, size: 40, color: Color(0xFF2E7D32)),
          SizedBox(height: 12),
          Text('No hay comisiones registradas'),
        ],
      ),
    );
  }

  Widget _pagination() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        OutlinedButton(
          onPressed: _viewModel.hasPreviousPage ? () => _viewModel.loadFees(page: _viewModel.page - 1) : null,
          child: const Text('Anterior'),
        ),
        Text('Página ${_viewModel.page + 1} de ${_viewModel.totalPages}'),
        OutlinedButton(
          onPressed: _viewModel.hasNextPage ? () => _viewModel.loadFees(page: _viewModel.page + 1) : null,
          child: const Text('Siguiente'),
        ),
      ],
    );
  }

  Future<void> _showFeeDialog({OperationFee? fee}) async {
    final result = await showDialog<bool>(
      context: context,
      builder: (_) => FxFeeDialog(
        viewModel: _viewModel,
        initialFee: fee,
      ),
    );
    if (result == true && mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(fee == null ? 'Comisión creada exitosamente' : 'Comisión actualizada exitosamente'),
        ),
      );
    }
  }

  InputDecoration _inputDecoration(String label) {
    return InputDecoration(
      labelText: label,
      border: OutlineInputBorder(borderRadius: BorderRadius.circular(14)),
    );
  }

  Future<void> _deleteFee(OperationFee fee) async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Eliminar comisión'),
        content: Text('¿Deseas eliminar la comisión de ${_operationLabel(fee.operationCode)}?'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context, false), child: const Text('Cancelar')),
          FilledButton(onPressed: () => Navigator.pop(context, true), child: const Text('Eliminar')),
        ],
      ),
    );
    if (confirm == true) {
      final ok = await _viewModel.deleteFee(fee.id);
      if (ok && mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Comisión eliminada exitosamente')),
        );
      }
    }
  }

  String _operationLabel(String code) {
    switch (code) {
      case 'TRANSFER': return 'Transferencia';
      case 'CONVERSION': return 'Conversión';
      case 'DEPOSIT': return 'Depósito';
      case 'WITHDRAWAL': return 'Retiro';
      case 'PAYMENT': return 'Pago';
      default: return code;
    }
  }

  String _feeTypeLabel(String code) {
    switch (code) {
      case 'NONE': return 'Sin comisión';
      case 'FIXED': return 'Fija';
      case 'PERCENTAGE': return 'Porcentaje';
      default: return code;
    }
  }

  String _calculationModeLabel(String code) {
    switch (code) {
      case 'SEPARATE': return 'Separado';
      case 'INCLUDED': return 'Incluido';
      default: return code;
    }
  }

  String _feeValueLabel(OperationFee fee) {
    return fee.feeType == 'PERCENTAGE' ? '${fee.feeValue.toStringAsFixed(2)}%' : fee.feeValue.toStringAsFixed(2);
  }
}

class FxFeeDialog extends StatefulWidget {
  final FxFeesViewModel viewModel;
  final OperationFee? initialFee;

  const FxFeeDialog({
    super.key,
    required this.viewModel,
    required this.initialFee,
  });

  @override
  State<FxFeeDialog> createState() => _FxFeeDialogState();
}

class _FxFeeDialogState extends State<FxFeeDialog> {
  final _formKey = GlobalKey<FormState>();
  late String _operationCode;
  late String _feeType;
  late String _calculationMode;
  late String _feeValueText;
  late String _descriptionText;
  late bool _active;
  bool _submitting = false;

  @override
  void initState() {
    super.initState();
    final fee = widget.initialFee;
    _operationCode = fee?.operationCode ?? 'TRANSFER';
    _feeType = fee?.feeType ?? 'FIXED';
    _calculationMode = fee?.calculationMode ?? 'SEPARATE';
    _feeValueText = fee?.feeValue.toString() ?? '0.00';
    _descriptionText = fee?.description ?? '';
    _active = fee?.active ?? true;
  }

  @override
  Widget build(BuildContext context) {
    final isEditing = widget.initialFee != null;
    return AlertDialog(
      title: Text(isEditing ? 'Editar Comisión Operativa' : 'Nueva Comisión Operativa'),
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
                  value: _operationCode,
                  decoration: _inputDecoration('Operación'),
                  items: const ['TRANSFER', 'CONVERSION', 'DEPOSIT', 'WITHDRAWAL', 'PAYMENT']
                      .map((value) => DropdownMenuItem(value: value, child: Text(value)))
                      .toList(),
                  onChanged: _submitting ? null : (value) => setState(() => _operationCode = value ?? _operationCode),
                ),
                const SizedBox(height: 12),
                DropdownButtonFormField<String>(
                  value: _feeType,
                  decoration: _inputDecoration('Tipo'),
                  items: const ['NONE', 'FIXED', 'PERCENTAGE']
                      .map((value) => DropdownMenuItem(value: value, child: Text(value)))
                      .toList(),
                  onChanged: _submitting ? null : (value) => setState(() => _feeType = value ?? _feeType),
                ),
                const SizedBox(height: 12),
                DropdownButtonFormField<String>(
                  value: _calculationMode,
                  decoration: _inputDecoration('Modo'),
                  items: const ['SEPARATE', 'INCLUDED']
                      .map((value) => DropdownMenuItem(value: value, child: Text(value)))
                      .toList(),
                  onChanged: _submitting ? null : (value) => setState(() => _calculationMode = value ?? _calculationMode),
                ),
                const SizedBox(height: 12),
                TextFormField(
                  initialValue: _feeValueText,
                  enabled: !_submitting,
                  keyboardType: const TextInputType.numberWithOptions(decimal: true),
                  decoration: _inputDecoration('Valor'),
                  onChanged: (value) => _feeValueText = value,
                  validator: (value) {
                    final parsed = double.tryParse((value ?? '').replaceAll(',', '.'));
                    if (parsed == null) return 'Ingresa un valor válido';
                    if (_feeType == 'NONE' && parsed != 0) {
                      return 'Si es sin comisión, el valor debe ser 0';
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
                  onChanged: _submitting ? null : (value) => setState(() => _active = value),
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
    final value = double.parse(_feeValueText.replaceAll(',', '.'));
    setState(() => _submitting = true);
    final fee = widget.initialFee;
    final success = fee == null
        ? await widget.viewModel.createFee(
            operationCode: _operationCode,
            feeType: _feeType,
            feeValue: value,
            calculationMode: _calculationMode,
            active: _active,
            description: _descriptionText.trim(),
          )
        : await widget.viewModel.updateFee(
            fee.id,
            operationCode: _operationCode,
            feeType: _feeType,
            feeValue: value,
            calculationMode: _calculationMode,
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
