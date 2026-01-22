package com.jangjak.chagok.point.Entity;

import com.jangjak.chagok.point.enums.PurchaseState;
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
public class PurchaseHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long purchaseHistoryId;

    @Column(nullable = false)
    private Long itemId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer count;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchaseState state;
}
