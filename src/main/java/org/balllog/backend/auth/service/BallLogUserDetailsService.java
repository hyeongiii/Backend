package org.balllog.backend.auth.service;

import lombok.RequiredArgsConstructor;
import org.balllog.backend.auth.userdetails.BallLogUserDetails;
import org.balllog.backend.user.entity.User;
import org.balllog.backend.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BallLogUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String socialId) throws UsernameNotFoundException {
        User user = userRepository.findBySocialId(socialId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with social Id: " + socialId));

        return new BallLogUserDetails(user);
    }
}
