package com.SB1.controller;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SB1.dto.request.ApiResponse;
import com.SB1.dto.request.AuthenticationRequest;
import com.SB1.dto.request.IntrospectRequest;
import com.SB1.dto.response.AuthenticationResponse;
import com.SB1.dto.response.IntrospectResponse;
import com.SB1.repository.UserRepository;
import com.SB1.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
	AuthenticationService authenticationService;
	
	@PostMapping("/token")
	ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) 
	{
		ApiResponse<AuthenticationResponse> apiResponse= new ApiResponse<AuthenticationResponse>(); 
		var result = authenticationService.authenticate(request);
		apiResponse.setResult(result);
		return apiResponse;
	}
	
	@PostMapping("/introspect")
	ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request) throws ParseException, JOSEException 
	{
		var result = authenticationService.introspect(request);
		return ApiResponse.<IntrospectResponse>builder()
				.result(result)
				.build();
	}
}
