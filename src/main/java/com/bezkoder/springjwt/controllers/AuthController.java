package com.bezkoder.springjwt.controllers;

import java.util.*;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.bezkoder.springjwt.payload.response.CommonResponse;
import com.bezkoder.springjwt.payload.response.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import com.bezkoder.springjwt.models.ERole;
import com.bezkoder.springjwt.models.Role;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.request.LoginRequest;
import com.bezkoder.springjwt.payload.request.SignupRequest;
import com.bezkoder.springjwt.payload.response.JwtResponse;
import com.bezkoder.springjwt.payload.response.MessageResponse;
import com.bezkoder.springjwt.repository.RoleRepository;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.security.jwt.JwtUtils;
import com.bezkoder.springjwt.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new CommonResponse(true, "logged_successful", new JwtResponse(jwt,
                        userDetails.getId(),
                        userDetails.getName(),
                        userDetails.getUsername(),
                        roles)));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            if (userRepository.existsByUsername(signUpRequest.getUsername())) {
                return ResponseEntity
                        .badRequest()
                        .body(new CommonResponse(false, "username_taken", null));
            }

            User user = new User();
            user.setName(signUpRequest.getName());
            user.setUsername(signUpRequest.getUsername());
            user.setPassword(encoder.encode(signUpRequest.getPassword()));
            user.setType(signUpRequest.getType());
            user.setEmail(signUpRequest.getEmail());
            user.setPhone(signUpRequest.getPhone());

            Set<String> strRoles = signUpRequest.getRole();
            Set<Role> roles = new HashSet<>();

            if (strRoles == null) {
                Role userRole = roleRepository.findByName(ERole.ROLE_PARENT)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(userRole);
            } else {
                strRoles.forEach(role -> {
                    switch (role) {
                        case "super_admin":
                            Role adminRole = roleRepository.findByName(ERole.ROLE_SUPER_ADMIN)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(adminRole);
                            break;
                        case "admin":
                            Role modRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(modRole);
                            break;

                        case "teacher":
                            Role teacherRole = roleRepository.findByName(ERole.ROLE_TEACHER)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(teacherRole);
                            break;

                        default:
                            Role parentRole = roleRepository.findByName(ERole.ROLE_PARENT)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(parentRole);
                            break;
                    }
                });
            }

            user.setRoles(roles);
            userRepository.save(user);

            return ResponseEntity.ok(new CommonResponse(true, "regis_successful", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new CommonResponse(false, "regis_unsuccessful", e.getMessage()));
        }
    }

    @GetMapping("get/all/customers")
    public ResponseEntity<?> getAllCustomers() {
        return ResponseEntity.ok(userRepository.getAllCustomers());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.add(fieldName + " " + errorMessage);
        });
        return ResponseEntity.ok(new CommonResponse(false, "bad_request", null, errors));
    }
}
