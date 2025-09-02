package org.atmgigi.hyobankingbe.user.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "verificationCode")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String phone;
    private String code;
    private boolean isVerified;

    public void success() {
        this.isVerified = true;
    }
}
