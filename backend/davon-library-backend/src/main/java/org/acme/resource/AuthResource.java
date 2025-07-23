package org.acme.resource;

import org.acme.model.Member;
import org.acme.model.User;
import org.acme.service.AuthenticationService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Optional;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    private AuthenticationService authService;

    public static class LoginRequest {
        public String username;
        public String password;
    }

    public static class RegisterRequest {
        public String username;
        public String password;
        public String email;
        public String firstName;
        public String lastName;
        public String phoneNumber;
    }

    @POST
    @Path("/login")
    public Response login(LoginRequest loginRequest) {
        try {
            Optional<User> user = authService.login(loginRequest.username, loginRequest.password);
            if (user.isPresent()) {
                return Response.ok(user.get()).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Invalid username or password\"}")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/register")
    public Response register(RegisterRequest registerRequest) {
        try {
            Member newMember = authService.registerMember(
                    registerRequest.username,
                    registerRequest.password,
                    registerRequest.email,
                    registerRequest.firstName,
                    registerRequest.lastName,
                    registerRequest.phoneNumber);
            return Response.status(Response.Status.CREATED)
                    .entity(newMember)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}