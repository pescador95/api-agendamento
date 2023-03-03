package app.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.security.jpa.RolesValue;

import javax.persistence.*;

@Table(name = "role")
@Entity
public class Role extends PanacheEntity {

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public Usuario usuario;

    @Column
    @RolesValue
    public String privilegio;

    public Role() {

    }
}