Guia de Referências Técnicas para DBAs

Este documento organiza as fontes de estudo e ferramentas extraídas do material técnico, estruturadas para facilitar a consulta rápida em repositórios de desenvolvimento por DBAs e Arquitetos de Dados.

1. Documentação Oficial

* MySQL 8.0 Reference Manual: Arquivo disponível no NotebookLM. Referência primária para sintaxe, configuração e administração do motor MySQL.

2. Performance e Tuning

* Monitoramento de desempenho no Linux - MoDCS (Rubens Matos): Arquivo disponível no NotebookLM. Material técnico focado no comportamento do sistema durante testes de stress, abordando métricas de workers e alocação/desalocação de memória.
* iostat: Utilitário de monitoramento de I/O capaz de gerar relatórios de utilização de CPU (nível de sistema) e de utilização de dispositivos individuais (ex: iostat -d /dev/sda).
* pidstat: Ferramenta avançada para monitoramento de estatísticas de processos, incluindo processos filhos e threads associadas de forma conjunta.
* mpstat: Utilitário integrante do pacote sysstat voltado para o monitoramento de performance de processadores e recursos de memória.

3. Internals e Manutenção

* /proc/meminfo: Interface do sistema que fornece contadores essenciais (MemFree, Buffers, Cached, SwapFree) para avaliação de pressão e desempenho de memória global.
* /proc/<pid>/status: Interface para medição de recursos de processos específicos. Destaca métricas críticas: VmSize (memória virtual total), VmRSS (RAM física/Resident Set Size) e VmHWM (pico de RSS atingido pelo processo).
* sa2: Script do pacote sysstat utilizado para automatizar a geração de relatórios diários de performance com base nos dados coletados.

4. Infraestrutura e Linux

* Man-pages do Linux: Documentação nativa do sistema; utilize o comando man proc para detalhes exaustivos sobre a hierarquia e os contadores do sistema de arquivos /proc.
* Site oficial do iostat: Repositório externo para atualizações e documentação da ferramenta desenvolvida por Sebastien Godard.
* Pacote sysstat: Conjunto de utilitários de baixo nível para coleta de dados de desempenho, gerenciamento de intervalos de monitoramento e conversão de unidades.
* Pseudo-sistema de arquivos /proc (Proc filesystem): Interface estruturada para o kernel; atua como ponte para acessar estruturas de dados internas, contadores de hardware e alteração de parâmetros do SO em tempo de execução.

5. Cloud e Alta Disponibilidade

* Nenhuma fonte específica identificada no contexto atual.

6. SQL Avançado

* MySQL Triggers (Prof. B.A. Khivsara): Arquivo disponível no NotebookLM. Guia sobre gatilhos (BEFORE/AFTER) para controle de integridade, auditoria e lógica de negócios invisível à aplicação.
* Aula 15: Procedimentos armazenados e Funções no MySQL (Edmilson Campos): Arquivo disponível no NotebookLM. Conteúdo baseado na obra "SQL: Curso Prático" de Celso Henrique. Aborda o uso de DELIMITER para blocos de código, parâmetros (IN, OUT, INOUT) e a palavra reservada INTO para atribuição de resultados em variáveis.
* Stored Procedures no MySQL: Artigo técnico focado na implementação de objetos programáveis e lógica procedural no servidor.
* Implementando controle de estoque no MySQL com Triggers e Procedures: Estudo de caso prático para aplicação de lógica de banco de dados em cenários de movimentação de inventário.


--------------------------------------------------------------------------------


Rodapé Informativo A curadoria deste índice é baseada em materiais acadêmicos e técnicos desenvolvidos por especialistas como Rubens Matos, Prof. B.A. Khivsara e Edmilson Campos, consolidando fontes para fins de referência profissional e estudo avançado em administração de bancos de dados.
