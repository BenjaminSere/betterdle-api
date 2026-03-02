package betterdle.api.user.service;

import betterdle.api.user.dto.UserProfileDTO;
import betterdle.api.user.dto.UserPreferencesUpdateDTO;
import betterdle.api.user.dto.UserUpdateDTO;
import betterdle.api.user.model.User;
import betterdle.api.user.model.UserPreference;
import betterdle.api.user.repository.UserPreferenceRepository;
import betterdle.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserPreferenceRepository userPreferenceRepository;

    public UserProfileDTO getUserProfile(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserPreference pref = user.getPreference();

        return UserProfileDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .language(pref != null ? pref.getLanguage() : "en_US")
                .theme(pref != null ? pref.getTheme() : "dark")
                .appearInLeaderboards(pref == null || pref.isAppearInLeaderboards())
                .roles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public UserProfileDTO updateProfile(Integer userId, UserUpdateDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getUsername().equals(dto.getUsername()) && userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already taken");
        }
        if (!user.getEmail().equals(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already taken");
        }

        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        userRepository.save(user);

        return getUserProfile(userId);
    }

    @Transactional
    public UserProfileDTO updatePreferences(Integer userId, UserPreferencesUpdateDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserPreference pref = user.getPreference();
        if (pref == null) {
            pref = UserPreference.builder().user(user).build();
            user.setPreference(pref);
        }

        pref.setLanguage(dto.getLanguage());
        pref.setTheme(dto.getTheme());
        pref.setAppearInLeaderboards(dto.getAppearInLeaderboards());
        userPreferenceRepository.save(pref);

        return getUserProfile(userId);
    }
}
