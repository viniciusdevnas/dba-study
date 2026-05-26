Plano de Estudos Detalhado: DBA Sênior de Desenvolvimento e Cloud

1. Visão Geral da Formação

Objetivo da Etapa Esta formação foi desenhada para elevar o patamar de desenvolvedores e DBAs plenos ao nível de Especialista em Infraestrutura Crítica. O foco transcende a escrita de queries; o objetivo é compreender a engenharia interna dos motores (PostgreSQL e MySQL), projetar arquiteturas distribuídas e resilientes em Cloud e dominar a arte do tuning baseado em evidências de baixo nível (I/O, CPU, Memória e Latência de Rede).

Resultado Esperado Ao final desta trilha, o profissional será capaz de:

[] Arquitetar sistemas multi-tenant escaláveis e seguros.
[] Realizar diagnósticos avançados de performance utilizando internals (Wait Events, Plan Nodes).
[] Gerenciar infraestruturas complexas em Cloud (AWS Aurora/RDS) via IaC (Terraform).
[] Garantir a integridade e disponibilidade de dados sob condições de desastre e alta concorrência.
[] Dominar o ecossistema Linux/Networking aplicado especificamente a bancos de dados.

Nível de Dificuldade: Avançado / Especialista.

Pré-requisitos: Domínio de modelagem relacional (ER), sintaxe DDL/DML, e compreensão básica de como índices funcionam.


--------------------------------------------------------------------------------


2. Roadmap em Fases (PostgreSQL e MySQL)

Fase 1: O Coração dos Dados (Fundamentos & Internals)

* Objetivo: Consolidar a base física e teórica. Sem entender como o dado toca o disco, o tuning é mera adivinhação.
* Duração: 6 semanas.
* O que estudar:
  1. Álgebra Relacional e Normalização: Além das Formas Normais (1NF a 3NF/BCNF), estude Decomposição e Dependência Funcional.
  2. Arquitetura de Modelagem: Diferença profunda entre OLTP vs. OLAP. Domine Star Schema e Snowflake Schema para ambientes analíticos.
  3. Internals de Armazenamento: Anatomia da Página (8KB no Postgres, 16KB no InnoDB), Buffer Pool, Shared Buffers e o processo de Checkpointer e Background Writer.
  4. ACID e MVCC: Como o isolamento Snapshot funciona. Estude o Transaction ID (XID) Wraparound e como o Vacuum (Postgres) e o Purge (MySQL) limpam versões mortas de linhas.

Fase 2: Performance, Tuning e SQL Avançado

* Objetivo: Dominar o otimizador e objetos complexos.
* Duração: 10 semanas.
* O que estudar:
  1. SQL de Elite: Window Functions complexas, CTEs Recursivas, Materialized Views (Refresh strategies), Triggers (Performance overhead) e Stored Procedures vs. Functions.
  2. Estruturas de Índices: B-Tree (Fan-out), GIN (Full-text/JSONB), GiST (Geospatial/Ranges), BRIN (Big Data) e Índices Parciais/Expressão.
  3. O Otimizador: Interpretação de EXPLAIN (ANALYZE, BUFFERS). Diferencie Nested Loop, Hash Join e Merge Join. Entenda por que o otimizador escolhe um Sequential Scan mesmo com índice.
  4. Estatísticas: Cardinalidade, Histogramas e ANALYZE.

Fase 3: Operações, Cloud e Hardening

* Objetivo: Sustentar a operação em escala corporativa.
* Duração: 12 semanas.
* O que estudar:
  1. Alta Disponibilidade: Replicação Física (Streaming) vs. Lógica. Slots de replicação, Quorum-based commits e Failover automático.
  2. Cloud Architecture: AWS Aurora (Storage auto-scaling), RDS Proxy, Multi-AZ vs. Read Replicas.
  3. Linux & Networking: Tuning de kernel (sysctl), TCP/IP para DBAs, Firewalls (iptables/security groups) e monitoramento de I/O (iostat, iotop).
  4. Segurança: Row Level Security (RLS), Criptografia em trânsito (TLS) e em repouso (TDE/KMS), Auditoria e conformidade.

Critérios de Evolução: Amador vs. Sênior

Área	Abordagem Amadora	Abordagem Sênior (Sua Meta)
Performance	Cria índices "por intuição"	Analisa pg_stat_statements e custo de buffers no EXPLAIN.
Backups	Faz dump diário (Backup Lógico)	Implementa Incremental PITR com validação automática.
Escalabilidade	Aumenta o tamanho da instância (Vertical)	Implementa Read Replicas e estratégias de Sharding/Partitioning.
Incidentes	Reinicia o servidor na lentidão	Identifica locks via pg_stat_activity e analisa Wait Events.
Modelagem	Deleta registros fisicamente	Implementa Soft Delete com Índices Parciais ou Audit Logs.


--------------------------------------------------------------------------------


3. Conceitos Teóricos (Internals e Arquitetura)

Mecanismos de Armazenamento e WAL

* Write-Ahead Logging (WAL): O banco nunca escreve no arquivo de dados antes de garantir que a alteração está no log sequencial (WAL/Redo Log). Isso resolve o problema de durabilidade ACID.
* Checkpoints: O momento em que as páginas "sujas" (em memória) são sincronizadas com o disco. Checkpoints muito frequentes causam picos de I/O; muito esparsos aumentam o tempo de recovery.

Concorrência e Heap Only Tuples (HOT)

No PostgreSQL, updates geram novas versões de linhas. O HOT Update permite que, se o update não alterar colunas indexadas e houver espaço na mesma página, o banco evite atualizar os índices, reduzindo drasticamente o overhead de escrita e o bloat.

Estruturas de Índices e Trade-offs

* B-Tree: Ideal para ordenação e busca por igualdade/intervalo.
* GIN (Generalized Inverted Index): Essencial para busca em documentos JSONB ou arrays. Problema: lentidão extrema em inserções.
* GiST: Resolve problemas de proximidade (geolocalização/GIS).
* Impacto: Muitos índices aumentam a latência de INSERT/UPDATE e o tempo de CHECKPOINT. O sênior utiliza Índices Parciais para indexar apenas dados ativos (ex: WHERE status = 'ACTIVE').


--------------------------------------------------------------------------------


4. Exercícios Práticos

Nível Avançado (SQL & Tuning)

1. CTE Recursiva: Escreva uma query que percorra uma tabela de categorias pai-filho (Hierarquia) de profundidade arbitrária e identifique se há referências circulares.
2. Memory Spill Diagnosis: Force um Join pesado em uma sessão com work_mem baixo (ex: 4MB) e identifique no EXPLAIN ANALYZE o uso de arquivos temporários em disco (External Merge Sort).
3. Tuning de Janela: Use Window Functions (LEAD/LAG) para calcular a variação percentual de vendas entre meses e compare a performance com um SELF JOIN tradicional.

Troubleshooting de Concorrência

Simule um Deadlock em duas janelas de terminal:

* Sessão A: UPDATE conta SET saldo = 100 WHERE id = 1;
* Sessão B: UPDATE conta SET saldo = 200 WHERE id = 2;
* Sessão A: UPDATE conta SET saldo = 300 WHERE id = 2; (Bloqueado)
* Sessão B: UPDATE conta SET saldo = 400 WHERE id = 1; (Deadlock) Tarefa: Identifique os PIDs envolvidos consultando as views pg_locks e pg_stat_activity.


--------------------------------------------------------------------------------


5. Laboratórios

Laboratório 1: Ambiente Linux Hardening para DBA

Configure um servidor Ubuntu 22.04 e aplique o tuning de kernel no /etc/sysctl.conf:

# Otimização para DB de alta carga
vm.swappiness = 10
vm.dirty_background_ratio = 5
vm.dirty_ratio = 15
vm.overcommit_memory = 2
kernel.sched_migration_cost_ns = 5000000


Tarefa: Configure o firewall (ufw) para permitir apenas tráfego na porta 5432 vindo do IP da aplicação e configure o huge_pages = try no Postgres.

Laboratório 2: Docker e Replicação Síncrona

Crie um docker-compose.yml com um mestre e duas réplicas. Tarefa: Configure o parâmetro synchronous_commit = on e synchronous_standby_names = 'ANY 1 (s1, s2)'. Simule a queda do mestre e promova uma réplica manualmente usando pg_ctl promote.

Laboratório 3: Observabilidade com pg_stat_statements

Integre Prometheus e Grafana para monitorar:

* Cache Hit Ratio: sum(heap_blks_hit) / (sum(heap_blks_hit) + sum(heap_blks_read))
* Transaction Throughput: Commits vs Rollbacks.
* Wait Events: Identifique se o banco está esperando por CPU, I/O ou Locks.


--------------------------------------------------------------------------------


6. Projetos Progressivos

Projeto A: Arquitetura SaaS Multi-tenant

Projete um banco para uma plataforma SaaS.

* Desafio: Compare a estratégia de "Schema per Tenant" vs. "Shared Schema com Row Level Security (RLS)".
* Requisito: Implementar RLS para garantir que o usuário do tenant X jamais veja dados do tenant Y, mesmo executando SELECT * FROM table.
* Relatório: Documente o overhead de performance do RLS e como evitar o "Noisy Neighbor Problem" usando limites de conexão.

Projeto B: Ledger Financeiro com Event Sourcing

Crie um sistema de conta corrente onde o saldo é derivado de uma tabela de eventos imutável.

* Desafio: Use Materialized Views para o saldo atual e Triggers para garantir integridade.
* Entregável: Script de auditoria que detecta qualquer alteração manual (bypass) feita fora da aplicação.

Projeto C: Disaster Recovery e Caos Engineering

Simule a corrupção de um datafile ou a deleção acidental de uma tabela de vendas_nfe.

* Tarefa: Realize o Point-in-Time Recovery (PITR). Você deve restaurar o backup físico e aplicar os logs do WAL até 1 segundo antes do incidente ocorrer.


--------------------------------------------------------------------------------


7. Cronograma de Estudos

Trilha	Frequência	Foco Principal
Consistência (1h/dia)	Seg a Sex	20min Teoria (Docs/Livros) + 40min de Scripts SQL/Tuning.
Imersão (2h/dia)	Seg a Sex	1h Lab de Infra (Linux/Docker/Cloud) + 1h de SQL Internals.
Maratona (Final de Semana)	Sáb ou Dom	Bloco de 4h para execução de um dos Projetos Progressivos.


--------------------------------------------------------------------------------


8. Recursos de Estudo

* Bibliografia Obrigatória:
  * Designing Data-Intensive Applications (Martin Kleppmann) - Para arquitetura.
  * PostgreSQL 14 Internals (Egor Rogov) - Para internals profundos.
  * High Performance MySQL (Silvia Botros) - Para especialistas em MySQL.
* Ferramentas Essenciais:
  * Diagnóstico: pg_badger (análise de logs), pg_stat_statements, explain.depesz.com.
  * Infra: Terraform, Ansible, Docker.
  * Benchmarking: pgbench, sysbench.


--------------------------------------------------------------------------------


9. Modo de Estudo e Metodologia

1. Active Recall: Após estudar sobre "Isolation Levels", escreva um resumo de 10 linhas sobre como o Serializable impede anomalias de escrita.
2. GitHub como Portfólio Técnico: Não suba apenas scripts SQL. Crie repositórios com:
  * README.md explicando um cenário de incidente e o passo a passo da resolução.
  * Diagramas de arquitetura em Mermaid.
  * Benchmarks comparando diferentes tipos de índices em tabelas de 10M+ linhas.


--------------------------------------------------------------------------------


10. Preparação Profissional

Rotina do Sênior

O DBA Sênior não é um "apagador de incêndios", ele é um engenheiro de confiabilidade. Sua rotina envolve:

* Capacity Planning: Analisar o crescimento de disco e prever upgrade de instância 3 meses antes do limite.
* Query Review: Atuar como gatekeeper em Pull Requests que alteram schemas ou adicionam queries pesadas.
* Health Checks: Automatizar alertas para Transaction ID Wraparound e Replication Lag.

Incidentes Reais e Resolução Sênior

1. Exaustão de Conexões: A aplicação trava com "too many clients". Solução: Implementar PgBouncer para connection pooling e ajustar max_connections baseado na RAM disponível.
2. Bloqueio de Autovacuum: O autovacuum está consumindo todo o I/O. Solução: Tuning de autovacuum_vacuum_cost_limit e autovacuum_work_mem, ou agendamento para horários de baixa carga.
3. Deadlock em Lote: Processo noturno de carga trava a API. Solução: Reordenar a ordem de atualização das tabelas nos processos batch e usar SELECT ... FOR UPDATE SKIP LOCKED.

Certificações Recomendadas

* AWS Certified Database - Specialty.
* Professional PostgreSQL Database Administrator (EDB).
