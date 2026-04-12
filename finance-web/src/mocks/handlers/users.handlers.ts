import { http, HttpResponse } from "msw";
import { usersMock } from "../data/users.mock";

export const usersHandlers = [
  http.get("/api/usersa", () => {
    return HttpResponse.json(usersMock);
  }),
];
