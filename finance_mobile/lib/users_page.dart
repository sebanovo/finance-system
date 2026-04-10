import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:finance_mobile/constants/env.dart';
import 'package:shared_preferences/shared_preferences.dart';

class UsuariosPage extends StatefulWidget {
  const UsuariosPage({super.key});

  @override
  State<UsuariosPage> createState() => _UsuariosPageState();
}

class _UsuariosPageState extends State<UsuariosPage> {
  List usuarios = [];
  bool loading = true;

  @override
  void initState() {
    super.initState();
    fetchUsuarios();
  }

  Future<void> fetchUsuarios() async {
    final prefs = await SharedPreferences.getInstance();
    final subdomain = prefs.getString("subdomain");

    // final url = Uri.parse(
    //   "http://$subdomain.${Env.serverHost}:8000/api/v1/users/",
    // );

    final url = Uri.parse("${Env.baseUrl}:8000/api/v1/users/");

    final response = await http.get(url);

    if (response.statusCode == 200) {
      print(response.body);
      setState(() {
        usuarios = jsonDecode(response.body);
        loading = false;
      });
    } else {
      print("Error: ${response.body}");
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Usuarios")),
      body: loading
          ? const Center(child: CircularProgressIndicator())
          : ListView.builder(
              itemCount: usuarios.length,
              itemBuilder: (context, index) {
                final user = usuarios[index];

                return ListTile(
                  leading: ClipOval(
                    child: Image.network(
                      "",
                      width: 40,
                      height: 40,
                      fit: BoxFit.cover,

                      errorBuilder: (context, error, stackTrace) {
                        return Container(
                          width: 40,
                          height: 40,
                          color: Colors.grey[300],
                          child: const Icon(Icons.person, color: Colors.grey),
                        );
                      },

                      // 3. Opcional: Indicador de carga mientras baja la imagen
                      loadingBuilder: (context, child, loadingProgress) {
                        if (loadingProgress == null) return child;
                        return const SizedBox(
                          width: 40,
                          height: 40,
                          child: CircularProgressIndicator(strokeWidth: 2),
                        );
                      },
                    ),
                  ),
                  title: Text(user["username"] ?? "Sin nombre"),
                  subtitle: Text("${user["email"] ?? "Sin correo"}"),
                );
              },
            ),
    );
  }
}
