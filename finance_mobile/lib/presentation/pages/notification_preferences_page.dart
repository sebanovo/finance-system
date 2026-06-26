import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../../core/di/injection_container.dart' as di;
import '../../domain/entities/notification_preference.dart';
import '../viewmodels/notification_preferences_viewmodel.dart';

const _green = Color(0xFF166534);
const _surface = Color(0xFFFFFFFF);
const _outline = Color(0xFFE5E7EB);
const _ink = Color(0xFF111827);

class NotificationPreferencesPage extends StatefulWidget {
  const NotificationPreferencesPage({super.key});

  @override
  State<NotificationPreferencesPage> createState() =>
      _NotificationPreferencesPageState();
}

class _NotificationPreferencesPageState
    extends State<NotificationPreferencesPage> {
  late NotificationPreferencesViewModel _viewModel;
  final Map<String, NotificationPreferenceDraft> _drafts = {};

  @override
  void initState() {
    super.initState();
    _viewModel = di.sl<NotificationPreferencesViewModel>();
    _viewModel.addListener(_onChanged);
    _viewModel.loadPreferences();
  }

  void _onChanged() {
    if (!mounted) return;
    if (_viewModel.errorMessage != null) {
      _showSnackBar(_viewModel.errorMessage!);
      _viewModel.clearMessages();
    }
    if (_viewModel.successMessage != null) {
      _showSnackBar(_viewModel.successMessage!, isError: false);
      _viewModel.clearMessages();
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

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Preferencias'),
        backgroundColor: _surface,
        elevation: 0,
        foregroundColor: _green,
        actions: [
          IconButton(
            icon: const Icon(Icons.notifications),
            tooltip: 'Notificaciones',
            onPressed: () => context.push('/notifications'),
          ),
        ],
      ),
      body: _buildBody(),
    );
  }

  Widget _buildBody() {
    if (_viewModel.loading && _viewModel.preferences.isEmpty) {
      return const Center(child: CircularProgressIndicator());
    }

    return RefreshIndicator(
      onRefresh: () => _viewModel.loadPreferences(),
      color: _green,
      child: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: _surface,
              borderRadius: BorderRadius.circular(24),
              border: Border.all(color: _outline),
            ),
            child: const Text(
              'Controla qué tipo de alertas quieres recibir por categoría.',
              style: TextStyle(color: _ink, fontSize: 15),
            ),
          ),
          const SizedBox(height: 16),
          ...NotificationPreferencesViewModel.categories.map((category) {
            final preference = _drafts[category] ??=
                NotificationPreferenceDraft.fromPreference(
                  _viewModel.preferenceFor(category),
                );
            return Padding(
              padding: const EdgeInsets.only(bottom: 12),
              child: _buildPreferenceCard(preference),
            );
          }),
        ],
      ),
    );
  }

  Widget _buildPreferenceCard(NotificationPreferenceDraft draft) {
    return Card(
      color: _surface,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(20),
        side: const BorderSide(color: _outline),
      ),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(_iconForCategory(draft.category), color: _green),
                const SizedBox(width: 10),
                Expanded(
                  child: Text(
                    _labelForCategory(draft.category),
                    style: const TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                      color: _ink,
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            _switchTile(
              title: 'In-app',
              subtitle: 'Se muestra dentro de la app',
              value: draft.inAppEnabled,
              onChanged: (value) => setState(() => draft.inAppEnabled = value),
            ),
            _switchTile(
              title: 'Push',
              subtitle: 'Notificación del dispositivo',
              value: draft.pushEnabled,
              onChanged: (value) => setState(() => draft.pushEnabled = value),
            ),
            _switchTile(
              title: 'Email',
              subtitle: 'Correo electrónico',
              value: draft.emailEnabled,
              onChanged: (value) => setState(() => draft.emailEnabled = value),
            ),
            _switchTile(
              title: 'SMS',
              subtitle: 'Mensajes de texto',
              value: draft.smsEnabled,
              onChanged: (value) => setState(() => draft.smsEnabled = value),
            ),
            const SizedBox(height: 8),
            Align(
              alignment: Alignment.centerRight,
              child: ElevatedButton(
                onPressed: _viewModel.saving
                    ? null
                    : () async {
                        await _viewModel.savePreference(draft.toPreference());
                      },
                style: ElevatedButton.styleFrom(
                  backgroundColor: _green,
                  foregroundColor: Colors.white,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(20),
                  ),
                ),
                child: const Text('Guardar'),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _switchTile({
    required String title,
    required String subtitle,
    required bool value,
    required ValueChanged<bool> onChanged,
  }) {
    return SwitchListTile(
      contentPadding: EdgeInsets.zero,
      title: Text(title),
      subtitle: Text(subtitle),
      value: value,
      onChanged: onChanged,
      activeThumbColor: _green,
    );
  }

  String _labelForCategory(String category) {
    switch (category) {
      case 'TRANSACTIONS':
        return 'Transacciones';
      case 'PAYMENTS':
        return 'Pagos';
      case 'ACCOUNTS':
        return 'Cuentas';
      case 'SECURITY':
        return 'Seguridad';
      case 'SYSTEM':
        return 'Sistema';
      case 'FX':
        return 'Divisas';
      case 'LIMITS':
        return 'Límites';
      default:
        return category;
    }
  }

  IconData _iconForCategory(String category) {
    switch (category) {
      case 'TRANSACTIONS':
        return Icons.swap_horiz;
      case 'PAYMENTS':
        return Icons.payments_outlined;
      case 'ACCOUNTS':
        return Icons.account_balance_wallet_outlined;
      case 'SECURITY':
        return Icons.security_outlined;
      case 'SYSTEM':
        return Icons.settings_outlined;
      case 'FX':
        return Icons.currency_exchange;
      case 'LIMITS':
        return Icons.gavel_outlined;
      default:
        return Icons.notifications_active_outlined;
    }
  }

  @override
  void dispose() {
    _viewModel.removeListener(_onChanged);
    super.dispose();
  }
}

class NotificationPreferenceDraft {
  final String category;
  bool pushEnabled;
  bool inAppEnabled;
  bool emailEnabled;
  bool smsEnabled;

  NotificationPreferenceDraft({
    required this.category,
    required this.pushEnabled,
    required this.inAppEnabled,
    required this.emailEnabled,
    required this.smsEnabled,
  });

  factory NotificationPreferenceDraft.fromPreference(
    NotificationPreference preference,
  ) {
    return NotificationPreferenceDraft(
      category: preference.category,
      pushEnabled: preference.pushEnabled,
      inAppEnabled: preference.inAppEnabled,
      emailEnabled: preference.emailEnabled,
      smsEnabled: preference.smsEnabled,
    );
  }

  NotificationPreference toPreference() {
    return NotificationPreference(
      id: '',
      userId: '',
      category: category,
      pushEnabled: pushEnabled,
      inAppEnabled: inAppEnabled,
      emailEnabled: emailEnabled,
      smsEnabled: smsEnabled,
    );
  }
}
