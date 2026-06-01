import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import '../../../../core/di/injection_container.dart' as di;
import '../../../../core/network/api_client.dart';
import '../../domain/entities/account.dart';
import '../../domain/entities/limit_evaluation.dart';
import '../../domain/entities/limit_rule.dart';
import '../viewmodels/limits_viewmodel.dart';

class LimitsPage extends StatefulWidget {
  const LimitsPage({super.key});

  @override
  State<LimitsPage> createState() => _LimitsPageState();
}

class _LimitsPageState extends State<LimitsPage> {
  late LimitsViewModel _viewModel;
  late ApiClient _apiClient;

  final _formKey = GlobalKey<FormState>();
  final _amountController = TextEditingController();
  final _targetAccountController = TextEditingController();
  String? _selectedSourceAccountId;
  String _selectedTransactionType = 'TRANSFER';

  static const List<String> _transactionTypes = [
    'TRANSFER',
    'DEPOSIT',
    'WITHDRAWAL',
    'PAYMENT',
    'REVERSAL',
    'REFUND',
    'FEE',
    'ADJUSTMENT',
    'HOLD',
    'RELEASE',
    'SETTLEMENT',
  ];

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<LimitsViewModel>();
    _apiClient = di.sl<ApiClient>();
    _viewModel.addListener(_onViewModelChanged);
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (!mounted) return;
      _viewModel.loadInitial();
    });
  }

  void _onViewModelChanged() {
    if (!mounted) return;
    if (_viewModel.errorMessage != null) {
      _showSnackBar(_viewModel.errorMessage!);
      _viewModel.clearError();
    }
    if (_viewModel.evaluationErrorMessage != null) {
      _showSnackBar(_viewModel.evaluationErrorMessage!);
      _viewModel.clearError();
    }
    setState(() {
      _selectedSourceAccountId ??= _viewModel.accounts.isNotEmpty ? _viewModel.accounts.first.id : null;
    });
  }

  void _showSnackBar(String message, {bool isError = true}) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: isError ? Colors.red.shade700 : Colors.green.shade700,
      ),
    );
  }

  Future<void> _onRefresh() async {
    await _viewModel.refresh();
  }

  Future<void> _evaluate() async {
    if (!_formKey.currentState!.validate()) return;
    final sourceAccountId = _selectedSourceAccountId;
    if (sourceAccountId == null || sourceAccountId.isEmpty) {
      _showSnackBar('Selecciona una cuenta origen');
      return;
    }

    await _viewModel.evaluateOperation(
      sourceAccountId: sourceAccountId,
      transactionType: _selectedTransactionType,
      amount: _amountController.text,
      targetAccountNumber: _selectedTransactionType == 'TRANSFER'
          ? _targetAccountController.text
          : null,
    );
  }

  bool get _canReadRules => _apiClient.hasPermission('limits.read');
  bool get _canEvaluate => _apiClient.hasPermission('limits.evaluate');

  @override
  Widget build(BuildContext context) {
    final canAccess = _canReadRules || _canEvaluate;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Límites'),
        backgroundColor: Colors.white,
        foregroundColor: const Color(0xFF2E7D32),
        elevation: 0,
        actions: [
          IconButton(
            icon: const Icon(Icons.notifications),
            tooltip: 'Notificaciones',
            onPressed: () => context.push('/notifications'),
          ),
        ],
      ),
      body: !canAccess
          ? _buildPermissionDenied()
          : RefreshIndicator(
              onRefresh: _onRefresh,
              color: const Color(0xFF2E7D32),
              child: ListView(
                physics: const AlwaysScrollableScrollPhysics(),
                padding: const EdgeInsets.all(16),
                children: [
                  _buildHero(),
                  const SizedBox(height: 16),
                  if (_canReadRules) ...[
                    _buildRulesSection(),
                    const SizedBox(height: 16),
                  ],
                  if (_canEvaluate) _buildEvaluatorSection(),
                  if (_viewModel.evaluation != null) ...[
                    const SizedBox(height: 16),
                    _buildEvaluationResult(_viewModel.evaluation!),
                  ],
                ],
              ),
            ),
    );
  }

  Widget _buildPermissionDenied() {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.lock_outline, size: 64, color: Colors.green.shade300),
            const SizedBox(height: 16),
            const Text(
              'No tienes permisos para ver límites',
              textAlign: TextAlign.center,
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 8),
            const Text(
              'Tu perfil no tiene acceso a la consulta o evaluación de límites.',
              textAlign: TextAlign.center,
              style: TextStyle(color: Colors.black54),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildHero() {
    final activeRules = _viewModel.rules.where((rule) => rule.active).length;
    final reviewRules = _viewModel.rules.where((rule) => rule.requireReviewExceed).length;

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
            'Límites',
            style: TextStyle(
              color: Colors.white,
              fontSize: 24,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 6),
          Text(
            'Consulta reglas activas y valida una operación sin enviar datos técnicos innecesarios.',
            style: TextStyle(
              color: Colors.white.withValues(alpha: 0.9),
              fontSize: 13,
            ),
          ),
          const SizedBox(height: 14),
          Wrap(
            spacing: 8,
            runSpacing: 8,
            children: [
              _heroChip('Reglas', '${_viewModel.rules.length}'),
              _heroChip('Activas', '$activeRules'),
              _heroChip('Revisión', '$reviewRules'),
              _heroChip('Cuentas', '${_viewModel.accounts.length}'),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildRulesSection() {
    final rules = _viewModel.rules;
    return _SectionCard(
      title: 'Reglas activas',
      subtitle: 'Solo lo más relevante para móvil',
      child: _viewModel.loading && rules.isEmpty
          ? const Center(child: CircularProgressIndicator())
          : rules.isEmpty
              ? const _EmptySection(message: 'No hay reglas de límites para mostrar')
              : Column(
                  children: rules
                      .take(6)
                      .map(
                        (rule) => Padding(
                          padding: const EdgeInsets.only(bottom: 10),
                          child: _LimitRuleCard(rule: rule),
                        ),
                      )
                      .toList(),
                ),
    );
  }

  Widget _buildEvaluatorSection() {
    return _SectionCard(
      title: 'Evaluar operación',
      subtitle: 'Simula una transacción antes de ejecutarla',
      child: Form(
        key: _formKey,
        child: Column(
          children: [
            DropdownButtonFormField<String>(
              initialValue: _selectedSourceAccountId,
              decoration: _inputDecoration('Cuenta origen'),
              items: _viewModel.accounts
                  .map(
                    (account) => DropdownMenuItem(
                      value: account.id,
                      child: Text(_accountLabel(account)),
                    ),
                  )
                  .toList(),
              onChanged: (value) {
                setState(() {
                  _selectedSourceAccountId = value;
                });
              },
              validator: (value) => value == null || value.isEmpty ? 'Selecciona una cuenta origen' : null,
            ),
            const SizedBox(height: 12),
            DropdownButtonFormField<String>(
              initialValue: _selectedTransactionType,
              decoration: _inputDecoration('Tipo de operación'),
              items: _transactionTypes
                  .map(
                    (type) => DropdownMenuItem(
                      value: type,
                      child: Text(_transactionTypeLabel(type)),
                    ),
                  )
                  .toList(),
              onChanged: (value) {
                setState(() {
                  _selectedTransactionType = value ?? 'TRANSFER';
                });
              },
            ),
            const SizedBox(height: 12),
            TextFormField(
              controller: _amountController,
              keyboardType: const TextInputType.numberWithOptions(decimal: true),
              decoration: _inputDecoration('Monto'),
              validator: (value) {
                final parsed = double.tryParse((value ?? '').replaceAll(',', '.'));
                if (parsed == null || parsed <= 0) {
                  return 'Ingresa un monto válido';
                }
                return null;
              },
            ),
            if (_selectedTransactionType == 'TRANSFER') ...[
              const SizedBox(height: 12),
              TextFormField(
                controller: _targetAccountController,
                keyboardType: TextInputType.number,
                decoration: _inputDecoration('Número de cuenta destino'),
                validator: (value) {
                  if (_selectedTransactionType != 'TRANSFER') return null;
                  if (value == null || value.trim().isEmpty) {
                    return 'Ingresa la cuenta destino';
                  }
                  return null;
                },
              ),
            ],
            const SizedBox(height: 16),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton.icon(
                onPressed: _viewModel.loadingEvaluation ? null : _evaluate,
                icon: _viewModel.loadingEvaluation
                    ? const SizedBox(
                        width: 16,
                        height: 16,
                        child: CircularProgressIndicator(
                          strokeWidth: 2,
                          color: Colors.white,
                        ),
                      )
                    : const Icon(Icons.play_arrow),
                label: Text(_viewModel.loadingEvaluation ? 'Evaluando...' : 'Evaluar'),
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFF2E7D32),
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 14),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildEvaluationResult(LimitEvaluation evaluation) {
    final checks = evaluation.checks.where((check) => check.matched).toList();

    return _SectionCard(
      title: 'Resultado',
      subtitle: 'Solo se muestran los datos útiles',
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Wrap(
            spacing: 8,
            runSpacing: 8,
            children: [
              _statusChip(
                evaluation.allowed ? 'Permitido' : 'Bloqueado',
                evaluation.allowed ? Colors.green : Colors.red,
              ),
              if (evaluation.requiresReview)
                _statusChip('Requiere revisión', Colors.orange),
              _statusChip(_transactionTypeLabel(evaluation.transactionType), Colors.blueGrey),
            ],
          ),
          const SizedBox(height: 12),
          Text(
            evaluation.reason,
            style: const TextStyle(fontWeight: FontWeight.w600),
          ),
          const SizedBox(height: 12),
          Text(
            '${evaluation.amount.toStringAsFixed(2)} ${evaluation.currency}',
            style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Color(0xFF1B5E20)),
          ),
          const SizedBox(height: 14),
          if (checks.isEmpty)
            const _EmptySection(message: 'No se aplicaron reglas específicas')
          else
            ExpansionTile(
              tilePadding: EdgeInsets.zero,
              childrenPadding: const EdgeInsets.only(top: 8),
              title: Text('Reglas evaluadas (${checks.length})'),
              children: checks
                  .take(5)
                  .map((check) => Padding(
                        padding: const EdgeInsets.only(bottom: 10),
                        child: _EvaluationCheckCard(check: check),
                      ))
                  .toList(),
            ),
        ],
      ),
    );
  }

  InputDecoration _inputDecoration(String label) {
    return InputDecoration(
      labelText: label,
      border: OutlineInputBorder(borderRadius: BorderRadius.circular(14)),
      focusedBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(14),
        borderSide: const BorderSide(color: Color(0xFF2E7D32)),
      ),
    );
  }

  Widget _heroChip(String label, String value) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
      decoration: BoxDecoration(
        color: Colors.white.withValues(alpha: 0.15),
        borderRadius: BorderRadius.circular(16),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(label, style: const TextStyle(color: Colors.white70, fontSize: 11)),
          const SizedBox(height: 2),
          Text(value, style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
        ],
      ),
    );
  }

  Widget _statusChip(String label, Color color) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
      decoration: BoxDecoration(
        color: color.withValues(alpha: 0.12),
        borderRadius: BorderRadius.circular(14),
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

  String _accountLabel(Account account) {
    return '${account.displayName} · ${account.accountNumber}';
  }

  String _transactionTypeLabel(String value) {
    switch (value) {
      case 'TRANSFER':
        return 'Transferencia';
      case 'DEPOSIT':
        return 'Depósito';
      case 'WITHDRAWAL':
        return 'Retiro';
      case 'PAYMENT':
        return 'Pago';
      case 'REVERSAL':
        return 'Reversa';
      case 'REFUND':
        return 'Reembolso';
      case 'FEE':
        return 'Tarifa';
      case 'ADJUSTMENT':
        return 'Ajuste';
      case 'HOLD':
        return 'Retención';
      case 'RELEASE':
        return 'Liberación';
      case 'SETTLEMENT':
        return 'Liquidación';
      default:
        return value;
    }
  }
}

class _SectionCard extends StatelessWidget {
  final String title;
  final String? subtitle;
  final Widget child;

  const _SectionCard({
    required this.title,
    required this.child,
    this.subtitle,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(22),
        border: Border.all(color: const Color(0xFFD7E7D6)),
        boxShadow: [
          BoxShadow(
            color: Colors.green.shade50,
            blurRadius: 10,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            title,
            style: const TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.bold,
              color: Color(0xFF1B5E20),
            ),
          ),
          if (subtitle != null) ...[
            const SizedBox(height: 4),
            Text(
              subtitle!,
              style: const TextStyle(color: Colors.black54, fontSize: 12),
            ),
          ],
          const SizedBox(height: 14),
          child,
        ],
      ),
    );
  }
}

class _EmptySection extends StatelessWidget {
  final String message;

  const _EmptySection({required this.message});

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
        color: const Color(0xFFF6FBF5),
        borderRadius: BorderRadius.circular(16),
      ),
      child: Text(
        message,
        style: const TextStyle(color: Colors.black54),
      ),
    );
  }
}

class _LimitRuleCard extends StatelessWidget {
  final LimitRule rule;

  const _LimitRuleCard({required this.rule});

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
        color: const Color(0xFFF9FBF8),
        borderRadius: BorderRadius.circular(18),
        border: Border.all(color: const Color(0xFFE0E9DE)),
      ),
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
                      rule.name,
                      style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      rule.description.isNotEmpty ? rule.description : rule.scopeDescription,
                      style: const TextStyle(fontSize: 12, color: Colors.black54),
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                    ),
                  ],
                ),
              ),
              const SizedBox(width: 10),
              _statusChip(rule.active ? 'Activa' : 'Inactiva', rule.active ? const Color(0xFF2E7D32) : Colors.grey),
            ],
          ),
          const SizedBox(height: 12),
          Wrap(
            spacing: 8,
            runSpacing: 8,
            children: [
              _ruleChip('Tipo', _limitTypeLabel(rule.limitType)),
              _ruleChip('Alcance', _scopeLabel(rule.scopeType)),
              _ruleChip('Periodo', _periodLabel(rule.period)),
              if (rule.currency != null) _ruleChip('Moneda', rule.currency!),
              if (rule.transactionType != null) _ruleChip('Operación', _transactionTypeLabel(rule.transactionType!)),
              if (rule.accountType != null) _ruleChip('Cuenta', _accountTypeLabel(rule.accountType!)),
            ],
          ),
          const SizedBox(height: 12),
          Text(
            _thresholdSummary(rule),
            style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w600, color: Color(0xFF1B5E20)),
          ),
        ],
      ),
    );
  }

  Widget _statusChip(String label, Color color) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
      decoration: BoxDecoration(
        color: color.withValues(alpha: 0.12),
        borderRadius: BorderRadius.circular(14),
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

  Widget _ruleChip(String label, String value) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 8),
      decoration: BoxDecoration(
        color: const Color(0xFFE8F5E9),
        borderRadius: BorderRadius.circular(14),
      ),
      child: Text(
        '$label: $value',
        style: const TextStyle(
          color: Color(0xFF1B5E20),
          fontSize: 11,
          fontWeight: FontWeight.w600,
        ),
      ),
    );
  }

  String _thresholdSummary(LimitRule rule) {
    if (rule.maxAmount != null && rule.maxCount != null) {
      return 'Máximo ${rule.maxAmount!.toStringAsFixed(2)} y ${rule.maxCount} operaciones';
    }
    if (rule.maxAmount != null) {
      return 'Máximo ${rule.maxAmount!.toStringAsFixed(2)}';
    }
    if (rule.maxCount != null) {
      return 'Máximo ${rule.maxCount} operaciones';
    }
    if (rule.minAmount != null) {
      return 'Mínimo ${rule.minAmount!.toStringAsFixed(2)}';
    }
    return 'Sin tope visible';
  }

  String _limitTypeLabel(String value) {
    switch (value) {
      case 'PER_TRANSACTION_AMOUNT':
        return 'Monto por operación';
      case 'DAILY_AMOUNT':
        return 'Monto diario';
      case 'MONTHLY_AMOUNT':
        return 'Monto mensual';
      case 'DAILY_COUNT':
        return 'Cantidad diaria';
      case 'MONTHLY_COUNT':
        return 'Cantidad mensual';
      case 'MIN_AMOUNT':
        return 'Monto mínimo';
      case 'MAX_BALANCE':
        return 'Saldo máximo';
      default:
        return value;
    }
  }

  String _scopeLabel(String value) {
    switch (value) {
      case 'TENANT':
        return 'Tenant';
      case 'ACCOUNT_TYPE':
        return 'Tipo de cuenta';
      case 'TRANSACTION_TYPE':
        return 'Tipo de transacción';
      case 'USER':
        return 'Usuario';
      case 'ACCOUNT':
        return 'Cuenta';
      default:
        return value;
    }
  }

  String _periodLabel(String value) {
    switch (value) {
      case 'TRANSACTION':
        return 'Operación';
      case 'DAILY':
        return 'Diario';
      case 'MONTHLY':
        return 'Mensual';
      default:
        return value;
    }
  }

  String _accountTypeLabel(String value) {
    switch (value) {
      case 'WALLET':
        return 'Billetera';
      case 'SAVINGS':
        return 'Ahorro';
      case 'CHECKING':
        return 'Corriente';
      case 'CREDIT_CARD':
        return 'Crédito';
      case 'PREPAID_CARD':
        return 'Prepago';
      case 'LOAN':
        return 'Préstamo';
      default:
        return value;
    }
  }

  String _transactionTypeLabel(String value) {
    switch (value) {
      case 'TRANSFER':
        return 'Transferencia';
      case 'DEPOSIT':
        return 'Depósito';
      case 'WITHDRAWAL':
        return 'Retiro';
      case 'PAYMENT':
        return 'Pago';
      case 'REVERSAL':
        return 'Reversa';
      case 'REFUND':
        return 'Reembolso';
      case 'FEE':
        return 'Tarifa';
      case 'ADJUSTMENT':
        return 'Ajuste';
      case 'HOLD':
        return 'Retención';
      case 'RELEASE':
        return 'Liberación';
      case 'SETTLEMENT':
        return 'Liquidación';
      default:
        return value;
    }
  }
}

class _EvaluationCheckCard extends StatelessWidget {
  final LimitEvaluationRuleCheck check;

  const _EvaluationCheckCard({required this.check});

  @override
  Widget build(BuildContext context) {
    final statusColor = check.allowed
        ? const Color(0xFF2E7D32)
        : (check.requiresReview ? Colors.orange : Colors.red);

    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: const Color(0xFFF9FBF8),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: const Color(0xFFE0E9DE)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Expanded(
                child: Text(
                  check.name,
                  style: const TextStyle(fontWeight: FontWeight.bold),
                ),
              ),
              _chip(check.allowed ? 'Permitida' : 'Bloqueada', statusColor),
            ],
          ),
          const SizedBox(height: 6),
          Text(
            '${_limitTypeLabel(check.limitType)} · ${_scopeLabel(check.scopeType)} · ${_periodLabel(check.period)}',
            style: const TextStyle(fontSize: 12, color: Colors.black54),
          ),
          const SizedBox(height: 6),
          Text(
            check.reason,
            style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w600),
          ),
          const SizedBox(height: 8),
          Text(
            _summary(check),
            style: const TextStyle(fontSize: 11, color: Colors.black54),
          ),
        ],
      ),
    );
  }

  Widget _chip(String label, Color color) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
      decoration: BoxDecoration(
        color: color.withValues(alpha: 0.12),
        borderRadius: BorderRadius.circular(14),
      ),
      child: Text(
        label,
        style: TextStyle(
          color: color,
          fontSize: 11,
          fontWeight: FontWeight.w700,
        ),
      ),
    );
  }

  String _summary(LimitEvaluationRuleCheck check) {
    if (check.maxAmount != null) {
      final current = check.currentAmount?.toStringAsFixed(2) ?? '0.00';
      final max = check.maxAmount!.toStringAsFixed(2);
      return 'Monto: $current / $max';
    }
    if (check.maxCount != null) {
      final current = check.currentCount?.toString() ?? '0';
      final max = check.maxCount.toString();
      return 'Cantidad: $current / $max';
    }
    return 'Sin métrica visible';
  }

  String _limitTypeLabel(String value) {
    switch (value) {
      case 'PER_TRANSACTION_AMOUNT':
        return 'Monto por operación';
      case 'DAILY_AMOUNT':
        return 'Monto diario';
      case 'MONTHLY_AMOUNT':
        return 'Monto mensual';
      case 'DAILY_COUNT':
        return 'Cantidad diaria';
      case 'MONTHLY_COUNT':
        return 'Cantidad mensual';
      case 'MIN_AMOUNT':
        return 'Monto mínimo';
      case 'MAX_BALANCE':
        return 'Saldo máximo';
      default:
        return value;
    }
  }

  String _scopeLabel(String value) {
    switch (value) {
      case 'TENANT':
        return 'Tenant';
      case 'ACCOUNT_TYPE':
        return 'Tipo de cuenta';
      case 'TRANSACTION_TYPE':
        return 'Tipo de transacción';
      case 'USER':
        return 'Usuario';
      case 'ACCOUNT':
        return 'Cuenta';
      default:
        return value;
    }
  }

  String _periodLabel(String value) {
    switch (value) {
      case 'TRANSACTION':
        return 'Operación';
      case 'DAILY':
        return 'Diario';
      case 'MONTHLY':
        return 'Mensual';
      default:
        return value;
    }
  }
}
