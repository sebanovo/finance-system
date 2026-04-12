import { http, HttpResponse } from "msw";
import { LoginRequest } from "../../app/core/session/model/auth-user.type";
import { usersMock } from "../data/users.mock";

const createErrorResponse = (message: string) => {
  return {
    success: false,
    message,
    errors: [],
    timestamp: new Date().toISOString(),
  };
};

export const authHandlers = [
  http.post("/api/auth/logina", async ({ request }) => {
    const tenantSlugHeader = request.headers.get("X-Tenant-Slug");

    if (!tenantSlugHeader) {
      return new HttpResponse(
        createErrorResponse(
          "Required tenant header 'X-Tenant-Slug' is missing"
        ),
        { status: 400 }
      );
    }

    const body = (await request.json()) as LoginRequest; // Tipamos la solicitud con LoginRequest

    // Verificamos si el body tiene email y password
    if (!body || !body.email || !body.password) {
      return new HttpResponse(
        createErrorResponse("Email and password are required"),
        { status: 400 }
      );
    }

    const { email, password } = body;

    const matchedUser = usersMock.find(
      mockUser => mockUser.email === email && mockUser.password === password
    );

    if (matchedUser) {
      return HttpResponse.json(
        {
          success: true,
          message: "Login Exitoso",
          data: {
            tokenType: "Bearer",
            accessToken: "access_token_super_secreto",
            refreshToken: "refresh_token_super_secreto",
            accessExpiresInMs: 10000,
          },
          timestamp: new Date().toISOString(),
        },
        {
          status: 200,
        }
      );
    }

    return new HttpResponse(createErrorResponse("Credenciales incorrectas"), {
      status: 401,
    });
  }),

  // Simulación del endpoint GET /api/me
  http.get("/api/auth/mea", async ({ request }) => {
    // const cookieHeader = request.headers.get("Cookie"); // Recuperamos la cookie

    // if (!cookieHeader) {
    //   // Si no hay cookie de sesión o no es válida, devolvemos un error 401
    //   console.log("no hay cookie");
    //   return new HttpResponse(createErrorResponse("No autorizado"), {
    //     status: 401,
    //   });
    // }

    // Si la cookie está presente, simulamos que recuperamos los datos del usuario
    const userData = usersMock.find(user => {
      if (user.email === "thunder@gmail.com") {
        return user;
      }
      return null;
    });

    if (!userData) {
      return new HttpResponse(
        createErrorResponse("No se pudo encontrar al usuario"),
        { status: 404 }
      );
    }

    // Devolvemos la información del usuario autenticado
    return HttpResponse.json(
      {
        success: true,
        message: "respuesta exitosa",
        data: {
          id: userData.id,
          email: userData.email,
          firstName: userData.firstName,
          lastName: userData.lastName,
          active: userData.active,
          status: userData.status,
          tenantSlug: userData.tenantSlug,
          roles: userData.roles,
        },
        timestamp: userData.timeStamp,
      },
      { status: 200 }
    );
  }),

  // Otros manejadores pueden seguir aquí...

  // Otros manejadores pueden seguir aquí...
];
