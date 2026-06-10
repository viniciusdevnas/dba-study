# 🚀 Plano de Sprints: Engenharia de Software e Cloud DBA (SGV)

*   **Foco:** 20% Conceito (Arquitetura/UML) | 80% Mão na Massa (SQL/Cloud)
*   **Ambiente:** Local (Windows/pgAdmin) ➡️ Nuvem (Google Cloud Platform)
*   **Frequência:** 1 Sprint por Semana (1 Dia de dedicação exclusivo)

---

## 🏃‍♂️ Ciclo 1: Engenharia Reversa e Documentação UML

### Sprint 1: Mapeamento Estrutural (O Diagrama de Classes)
*   **Teoria (20%):** Sintaxe rigorosa do Diagrama de Classes UML. Entender a tradução visual de Associações, Agregações e Composições a partir do DDL.
*   **Prática (80%):** Pegar o DDL atual do seu SGV e fazer a engenharia reversa. Desenhar o Diagrama de Classes do core do sistema (`office`, `seller`, `client`, `product`).
*   **Desafio de Entrega:** Mapear a relação entre `requested` (Pedido) e `item_requested` (Itens). Garantir que a Composição gráfica reflita a constraint `ON DELETE CASCADE` do banco.

### Sprint 2: Mapeamento de Fronteiras (Diagrama de Casos de Uso)
*   **Teoria (20%):** Conceito de Atores, Limites do Sistema e relacionamentos `<<include>>` e `<<extend>>` aplicados a processos comerciais.
*   **Prática (80%):** Modelar o Diagrama de Casos de Uso do SGV. Identificar as ações dos atores (`seller`, `manager`, `system`).
*   **Desafio de Entrega:** Desenhar o caso de uso "Efetivar Venda" e mapear o gatilho automático que dispara a validação de estoque e o cálculo estatístico como um `<<include>>`.

---

## 🛢️ Ciclo 2: Preparação de Massa e Otimização Local (DML)

### Sprint 3: Planejamento de Carga e Geração de Dados (Seeds)
*   **Teoria (20%):** Teoria de integridade referencial para importação. Estratégias de carga em lote (Bulk Insert) vs. Inserts individuais.
*   **Prática (80%):** Definir a ordem exata de importação baseada nas chaves estrangeiras ($FK$). Usar o agente de IA para gerar arquivos `.csv` ricos e realistas para todas as tabelas (escala de 5.000 a 10.000 linhas para testes locais).
*   **Desafio de Entrega:** Realizar a importação de todos os arquivos `.csv` para o seu Postgres local no Windows utilizando a ferramenta "Import/Export" do pgAdmin.

### Sprint 4: Dominando DML e Análise Estatística (PL/pgSQL)
*   **Teoria (20%):** Entender o funcionamento interno de agregação de dados (`GROUP BY` vs `HAVING`) e o comportamento de memória em funções procedurais.
*   **Prática (80%):** Escrever e testar as queries de análise comercial. Colocar para rodar a procedure em PL/pgSQL que calcula a variância e o desvio padrão dos salários/vendas dos vendedores.
*   **Desafio de Entrega:** Validar matematicamente se o resultado da sua procedure bate com os dados gerados pela IA no passo anterior.

### Sprint 5: Tuning Local e Planos de Execução
*   **Teoria (20%):** Como o otimizador do Postgres lê o disco. Diferença entre *Sequential Scan* e *Index Scan*. O impacto dos índices B-Tree.
*   **Prática (80%):** Usar o comando `EXPLAIN ANALYZE` no pgAdmin para auditar as queries mais pesadas do SGV (como buscas de vendas por período cruzando fabricantes).
*   **Desafio de Entrega:** Identificar um gargalo de leitura, criar um índice estratégico para corrigi-lo e tirar um print do "antes e depois" do tempo de execução para o seu portfólio.

---

## ☁️ Ciclo 3: Migração e Arquitetura Google Cloud (GCP)

### Sprint 6: Provisionamento no Google Cloud SQL
*   **Teoria (20%):** Conceitos de Cloud: Bancos de dados gerenciados, Alta Disponibilidade (HA), tipos de armazenamento (SSD vs HDD na nuvem) e cálculo de IOPS.
*   **Prática (80%):** Criar a conta na GCP, ativar os créditos gratuitos, criar o projeto `SGV-Fintech` e provisionar uma instância do Cloud SQL para PostgreSQL pelo navegador.
*   **Desafio de Entrega:** Configurar as regras de firewall e rede na nuvem para permitir que o seu pgAdmin local (Windows) se conecte com segurança ao IP público do banco na GCP.

### Sprint 7: Estratégia de Extração e Dump de Dados
*   **Teoria (20%):** Teoria de Backup Lógico vs. Backup Físico. Anatomia do utilitário `pg_dump`.
*   **Prática (80%):** Executar a extração do banco de dados local do SGV no Windows via Prompt de Comando, gerando um arquivo `.dump` limpo e estruturado (DDL + DML).
*   **Desafio de Entrega:** Criar um Bucket no Google Cloud Storage (GCS) e fazer o upload do arquivo de dump para lá.

### Sprint 8: O Grande Deploy na Nuvem (Restauração)
*   **Teoria (20%):** Mecanismos de importação em nuvem e gerenciamento de privilégios de superusuário (Cloud IAM).
*   **Prática (80%):** Disparar o processo de importação no Cloud SQL puxando o arquivo `.dump` direto do Bucket do Storage. 
*   **Desafio de Entrega:** Conectar o pgAdmin na instância da nuvem e rodar queries de contagem (`COUNT`) para garantir que 100% dos dados e das suas procedures de estatística subiram com integridade.

---

## 🔐 Ciclo 4: Segurança, Auditoria e Portfólio

### Sprint 9: Governança, Roles e Backup em Nuvem
*   **Teoria (20%):** Princípio do menor privilégio (Least Privilege). Estratégias de Point-in-Time Recovery (PITR) na nuvem.
*   **Prática (80%):** Criar papéis de acesso no Postgres (`ROLES`) separando o que um usuário da aplicação pode fazer do que um Analista de BI pode ler. Configurar a janela de backup automático no painel da GCP.
*   **Desafio de Entrega:** Testar um acesso restrito: garantir que a role de leitura não consiga rodar um `DROP TABLE` ou alterar dados de vendas.

### Sprint 10: Consolidação de Arquitetura e Vitrine (LinkedIn)
*   **Teoria (20%):** Storytelling técnico para engenharia de software. Como apresentar projetos de infraestrutura de forma comercial.
*   **Prática (80%):** Organizar o repositório do Git com o DDL original, os Diagramas UML gerados, as procedures estatísticas e prints do painel do Google Cloud SQL rodando.
*   **Desafio de Entrega:** Publicar o encerramento do projeto no LinkedIn destacando seu domínio em: Engenharia Reversa com UML, Otimização de Queries e Deploy de Banco de Dados Relacional na GCP.
