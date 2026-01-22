package com.jangjak.chagok.user.entity;

import com.jangjak.chagok.point.Entity.keys.UserItemCompositeKey;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserItem {

    @EmbeddedId
    private UserItemCompositeKey id;

    @Column(nullable = false)
    private Long count;
}
