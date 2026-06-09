package com.sport_pro_be.modules.chat.config;

import com.sport_pro_be.modules.auth.domain.User;
import com.sport_pro_be.modules.auth.interfaces.IJwtService;
import com.sport_pro_be.modules.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/// Authenticates the STOMP CONNECT frame using the same JWT as the REST API.
/// On success the STOMP session Principal name is set to the user's id (as a
/// String), so the server can target a user via `convertAndSendToUser(id, ...)`.
@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final IJwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                authenticate(accessor, authHeader.substring(7));
            }
        }
        return message;
    }

    private void authenticate(StompHeaderAccessor accessor, String token) {
        String email = jwtService.extractEmailFromAccessToken(token);
        if (email == null) {
            return;
        }
        User user = userRepository.findByEmailIgnoreCase(email).orElse(null);
        Integer tokenVersion = jwtService.extractTokenVersionFromAccessToken(token);
        if (user == null || !user.getTokenVersion().equals(tokenVersion)) {
            return;
        }
        List<GrantedAuthority> authorities = jwtService.extractRolesFromAccessToken(token).stream()
                .map(r -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + r))
                .collect(Collectors.toList());
        // Principal name == user id → enables convertAndSendToUser(userId, ...).
        accessor.setUser(new UsernamePasswordAuthenticationToken(
                String.valueOf(user.getId()), null, authorities));
    }
}
