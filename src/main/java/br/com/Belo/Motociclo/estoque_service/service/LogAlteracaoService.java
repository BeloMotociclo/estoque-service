package br.com.Belo.Motociclo.estoque_service.service;

import br.com.Belo.Motociclo.estoque_service.entity.AcaoLog;
import br.com.Belo.Motociclo.estoque_service.entity.LogAlteracao;
import br.com.Belo.Motociclo.estoque_service.repository.LogAlteracaoRepository;
import org.springframework.stereotype.Service;

@Service
public class LogAlteracaoService {

    private final LogAlteracaoRepository repository;

    public LogAlteracaoService(LogAlteracaoRepository repository) {
        this.repository = repository;
    }

    public void registrar(String entidade, String entidadeId, AcaoLog acao, String detalhes) {
        LogAlteracao log = new LogAlteracao();
        log.setEntidade(entidade);
        log.setEntidadeId(entidadeId);
        log.setAcao(acao);
        log.setDetalhes(detalhes);
        repository.save(log);
    }
}