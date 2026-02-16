package com.delimce.aibroker.domain.entities;

import com.delimce.aibroker.domain.enums.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tbl_user", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 90)
    private String name;

    @Column(nullable = false, length = 90)
    private String lastName;

    @Column(nullable = false, length = 130)
    private String email;

    @Column(nullable = false, length = 130)
    private String password;

    @Column(nullable = true, length = 255)
    private String tempToken;

    @Column(nullable = true)
    private Long tokenTs;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserStatus status = UserStatus.PENDING;

}
