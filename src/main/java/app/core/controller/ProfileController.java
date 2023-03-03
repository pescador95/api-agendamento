package app.core.controller;

import app.agendamento.model.Pessoa;
import app.core.model.MultiPartFormData;
import app.core.model.Profile;
import app.core.model.Responses;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@ApplicationScoped
@Transactional
public class ProfileController {
    @ConfigProperty(name = "quarkus.http.body.uploads-directory")
    @Inject
    String directory;

    private Responses responses;

    public Profile findOne(Long id) {

        Profile profile = Profile.findById(id);

        if (profile == null) {
            throw new RuntimeException("Arquivo não encontrado");
        }

        return profile;
    }

    public Response sendUpload(@NotNull MultiPartFormData file, String fileRefence, Long idCliente)
            throws IOException {

        responses = new Responses();
        responses.messages = new ArrayList<>();

        Profile profileCheck = Profile.find("idCliente = ?1", idCliente).firstResult();

        Pessoa pessoa = Pessoa.findById(idCliente);

        if (pessoa != null) {
            if (profileCheck == null) {
                Profile profile = new Profile();
                List<String> mimetype = Arrays.asList("image/jpg", "image/jpeg", "application/msword",
                        "application/vnd.ms-excel", "application/xml",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "image/gif",
                        "image/png", "text/plain", "application/vnd.ms-powerpoint", "application/pdf", "text/csv",
                        "document/doc", "document/docx",
                        "application/vnd.openxmlformats-officedocument.presentationml.presentation", "application/zip",
                        "application/vnd.sealed.xls");

                if (!mimetype.contains(file.getFile().contentType())) {
                    throw new IOException(
                            "Tipo de arquivo não suportado. Aceito somente arquivos nos formatos: ppt, pptx, csv, doc, docx, txt, pdf, xlsx, xml, xls, jpg, jpeg, png e zip.");
                }

                if (file.getFile().size() > 1024 * 1024 * 4) {
                    throw new IOException("Arquivo muito grande.");
                }

                String fileName = idCliente + "-" + file.getFile().fileName();

                profile.originalName = file.getFile().fileName();

                profile.keyName = fileName;

                profile.mimetype = file.getFile().contentType();

                profile.fileSize = file.getFile().size();

                profile.dataCriado = new Date();

                profile.pessoa = pessoa;

                profile.nomeCliente = profile.pessoa.nome;

                profile.fileReference = fileRefence;

                profile.persist();

                Files.copy(file.getFile().filePath(), Paths.get(directory + fileName));
                responses.status = 200;
                responses.messages.add("Arquivo adicionado com sucesso!");
                return Response.ok(responses).status(Response.Status.ACCEPTED).build();
            } else {
                responses.status = 400;
                responses.messages.add("Já existe um arquivo com o mesmo nome. Verifique!");
            }
        } else {
            responses.status = 400;
            responses.messages.add("Por favor, verifique o Animal do anexo.");
        }
        return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
    }

    public Response removeUpload(List<Long> pListIdProfile) {

        List<Profile> profiles;
        responses = new Responses();
        responses.messages = new ArrayList<>();
        profiles = Profile.list("id in ?1 and isAtivo = true", pListIdProfile);
        int count = profiles.size();

        try {
            profiles.forEach((profile) -> {

                try {
                    Profile.delete("id = ?1", profile.id);
                    Files.deleteIfExists(Paths.get(directory + profile.keyName));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            responses.status = 200;
            if (count <= 1) {
                responses.messages.add("Arquivo excluído com sucesso!");
            } else {
                responses.messages.add(count + " Arquivos excluídos com sucesso!");
            }
            return Response.ok(responses).status(Response.Status.ACCEPTED).build();
        } catch (Exception e) {
            responses.status = 400;
            if (count <= 1) {
                responses.messages.add("Arquivo não localizado ou já excluído.");
            } else {
                responses.messages.add("Arquivos não localizados ou já excluídos.");
            }
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }
}