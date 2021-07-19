package com.studyolle.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
@Entity
public class Account {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    private boolean emailVerified;

    private String emailCheckToken;

    private LocalDateTime emailCheckTokenGeneratedAt;

    private LocalDateTime joinedAt;

    private String bio;

    private String url;

    private String occupation;

    private String location;  // varchar(255)

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private boolean studyCreatedByEmail;
    private boolean studyCreatedByWeb;
    private boolean studyEnrollmentResultByEmail;
    private boolean studyEnrollmentResultByWeb;
    private boolean studyUpdatedByEmail;
    private boolean studyUpdatedByWeb;


    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
    }

    public void completeSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public boolean canSendConfirmEmail() {
        
        // 제일 처음 재전송 메일을 보내는 경우
        if(this.emailCheckTokenGeneratedAt == null) {
            return resetEmailCheckTokenGeneratedTime();
        }
        // 재전송 메일을 두번째 보내는 경우
        else {
            // 1시간 안에 보낸 메일인가?
            if (this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1))){
                return resetEmailCheckTokenGeneratedTime();
            }
            return false;
        }

    }

    private boolean resetEmailCheckTokenGeneratedTime() {
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
        return true;
    }
}
