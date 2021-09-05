package ru.javaops.topjava2.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

@Schema(description = "Restaurant object")
@Entity
@Table(name = "restaurant", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "address"}, name = "restaurant_unique_name_address_idx")})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class Restaurant extends NamedEntity {

    @Schema(description = "Address", example = "New City, Clean str., 22")
    @Size(min = 2, max = 100)
    @Column(name = "address")
    private String address;

    @Schema(description = "Phone", example = "8(000)-333-22-11")
    @Size(min = 2, max = 100)
    @Column(name = "phone")
    private String phone;

    public Restaurant(Integer id, String name, String address, String phone) {
        super(id, name);
        this.address = address;
        this.phone = phone;
    }
}
