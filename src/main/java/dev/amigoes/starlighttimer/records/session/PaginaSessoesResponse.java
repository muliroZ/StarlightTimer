package dev.amigoes.starlighttimer.records.session;

import java.util.List;

public record PaginaSessoesResponse(List<SessaoResponse> itens, int pagina, int tamanho, boolean proximaPagina) {}
