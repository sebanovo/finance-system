import 'package:flutter_dotenv/flutter_dotenv.dart';

class Env {
  static String baseUrl = dotenv.get(
    "BASE_URL",
    fallback: "http://3.144.192.20:80",
  );
  static String systemName = dotenv.get(
    "SYSTEM_NAME",
    fallback: "Finance System",
  );
  static String openRouterApiKey = dotenv.get(
    "OPEN_ROUTER_API_KEY",
    fallback: 'aqui-colocar-api-key-de-open-router',
  );
}
