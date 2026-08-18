package br.com.Belo.Motociclo.estoque_service.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import br.com.Belo.Motociclo.estoque_service.config.MinioProperties;

import java.util.UUID;

@Service
public class MinioService {

    private final MinioClient minioClient;
    private final MinioProperties properties;

    public MinioService(MinioClient minioClient, MinioProperties properties) {
        this.minioClient = minioClient;
        this.properties = properties;
    }

    public String upload(MultipartFile arquivo, String pasta) {
        try {
            String nomeArquivo = pasta + "/" + UUID.randomUUID() + "_" + arquivo.getOriginalFilename();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(nomeArquivo)
                            .stream(arquivo.getInputStream(), arquivo.getSize(), -1)
                            .contentType(arquivo.getContentType())
                            .build()
            );
            return properties.getUrl() + "/" + properties.getBucket() + "/" + nomeArquivo;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao fazer upload da imagem: " + e.getMessage());
        }
    }

    public void deletar(String url) {
        try {
            String nomeArquivo = url.replace(
                    properties.getUrl() + "/" + properties.getBucket() + "/", ""
            );
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(nomeArquivo)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Erro ao deletar imagem: " + e.getMessage());
        }
    }
}