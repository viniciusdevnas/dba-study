# 🗺️ Plano de Estudos: DBA Sênior de Desenvolvimento e Cloud

## 1. Visão Geral da Formação

### Objetivo da Etapa
Esta formação foi desenhada para elevar o patamar de desenvolvedores e DBAs plenos ao nível de **Especialista em Infraestrutura Crítica**. O foco transcende a escrita de queries; o objetivo é compreender a engenharia interna dos motores (PostgreSQL e MySQL), projetar arquiteturas distribuídas e resilientes em Cloud e dominar a arte do tuning baseado em evidências de baixo nível (I/O, CPU, Memória e Latência de Rede).

### Resultado Esperado
Ao final desta trilha, o profissional será capaz de:

- [ ] Arquitetar sistemas *multi-tenant* escaláveis e seguros.
- [ ] Realizar diagnósticos avançados de performance utilizando *internals* (*Wait Events*, *Plan Nodes*).
- [ ] Gerenciar infraestruturas complexas em Cloud (AWS Aurora/RDS) via IaC (Terraform).
- [ ] Garantir a integridade e disponibilidade de dados sob condições de desastre e alta concorrência.
- [ ] Dominar o ecossistema Linux/Networking aplicado especificamente a bancos de dados.

* **Nível de Dificuldade:** Avançado / Especialista.
* **Pré-requisitos:** Domínio de modelagem relacional (ER), sintaxe DDL/DML, e compreensão de como índices funcionam.

---

## 2. Roadmap em Fases (PostgreSQL e MySQL)

### 🔴 Fase 1: O Coração dos Dados (Fundamentos & Internals)
* **Objetivo:** Consolidar a base física e teórica. Sem entender como o dado toca o disco, o tuning é mera adivinhação.
* **Duração:** 6 semanas.
* **O que estudar:**
  1. **Álgebra Relacional e Normalização:** Além das Formas Normais (1NF a 3NF/BCNF), estude Decomposição e Dependência Funcional.
  2. **Arquitetura de Modelagem:** Diferença profunda entre OLTP vs. OLAP. Domine *Star Schema* e *Snowflake Schema* para ambientes analíticos.
  3. **Internals de Armazenamento:** Anatomia da Página (8KB no Postgres, 16KB no InnoDB), *Buffer Pool*, *Shared Buffers* e o processo de *Checkpointer* e *Background Writer*.
  4. **ACID e MVCC:** Como o isolamento *Snapshot* funciona. Estude o *Transaction ID (XID) Wraparound* e como o *Vacuum* (Postgres) e o *Purge* (MySQL) limpam versões mortas de linhas.

### 🟡 Fase 2: Performance, Tuning e SQL Avançado
* **Objetivo:** Dominar o otimizador e objetos complexos.
* **Duração:** 10 semanas.
* **O que estudar:**
  1. **SQL de Elite:** *Window Functions* complexas, CTEs Recursivas, *Materialized Views* (*Refresh strategies*), *Triggers* (*Performance overhead*) e *Stored Procedures* vs. *Functions*.
  2. **Estruturas de Índices:** B-Tree (Fan-out), GIN (Full-text/JSONB), GiST (Geospatial/Ranges), BRIN (Big Data) e Índices Parciais/Expressão.
  3. **O Otimizador:** Interpretação de `EXPLAIN (ANALYZE, BUFFERS)`. Diferencie *Nested Loop*, *Hash Join* e *Merge Join*. Entenda por que o otimizador escolhe um *Sequential Scan* mesmo existindo índice.
  4. **Estatísticas:** Cardinalidade, Histogramas e `ANALYZE`.

### 🟢 Fase 3: Operações, Cloud e Hardening
* **Objetivo:** Sustentar a operação em escala corporativa.
* **Duração:** 12 semanas.
* **O que estudar:**
  1. **Alta Disponibilidade:** Replicação Física (Streaming) vs. Lógica. Slots de replicação, *Quorum-based commits* e *Failover* automático.
  2. **Cloud Architecture:** AWS Aurora (*Storage auto-scaling*), RDS Proxy, Multi-AZ vs. *Read Replicas*.
  3. **Linux & Networking:** Tuning de kernel (`sysctl`), TCP/IP para DBAs, Firewalls (`iptables`/`security groups`) e monitoramento de I/O (`iostat`, `iotop`).
  4. **Segurança:** *Row Level Security* (RLS), Criptografia em trânsito (TLS) e em repouso (TDE/KMS), Auditoria e conformidade.

### ⚖️ Critérios de Evolução: Amador vs. Sênior

| Área | Abordagem Amadora | Abordagem Sênior (Sua Meta) |
| :--- | :--- | :--- |
| **Performance** | Cria índices "por intuição" | Analisa `pg_stat_statements` e custo de buffers no `EXPLAIN`. |
| **Backups** | Faz dump diário (Backup Lógico) | Implementa Incremental PITR com validação automática. |
| **Escalabilidade** | Aumenta o tamanho da instância (Vertical) | Implementa *Read Replicas* e estratégias de *Sharding*/*Partitioning*. |
| **Incidentes** | Reinicia o servidor na lentidão | Identifica locks via `pg_stat_activity` e analisa *Wait Events*. |
| **Modelagem** | Deleta registros fisicamente | Implementa *Soft Delete* com Índices Parciais ou *Audit Logs*. |

---

## 3. Conceitos Teóricos (Internals e Arquitetura)

### Mecanismos de Armazenamento e WAL
* **Write-Ahead Logging (WAL):** O banco nunca escreve no arquivo de dados antes de garantir que a alteração está no log sequencial (WAL/Redo Log). Isso garante a durabilidade ACID.
* **Checkpoints:** O momento em que as páginas "sujas" (em memória) são sincronizadas com o disco. Checkpoints muito frequentes causam picos de I/O; muito esparsos aumentam o tempo de recovery (*crash recovery*).



### Concorrência e Heap Only Tuples (HOT)
No PostgreSQL, `UPDATE`s geram novas versões de linhas. O *HOT Update* permite que, se o update não alterar colunas indexadas e houver espaço livre na mesma página física, o banco evite atualizar os índices, reduzindo drasticamente o overhead de escrita e o *bloat*.

### Estruturas de Índices e Trade-offs
* **B-Tree:** Ideal para ordenação e busca por igualdade ou intervalo.
* **GIN (Generalized Inverted Index):** Essencial para busca em documentos JSONB ou arrays. **Trade-off:** lentidão extrema em inserções.
* **GiST:** Resolve problemas de proximidade (geolocalização/GIS).
* **Impacto em Escrita:** Muitos índices aumentam a latência de `INSERT`/`UPDATE` e o tempo de `CHECKPOINT`. O sênior utiliza **Índices Parciais** para indexar apenas dados ativos (ex: `WHERE status = 'ACTIVE'`).

---

## 4. Exercícios Práticos

### Nível Avançado (SQL & Tuning)

1. **CTE Recursiva:** Escreva uma query que percorra uma tabela de categorias pai-filho (Hierarquia) de profundidade arbitrária e identifique se há referências circulares.
2. **Memory Spill Diagnosis:** Force um `JOIN` pesado em uma sessão com `work_mem` baixo (ex: 4MB) e identifique no `EXPLAIN ANALYZE` o uso de arquivos temporários em disco (*External Merge Sort*).
3. **Tuning de Janela:** Use *Window Functions* (`LEAD`/`LAG`) para calcular a variação percentual de vendas entre meses e compare a performance com um `SELF JOIN` tradicional.

### Troubleshooting de Concorrência

Simule um **Deadlock** utilizando duas janelas de terminal abertas no banco:

* **Sessão A:** `UPDATE conta SET saldo = 100 WHERE id = 1;`
* **Sessão B:** `UPDATE conta SET saldo = 200 WHERE id = 2;`
* **Sessão A:** `UPDATE conta SET saldo = 300 WHERE id = 2;` *(Fica bloqueada)*
* **Sessão B:** `UPDATE conta SET saldo = 400 WHERE id = 1;` *(Gera o Deadlock)*

**Tarefa:** Identifique os PIDs envolvidos e a query causadora do bloqueio consultando as views `pg_locks` e `pg_stat_activity`.

---

## 5. Laboratórios

### Laboratório 1: Ambiente Linux Hardening para DBA
Configure um servidor Ubuntu 22.04 e aplique o tuning de kernel no arquivo `/etc/sysctl.conf`:

```ini
# Otimização para DB de alta carga
vm.swappiness = 10
vm.dirty_background_ratio = 5
vm.dirty_ratio = 15
vm.overcommit_memory = 2
kernel.sched_migration_cost_ns = 5000000tabase Administrator (EDB).
