# 09 — Arquitetura e Plano de Ação

Notas complementares de arquitetura. Para topologia e regras vigentes, veja [Macro-arquitetura](Macro-arquitetura.md), [Contexo](Contexo.md) e [02-architecture](02-architecture.md). Para execução, veja [Backlog](Backlog.md).

---
## Arquitetura

A seção descreve a topologia planejada. Na `dev`, API, PostgreSQL e Redis já estão no Compose. nginx, Kafka, outbox e workers seguem no [Backlog](Backlog.md).
A arquitetura trata da organização e desenho do sistema em um nível mais abstrato. Entendam isso como um mini _System Design_ com detalhes de stack. Aqui estão representados de forma abstrata os fluxos de comunicação, as tecnologias, responsabilidades e abordagens que serão utilizadas no projeto. Caso surja algo a mais, eu (Muliro) atualizo.

![Macro-arquitetura](Macro-arquitetura.md)
### Pontos principais:
- **Nginx:** Atuará como proxy reverso da aplicação. De forma simples, ele recebe todas as requisições (POST, GET, etc.) e as redireciona para o(s) servidor(es) backend. Também pode distribuir conteúdo estático (páginas html, por exemplo).
- **Redis (Cache):** Atuará como banco de cache da aplicação. Ajuda a evitar a sobrecarga do banco de dados ao armazenar consultas frequentes em um banco _in-memory_ super rápido.
- **Cache-Aside Pattern:** É um padrão de projeto que diz como montar uma estratégia de _caching_ eficiente para aplicações. Veja a seção de [Padrões de Projeto](#padr%C3%B5es-de-projeto) para mais detalhes.
- **Kafka:** Atuará como uma fila de mensageria conectando, pilar da arquitetura orientada à eventos. É a evolução dos padrões de projeto comportamentais **Observer** e **Mediator**. Será utilizado junto com o _[Outbox Pattern](#outbox)_ para mitigar o problema de dupla escrita (_Dual Write Problem_).
- **Workers:** São módulos independentes que se comunicam com nosso backend principal através de uma fila de mensageria (Kafka). São assíncronos, altamente flexíveis em relação à tecnologias e fracamente acoplados. Adicionar um novo é a coisa mais simples de fazer (mas talvez não de manter).

---
## Micro-Arquitetura
A micro-arquitetura trata principalmente de aspectos de baixo nível, como a organização da codebase

---
## Estrutura do Projeto

Os módulos `identity`, `session`, `annotation`, `progression` e `social` ficam em `core`. Cada módulo reúne domínio Java puro, persistência Spring e serviços de caso de uso. Controllers, eventos, exceções comuns, enums e records têm pacotes centrais. Ver [[Contexo]] §6 e [[02-architecture]].

## Padrões de Projeto
Abaixo estão listados e explicados os padrões de projeto que utilizaremos na nossa aplicação:

### Cache-Aside
O padrão **Cache-Aside** é uma estratégia de cache manual onde o código de sua aplicação gerencia diretamente o fluxo de dados entre o cache e o banco de dados principal. Também é conhecido como _lazy loading_, porque os dados são armazenados no cache apenas quando necessário.

**Como funciona**
- **Fluxo de Leitura:**
	1. A aplicação verifica o cache primeiro.
	2. Se os dados estiverem lá (**cache hit**), ele os retorna imediatamente.
	3. Se os dados **não** estiverem lá (**cache miss**), a aplicação consulta o banco de dados, armazena o resultado no cache, e retorna os dados para quem fez a requisição.

- **Fluxo de Escrita:**
	1. A aplicação atualiza o banco de dados principal primeiro.
	2. Depois ela invalida ou exclui os itens correspondentes do cache para evitar servir dados desatualizados.

**Principais Benefícios**
- **Eficiência de Memória:** Apenas os dados que estão sendo requisitados atualmente são _cacheados_, evitando desperdiçar espaço com dados não utilizados (ou não tão frequentes).
- **Resiliência:** Se o servidor/banco de cache cair, a aplicação continua funcionando normalmente fazendo as requisições direto no banco de dados.
- **Simplicidade:** Não é necessário nenhum _middleware_ complexo para sincronizar os dados automaticamente.

**Desafios Comuns**
- **Dados Desatualizados:** Uma breve janela de inconsistência pode ocorrer entre a atualização do banco e a limpeza do cache.
- **_Cache Stampede:_** Quando uma chave de cache muito utilizada expira, várias requisições simultâneas podem dar _miss_ no cache e sobrecarregar o banco de uma vez.
- **Invalidação Manual:** O desenvolvedor deve escrever um código para lidar com atualizações de cache explicitamente, o que acaba abrindo brechas para errar ou esquecer de implementar um caminho de invalidação específico.

### Observer
O padrão **Observer** é um padrão de projeto comportamental que define uma relação de dependência um-para-muitos entre objetos Isso significa que, quando um objeto muda de estado, todos os seus dependentes são automaticamente notificados e atualizados sem que precisem ficar verificando isso constantemente.

---
Componentes principais

- **Sujeito (Subject / Observable):** O objeto que está sendo observado. Ele mantém uma lista de assinantes e fornece métodos para adicionar, remover e notificar esses observadores.

- **Observador (Observer / Subscriber):** A interface ou classe abstrata que define um método de atualização para receber as notificações do sujeito.

- **Observadores Concretos:** As classes que implementam a interface do observador e definem o que deve acontecer quando a notificação é disparada (por exemplo, enviar um e-mail ou atualizar uma interface gráfica).

---
Imagine um sistema de e-commerce onde um objeto `Pedido` muda de status:

- O **Pedido** é o Sujeito.

- Classes como `Email` e `WhatsApp` implementam a interface de Observador.

- Quando o status do pedido muda para "Enviado", o `Pedido` percorre sua lista de observadores e chama o método de notificação de cada um.

- O observador de `Email` envia a mensagem; o observador do `WhatsApp` dispara a notificação no celular.

---
Vantagens e Desvantagens

- **Vantagens:**
    - **Baixo acoplamento:** O sujeito não precisa conhecer as classes concretas dos observadores, apenas a interface comum.
    - **Flexibilidade:** Você pode adicionar ou remover observadores dinamicamente em tempo de execução.
    - **Arquitetura reativa:** Promove a comunicação baseada em eventos sem loops de verificação (_polling_).

- **Desvantagens:**
    - **Complexidade:** Pode ser difícil rastrear o fluxo se houver muitos observadores ou atualizações em cascata.
    - **Desempenho:** Notificar muitos observadores de forma síncrona pode gerar gargalos se não for bem gerenciado.

### Mediator
### Facade

### Outbox
