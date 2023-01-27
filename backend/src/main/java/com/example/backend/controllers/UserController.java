package com.example.backend.controllers;

import com.example.backend.configuration.TokenProvider;
import com.example.backend.model.Item;
import com.example.backend.model.Role;
import com.example.backend.model.User;
import com.example.backend.services.UserService;
import com.example.backend.utils.LoginForm;
import com.example.backend.utils.UserWithToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/")
public class UserController  {

    private final AuthenticationManager authenticationManager;

    private final TokenProvider jwtTokenUtil;

    private final UserService userService;

    public UserController(AuthenticationManager authenticationManager, TokenProvider jwtTokenUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
    }





    @Operation(
            operationId = "loginUser",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User successfully logged in.", content = @Content(mediaType = "application/json", schema = @Schema(implementation =  UserWithToken.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request body."),
                    @ApiResponse(responseCode = "401", description = "Bad credentials.")
            }
    )
    @PostMapping(value = "/users/login", produces = { "application/json" }, consumes = { "application/json" })
    public ResponseEntity<UserWithToken> loginUser(@Valid @RequestBody LoginForm loginForm) throws AuthenticationException {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginForm.getUsername(),
                        loginForm.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userService.findOne(loginForm.getUsername());
        return ResponseEntity.ok(new UserWithToken(user.getId(),user.getUsername(),"<hidden>",user.getFirstName(), user.getLastName(), user.getEmail(),(jwtTokenUtil.generateToken(authentication))));
    }


    @Operation(
            operationId = "createUser",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation =  User.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request body.")
            }
    )
    @PostMapping(value = "/users", produces = { "application/json" }, consumes = { "application/json" })
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User result = userService.addNewUser(user);

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }


    @Operation(
            operationId = "deleteUser",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User successfully deleted."),
                    @ApiResponse(responseCode = "401", description = "User not authenticated."),
                    @ApiResponse(responseCode = "403", description = "User not authorized to delete this user."),
                    @ApiResponse(responseCode = "404", description = "User not found.")
            }
    )
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Integer id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }



    @Operation(
            operationId = "editUser",
            responses = {
                    @ApiResponse(responseCode = "200", description = "A user object of a given id.", content = @Content(mediaType = "application/json", schema = @Schema(implementation =  User.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request body."),
                    @ApiResponse(responseCode = "401", description = "User not authenticated."),
                    @ApiResponse(responseCode = "403", description = "User not authorized to edit this user."),
                    @ApiResponse(responseCode = "404", description = "User not found.")
            }
    )
    @PutMapping(value = "/users/{id}", produces = { "application/json" }, consumes = { "application/json" })
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<User> editUser(@PathVariable("id") Integer id, @Valid @RequestBody User user) {
        User editedUser = userService.edit(id, user);
        return ResponseEntity.ok().body(editedUser);
    }


    @Operation(
            operationId = "getUserInfo",
            responses = {
                    @ApiResponse(responseCode = "200", description = "A user object of a given id.", content = @Content(mediaType = "application/json", schema = @Schema(implementation =  User.class))),
                    @ApiResponse(responseCode = "401", description = "User not authenticated."),
                    @ApiResponse(responseCode = "404", description = "User not found.")
            }
    )
    @GetMapping(value = "/users/{id}", produces = { "application/json" })
    public ResponseEntity<User> getUser(@PathVariable("id") Integer id) {
        User user = userService.findOne(id);
        user.setPassword("<hidden>");
        return ResponseEntity.ok().body(user);
    }

    @Operation(
            operationId = "getUserItems",
            responses = {
                    @ApiResponse(responseCode = "200", description = "A user object of a given id.", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "User not authenticated."),
                    @ApiResponse(responseCode = "404", description = "Cart not found.")
            }
    )
    @GetMapping(value = "/users/cart/{id}", produces = { "application/json" })
    public ResponseEntity<Set<Item>> getAllItemsFromCart(@PathVariable("id") Integer id) {
        Set<Item> result = userService.findAllFromCart(id);

        return ResponseEntity.ok(result);
    }

    @Operation(
            operationId = "getUserRoles",
            responses = {
                    @ApiResponse(responseCode = "200", description = "A role of user object of a given id.", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "User not authenticated."),
                    @ApiResponse(responseCode = "404", description = "User not found.")
            }
    )
    @GetMapping(value = "/users/{id}/role", produces = { "application/json" })
    public ResponseEntity<Set<Role>> getUserRoles(@PathVariable("id") Integer id) {
        Set<Role> roles = userService.getUserRoles(id);

        return  ResponseEntity.ok(roles);
    }



}
