package com.sport_pro_be.modules.setting.domain;

import com.sport_pro_be.common.AbstractAuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "site_settings")
@Getter
@Setter
public class SiteSetting extends AbstractAuditingEntity {

    @Id
    private Long id;

    @Column(name = "return_policy", columnDefinition = "TEXT")
    private String returnPolicy;
}
