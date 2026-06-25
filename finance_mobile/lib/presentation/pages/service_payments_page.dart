import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import '../../../../core/di/injection_container.dart' as di;
import '../../../../core/network/api_client.dart';
import '../../domain/entities/service_bill.dart';
import '../../domain/entities/service_bills_query_result.dart';
import '../../domain/entities/service_enrollment.dart';
import '../../domain/entities/service_payment.dart';
import '../../domain/entities/service_provider.dart';
import '../viewmodels/service_payments_viewmodel.dart';
import '../widgets/service_payment_detail_card.dart';

class ServicePaymentsPage extends StatefulWidget {
  const ServicePaymentsPage({super.key});

  @override
  State<ServicePaymentsPage> createState() => _ServicePaymentsPageState();
}

class _ServicePaymentsPageState extends State<ServicePaymentsPage> {
  late ServicePaymentsViewModel _viewModel;
  late ApiClient _apiClient;

  final _providerSearchController = TextEditingController();
  final _serviceCodeController = TextEditingController();
  final _aliasController = TextEditingController();
  final _manualCodeController = TextEditingController();

  String _section = 'affiliate';
  String? _selectedProviderId;
  String? _selectedBillId;
  String? _selectedSourceAccountNumber;

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<ServicePaymentsViewModel>();
    _apiClient = di.sl<ApiClient>();
    _viewModel.addListener(_onViewModelChanged);

    WidgetsBinding.instance.addPostFrameCallback((_) async {
      if (!mounted) {
        return;
      }
      await _viewModel.loadData();
      if (!mounted) {
        return;
      }
      if (_selectedSourceAccountNumber == null && _viewModel.accounts.isNotEmpty) {
        setState(() {
          _selectedSourceAccountNumber = _viewModel.accounts.first.accountNumber;
        });
      }
    });
  }

  @override
  void dispose() {
    _providerSearchController.dispose();
    _serviceCodeController.dispose();
    _aliasController.dispose();
    _manualCodeController.dispose();
    _viewModel.removeListener(_onViewModelChanged);
    super.dispose();
  }

  void _onViewModelChanged() {
    if (!mounted) {
      return;
    }

    if (_viewModel.errorMessage != null) {
      _showSnackBar(_viewModel.errorMessage!);
      _viewModel.clearError();
    }
    if (_viewModel.enrollmentCreated) {
      _viewModel.clearEnrollmentCreated();
      _showSnackBar('Servicio afiliado correctamente', isError: false);
    }
    if (_viewModel.enrollmentDeleted) {
      _viewModel.clearEnrollmentDeleted();
      _showSnackBar('Afiliación eliminada', isError: false);
    }
    if (_viewModel.paymentCreated) {
      _viewModel.clearPaymentCreated();
      _showSnackBar('Pago completado correctamente', isError: false);
    }
    setState(() {});
  }

  void _showSnackBar(String message, {bool isError = true}) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: isError ? Colors.red.shade700 : Colors.green.shade700,
      ),
    );
  }

  bool get _canReadEnrollments => _apiClient.hasPermission('me.service-enrollments.read');
  bool get _canCreateEnrollment => _apiClient.hasPermission('me.service-enrollments.create');
  bool get _canDeleteEnrollment => _apiClient.hasPermission('me.service-enrollments.delete');
  bool get _canQueryBills => _apiClient.hasPermission('me.service-bills.query');
  bool get _canReadPayments => _apiClient.hasPermission('me.service-payments.read');

  List<ServiceProvider> get _filteredProviders {
    final query = _providerSearchController.text.trim().toLowerCase();
    if (query.isEmpty) {
      return _viewModel.providers;
    }
    return _viewModel.providers.where((provider) {
      return provider.name.toLowerCase().contains(query) ||
          provider.code.toLowerCase().contains(query) ||
          provider.displayCategory.toLowerCase().contains(query);
    }).toList();
  }

  ServiceProvider? _providerById(String? providerId) {
    if (providerId == null) {
      return null;
    }
    for (final provider in _viewModel.providers) {
      if (provider.id == providerId) {
        return provider;
      }
    }
    return null;
  }

  List<String> _serviceCustomerCodeOptionsForProvider(String? providerId) {
    if (providerId == null || providerId.isEmpty) {
      return const [];
    }

    final catalogProvider = _viewModel.providerCatalog
        .where((entry) => entry.provider.id == providerId)
        .toList();
    final codes = catalogProvider.isNotEmpty
        ? catalogProvider
            .expand((entry) => entry.serviceCustomerCodes)
            .where((code) => code.isNotEmpty)
            .toSet()
            .toList()
        : _viewModel.enrollments
            .where((enrollment) => enrollment.provider.id == providerId)
            .map((enrollment) => enrollment.serviceCustomerCode.trim())
            .where((code) => code.isNotEmpty)
            .toSet()
            .toList();
    codes.sort();

    return codes;
  }

  String _formatDate(DateTime? date) {
    if (date == null) {
      return 'Sin fecha';
    }
    final local = date.toLocal();
    final day = local.day.toString().padLeft(2, '0');
    final month = local.month.toString().padLeft(2, '0');
    final year = local.year;
    return '$day/$month/$year';
  }

  Future<void> _openEnrollmentDialog(ServiceProvider provider) async {
    if (!_canCreateEnrollment) {
      _showSnackBar('No tienes permisos para afiliar servicios');
      return;
    }

    final aliasController = TextEditingController();
    final codeOptions = _serviceCustomerCodeOptionsForProvider(provider.id);
    final initialCode = _serviceCodeController.text.trim();
    String codeMode = codeOptions.contains(initialCode) ? initialCode : '__custom__';
    final manualCodeController = TextEditingController(
      text: codeMode == '__custom__' ? initialCode : codeMode,
    );

    try {
      await showModalBottomSheet<void>(
        context: context,
        isScrollControlled: true,
        shape: const RoundedRectangleBorder(
          borderRadius: BorderRadius.vertical(top: Radius.circular(28)),
        ),
        builder: (context) {
          return StatefulBuilder(
            builder: (context, setSheetState) {
            final useCustomCode = codeMode == '__custom__' || codeOptions.isEmpty;
              final currentCode = useCustomCode ? manualCodeController.text.trim() : codeMode;
              return Padding(
                padding: EdgeInsets.only(
                  left: 16,
                  right: 16,
                  top: 20,
                  bottom: MediaQuery.of(context).viewInsets.bottom + 20,
                ),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                  Text(
                    'Afiliar servicio',
                    style: Theme.of(context).textTheme.titleLarge?.copyWith(
                          fontWeight: FontWeight.bold,
                          color: const Color(0xFF2E7D32),
                        ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    provider.name,
                    style: const TextStyle(fontWeight: FontWeight.w600),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    'Código cliente: ${provider.serviceCustomerCodeLabel ?? 'Código'}',
                    style: TextStyle(color: Colors.grey.shade700),
                  ),
                  const SizedBox(height: 16),
                  if (codeOptions.isNotEmpty)
                    DropdownButtonFormField<String>(
                      key: ValueKey('service-code-${provider.id}-$codeMode'),
                      initialValue: codeMode == '__custom__' ? '__custom__' : codeMode,
                      isExpanded: true,
                      decoration: InputDecoration(
                        labelText: provider.serviceCustomerCodeLabel ?? 'Código de servicio',
                        border: const OutlineInputBorder(),
                        prefixIcon: const Icon(Icons.confirmation_number_outlined),
                      ),
                      items: [
                        ...codeOptions.map(
                          (code) => DropdownMenuItem(
                            value: code,
                            child: Text(
                              code,
                              overflow: TextOverflow.ellipsis,
                            ),
                          ),
                        ),
                        const DropdownMenuItem(
                          value: '__custom__',
                          child: Text('Otro código...'),
                        ),
                      ],
                      onChanged: (value) {
                        setSheetState(() {
                          codeMode = value ?? '__custom__';
                          if (codeMode != '__custom__') {
                            manualCodeController.text = codeMode;
                          }
                        });
                      },
                    )
                  else
                    TextField(
                      controller: manualCodeController,
                      decoration: InputDecoration(
                        labelText: provider.serviceCustomerCodeLabel ?? 'Código de servicio',
                        border: const OutlineInputBorder(),
                        prefixIcon: const Icon(Icons.confirmation_number_outlined),
                      ),
                    ),
                  if (codeOptions.isNotEmpty && useCustomCode) ...[
                    const SizedBox(height: 12),
                    TextField(
                      controller: manualCodeController,
                      decoration: InputDecoration(
                        labelText: provider.serviceCustomerCodeLabel ?? 'Código de servicio',
                        border: const OutlineInputBorder(),
                        prefixIcon: const Icon(Icons.confirmation_number_outlined),
                      ),
                    ),
                  ],
                  const SizedBox(height: 12),
                  TextField(
                    controller: aliasController,
                    decoration: const InputDecoration(
                      labelText: 'Alias opcional',
                      border: OutlineInputBorder(),
                      prefixIcon: Icon(Icons.label_outline),
                    ),
                  ),
                  const SizedBox(height: 16),
                  SizedBox(
                    width: double.infinity,
                    child: ElevatedButton.icon(
                      onPressed: _viewModel.creatingEnrollment
                          ? null
                          : () async {
                              final navigator = Navigator.of(context);
                              final code = currentCode.trim();
                              if (code.isEmpty) {
                                _showSnackBar('Selecciona o ingresa el código del servicio');
                                return;
                              }
                              await _viewModel.createEnrollment(
                                providerId: provider.id,
                                serviceCustomerCode: code,
                                alias: aliasController.text.trim().isEmpty
                                    ? null
                                    : aliasController.text.trim(),
                              );
                              if (mounted && _viewModel.errorMessage == null) {
                                _serviceCodeController.text = code;
                                navigator.pop();
                              }
                            },
                      style: ElevatedButton.styleFrom(
                        backgroundColor: const Color(0xFF2E7D32),
                        foregroundColor: Colors.white,
                      ),
                      icon: _viewModel.creatingEnrollment
                          ? const SizedBox(
                              width: 18,
                              height: 18,
                              child: CircularProgressIndicator(
                                strokeWidth: 2,
                                color: Colors.white,
                              ),
                            )
                          : const Icon(Icons.check_circle_outline),
                      label: Text(
                        _viewModel.creatingEnrollment ? 'Afiliando...' : 'Afiliar',
                      ),
                    ),
                  ),
                ],
                ),
              );
            },
          );
        },
      );
    } finally {
      aliasController.dispose();
      manualCodeController.dispose();
    }
  }

  Future<void> _openProviderSelectionSheet() async {
    if (!_canCreateEnrollment) {
      _showSnackBar('No tienes permisos para afiliar servicios');
      return;
    }

    await showModalBottomSheet<void>(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (context) {
        return DraggableScrollableSheet(
          initialChildSize: 0.9,
          minChildSize: 0.62,
          maxChildSize: 0.96,
          builder: (context, scrollController) {
            return Container(
              decoration: const BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.vertical(top: Radius.circular(28)),
              ),
              child: SafeArea(
                top: false,
                child: Column(
                  children: [
                    const SizedBox(height: 10),
                    Container(
                      width: 42,
                      height: 4,
                      decoration: BoxDecoration(
                        color: Colors.grey.shade300,
                        borderRadius: BorderRadius.circular(99),
                      ),
                    ),
                    Padding(
                      padding: const EdgeInsets.fromLTRB(18, 16, 18, 10),
                      child: Row(
                        children: [
                          const Expanded(
                            child: Text(
                              'Afiliar servicio',
                              style: TextStyle(
                                fontSize: 18,
                                fontWeight: FontWeight.bold,
                                color: Color(0xFF2E7D32),
                              ),
                            ),
                          ),
                          IconButton(
                            onPressed: () => Navigator.of(context).pop(),
                            icon: const Icon(Icons.close),
                          ),
                        ],
                      ),
                    ),
                    const Divider(height: 1),
                    Expanded(
                      child: ListView(
                        controller: scrollController,
                        padding: const EdgeInsets.all(16),
                        children: [
                          TextField(
                            controller: _providerSearchController,
                            onChanged: (_) => setState(() {}),
                            decoration: InputDecoration(
                              labelText: 'Buscar proveedor',
                              prefixIcon: const Icon(Icons.search),
                              border: OutlineInputBorder(
                                borderRadius: BorderRadius.circular(16),
                              ),
                            ),
                          ),
                          const SizedBox(height: 12),
                          _buildProviderGrid(inSheet: true),
                        ],
                      ),
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

  Future<void> _openManualPaymentSheet() async {
    if (!_canQueryBills) {
      _showSnackBar('No tienes permisos para consultar deudas');
      return;
    }

    await showModalBottomSheet<void>(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (context) {
        return DraggableScrollableSheet(
          initialChildSize: 0.9,
          minChildSize: 0.62,
          maxChildSize: 0.96,
          builder: (context, scrollController) {
            return StatefulBuilder(
              builder: (context, setSheetState) {
                return Container(
                  decoration: const BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.vertical(top: Radius.circular(28)),
                  ),
                  child: SafeArea(
                    top: false,
                    child: Column(
                      children: [
                        const SizedBox(height: 10),
                        Container(
                          width: 42,
                          height: 4,
                          decoration: BoxDecoration(
                            color: Colors.grey.shade300,
                            borderRadius: BorderRadius.circular(99),
                          ),
                        ),
                        Padding(
                          padding: const EdgeInsets.fromLTRB(18, 16, 18, 10),
                          child: Row(
                            children: [
                              const Expanded(
                                child: Text(
                                  'Pago por código',
                                  style: TextStyle(
                                    fontSize: 18,
                                    fontWeight: FontWeight.bold,
                                    color: Color(0xFF2E7D32),
                                  ),
                                ),
                              ),
                              IconButton(
                                onPressed: () => Navigator.of(context).pop(),
                                icon: const Icon(Icons.close),
                              ),
                            ],
                          ),
                        ),
                        const Divider(height: 1),
                        Expanded(
                          child: ListView(
                            controller: scrollController,
                            padding: const EdgeInsets.all(16),
                            children: [
                              _buildSheetBillsResults(
                                setSheetState: setSheetState,
                              ),
                            ],
                          ),
                        ),
                      ],
                    ),
                  ),
                );
              },
            );
          },
        );
      },
    );
  }

  Future<void> _showPaymentDetail(ServicePayment payment) async {
    await showDialog<void>(
      context: context,
      builder: (context) {
        return Dialog(
          insetPadding: const EdgeInsets.all(16),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(28)),
          child: ConstrainedBox(
            constraints: BoxConstraints(
              maxWidth: 560,
              maxHeight: MediaQuery.of(context).size.height * 0.9,
            ),
            child: SafeArea(
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Padding(
                    padding: const EdgeInsets.fromLTRB(18, 16, 18, 10),
                    child: Row(
                      children: [
                        const Expanded(
                          child: Text(
                            'Detalle del pago',
                            style: TextStyle(
                              fontSize: 18,
                              fontWeight: FontWeight.bold,
                              color: Color(0xFF2E7D32),
                            ),
                          ),
                        ),
                        IconButton(
                          onPressed: () => Navigator.of(context).pop(),
                          icon: const Icon(Icons.close),
                        ),
                      ],
                    ),
                  ),
                  const Divider(height: 1),
                  Flexible(
                    child: SingleChildScrollView(
                      padding: const EdgeInsets.all(16),
                      child: Column(
                        children: [
                          ServicePaymentDetailCard(payment: payment),
                          const SizedBox(height: 12),
                          if (payment.transactionId != null && payment.transactionId!.isNotEmpty)
                            SizedBox(
                              width: double.infinity,
                              child: OutlinedButton.icon(
                                onPressed: () {
                                  Navigator.of(context).pop();
                                  context.push('/transactions/${payment.transactionId}');
                                },
                                icon: const Icon(Icons.open_in_new),
                                label: const Text('Ver transacción'),
                              ),
                            ),
                        ],
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
        );
      },
    );
  }

  Future<void> _closeSheetAndShowPaymentDetail() async {
    final payment = _viewModel.lastCreatedPayment;
    Navigator.of(context).pop();
    await Future.delayed(const Duration(milliseconds: 220));
    if (!mounted) {
      return;
    }
    if (payment != null) {
      await _showPaymentDetail(payment);
      _viewModel.clearLastCreatedPayment();
    }
  }

  Future<void> _consultManualBills() async {
    if (!_canQueryBills) {
      _showSnackBar('No tienes permisos para consultar deudas');
      return;
    }

    final providerId = _selectedProviderId;
    final code = _manualCodeController.text.trim();
    if (providerId == null || code.isEmpty) {
      _showSnackBar('Selecciona proveedor y escribe el código de servicio');
      return;
    }

    _selectedBillId = null;
    await _viewModel.queryBills(
      providerId: providerId,
      serviceCustomerCode: code,
    );
    if (!mounted) {
      return;
    }

    if (_viewModel.errorMessage != null) {
      return;
    }

    await _openManualPaymentSheet();
  }

  Future<void> _deleteEnrollment(ServiceEnrollment enrollment) async {
    if (!_canDeleteEnrollment) {
      _showSnackBar('No tienes permisos para eliminar afiliaciones');
      return;
    }

    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Eliminar afiliación'),
        content: Text(
          '¿Deseas eliminar ${enrollment.provider.name} - ${enrollment.serviceCustomerCode}?',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(false),
            child: const Text('Cancelar'),
          ),
          FilledButton(
            onPressed: () => Navigator.of(context).pop(true),
            child: const Text('Eliminar'),
          ),
        ],
      ),
    );

    if (confirmed == true) {
      await _viewModel.deleteEnrollment(enrollment.id);
    }
  }

  Future<void> _consultBillsFromEnrollment(ServiceEnrollment enrollment, {required bool paymentMode}) async {
    if (!_canQueryBills) {
      _showSnackBar('No tienes permisos para consultar deudas');
      return;
    }

    await _openAffiliateBillsSheet(enrollment: enrollment, paymentMode: paymentMode);
  }

  Future<void> _openAffiliateBillsSheet({
    required ServiceEnrollment enrollment,
    required bool paymentMode,
  }) async {
    await _viewModel.queryBills(enrollmentId: enrollment.id);
    if (!mounted) {
      return;
    }

    final query = _viewModel.currentBillsQuery;
    if (query == null) {
      return;
    }

    var selectedBillId = _preferredBillId(query.bills);
    var selectedAccountNumber = _selectedSourceAccountNumber ??
        (_viewModel.accounts.isNotEmpty ? _viewModel.accounts.first.accountNumber : null);

    await showModalBottomSheet<void>(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (context) {
        return DraggableScrollableSheet(
          initialChildSize: paymentMode ? 0.88 : 0.78,
          minChildSize: 0.52,
          maxChildSize: 0.95,
          builder: (context, scrollController) {
            return StatefulBuilder(
              builder: (context, setSheetState) {
                return Container(
                  decoration: const BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.vertical(top: Radius.circular(28)),
                  ),
                  child: SafeArea(
                    top: false,
                    child: Column(
                      children: [
                        const SizedBox(height: 10),
                        Container(
                          width: 42,
                          height: 4,
                          decoration: BoxDecoration(
                            color: Colors.grey.shade300,
                            borderRadius: BorderRadius.circular(99),
                          ),
                        ),
                        Padding(
                          padding: const EdgeInsets.fromLTRB(18, 16, 18, 10),
                          child: Row(
                            children: [
                              Expanded(
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Text(
                                      paymentMode ? 'Pagar' : 'Consultar',
                                      style: const TextStyle(
                                        fontSize: 18,
                                        fontWeight: FontWeight.bold,
                                        color: Color(0xFF2E7D32),
                                      ),
                                    ),
                                    const SizedBox(height: 4),
                                    Text(
                                      '${query.provider.name} · ${query.serviceCustomerCode}',
                                      style: TextStyle(color: Colors.grey.shade700),
                                    ),
                                  ],
                                ),
                              ),
                              IconButton(
                                onPressed: () => Navigator.of(context).pop(),
                                icon: const Icon(Icons.close),
                              ),
                            ],
                          ),
                        ),
                        const Divider(height: 1),
                        Expanded(
                          child: ListView(
                            controller: scrollController,
                            padding: const EdgeInsets.all(16),
                            children: [
                              if (query.bills.isEmpty)
                                const _EmptyCard(
                                  title: 'Sin deudas pendientes',
                                  message: 'No hay deudas disponibles para este servicio.',
                                )
                              else ...[
                                ...query.bills.map(
                                  (bill) => Padding(
                                    padding: const EdgeInsets.only(bottom: 10),
                                    child: _buildBillTile(
                                      bill,
                                      onTap: () {
                                        setSheetState(() {
                                          selectedBillId = bill.id;
                                        });
                                      },
                                      selected: selectedBillId == bill.id,
                                    ),
                                  ),
                                ),
                              ],
                              if (paymentMode && query.bills.isNotEmpty) ...[
                                const SizedBox(height: 10),
                                _buildSheetAccountSelector(
                                  selectedAccountNumber: selectedAccountNumber,
                                  onChanged: (value) {
                                    setSheetState(() {
                                      selectedAccountNumber = value;
                                    });
                                  },
                                ),
                                const SizedBox(height: 12),
                                SizedBox(
                                  width: double.infinity,
                                  child: ElevatedButton.icon(
                                    onPressed: _viewModel.creatingPayment
                                        ? null
                                        : () async {
                                            final selectedBill = selectedBillId;
                                            final accountNumber = selectedAccountNumber;
                                            if (selectedBill == null) {
                                              _showSnackBar('Selecciona una deuda');
                                              return;
                                            }
                                            if (accountNumber == null || accountNumber.isEmpty) {
                                              _showSnackBar('Selecciona una cuenta a debitar');
                                              return;
                                            }
                                            await _viewModel.createPayment(
                                              sourceAccountNumber: accountNumber,
                                              providerId: query.provider.id,
                                              serviceCustomerCode: query.serviceCustomerCode,
                                              billId: selectedBill,
                                              enrollmentId: enrollment.id,
                                              idempotencyKey: _generateIdempotencyKey(),
                                            );
                                            if (mounted && _viewModel.errorMessage == null) {
                                              await _closeSheetAndShowPaymentDetail();
                                            }
                                          },
                                    style: ElevatedButton.styleFrom(
                                      backgroundColor: const Color(0xFF2E7D32),
                                      foregroundColor: Colors.white,
                                      padding: const EdgeInsets.symmetric(vertical: 16),
                                      minimumSize: const Size.fromHeight(54),
                                    ),
                                    icon: _viewModel.creatingPayment
                                        ? const SizedBox(
                                            width: 18,
                                            height: 18,
                                            child: CircularProgressIndicator(
                                              strokeWidth: 2,
                                              color: Colors.white,
                                            ),
                                          )
                                        : const Icon(Icons.payments_outlined),
                                    label: Text(_viewModel.creatingPayment ? 'Procesando...' : 'Pagar'),
                                  ),
                                ),
                              ],
                            ],
                          ),
                        ),
                      ],
                    ),
                  ),
                );
              },
            );
          },
        );
      },
    );
  }

  String? _preferredBillId(List<ServiceBill> bills) {
    if (bills.isEmpty) {
      return null;
    }
    final pending = bills.where((bill) => bill.isPending).toList();
    return (pending.isNotEmpty ? pending.first : bills.first).id;
  }

  String _generateIdempotencyKey() {
    return 'sp_${DateTime.now().millisecondsSinceEpoch}_${DateTime.now().microsecond}';
  }

  @override
  Widget build(BuildContext context) {
    final isSmallScreen = MediaQuery.of(context).size.width < 520;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Pagos de servicios'),
        backgroundColor: Colors.white,
        elevation: 0,
        foregroundColor: const Color(0xFF2E7D32),
      ),
      body: RefreshIndicator(
        onRefresh: _viewModel.loadData,
        color: const Color(0xFF2E7D32),
        child: ListView(
          physics: const AlwaysScrollableScrollPhysics(),
          padding: const EdgeInsets.all(16),
          children: [
            _buildHeroCard(context, isSmallScreen),
            const SizedBox(height: 16),
            AnimatedSwitcher(
              duration: const Duration(milliseconds: 220),
              child: _section == 'affiliate'
                  ? _buildAffiliateSection(context)
                  : _buildPaymentSection(context),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildHeroCard(BuildContext context, bool isSmallScreen) {
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
        boxShadow: [
          BoxShadow(
            color: Colors.green.shade200.withValues(alpha: 0.45),
            blurRadius: 18,
            offset: const Offset(0, 10),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text(
                      'Pago de servicios',
                      style: TextStyle(
                        color: Colors.white,
                        fontSize: 24,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 6),
                    Text(
                      'Afiliaciones y pagos de servicios.',
                      style: TextStyle(
                        color: Colors.white.withValues(alpha: 0.9),
                        fontSize: 13,
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(width: 12),
              _buildMiniChip(
                label: 'Afiliaciones',
                value: '${_viewModel.enrollments.length}',
              ),
            ],
          ),
          const SizedBox(height: 14),
          Wrap(
            spacing: 10,
            runSpacing: 10,
            children: [
              _buildModeButton(
                label: 'Afiliaciones',
                selected: _section == 'affiliate',
                onTap: () => setState(() => _section = 'affiliate'),
              ),
              _buildModeButton(
                label: 'Pagos',
                selected: _section == 'payment',
                onTap: () => setState(() => _section = 'payment'),
              ),
            ],
          ),
          if (isSmallScreen) const SizedBox(height: 6),
        ],
      ),
    );
  }

  Widget _buildModeButton({
    required String label,
    required bool selected,
    required VoidCallback onTap,
  }) {
    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(999),
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 180),
        padding: const EdgeInsets.symmetric(horizontal: 18, vertical: 12),
        decoration: BoxDecoration(
          color: selected ? Colors.white : Colors.white.withValues(alpha: 0.12),
          borderRadius: BorderRadius.circular(999),
          border: Border.all(
            color: Colors.white.withValues(alpha: selected ? 0.95 : 0.45),
          ),
        ),
        child: Text(
          label,
          style: TextStyle(
            color: selected ? const Color(0xFF2E7D32) : Colors.white,
            fontWeight: FontWeight.w700,
          ),
        ),
      ),
    );
  }

  Widget _buildMiniChip({required String label, required String value}) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
      decoration: BoxDecoration(
        color: Colors.white.withValues(alpha: 0.16),
        borderRadius: BorderRadius.circular(16),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(
            label,
            style: const TextStyle(
              color: Colors.white70,
              fontSize: 11,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 2),
          Text(
            value,
            style: const TextStyle(
              color: Colors.white,
              fontSize: 17,
              fontWeight: FontWeight.bold,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildAffiliateSection(BuildContext context) {
    return Column(
      key: const ValueKey('affiliate-section'),
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        if (_canReadEnrollments) ...[
          _sectionHeader(
            title: 'Mis afiliaciones',
            subtitle: 'Servicios vinculados a tu cuenta.',
          ),
          const SizedBox(height: 12),
          SizedBox(
            width: double.infinity,
            child: ElevatedButton.icon(
              onPressed: _canCreateEnrollment ? _openProviderSelectionSheet : null,
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF2E7D32),
                foregroundColor: Colors.white,
                padding: const EdgeInsets.symmetric(vertical: 16),
                minimumSize: const Size.fromHeight(54),
              ),
              icon: const Icon(Icons.add_circle_outline),
              label: const Text('Afiliar servicio'),
            ),
          ),
          const SizedBox(height: 20),
          _buildEnrollmentList(),
          const SizedBox(height: 20),
        ],
        if (_canReadPayments) ...[
          _sectionHeader(
            title: 'Pagos recientes',
            subtitle: 'Últimos movimientos de pago de servicios.',
          ),
          const SizedBox(height: 12),
          _buildPaymentsList(),
          const SizedBox(height: 20),
        ],
      ],
    );
  }

  Widget _buildPaymentSection(BuildContext context) {
    return Column(
      key: const ValueKey('payment-section'),
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _sectionHeader(
          title: 'Pagos',
          subtitle: 'Consulta y paga servicios por código.',
        ),
        const SizedBox(height: 12),
        Card(
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                DropdownButtonFormField<String>(
                  key: ValueKey('main-provider-${_selectedProviderId ?? 'none'}'),
                  initialValue: _selectedProviderId,
                  isExpanded: true,
                  decoration: const InputDecoration(
                    labelText: 'Proveedor',
                    border: OutlineInputBorder(),
                  ),
                  items: _viewModel.providers
                      .map(
                        (provider) => DropdownMenuItem(
                          value: provider.id,
                          child: Text(
                            provider.name,
                            overflow: TextOverflow.ellipsis,
                          ),
                        ),
                      )
                      .toList(),
                  onChanged: (value) {
                    setState(() {
                      _selectedProviderId = value;
                      _selectedBillId = null;
                    });
                  },
                ),
                const SizedBox(height: 12),
                TextField(
                  controller: _manualCodeController,
                  decoration: InputDecoration(
                    labelText: _providerById(_selectedProviderId)?.serviceCustomerCodeLabel ??
                        'Código de servicio',
                    border: const OutlineInputBorder(),
                    prefixIcon: const Icon(Icons.confirmation_number_outlined),
                  ),
                ),
                const SizedBox(height: 14),
                SizedBox(
                  width: double.infinity,
                  child: ElevatedButton.icon(
                    onPressed: _canQueryBills ? _consultManualBills : null,
                    style: ElevatedButton.styleFrom(
                      backgroundColor: const Color(0xFF2E7D32),
                      foregroundColor: Colors.white,
                      padding: const EdgeInsets.symmetric(vertical: 16),
                      minimumSize: const Size.fromHeight(54),
                    ),
                    icon: const Icon(Icons.search),
                    label: const Text('Consultar'),
                  ),
                ),
              ],
            ),
          ),
        ),
        const SizedBox(height: 16),
        if (_canReadPayments) ...[
          _sectionHeader(
            title: 'Pagos recientes',
            subtitle: 'Últimos movimientos de pago de servicios.',
          ),
          const SizedBox(height: 12),
          _buildPaymentsList(),
        ],
      ],
    );
  }

  Widget _buildBillTile(
    ServiceBill bill, {
    bool selected = false,
    VoidCallback? onTap,
  }) {
    return InkWell(
      onTap: onTap ??
          () {
            setState(() {
              _selectedBillId = bill.id;
            });
          },
      borderRadius: BorderRadius.circular(16),
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 160),
        padding: const EdgeInsets.all(14),
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(16),
          border: Border.all(
            color: selected ? const Color(0xFF2E7D32) : Colors.grey.shade300,
            width: selected ? 1.8 : 1,
          ),
          color: selected ? Colors.green.shade50 : Colors.white,
        ),
        child: Row(
          children: [
            Icon(
              selected ? Icons.check_circle : Icons.receipt_long_outlined,
              color: selected ? const Color(0xFF2E7D32) : Colors.grey.shade500,
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    bill.billingPeriod,
                    style: const TextStyle(fontWeight: FontWeight.w700),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    'Vence ${_formatDate(bill.dueDate)} · ${bill.status}',
                    style: TextStyle(color: Colors.grey.shade700),
                  ),
                ],
              ),
            ),
            const SizedBox(width: 12),
            Text(
              bill.formattedAmount,
              style: const TextStyle(
                fontWeight: FontWeight.bold,
                color: Color(0xFF2E7D32),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSheetAccountSelector({
    required String? selectedAccountNumber,
    required ValueChanged<String?> onChanged,
  }) {
    if (_viewModel.loadingAccounts) {
      return const _LoadingCard(message: 'Cargando cuentas...');
    }

    if (_viewModel.accounts.isEmpty) {
      return const _EmptyCard(
        title: 'Sin cuentas disponibles',
        message: 'No tienes cuentas para debitar el pago.',
      );
    }

    return Card(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: DropdownButtonFormField<String>(
          key: ValueKey('sheet-account-${selectedAccountNumber ?? 'none'}'),
          initialValue: selectedAccountNumber,
          isExpanded: true,
          decoration: const InputDecoration(
            labelText: 'Cuenta a debitar',
            border: OutlineInputBorder(),
          ),
          items: _viewModel.accounts
              .map(
                (account) => DropdownMenuItem(
                  value: account.accountNumber,
                  child: Text(
                    '${account.displayName} - ${account.accountNumber} - ${account.formattedAvailableBalance}',
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
              )
              .toList(),
          onChanged: onChanged,
        ),
      ),
    );
  }

  Widget _buildSheetBillsResults({
    required StateSetter setSheetState,
  }) {
    final query = _viewModel.currentBillsQuery;
    if (_viewModel.queryingBills) {
      return const _LoadingCard(message: 'Consultando deudas...');
    }

    if (query == null) {
      return const _EmptyCard(
        title: 'Sin consulta activa',
        message: 'Completa proveedor y código para consultar.',
      );
    }

    var selectedBillId = _selectedBillId ?? _preferredBillId(query.bills);
    if (selectedBillId != _selectedBillId) {
      _selectedBillId = selectedBillId;
    }
    var selectedAccountNumber = _selectedSourceAccountNumber ??
        (_viewModel.accounts.isNotEmpty ? _viewModel.accounts.first.accountNumber : null);

    return Column(
      children: [
        _buildBillsResultCard(query),
        if (query.bills.isNotEmpty) ...[
          const SizedBox(height: 16),
          _buildSheetAccountSelector(
            selectedAccountNumber: selectedAccountNumber,
            onChanged: (value) {
              setSheetState(() {
                _selectedSourceAccountNumber = value;
              });
            },
          ),
          const SizedBox(height: 12),
          SizedBox(
            width: double.infinity,
            child: ElevatedButton.icon(
              onPressed: _viewModel.creatingPayment
                  ? null
                  : () async {
                      final selectedBill = selectedBillId;
                      final accountNumber = selectedAccountNumber;
                      if (selectedBill == null) {
                        _showSnackBar('Selecciona una deuda');
                        return;
                      }
                      if (accountNumber == null || accountNumber.isEmpty) {
                        _showSnackBar('Selecciona una cuenta a debitar');
                        return;
                      }
                      await _viewModel.createPayment(
                        sourceAccountNumber: accountNumber,
                        providerId: query.provider.id,
                        serviceCustomerCode: query.serviceCustomerCode,
                        billId: selectedBill,
                        enrollmentId: null,
                        idempotencyKey: _generateIdempotencyKey(),
                      );
                      if (mounted && _viewModel.errorMessage == null) {
                        await _closeSheetAndShowPaymentDetail();
                      }
                    },
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF2E7D32),
                foregroundColor: Colors.white,
                padding: const EdgeInsets.symmetric(vertical: 16),
                minimumSize: const Size.fromHeight(54),
              ),
              icon: _viewModel.creatingPayment
                  ? const SizedBox(
                      width: 18,
                      height: 18,
                      child: CircularProgressIndicator(
                        strokeWidth: 2,
                        color: Colors.white,
                      ),
                    )
                  : const Icon(Icons.payments_outlined),
              label: Text(_viewModel.creatingPayment ? 'Procesando...' : 'Pagar'),
            ),
          ),
        ],
      ],
    );
  }

  Widget _buildBillsResultCard(ServiceBillsQueryResult query) {
    final bills = query.bills;

    return Card(
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
                        query.provider.name,
                        style: const TextStyle(
                          fontSize: 17,
                          fontWeight: FontWeight.bold,
                          color: Color(0xFF2E7D32),
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        '${query.serviceCustomerCode} · ${query.serviceCustomerName}',
                        style: TextStyle(color: Colors.grey.shade700),
                      ),
                    ],
                  ),
                ),
                if (bills.isNotEmpty)
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
                    decoration: BoxDecoration(
                      color: Colors.green.shade50,
                      borderRadius: BorderRadius.circular(999),
                    ),
                    child: Text(
                      '${bills.length} deuda(s)',
                      style: TextStyle(
                        color: Colors.green.shade800,
                        fontWeight: FontWeight.w700,
                      ),
                    ),
                  ),
              ],
            ),
            const SizedBox(height: 14),
            if (bills.isEmpty)
              const _EmptyCard(
                title: 'Sin deudas pendientes',
                message: 'No hay deudas disponibles para este servicio.',
              )
            else
              Column(
                children: [
                  ...bills.map(
                    (bill) => Padding(
                      padding: const EdgeInsets.only(bottom: 10),
                      child: _buildBillTile(bill),
                    ),
                  ),
                ],
              ),
          ],
        ),
      ),
    );
  }

  Widget _buildPaymentsList() {
    if (_viewModel.loadingPayments) {
      return const _LoadingCard(message: 'Cargando pagos recientes...');
    }

    if (_viewModel.payments.isEmpty) {
      return const _EmptyCard(
        title: 'Sin pagos recientes',
        message: 'Cuando realices un pago, aparecerá aquí el historial.',
      );
    }

    return Column(
      children: _viewModel.payments
          .take(5)
          .map(
            (payment) => Padding(
              padding: const EdgeInsets.only(bottom: 10),
              child: _buildPaymentTile(payment),
            ),
          )
          .toList(),
    );
  }

  Widget _buildPaymentTile(ServicePayment payment) {
    return Card(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(18)),
      child: ListTile(
        leading: CircleAvatar(
          backgroundColor: payment.isCompleted ? Colors.green.shade100 : Colors.orange.shade100,
          child: Icon(
            Icons.receipt_long,
            color: payment.isCompleted ? Colors.green.shade700 : Colors.orange.shade700,
          ),
        ),
        title: Text(
          '${payment.provider.name} · ${payment.billingPeriod}',
          maxLines: 1,
          overflow: TextOverflow.ellipsis,
        ),
        subtitle: Text(
          '${payment.receiptNumber} · ${payment.sourceAccountNumber}',
          maxLines: 1,
          overflow: TextOverflow.ellipsis,
        ),
        trailing: Text(
          payment.formattedAmount,
          style: const TextStyle(
            fontWeight: FontWeight.bold,
            color: Color(0xFF2E7D32),
          ),
        ),
        onTap: payment.transactionId == null || payment.transactionId!.isEmpty
            ? null
            : () => context.push('/transactions/${payment.transactionId}'),
      ),
    );
  }

  Widget _buildProviderGrid({bool inSheet = false}) {
    if (_viewModel.loadingProviders) {
      return const _LoadingCard(message: 'Cargando proveedores...');
    }

    final providers = _filteredProviders;
    if (providers.isEmpty) {
      return const _EmptyCard(
        title: 'Sin proveedores',
        message: 'No hay proveedores para mostrar con ese filtro.',
      );
    }

    return LayoutBuilder(
      builder: (context, constraints) {
        final columns = constraints.maxWidth >= 720
            ? 3
            : constraints.maxWidth >= 500
                ? 2
                : 1;
        final spacing = 12.0;
        final itemWidth = (constraints.maxWidth - ((columns - 1) * spacing)) / columns;

        return Wrap(
          spacing: spacing,
          runSpacing: spacing,
          children: providers
              .map(
                (provider) => SizedBox(
                  width: itemWidth,
                  child: _buildProviderCard(provider, inSheet: inSheet),
                ),
              )
              .toList(),
        );
      },
    );
  }

  Widget _buildProviderCard(ServiceProvider provider, {bool inSheet = false}) {
    return Card(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
      elevation: 1.5,
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
                        provider.name,
                        style: const TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        provider.code,
                        style: TextStyle(color: Colors.grey.shade700),
                      ),
                    ],
                  ),
                ),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
                  decoration: BoxDecoration(
                    color: provider.isActive ? Colors.green.shade50 : Colors.grey.shade200,
                    borderRadius: BorderRadius.circular(999),
                  ),
                  child: Text(
                    provider.displayCategory,
                    style: TextStyle(
                      color: provider.isActive ? Colors.green.shade800 : Colors.grey.shade700,
                      fontWeight: FontWeight.w700,
                      fontSize: 11,
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 10),
            Text(
              'Código cliente: ${provider.serviceCustomerCodeLabel ?? 'Código'}',
              style: TextStyle(color: Colors.grey.shade700, fontSize: 12),
            ),
            const SizedBox(height: 14),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: _canCreateEnrollment ? () => _openEnrollmentDialog(provider) : null,
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFF2E7D32),
                  foregroundColor: Colors.white,
                ),
                child: Text(inSheet ? 'Afiliar' : 'Afiliar'),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildEnrollmentList() {
    if (_viewModel.loadingEnrollments) {
      return const _LoadingCard(message: 'Cargando afiliaciones...');
    }

    if (_viewModel.enrollments.isEmpty) {
      return const _EmptyCard(
        title: 'Sin afiliaciones',
        message: 'Afíliate a un proveedor para pagar más rápido.',
      );
    }

    return Column(
      children: _viewModel.enrollments
          .map(
            (enrollment) => Padding(
              padding: const EdgeInsets.only(bottom: 12),
              child: _buildEnrollmentCard(enrollment),
            ),
          )
          .toList(),
    );
  }

  Widget _buildEnrollmentCard(ServiceEnrollment enrollment) {
    return Card(
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
                        enrollment.provider.name,
                        style: const TextStyle(
                          fontWeight: FontWeight.bold,
                          fontSize: 16,
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        '${enrollment.serviceCustomerCode} · ${enrollment.serviceCustomerName}',
                        style: TextStyle(color: Colors.grey.shade700),
                      ),
                    ],
                  ),
                ),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
                  decoration: BoxDecoration(
                    color: enrollment.isActive ? Colors.green.shade50 : Colors.orange.shade50,
                    borderRadius: BorderRadius.circular(999),
                  ),
                  child: Text(
                    enrollment.status,
                    style: TextStyle(
                      color: enrollment.isActive ? Colors.green.shade800 : Colors.orange.shade800,
                      fontWeight: FontWeight.w700,
                      fontSize: 11,
                    ),
                  ),
                ),
              ],
            ),
            if ((enrollment.alias ?? '').isNotEmpty) ...[
              const SizedBox(height: 8),
              Text(
                enrollment.alias!,
                style: TextStyle(color: Colors.grey.shade600),
              ),
            ],
            const SizedBox(height: 14),
            Wrap(
              spacing: 10,
              runSpacing: 10,
              children: [
                OutlinedButton(
                  onPressed: _canQueryBills
                      ? () => _consultBillsFromEnrollment(enrollment, paymentMode: false)
                      : null,
                  child: const Text('Consultar'),
                ),
                ElevatedButton(
                  onPressed: _canQueryBills
                      ? () => _consultBillsFromEnrollment(enrollment, paymentMode: true)
                      : null,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color(0xFF2E7D32),
                    foregroundColor: Colors.white,
                  ),
                  child: const Text('Pagar'),
                ),
                if (_canDeleteEnrollment)
                  TextButton(
                    onPressed: _viewModel.deletingEnrollment ? null : () => _deleteEnrollment(enrollment),
                    style: TextButton.styleFrom(foregroundColor: Colors.red.shade700),
                    child: const Text('Eliminar'),
                  ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _sectionHeader({
    required String title,
    required String subtitle,
  }) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          title,
          style: const TextStyle(
            fontSize: 18,
            fontWeight: FontWeight.bold,
            color: Color(0xFF2E7D32),
          ),
        ),
        const SizedBox(height: 4),
        Text(
          subtitle,
          style: TextStyle(color: Colors.grey.shade700),
        ),
      ],
    );
  }
}

class _LoadingCard extends StatelessWidget {
  final String message;

  const _LoadingCard({required this.message});

  @override
  Widget build(BuildContext context) {
    return Card(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
      child: Padding(
        padding: const EdgeInsets.all(24),
        child: Row(
          children: [
            const SizedBox(
              width: 20,
              height: 20,
              child: CircularProgressIndicator(strokeWidth: 2),
            ),
            const SizedBox(width: 12),
            Expanded(child: Text(message)),
          ],
        ),
      ),
    );
  }
}

class _EmptyCard extends StatelessWidget {
  final String title;
  final String message;

  const _EmptyCard({
    required this.title,
    required this.message,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              title,
              style: const TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 6),
            Text(
              message,
              style: TextStyle(color: Colors.grey.shade700),
            ),
          ],
        ),
      ),
    );
  }
}
