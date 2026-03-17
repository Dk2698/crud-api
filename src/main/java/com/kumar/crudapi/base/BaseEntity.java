package com.kumar.crudapi.base;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Version;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
public abstract class BaseEntity<ID extends Serializable> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private ID id;

    @Version
    private int version;

    @Builder.Default
    @Column(nullable = false)
    private boolean deleted = false;

    public void softDelete() {
        this.deleted = true;
    }

    public boolean getDeleted() {
        return this.deleted;
    }
}