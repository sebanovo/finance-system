import 'package:flutter/material.dart';
import '../../core/di/injection_container.dart' as di;
import '../../domain/entities/account.dart';
import '../../infrastructure/models/loan_model.dart';
import '../viewmodels/loans_viewmodel.dart';

class MyLoansPage extends StatefulWidget {
  const MyLoansPage({super.key});

  @override
  State<MyLoansPage> createState() => _MyLoansPageState();
}

class _MyLoansPageState extends State<MyLoansPage> {
  static const _green = Color(0xFF2E7D32);
  late final LoansViewModel _vm;
  final _formKey = GlobalKey<FormState>();

  bool _showForm = false;
  String? _accountId;
  final _principalCtrl = TextEditingController();
  final _rateCtrl = TextEditingController(text: '12');
  final _termCtrl = TextEditingController(text: '6');
  String _method = 'FLAT';
  final _purposeCtrl = TextEditingController();

  @override
  void initState() {
    super.initState();
    _vm = di.sl<LoansViewModel>();
    _vm.addListener(_onChange);
    _vm.init();
  }

  void _onChange() {
    if (!mounted) return;
    final msg = _vm.errorMessage ?? _vm.successMessage;
    if (msg != null) {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(
        content: Text(msg),
        backgroundColor: _vm.errorMessage != null ? Colors.red.shade700 : Colors.green.shade700,
      ));
      _vm.clearMessages();
    }
    setState(() {});
  }

  @override
  void dispose() {
    _vm.removeListener(_onChange);
    _principalCtrl.dispose();
    _rateCtrl.dispose();
    _termCtrl.dispose();
    _purposeCtrl.dispose();
    super.dispose();
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate() || _accountId == null) return;
    final ok = await _vm.requestLoan(
      accountId: _accountId!,
      principal: double.parse(_principalCtrl.text.trim()),
      annualInterestRate: double.parse(_rateCtrl.text.trim()),
      termMonths: int.parse(_termCtrl.text.trim()),
      interestMethod: _method,
      purpose: _purposeCtrl.text.trim().isEmpty ? null : _purposeCtrl.text.trim(),
    );
    if (ok) {
      setState(() {
        _showForm = false;
        _principalCtrl.clear();
        _purposeCtrl.clear();
        _accountId = null;
      });
    }
  }

  Future<void> _payDialog(String loanId, double outstanding) async {
    final ctrl = TextEditingController(text: outstanding.toStringAsFixed(2));
    final amount = await showDialog<double>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Pagar préstamo'),
        content: TextField(
          controller: ctrl,
          keyboardType: const TextInputType.numberWithOptions(decimal: true),
          decoration: const InputDecoration(labelText: 'Monto'),
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('Cancelar')),
          ElevatedButton(
            onPressed: () => Navigator.pop(ctx, double.tryParse(ctrl.text.trim())),
            child: const Text('Pagar'),
          ),
        ],
      ),
    );
    if (amount != null && amount > 0) {
      await _vm.payLoan(loanId, amount);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Mis préstamos'),
        backgroundColor: Colors.white,
        foregroundColor: _green,
        elevation: 0,
        actions: [
          IconButton(
            icon: Icon(_showForm ? Icons.close : Icons.add),
            onPressed: () => setState(() => _showForm = !_showForm),
          ),
        ],
      ),
      body: _vm.isLoading
          ? const Center(child: CircularProgressIndicator(color: _green))
          : RefreshIndicator(
              onRefresh: _vm.refreshLoans,
              child: ListView(
                padding: const EdgeInsets.all(16),
                children: [
                  if (_showForm) _buildForm(),
                  if (_vm.loans.isEmpty)
                    const Padding(
                      padding: EdgeInsets.only(top: 40),
                      child: Center(child: Text('Aún no tienes préstamos.')),
                    ),
                  ..._vm.loans.map(_buildLoanCard),
                ],
              ),
            ),
    );
  }

  Widget _buildForm() {
    return Card(
      margin: const EdgeInsets.only(bottom: 16),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text('Solicitar préstamo', style: TextStyle(fontWeight: FontWeight.bold, color: _green)),
              const SizedBox(height: 12),
              DropdownButtonFormField<String>(
                initialValue: _accountId,
                decoration: const InputDecoration(labelText: 'Cuenta de desembolso'),
                items: _vm.accounts
                    .map((Account a) => DropdownMenuItem(
                          value: a.id,
                          child: Text('${a.accountNumber} · ${a.currency} · ${a.availableBalance.toStringAsFixed(2)}'),
                        ))
                    .toList(),
                onChanged: (v) => setState(() => _accountId = v),
                validator: (_) => _accountId == null ? 'Selecciona una cuenta' : null,
              ),
              const SizedBox(height: 8),
              TextFormField(
                controller: _principalCtrl,
                keyboardType: const TextInputType.numberWithOptions(decimal: true),
                decoration: const InputDecoration(labelText: 'Monto'),
                validator: (v) => (double.tryParse(v?.trim() ?? '') ?? 0) <= 0 ? 'Monto inválido' : null,
              ),
              const SizedBox(height: 8),
              TextFormField(
                controller: _rateCtrl,
                keyboardType: const TextInputType.numberWithOptions(decimal: true),
                decoration: const InputDecoration(labelText: 'Tasa anual (%)'),
                validator: (v) => double.tryParse(v?.trim() ?? '') == null ? 'Tasa inválida' : null,
              ),
              const SizedBox(height: 8),
              TextFormField(
                controller: _termCtrl,
                keyboardType: TextInputType.number,
                decoration: const InputDecoration(labelText: 'Plazo (meses)'),
                validator: (v) => (int.tryParse(v?.trim() ?? '') ?? 0) <= 0 ? 'Plazo inválido' : null,
              ),
              const SizedBox(height: 8),
              DropdownButtonFormField<String>(
                initialValue: _method,
                decoration: const InputDecoration(labelText: 'Método de interés'),
                items: const [
                  DropdownMenuItem(value: 'FLAT', child: Text('Fijo (FLAT)')),
                  DropdownMenuItem(value: 'FRENCH', child: Text('Francés (cuota fija)')),
                ],
                onChanged: (v) => setState(() => _method = v ?? 'FLAT'),
              ),
              const SizedBox(height: 8),
              TextFormField(
                controller: _purposeCtrl,
                decoration: const InputDecoration(labelText: 'Propósito (opcional)'),
              ),
              const SizedBox(height: 12),
              SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  style: ElevatedButton.styleFrom(backgroundColor: _green, foregroundColor: Colors.white),
                  onPressed: _vm.isSubmitting ? null : _submit,
                  child: _vm.isSubmitting
                      ? const SizedBox(height: 18, width: 18, child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white))
                      : const Text('Enviar solicitud'),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildLoanCard(LoanModel loan) {
    final expanded = _vm.expandedLoanId == loan.id;
    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                _statusChip(loan.status),
                Text('${loan.principal.toStringAsFixed(2)} ${loan.currency}',
                    style: const TextStyle(fontWeight: FontWeight.bold, color: _green, fontSize: 16)),
              ],
            ),
            const SizedBox(height: 4),
            Text('${loan.annualInterestRate}% · ${loan.termMonths} meses · ${loan.interestMethod}',
                style: TextStyle(color: Colors.grey.shade600, fontSize: 12)),
            const SizedBox(height: 8),
            Row(
              children: [
                _stat('Total', loan.totalDue),
                _stat('Pagado', loan.totalPaid),
                _stat('Saldo', loan.outstandingBalance),
              ],
            ),
            const SizedBox(height: 8),
            Row(
              children: [
                TextButton(
                  onPressed: () => _vm.toggleSchedule(loan.id),
                  child: Text(expanded ? 'Ocultar cuotas' : 'Ver cuotas', style: const TextStyle(color: _green)),
                ),
                if (loan.status == 'DISBURSED')
                  ElevatedButton(
                    style: ElevatedButton.styleFrom(backgroundColor: _green, foregroundColor: Colors.white),
                    onPressed: _vm.isSubmitting ? null : () => _payDialog(loan.id, loan.outstandingBalance),
                    child: const Text('Pagar'),
                  ),
              ],
            ),
            if (expanded) ..._vm.schedule.map((i) => Padding(
                  padding: const EdgeInsets.symmetric(vertical: 2),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text('#${i.number} · ${i.dueDate}', style: const TextStyle(fontSize: 12)),
                      Text('${i.totalDue.toStringAsFixed(2)} · ${i.status}',
                          style: TextStyle(fontSize: 12, color: Colors.grey.shade700)),
                    ],
                  ),
                )),
          ],
        ),
      ),
    );
  }

  Widget _stat(String label, double value) {
    return Expanded(
      child: Column(
        children: [
          Text(label, style: TextStyle(fontSize: 11, color: Colors.grey.shade600)),
          Text(value.toStringAsFixed(2), style: const TextStyle(fontWeight: FontWeight.bold, color: _green)),
        ],
      ),
    );
  }

  Widget _statusChip(String status) {
    Color bg;
    Color fg;
    switch (status) {
      case 'DISBURSED':
        bg = const Color(0xFFE8F5E9);
        fg = _green;
        break;
      case 'PAID_OFF':
        bg = Colors.blue.shade50;
        fg = Colors.blue.shade700;
        break;
      case 'REJECTED':
      case 'CANCELLED':
        bg = Colors.red.shade50;
        fg = Colors.red.shade700;
        break;
      default:
        bg = Colors.amber.shade50;
        fg = Colors.amber.shade800;
    }
    final label = {
          'REQUESTED': 'Solicitado',
          'APPROVED': 'Aprobado',
          'REJECTED': 'Rechazado',
          'DISBURSED': 'Desembolsado',
          'PAID_OFF': 'Pagado',
          'CANCELLED': 'Cancelado',
        }[status] ??
        status;
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
      decoration: BoxDecoration(color: bg, borderRadius: BorderRadius.circular(20)),
      child: Text(label, style: TextStyle(color: fg, fontWeight: FontWeight.bold, fontSize: 12)),
    );
  }
}
