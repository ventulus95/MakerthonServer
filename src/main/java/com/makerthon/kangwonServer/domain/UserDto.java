package com.makerthon.kangwonServer.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@RequiredArgsConstructor
public class UserDto {

    private String email;
    private String password;
    private MultipartFile profilePic;
}
