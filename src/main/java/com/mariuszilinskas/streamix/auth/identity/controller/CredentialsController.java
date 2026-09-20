package com.mariuszilinskas.streamix.auth.identity.controller;

import com.mariuszilinskas.streamix.auth.identity.dto.SetupCredentialsRequest;
import com.mariuszilinskas.streamix.auth.identity.service.PasscodeService;
import com.mariuszilinskas.streamix.auth.identity.service.PasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/credentials")
@RequiredArgsConstructor
public class CredentialsController {

    private final PasswordService passwordService;
    private final PasscodeService passcodeService;

    @PostMapping("/setup")
    public ResponseEntity<Void> setupCredentials(@Valid @RequestBody SetupCredentialsRequest request) {
        passwordService.setupPassword(request);
        passcodeService.createPasscode(request.userId(), request.firstName(), request.email());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
