import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'users_page.dart';

class HomePage extends StatelessWidget {
  const HomePage({super.key});

  Future<void> logout(BuildContext context) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.clear();
    if (!context.mounted) return;
    Navigator.pushNamedAndRemoveUntil(context, '/login', (route) => false);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Dashboard")),

      drawer: Drawer(
        child: ListView(
          children: [
            const DrawerHeader(
              decoration: BoxDecoration(color: Colors.blue),
              child: Text(
                "Menú",
                style: TextStyle(color: Colors.white, fontSize: 20),
              ),
            ),
            ListTile(
              leading: const Icon(Icons.dashboard),
              title: const Text("Dashboard"),
              onTap: () {
                Navigator.pop(context);
              },
            ),
            ListTile(
              leading: const Icon(Icons.school),
              title: const Text("Usuarios"),
              onTap: () {
                Navigator.pop(context); // cierra el drawer
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (_) => const UsuariosPage()),
                );
              },
            ),
            ListTile(
              leading: const Icon(Icons.logout),
              title: const Text("Cerrar sesión"),
              onTap: () => logout(context),
            ),
          ],
        ),
      ),

      body: Column(
        children: [
          const Center(
            child: Text(
              "Bienvenido al Dashboard",
              style: TextStyle(fontSize: 22),
            ),
          ),
          Image.asset("logo.png", width: 100),
        ],
      ),
    );
  }
}
