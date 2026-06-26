import 'package:flutter/material.dart';
import 'package:finance_mobile/core/di/injection_container.dart' as di;
import 'package:finance_mobile/domain/entities/account.dart';
import 'package:finance_mobile/domain/entities/user.dart';
import 'package:finance_mobile/infrastructure/models/loan_model.dart';
import 'package:finance_mobile/presentation/viewmodels/owner_loans_viewmodel.dart';

class OwnerLoansPage extends StatefulWidget {
  const OwnerLoansPage({super.key});

  @override
  State<OwnerLoansPage> createState() => _OwnerLoansPageState();
}

class _OwnerLoansPageState extends State<OwnerLoansPage> {
  static const _green = Color(0xFF2E7D32);

  late final OwnerLoansViewModel _vm;
  final _formKey = GlobalKey<FormState>();
  bool _showForm = false;
  String? _selectedUserId;
  String? _selectedAccountId;
  final _principalCtrl = TextEditingController();
  final _rateCtrl = TextEditingController(text: '12');
  final _termCtrl = TextEditingController(text: '6');
  final _purposeCtrl = TextEditingController();
  String _method = 'FLAT';

  @override
  void initState() {
    super.initState();
    _vm = di.sl<OwnerLoansViewModel>();
    _vm.addListener(_onChange);
    _vm.init();
  }

  void _onChange() {
    if (!mounted) return;
    final msg = _vm.errorMessage ?? _vm.successMessage;
    if (msg != null) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(msg),
          backgroundColor: _vm.errorMessage != null ? Colors.red.shade700 : Colors.green.shade700,
        ),
      );
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

  List<Account> get _filteredAccounts {
    final userId = _selectedUserId;
    if (userId == null || userId.isEmpty) return const [];
    return _vm.accounts.where((account) => account.userId == userId).toList();
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate() || _selectedUserId == null || _selectedAccountId == null) {
      return;
    }
    final ok = await _vm.requestLoan(
      userId: _selectedUserId!,
      accountId: _selectedAccountId!,
      principal: double.parse(_principalCtrl.text.trim()),
      annualInterestRate: double.parse(_rateCtrl.text.trim()),
      termMonths: int.parse(_termCtrl.text.trim()),
      interestMethod: _method,
      purpose: _purposeCtrl.text.trim().isEmpty ? null : _purposeCtrl.text.trim(),
    );
    if (ok && mounted) {
      setState(() {
        _showForm = false;
        _selectedUserId = null;
        _selectedAccountId = null;
        _principalCtrl.clear();
        _rateCtrl.text = '12';
        _termCtrl.text = '6';
        _purposeCtrl.clear();
        _method = 'FLAT';
      });
    }
  }

  Future<void> _rejectDialog(String loanId) async {
    final reason = await showDialog<String>(
      context: context,
      builder: (_) => const _LoanReasonDialog(
        title: 'Rechazar préstamo',
        actionLabel: 'Rechazar',
        hintText: 'Motivo del rechazo (opcional)',
      ),
    );
    if (reason != null) {
      await _vm.rejectLoan(loanId, reason: reason);
    }
  }

  Future<void> _payDialog(String loanId, double outstanding) async {
    final amount = await showDialog<double>(
      context: context,
      builder: (_) => _LoanAmountDialog(initialAmount: outstanding),
    );
    if (amount != null && amount > 0) {
      await _vm.payLoan(loanId, amount);
    }
  }

  Color _statusBg(String status) {
    switch (status.toUpperCase()) {
      case 'REQUESTED':
        return const Color(0xFFFFF8E1);
      case 'APPROVED':
        return const Color(0xFFE8F5E9);
      case 'DISBURSED':
        return const Color(0xFFE3F2FD);
      case 'REJECTED':
        return const Color(0xFFFFEBEE);
      case 'PAID_OFF':
        return const Color(0xFFF1F8E9);
      case 'CANCELLED':
        return const Color(0xFFFFF3E0);
      default:
        return const Color(0xFFF5F5F5);
    }
  }

  Color _statusFg(String status) {
    switch (status.toUpperCase()) {
      case 'REQUESTED':
        return const Color(0xFF8A6D00);
      case 'APPROVED':
        return const Color(0xFF2E7D32);
      case 'DISBURSED':
        return const Color(0xFF1565C0);
      case 'REJECTED':
        return const Color(0xFFC62828);
      case 'PAID_OFF':
        return const Color(0xFF558B2F);
      case 'CANCELLED':
        return const Color(0xFFEF6C00);
      default:
        return const Color(0xFF5F6F5F);
    }
  }

  String _statusLabel(String status) {
    switch (status.toUpperCase()) {
      case 'REQUESTED':
        return 'Solicitado';
      case 'APPROVED':
        return 'Aprobado';
      case 'REJECTED':
        return 'Rechazado';
      case 'DISBURSED':
        return 'Desembolsado';
      case 'PAID_OFF':
        return 'Pagado';
      case 'CANCELLED':
        return 'Cancelado';
      default:
        return status;
    }
  }

  Widget _statusChip(String status) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
      decoration: BoxDecoration(
        color: _statusBg(status),
        borderRadius: BorderRadius.circular(999),
      ),
      child: Text(
        _statusLabel(status),
        style: TextStyle(
          color: _statusFg(status),
          fontSize: 12,
          fontWeight: FontWeight.w700,
        ),
      ),
    );
  }

  Widget _metric(String label, String value) {
    return Container(
      constraints: const BoxConstraints(minWidth: 96),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: Colors.white.withValues(alpha: 0.16),
        borderRadius: BorderRadius.circular(16),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(label, style: const TextStyle(color: Colors.white70, fontSize: 11, fontWeight: FontWeight.w600)),
          const SizedBox(height: 2),
          Text(value, style: const TextStyle(color: Colors.white, fontSize: 16, fontWeight: FontWeight.bold)),
        ],
      ),
    );
  }

  Widget _buildHero() {
    final requested = _vm.loans.where((loan) => loan.status == 'REQUESTED').length;
    final disbursed = _vm.loans.where((loan) => loan.status == 'DISBURSED').length;
    final paid = _vm.loans.where((loan) => loan.status == 'PAID_OFF').length;
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(18),
      decoration: BoxDecoration(
        gradient: const LinearGradient(
          colors: [Color(0xFF1B5E20), Color(0xFF2E7D32), Color(0xFF66BB6A)],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(24),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            'Préstamos',
            style: TextStyle(color: Colors.white, fontSize: 24, fontWeight: FontWeight.w900),
          ),
          const SizedBox(height: 6),
          Text(
            'Gestiona solicitudes, desembolsos, pagos y cronogramas del tenant.',
            style: TextStyle(color: Colors.white.withValues(alpha: 0.9), fontSize: 13),
          ),
          const SizedBox(height: 14),
          Wrap(
            spacing: 8,
            runSpacing: 8,
            children: [
              _metric('Solicitados', '$requested'),
              _metric('Desembolsados', '$disbursed'),
              _metric('Pagados', '$paid'),
              _metric('Total', '${_vm.loans.length}'),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildForm() {
    return Card(
      margin: const EdgeInsets.only(top: 16, bottom: 16),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text(
                'Nuevo préstamo',
                style: TextStyle(fontSize: 16, fontWeight: FontWeight.w800, color: _green),
              ),
              const SizedBox(height: 12),
              DropdownButtonFormField<String>(
                initialValue: _selectedUserId,
                decoration: const InputDecoration(labelText: 'Usuario prestatario'),
                items: _vm.users
                    .map(
                      (user) => DropdownMenuItem<String>(
                        value: user.id,
                        child: Text(
                          '${_displayUserName(user)} · ${user.email}',
                          overflow: TextOverflow.ellipsis,
                        ),
                      ),
                    )
                    .toList(),
                onChanged: (value) {
                  setState(() {
                    _selectedUserId = value;
                    _selectedAccountId = null;
                  });
                },
                validator: (_) => _selectedUserId == null ? 'Selecciona un usuario' : null,
              ),
              const SizedBox(height: 12),
              DropdownButtonFormField<String>(
                initialValue: _selectedAccountId,
                decoration: const InputDecoration(labelText: 'Cuenta de desembolso'),
                items: _filteredAccounts
                    .map(
                      (account) => DropdownMenuItem<String>(
                        value: account.id,
                        child: Text(
                          '${account.accountNumber} · ${account.displayName} · ${account.currency}',
                          overflow: TextOverflow.ellipsis,
                        ),
                      ),
                    )
                    .toList(),
                onChanged: _selectedUserId == null
                    ? null
                    : (value) => setState(() => _selectedAccountId = value),
                validator: (_) => _selectedAccountId == null ? 'Selecciona una cuenta' : null,
              ),
              const SizedBox(height: 12),
              TextFormField(
                controller: _principalCtrl,
                keyboardType: const TextInputType.numberWithOptions(decimal: true),
                decoration: const InputDecoration(labelText: 'Monto'),
                validator: (value) => (double.tryParse(value?.trim() ?? '') ?? 0) <= 0 ? 'Monto inválido' : null,
              ),
              const SizedBox(height: 12),
              TextFormField(
                controller: _rateCtrl,
                keyboardType: const TextInputType.numberWithOptions(decimal: true),
                decoration: const InputDecoration(labelText: 'Tasa anual (%)'),
                validator: (value) => double.tryParse(value?.trim() ?? '') == null ? 'Tasa inválida' : null,
              ),
              const SizedBox(height: 12),
              TextFormField(
                controller: _termCtrl,
                keyboardType: TextInputType.number,
                decoration: const InputDecoration(labelText: 'Plazo (meses)'),
                validator: (value) => (int.tryParse(value?.trim() ?? '') ?? 0) <= 0 ? 'Plazo inválido' : null,
              ),
              const SizedBox(height: 12),
              DropdownButtonFormField<String>(
                initialValue: _method,
                decoration: const InputDecoration(labelText: 'Método de interés'),
                items: const [
                  DropdownMenuItem(value: 'FLAT', child: Text('Fijo (FLAT)')),
                  DropdownMenuItem(value: 'FRENCH', child: Text('Francés (cuota fija)')),
                ],
                onChanged: (value) => setState(() => _method = value ?? 'FLAT'),
              ),
              const SizedBox(height: 12),
              TextFormField(
                controller: _purposeCtrl,
                decoration: const InputDecoration(labelText: 'Propósito (opcional)'),
              ),
              const SizedBox(height: 16),
              SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  style: ElevatedButton.styleFrom(
                    backgroundColor: _green,
                    foregroundColor: Colors.white,
                    padding: const EdgeInsets.symmetric(vertical: 14),
                  ),
                  onPressed: _vm.isSubmitting ? null : _submit,
                  child: _vm.isSubmitting
                      ? const SizedBox(
                          height: 18,
                          width: 18,
                          child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white),
                        )
                      : const Text('Crear solicitud'),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  String _displayUserName(User user) {
    final fullName = user.fullName.trim();
    if (fullName.isNotEmpty) return fullName;
    return user.email;
  }

  Widget _buildLoanCard(LoanModel loan) {
    final expanded = _vm.expandedLoanId == loan.id;
    User? borrower;
    for (final user in _vm.users) {
      if (user.id == loan.userId) {
        borrower = user;
        break;
      }
    }
    Account? account;
    for (final item in _vm.accounts) {
      if (item.id == loan.accountId) {
        account = item;
        break;
      }
    }

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
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      _statusChip(loan.status),
                      const SizedBox(height: 8),
                      Text(
                        '${loan.principal.toStringAsFixed(2)} ${loan.currency}',
                        style: const TextStyle(fontSize: 18, fontWeight: FontWeight.w900, color: _green),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        '${loan.annualInterestRate}% · ${loan.termMonths} meses · ${loan.interestMethod}',
                        style: TextStyle(fontSize: 12, color: Colors.grey.shade600),
                      ),
                    ],
                  ),
                ),
                Column(
                  crossAxisAlignment: CrossAxisAlignment.end,
                  children: [
                    Text(
                      'Saldo',
                      style: TextStyle(fontSize: 11, color: Colors.grey.shade600),
                    ),
                    Text(
                      loan.outstandingBalance.toStringAsFixed(2),
                      style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: _green),
                    ),
                  ],
                ),
              ],
            ),
            const SizedBox(height: 10),
            Wrap(
              spacing: 8,
              runSpacing: 8,
              children: [
                _smallInfoChip('Usuario', borrower != null ? _displayUserName(borrower) : loan.userId),
                _smallInfoChip('Cuenta', account != null ? account.accountNumber : loan.accountId),
                _smallInfoChip('Total', loan.totalDue.toStringAsFixed(2)),
                _smallInfoChip('Pagado', loan.totalPaid.toStringAsFixed(2)),
              ],
            ),
            if ((loan.purpose ?? '').trim().isNotEmpty) ...[
              const SizedBox(height: 10),
              Text(
                loan.purpose!,
                style: TextStyle(color: Colors.grey.shade700, fontSize: 13),
              ),
            ],
            const SizedBox(height: 12),
            Wrap(
              spacing: 8,
              runSpacing: 8,
              crossAxisAlignment: WrapCrossAlignment.center,
              children: [
                OutlinedButton(
                  onPressed: _vm.isSubmitting ? null : () => _vm.toggleSchedule(loan.id),
                  style: OutlinedButton.styleFrom(
                    foregroundColor: _green,
                    side: const BorderSide(color: _green),
                  ),
                  child: Text(expanded ? 'Ocultar cuotas' : 'Ver cuotas'),
                ),
                if (loan.status == 'REQUESTED')
                  ElevatedButton(
                    onPressed: _vm.isSubmitting ? null : () => _vm.approveLoan(loan.id),
                    style: ElevatedButton.styleFrom(backgroundColor: _green, foregroundColor: Colors.white),
                    child: const Text('Aprobar'),
                  ),
                if (loan.status == 'REQUESTED')
                  ElevatedButton(
                    onPressed: _vm.isSubmitting ? null : () => _rejectDialog(loan.id),
                    style: ElevatedButton.styleFrom(backgroundColor: Colors.red.shade700, foregroundColor: Colors.white),
                    child: const Text('Rechazar'),
                  ),
                if (loan.status == 'APPROVED')
                  ElevatedButton(
                    onPressed: _vm.isSubmitting ? null : () => _vm.disburseLoan(loan.id),
                    style: ElevatedButton.styleFrom(backgroundColor: _green, foregroundColor: Colors.white),
                    child: const Text('Desembolsar'),
                  ),
                if (loan.status == 'DISBURSED')
                  ElevatedButton(
                    onPressed: _vm.isSubmitting ? null : () => _payDialog(loan.id, loan.outstandingBalance),
                    style: ElevatedButton.styleFrom(backgroundColor: _green, foregroundColor: Colors.white),
                    child: const Text('Pagar'),
                  ),
              ],
            ),
            if (expanded) ...[
              const SizedBox(height: 16),
              Container(
                width: double.infinity,
                padding: const EdgeInsets.all(14),
                decoration: BoxDecoration(
                  color: const Color(0xFFF7FBF6),
                  borderRadius: BorderRadius.circular(16),
                  border: Border.all(color: const Color(0xFFDDEED8)),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text(
                      'Cronograma',
                      style: TextStyle(fontWeight: FontWeight.w800, color: _green),
                    ),
                    const SizedBox(height: 10),
                    if (_vm.schedule.isEmpty)
                      const Text('No hay cuotas para mostrar.')
                    else
                      Column(
                        children: _vm.schedule.map((installment) {
                          return Padding(
                            padding: const EdgeInsets.only(bottom: 8),
                            child: Container(
                              padding: const EdgeInsets.all(12),
                              decoration: BoxDecoration(
                                color: Colors.white,
                                borderRadius: BorderRadius.circular(14),
                                border: Border.all(color: const Color(0xFFE5EFE3)),
                              ),
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Row(
                                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                    children: [
                                      Text(
                                        'Cuota ${installment.number}',
                                        style: const TextStyle(fontWeight: FontWeight.w700),
                                      ),
                                      _statusChip(installment.status),
                                    ],
                                  ),
                                  const SizedBox(height: 6),
                                  Text('Vence: ${installment.dueDate}'),
                                  const SizedBox(height: 4),
                                  Text(
                                    'Capital ${installment.principalDue.toStringAsFixed(2)} · Interés ${installment.interestDue.toStringAsFixed(2)} · Total ${installment.totalDue.toStringAsFixed(2)}',
                                    style: TextStyle(color: Colors.grey.shade700, fontSize: 12),
                                  ),
                                  const SizedBox(height: 2),
                                  Text(
                                    'Pagado ${installment.paidAmount.toStringAsFixed(2)}',
                                    style: TextStyle(color: Colors.grey.shade600, fontSize: 12),
                                  ),
                                ],
                              ),
                            ),
                          );
                        }).toList(),
                      ),
                  ],
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }

  Widget _smallInfoChip(String label, String value) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 7),
      decoration: BoxDecoration(
        color: const Color(0xFFF5F9F4),
        borderRadius: BorderRadius.circular(999),
      ),
      child: Text(
        '$label: $value',
        style: TextStyle(color: Colors.grey.shade700, fontSize: 12, fontWeight: FontWeight.w600),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Préstamos'),
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
              onRefresh: _vm.refresh,
              child: ListView(
                padding: const EdgeInsets.all(16),
                physics: const AlwaysScrollableScrollPhysics(),
                children: [
                  _buildHero(),
                  if (_showForm) _buildForm(),
                  const SizedBox(height: 16),
                  if (_vm.loans.isEmpty)
                    const Padding(
                      padding: EdgeInsets.only(top: 40),
                      child: Center(child: Text('No hay préstamos.')),
                    )
                  else
                    ..._vm.loans.map(_buildLoanCard),
                ],
              ),
            ),
    );
  }
}

class _LoanAmountDialog extends StatefulWidget {
  final double initialAmount;

  const _LoanAmountDialog({required this.initialAmount});

  @override
  State<_LoanAmountDialog> createState() => _LoanAmountDialogState();
}

class _LoanAmountDialogState extends State<_LoanAmountDialog> {
  final _formKey = GlobalKey<FormState>();
  late final TextEditingController _controller;

  @override
  void initState() {
    super.initState();
    _controller = TextEditingController(text: widget.initialAmount.toStringAsFixed(2));
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text('Registrar pago'),
      content: Form(
        key: _formKey,
        child: TextFormField(
          controller: _controller,
          keyboardType: const TextInputType.numberWithOptions(decimal: true),
          decoration: const InputDecoration(labelText: 'Monto'),
          validator: (value) => (double.tryParse(value?.trim() ?? '') ?? 0) <= 0 ? 'Monto inválido' : null,
        ),
      ),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(context),
          child: const Text('Cancelar'),
        ),
        ElevatedButton(
          onPressed: () {
            if (!_formKey.currentState!.validate()) return;
            Navigator.pop(context, double.parse(_controller.text.trim()));
          },
          child: const Text('Pagar'),
        ),
      ],
    );
  }
}

class _LoanReasonDialog extends StatefulWidget {
  final String title;
  final String actionLabel;
  final String hintText;

  const _LoanReasonDialog({
    required this.title,
    required this.actionLabel,
    required this.hintText,
  });

  @override
  State<_LoanReasonDialog> createState() => _LoanReasonDialogState();
}

class _LoanReasonDialogState extends State<_LoanReasonDialog> {
  final _formKey = GlobalKey<FormState>();
  late final TextEditingController _controller;

  @override
  void initState() {
    super.initState();
    _controller = TextEditingController();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: Text(widget.title),
      content: Form(
        key: _formKey,
        child: TextFormField(
          controller: _controller,
          decoration: InputDecoration(labelText: widget.hintText),
          maxLines: 2,
        ),
      ),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(context),
          child: const Text('Cancelar'),
        ),
        ElevatedButton(
          onPressed: () {
            if (!_formKey.currentState!.validate()) return;
            Navigator.pop(context, _controller.text.trim());
          },
          child: Text(widget.actionLabel),
        ),
      ],
    );
  }
}
