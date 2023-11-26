package backend.spring.service;

import org.springframework.stereotype.Service;

@Service
public interface SecurityService {
    String createToken(String subject, long ttlMillis);

    String getSubject(String token);
}