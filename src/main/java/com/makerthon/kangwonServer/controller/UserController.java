package com.makerthon.kangwonServer.controller;

import com.makerthon.kangwonServer.Config.JwtProvider;
import com.makerthon.kangwonServer.domain.CurrentUser;
import com.makerthon.kangwonServer.domain.User;
import com.makerthon.kangwonServer.domain.UserDto;
import com.makerthon.kangwonServer.domain.UserRepository;
import com.makerthon.kangwonServer.service.RekognitionService;
import com.makerthon.kangwonServer.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final RekognitionService rekognitionService;

    @PostMapping("/signup")
    public Long join(UserDto userDto) throws IOException {
        String keyname = s3Service.upload(userDto.getProfilePic());
        return userRepository.save(User.builder()
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .roles(Collections.singletonList("ROLE_USER")) // 최초 가입시 USER 로 설정
                .profilePictureName(keyname)
                .build()).getId();
    }

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> user) {
        User member = userRepository.findByEmail(user.get("email"))
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));
        if (!passwordEncoder.matches(user.get("password"), member.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        return jwtProvider.createToken(member.getUsername(), member.getRoles());
    }

    @GetMapping("/user/check")
    public String checker(){
        return "USER!!";
    }

    @PostMapping("/user/carFaceVerified")
    public boolean FaceVerified(@CurrentUser User user, MultipartFile picture){
        return rekognitionService.matchingFace(user, picture);
    }
}
