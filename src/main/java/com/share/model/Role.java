package com.share.model;


import com.share.model.enums.ERole;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "Role")
@Table(name = "role")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role {// role türlerini tutar

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, unique = true)
    private ERole name;

}