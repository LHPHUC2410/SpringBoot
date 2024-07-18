package com.SB1.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.CollectionUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.SB1.dto.request.AuthenticationRequest;
import com.SB1.dto.request.IntrospectRequest;
import com.SB1.dto.response.AuthenticationResponse;
import com.SB1.dto.response.IntrospectResponse;
import com.SB1.entity.User;
import com.SB1.exception.AppException;
import com.SB1.exception.ErrorCode;
import com.SB1.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService {
	UserRepository userRepository;
	
	@NonFinal
	@Value("${jwt.signerKey}")
	protected String SIGNER_KEY;
			
	public IntrospectResponse introspect (IntrospectRequest request) throws ParseException //giai ma
, JOSEException
	{
		String token = request.getToken();
		JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
		
		SignedJWT signedJWT = SignedJWT.parse(token);
		
		var verified = signedJWT.verify(verifier); //kiem tra xem dung token hay chua
		
		// kiem tra xem token het han chua
		Date expitytime = signedJWT.getJWTClaimsSet().getExpirationTime();
		return IntrospectResponse.builder()
				.valid(verified && expitytime.after(new Date()))
				.build();
	}
	
	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		User user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));
		
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
		boolean authenticated = (passwordEncoder.matches(request.getPassword(), user.getPassword()));
		
		if(!authenticated) 
		{
			throw new AppException(ErrorCode.UNAUTHENTICATED);
		}
		
		var token = generateToken(user);
		
		return AuthenticationResponse.builder()
				.token(token)
				.authenticationResponse(true)
				.build();
	}
	
	private String generateToken(User user) {
		JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
		
		JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
				.subject(user.getUsername())
				.issuer("lhphuc")
				.issueTime(new Date())
				.expirationTime(new Date(										//hết hạn token sau 1 giờ
						Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
						))
				.claim("scope", buildScope(user))
				.build(); 
		Payload payload = new Payload(jwtClaimsSet.toJSONObject());
		JWSObject jwsObject = new JWSObject(jwsHeader, payload);
		
		try {
			jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
			return jwsObject.serialize();
		} catch (JOSEException e) {
			log.error("Cannot create token", e);
			throw new RuntimeException(e);
		}
		
	}
	
	private String buildScope(User user) 
	{
		StringJoiner stringJoiner = new StringJoiner(" ");
		if (!org.springframework.util.CollectionUtils.isEmpty(user.getRoles()))
		{
			user.getRoles().forEach(s -> stringJoiner.add(s));
			
		} 
		return stringJoiner.toString();

	}
}
