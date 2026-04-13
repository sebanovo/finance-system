import 'package:finance_mobile/forgot_password.dart';
import 'package:finance_mobile/home_page.dart';
import 'package:finance_mobile/presentation/pages/permissions_pages.dart';
import 'package:finance_mobile/presentation/pages/roles_pages.dart';
import 'package:finance_mobile/presentation/pages/login_page.dart';
import 'package:finance_mobile/reset_password_page.dart';
import 'package:finance_mobile/signup_page.dart';
import 'package:finance_mobile/users_page.dart';
import 'package:go_router/go_router.dart';

final appRouter = GoRouter(
  initialLocation: '/login',
  routes: [
    GoRoute(path: '/login', builder: (context, _) => const LoginPage()),
    GoRoute(path: '/signup', builder: (context, _) => const SignupPage()),
    GoRoute(path: '/home', builder: (context, _) => const HomePage()),
    GoRoute(
      path: '/forgot-password',
      builder: (context, _) => const ForgotPasswordPage(),
    ),
    GoRoute(path: '/users', builder: (context, _) => const UsuariosPage()),
    GoRoute(path: '/roles', builder: (context, _) => const RolesPage()),
    GoRoute(
      path: '/permissions',
      builder: (context, _) => const PermissionsPage(),
    ),
    GoRoute(
      path: '/reset-password:token',
      builder: (context, state) {
        // token: ModalRoute.of(context)?.settings.arguments as String?,
        final token = state.pathParameters['token'];
        return ResetPasswordPage(token: token);
      },
    ),
  ],
);
