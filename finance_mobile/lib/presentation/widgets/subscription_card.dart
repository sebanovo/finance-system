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
          child: Text(errorMessage!, style: const TextStyle(color: Colors.red)),
        ),
      );
    }

    if (subscription == null) {
      return const Center(child: Text('No hay información de suscripción'));
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
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text(
                  'Plan Demo',
                  style: TextStyle(
                    fontSize: 22,
                    fontWeight: FontWeight.bold,
                    color: Color(0xFF2E7D32),
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
            _buildSkeletonRow(Icons.category, 'Tipo de plan', 'Demo'),
            _buildSkeletonRow(Icons.people, 'Máx. usuarios', '2'),
            _buildSkeletonRow(Icons.security, 'Máx. roles', '2'),
            _buildSkeletonRow(Icons.calendar_today, 'Inicio', '10/4/2026'),
            _buildSkeletonRow(Icons.event_busy, 'Expira', '20/4/2026'),
          ],
        ),
      ),
    );
  }

  Widget _buildTrialBannerSkeleton(bool isSmallScreen) {
    if (isSmallScreen) {
      // ✅ Layout vertical para pantallas pequeñas
      return Container(
        padding: const EdgeInsets.all(8),
        decoration: BoxDecoration(
          color: const Color(0xFFE8F5E9),
          borderRadius: BorderRadius.circular(12),
        ),
        child: const Column(
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
        ),
      );
    } else {
      // ✅ Layout horizontal para pantallas grandes
      return Container(
        padding: const EdgeInsets.all(8),
        decoration: BoxDecoration(
          color: const Color(0xFFE8F5E9),
          borderRadius: BorderRadius.circular(12),
        ),
        child: const Row(
          children: [
            Icon(Icons.science, color: Color(0xFF2E7D32)),
            SizedBox(width: 8),
            Text('Período de prueba · 9 días restantes'),
          ],
        ),
      );
    }
  }

  Widget _buildSkeletonRow(IconData icon, String label, String value) {
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
              style: const TextStyle(fontWeight: FontWeight.w500),
            ),
          ),
          Expanded(
            child: Text(
              value,
              style: const TextStyle(fontWeight: FontWeight.w600),
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
                Container(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 12,
                    vertical: 6,
                  ),
                  decoration: BoxDecoration(
                    color: sub.status == 'ACTIVE'
                        ? Colors.green[100]
                        : Colors.orange[100],
                    borderRadius: BorderRadius.circular(20),
                  ),
                  child: Text(
                    sub.status,
                    style: TextStyle(
                      color: sub.status == 'ACTIVE'
                          ? Colors.green[700]
                          : Colors.orange[700],
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            if (sub.trial) _buildTrialBanner(sub, isSmallScreen),
            const SizedBox(height: 16),
            _infoRow(Icons.category, 'Tipo de plan', sub.planType),
            _infoRow(Icons.people, 'Máx. usuarios', '${sub.maxUsers}'),
            _infoRow(Icons.security, 'Máx. roles', '${sub.maxRoles}'),
            _infoRow(
              Icons.calendar_today,
              'Inicio',
              _formatDate(sub.startedAt),
            ),
            _infoRow(Icons.event_busy, 'Expira', _formatDate(sub.expiresAt)),
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

  Widget _buildTrialBanner(Subscription sub, bool isSmallScreen) {
    if (isSmallScreen) {
      // ✅ Layout vertical para pantallas pequeñas
      return Container(
        padding: const EdgeInsets.all(8),
        decoration: BoxDecoration(
          color: const Color(0xFFE8F5E9),
          borderRadius: BorderRadius.circular(12),
        ),
        child: Column(
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
        ),
      );
    } else {
      // ✅ Layout horizontal para pantallas grandes
      return Container(
        padding: const EdgeInsets.all(8),
        decoration: BoxDecoration(
          color: const Color(0xFFE8F5E9),
          borderRadius: BorderRadius.circular(12),
        ),
        child: Row(
          children: [
            const Icon(Icons.science, color: Color(0xFF2E7D32)),
            const SizedBox(width: 8),
            Text(
              'Período de prueba · ${sub.remainingDays} días restantes',
              style: const TextStyle(fontWeight: FontWeight.w500),
            ),
          ],
        ),
      );
    }
  }

  Widget _infoRow(IconData icon, String label, String value) {
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
  }

  String _formatDate(DateTime date) {
    return '${date.day}/${date.month}/${date.year}';
  }
}
