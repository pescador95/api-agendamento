package app.core.model;

import java.util.Date;

import javax.persistence.*;

import app.agendamento.model.Agendamento;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name = "organizacao")
@JsonIgnoreProperties({"usuarioAcao", "isAtivo", "dataAcao", "systemDateDeleted"})
public class Organizacao extends PanacheEntityBase {

    @Column()
    @SequenceGenerator(name = "organizacaoIdSequence", sequenceName = "organizacao_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "organizacaoIdSequence")
    @Id
    public Long id;

    @Column()
    public String nome;

    @Column()
    public String cnpj;

    @Column()
    public String telefone;

    @Column()
    public String email;

    @Column()
    public String endereco;

    @Column()
    @JsonIgnore
    public boolean isAtivo;

    @Column()
    @JsonIgnore

    public Date dataAcao;

    @Column()
    @JsonIgnore

    public Date systemDateDeleted;

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public Usuario usuario;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    @JoinColumn(name = "userId")
    public Usuario usuarioAcao;

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Agendamento agendamento;
    public Organizacao() {

    }

}
