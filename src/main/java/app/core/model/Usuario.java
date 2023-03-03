package app.core.model;

import app.agendamento.model.Pessoa;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.security.jpa.*;

import javax.persistence.*;
import java.util.*;


@Entity
@Table(name = "usuario")
@UserDefinition
@JsonIgnoreProperties({"isAtivo", "dataAcao", "systemDateDeleted"})
public class Usuario extends PanacheEntityBase {


    @Column()
    @SequenceGenerator(name = "usuarioIdSequence", sequenceName = "usuario_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "usuarioIdSequence")
    @Id
    public Long id;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Password
    public String password;

    @Column(nullable = false)
    @Username
    public String login;

    @Column(nullable = false)
    public String email;

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JoinColumn(name = "pessoaId")
    public Pessoa pessoa;

    @Column()
    @JsonIgnore
    public boolean isAtivo;

    @Column()
    @JsonIgnore
    public Date dataAcao;

    @Column()
    @JsonIgnore
    public Date systemDateDeleted;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuarioroles", joinColumns = {@JoinColumn(name = "userId")}, inverseJoinColumns = {
            @JoinColumn(name = "roleId")})
    @Roles
    public List<Role> privilegio = new ArrayList<>();

    @Column()
    @JsonIgnore
    public String usuario;

    @Column()
    @JsonIgnore
    public String usuarioAcao;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuarioorganizacao", joinColumns = {@JoinColumn(name = "userId")}, inverseJoinColumns = {
            @JoinColumn(name = "organizacaoId")})
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public List<Organizacao> organizacoes;

    public Usuario() {

    }
}
