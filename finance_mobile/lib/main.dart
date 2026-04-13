import 'package:flutter/material.dart';
import 'package:finance_mobile/constants/env.dart';
import 'package:finance_mobile/home_page.dart';
import 'login_page.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'forgot_password.dart';
import 'reset_password_page.dart';

Future<void> main() async {
  await dotenv.load(fileName: ".env");
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: Env.systemName,
      initialRoute: '/login',
      routes: {
        '/login': (_) => const LoginPage(),
        '/home': (_) => const HomePage(),
        '/forgot-password': (context) => const ForgotPasswordPage(),
        '/reset-password': (context) => ResetPasswordPage(
          token: ModalRoute.of(context)?.settings.arguments as String?,
        ),
      },
    );
  }
}
