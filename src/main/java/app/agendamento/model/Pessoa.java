package app.agendamento.model;

import java.util.Date;

import javax.persistence.*;

import app.core.model.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name = "pessoa")
@JsonIgnoreProperties({"usuarioAcao", "isAtivo", "dataAcao", "systemDateDeleted"})
public class Pessoa extends PanacheEntityBase {

    @Column()
    @SequenceGenerator(name = "pessoaIdSequence", sequenceName = "pessoa_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "pessoaIdSequence")
    @Id
    public Long id;

    @Column()
    public String nome;

    @Column()
    public String genero;

    @Column()
    public Date dataNascimento;

    @Column()
    public String telefone;

    @Column()
    public String telefone2;

    @Column()
    public String email;

    @Column()
    public String endereco;

    @Column()
    public Long cpf;

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    public Usuario usuario;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "userId")
    public Usuario usuarioAcao;

    @Column()
    @JsonIgnore
    public boolean isAtivo;

    @Column()
    @JsonIgnore

    public Date dataAcao;

    @Column()
    @JsonIgnore

    public Date systemDateDeleted;

    public Pessoa() {

    }

}
