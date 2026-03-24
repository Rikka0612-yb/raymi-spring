package com.rikka.raymispring.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "exception_log", schema = "SYSTEM")
public class ExceptionLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "error_code", nullable = false, length = 5)
    private String errorCode;

    @Column(name = "error_code_desc", nullable = false, length = 128)
    private String errorCodeDesc;

    @Column(name = "error_message", nullable = false, length = 1024)
    private String errorMessage;

    @Column(name = "remark", length = 512)
    private String remark;

    @Column(name = "data_source", nullable = false, length = 64)
    private String dataSource;

    @Column(name = "source_data_json", columnDefinition = "TEXT")
    private String sourceDataJson;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
