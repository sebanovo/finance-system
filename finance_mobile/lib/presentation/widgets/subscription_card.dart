import 'package:flutter/material.dart';
import 'package:skeletonizer/skeletonizer.dart';
import '../../domain/entities/subscription.dart';

class SubscriptionCard extends StatelessWidget {
  final Subscription? subscription;
  final String? errorMessage;
  final bool isLoading;

  const SubscriptionCard({
    super.key,
    this.subscription,
    this.errorMessage,
    this.isLoading = false,
  });

  @override
  Widget build(BuildContext context) {
    final screenWidth = MediaQuery.of(context).size.width;
    final isSmallScreen = screenWidth < 500;

    if (isLoading) {
      return Skeletonizer(
        enabled: true,
        child: _buildSkeletonContent(isSmallScreen),
      );
    }

    if (errorMessage != null) {
      return Card(
        color: Colors.red[50],
        child: Padding(
          padding: const EdgeInsets.all(12.0),
          child: Text(
            errorMessage!,
            style: const TextStyle(color: Colors.red),
          ),
        ),
      );
    }

    if (subscription == null) {
      return const SizedBox.shrink();
    }

    return _buildRealContent(subscription!, isSmallScreen);
  }

  Widget _buildSkeletonContent(bool isSmallScreen) {
    return Card(
      elevation: 4,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
      child: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            if (isSmallScreen)
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text(
                    'Plan Demo',
                    style: TextStyle(
                      fontSize: 22,
                      fontWeight: FontWeight.bold,
                      color: Color(0xFF2E7D32),
                    ),
                    overflow: TextOverflow.ellipsis,
                  ),
                  const SizedBox(height: 8),
                  Container(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 12,
                      vertical: 6,
                    ),
                    decoration: BoxDecoration(
                      color: Colors.green[100],
                      borderRadius: BorderRadius.circular(20),
                    ),
                    child: const Text('ACTIVO'),
                  ),
                ],
              )
            else
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  const Expanded(
                    child: Text(
                      'Plan Demo',
                      style: TextStyle(
                        fontSize: 22,
                        fontWeight: FontWeight.bold,
                        color: Color(0xFF2E7D32),
                      ),
                      overflow: TextOverflow.ellipsis,
                    ),
                  ),
                  Container(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 12,
                      vertical: 6,
                    ),
                    decoration: BoxDecoration(
                      color: Colors.green[100],
                      borderRadius: BorderRadius.circular(20),
                    ),
                    child: const Text('ACTIVO'),
                  ),
                ],
              ),
            const SizedBox(height: 12),
            _buildTrialBannerSkeleton(isSmallScreen),
            const SizedBox(height: 16),
            _buildInfoRow(Icons.category, 'Tipo de plan', 'Demo'),
            _buildInfoRow(Icons.people, 'Máx. usuarios', '2'),
            _buildInfoRow(Icons.security, 'Máx. roles', '2'),
            _buildInfoRow(Icons.calendar_today, 'Inicio', '10/4/2026'),
            _buildInfoRow(Icons.event_busy, 'Expira', '20/4/2026'),
          ],
        ),
      ),
    );
  }

  Widget _buildTrialBannerSkeleton(bool isSmallScreen) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(8),
      decoration: BoxDecoration(
        color: const Color(0xFFE8F5E9),
        borderRadius: BorderRadius.circular(12),
      ),
      child: isSmallScreen
          ? const Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    Icon(Icons.science, color: Color(0xFF2E7D32)),
                    SizedBox(width: 8),
                    Text('Período de prueba'),
                  ],
                ),
                SizedBox(height: 4),
                Text('9 días restantes'),
              ],
            )
          : const Row(
              children: [
                Icon(Icons.science, color: Color(0xFF2E7D32)),
                SizedBox(width: 8),
                Expanded(
                  child: Text(
                    'Período de prueba · 9 días restantes',
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
              ],
            ),
    );
  }

  Widget _buildRealContent(Subscription sub, bool isSmallScreen) {
    return Card(
      elevation: 4,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
      color: Colors.white,
      child: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            if (isSmallScreen)
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    sub.planName,
                    style: const TextStyle(
                      fontSize: 22,
                      fontWeight: FontWeight.bold,
                      color: Color(0xFF2E7D32),
                    ),
                    overflow: TextOverflow.ellipsis,
                  ),
                  const SizedBox(height: 8),
                  _buildStatusChip(sub.status),
                ],
              )
            else
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Expanded(
                    child: Text(
                      sub.planName,
                      style: const TextStyle(
                        fontSize: 22,
                        fontWeight: FontWeight.bold,
                        color: Color(0xFF2E7D32),
                      ),
                      overflow: TextOverflow.ellipsis,
                    ),
                  ),
                  const SizedBox(width: 8),
                  _buildStatusChip(sub.status),
                ],
              ),
            const SizedBox(height: 12),
            if (sub.trial) _buildTrialBanner(sub, isSmallScreen),
            const SizedBox(height: 16),
            _buildInfoRow(Icons.category, 'Tipo de plan', sub.planType),
            _buildInfoRow(Icons.people, 'Máx. usuarios', '${sub.maxUsers}'),
            _buildInfoRow(Icons.security, 'Máx. roles', '${sub.maxRoles}'),
            _buildInfoRow(
              Icons.calendar_today,
              'Inicio',
              _formatDate(sub.startedAt),
            ),
            _buildInfoRow(Icons.event_busy, 'Expira', _formatDate(sub.expiresAt)),
            if (sub.remainingDays < 0)
              Padding(
                padding: const EdgeInsets.only(top: 8),
                child: Text(
                  'Suscripción vencida',
                  style: TextStyle(color: Colors.red.shade700),
                ),
              ),
          ],
        ),
      ),
    );
  }

  Widget _buildStatusChip(String status) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
      decoration: BoxDecoration(
        color: status == 'ACTIVE' ? Colors.green[100] : Colors.orange[100],
        borderRadius: BorderRadius.circular(20),
      ),
      child: Text(
        status,
        style: TextStyle(
          color: status == 'ACTIVE' ? Colors.green[700] : Colors.orange[700],
          fontWeight: FontWeight.bold,
        ),
      ),
    );
  }

  Widget _buildTrialBanner(Subscription sub, bool isSmallScreen) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(8),
      decoration: BoxDecoration(
        color: const Color(0xFFE8F5E9),
        borderRadius: BorderRadius.circular(12),
      ),
      child: isSmallScreen
          ? Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Row(
                  children: [
                    Icon(Icons.science, color: Color(0xFF2E7D32)),
                    SizedBox(width: 8),
                    Text('Período de prueba'),
                  ],
                ),
                const SizedBox(height: 4),
                Text(
                  '${sub.remainingDays} días restantes',
                  style: const TextStyle(fontWeight: FontWeight.w500),
                ),
              ],
            )
          : Row(
              children: [
                const Icon(Icons.science, color: Color(0xFF2E7D32)),
                const SizedBox(width: 8),
                Expanded(
                  child: Text(
                    'Período de prueba · ${sub.remainingDays} días restantes',
                    style: const TextStyle(fontWeight: FontWeight.w500),
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
              ],
            ),
    );
  }

  Widget _buildInfoRow(IconData icon, String label, String value) {
    return LayoutBuilder(
      builder: (context, constraints) {
        final isCompact = constraints.maxWidth < 360;
        if (isCompact) {
          return Padding(
            padding: const EdgeInsets.symmetric(vertical: 8),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    Icon(icon, size: 20, color: const Color(0xFF4CAF50)),
                    const SizedBox(width: 8),
                    Expanded(
                      child: Text(
                        label,
                        style: const TextStyle(
                          fontWeight: FontWeight.w500,
                          color: Colors.grey,
                        ),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 6),
                Text(
                  value,
                  style: const TextStyle(fontWeight: FontWeight.w600),
                  overflow: TextOverflow.ellipsis,
                ),
              ],
            ),
          );
        }

        return Padding(
          padding: const EdgeInsets.symmetric(vertical: 8),
          child: Row(
            children: [
              Icon(icon, size: 20, color: const Color(0xFF4CAF50)),
              const SizedBox(width: 16),
              SizedBox(
                width: 100,
                child: Text(
                  label,
                  style: const TextStyle(
                    fontWeight: FontWeight.w500,
                    color: Colors.grey,
                  ),
                ),
              ),
              Expanded(
                child: Text(
                  value,
                  style: const TextStyle(fontWeight: FontWeight.w600),
                  overflow: TextOverflow.ellipsis,
                ),
              ),
            ],
          ),
        );
      },
    );
  }

  String _formatDate(DateTime date) {
    return '${date.day}/${date.month}/${date.year}';
  }
}
