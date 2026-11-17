# Rotas para verificação local

Referência da API em `dev` (`51bcf45`). O cabeçalho `X-Usuario-Id` só é aceito no perfil `local`. O cadastro local cria uma conta de desenvolvimento. O cadastro com senha e JWT é acompanhado pelas issues #73 e #74.

| Método  | Endpoint                            | Corpo retornado                                                 | `X-Usuario-Id` |
| ------- | ----------------------------------- | --------------------------------------------------------------- | -------------- |
| `POST`  | `/api/v1/usuarios/local`            | ID, email, nome e fuso do novo usuário                          | Não é usado    |
| `GET`   | `/api/v1/me/perfil`                 | Perfil e XP total                                               | Opcional       |
| `PATCH` | `/api/v1/me/perfil`                 | Perfil atualizado                                               | Opcional       |
| `GET`   | `/api/v1/me/configuracoes`          | Configurações do timer                                          | Opcional       |
| `PATCH` | `/api/v1/me/configuracoes`          | Configurações atualizadas                                       | Opcional       |
| `POST`  | `/api/v1/sessoes`                   | Sessão iniciada, ID e horário do servidor                       | Opcional       |
| `PATCH` | `/api/v1/sessoes/{id}`              | Sessão atualizada; recompensa se concluída                      | Opcional       |
| `GET`   | `/api/v1/me/sessao-ativa`           | Sessão ativa ou 204 se não houver                                    | Opcional       |
| `GET`   | `/api/v1/sessoes`                   | Histórico paginado de sessões                                   | Opcional       |
| `POST`  | `/api/v1/anotacoes`                 | Anotação criada                                                 | Opcional       |
| `GET`   | `/api/v1/anotacoes?data=2026-11-06` | Lista de anotações; também aceita `sessaoId` no lugar de `data` | Opcional       |
| `GET`   | `/api/v1/anotacoes/{id}`            | Uma anotação                                                    | Opcional       |
| `PATCH` | `/api/v1/anotacoes/{id}`            | Anotação atualizada                                             | Opcional       |
| `DELETE` | `/api/v1/anotacoes/{id}` | Sem corpo (204) | Opcional |
| `GET`   | `/api/v1/me/progressao`             | Nível, XP, patente, insígnias e constelações                    | Opcional       |
| `GET`   | `/api/v1/me/amigos`                 | Lista de amigos                                                 | Opcional       |
| `GET`   | `/api/v1/me/pedidos`                | Pedidos recebidos e enviados                                    | Opcional       |
| `POST`  | `/api/v1/amizades/pedidos`          | Pedido criado                                                   | Opcional       |
| `PATCH` | `/api/v1/amizades/pedidos/{id}`     | Pedido respondido ou amizade criada                             | Opcional       |
| `DELETE` | `/api/v1/amizades/{id}` | Sem corpo (204) | Opcional |
| `POST` | `/api/v1/me/bloqueios/{usuarioId}` | Sem corpo (204) | Opcional |
| `DELETE` | `/api/v1/me/bloqueios/{usuarioId}` | Sem corpo (204) | Opcional |
