# Sistema de Monitoramento de Consumo de Energia

Aplicacao desktop em Java para monitoramento, simulacao e analise de consumo de energia em dispositivos residenciais. O projeto foi desenvolvido com foco em Programacao Orientada a Objetos aplicada a um problema da Engenharia Eletrica, reunindo cadastro de equipamentos, leitura de sensores, historico de consumo, graficos e relatorios em uma interface grafica feita com Swing.

O sistema executa sem dependencias externas e mantem os dados em memoria durante a sessao. Isso o torna simples de compilar, executar e estudar, ao mesmo tempo em que preserva uma estrutura preparada para futuras evolucoes, como persistencia de dados e integracao com sensores reais.

## Principais Recursos

- Cadastro e remocao de dispositivos eletricos.
- Configuracao da tarifa de energia.
- Registro manual e simulacao automatica de leituras de sensores.
- Monitoramento em escalas de segundo, minuto, hora ou dia.
- Graficos de potencia em tempo real, consumo mensal por dispositivo e consumo diario do mes selecionado.
- Calendario mensal com quantidade real de dias por mes.
- Analise diaria detalhada por duplo clique em um mes do calendario.
- Relatorio mensal com exportacao em arquivo TXT.
- Simulacao baseada em estrategias por tipo de equipamento.
- Validacao de entrada, tratamento padronizado de excecoes e formatacao centralizada.
- Testes automatizados executaveis sem bibliotecas externas.

## Tecnologias

- Java
- Swing
- Java Collections Framework
- API de datas com `Calendar`
- Compilacao via `javac`

## Requisitos

- JDK 8 ou superior.
- Terminal com `javac` e `java` disponiveis no `PATH`, ou caminho absoluto para os executaveis do JDK.

## Como Executar

### 1. Compilar o projeto

```powershell
$files = Get-ChildItem src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac --release 8 -encoding UTF-8 -d bin $files
```

Caso o JDK nao esteja no `PATH`, informe o caminho completo do executavel:

```powershell
$files = Get-ChildItem src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
& 'C:\Program Files\Eclipse Adoptium\jdk-25.0.2.10-hotspot\bin\javac.exe' --release 8 -encoding UTF-8 -d bin $files
```

### 2. Iniciar a interface grafica

```powershell
java -cp bin App
```

Com caminho absoluto do JDK:

```powershell
& 'C:\Program Files\Eclipse Adoptium\jdk-25.0.2.10-hotspot\bin\java.exe' -cp bin App
```

### 3. Executar os testes

```powershell
java -cp bin testes.TestesSistemaEnergia
```

Saida esperada:

```text
Todos os testes passaram.
```

## Fluxo Basico de Uso

1. Inicie a aplicacao.
2. Cadastre novos dispositivos ou utilize os dispositivos iniciais.
3. Ajuste a tarifa de energia, se necessario.
4. Escolha a escala de simulacao no painel de monitoramento.
5. Inicie o monitoramento.
6. Consulte os graficos para acompanhar potencia e consumo.
7. Explore o calendario mensal para analisar o historico por periodo.
8. De dois cliques em um mes para abrir a analise diaria.
9. Gere ou exporte o relatorio mensal.

## Organizacao do Projeto

```text
src/
  App.java
  dispositivos/
    DispositivoEletrico.java
  gui/
    SistemaEnergiaGUI.java
  sensores/
    SensorEnergia.java
  simulacao/
    EstrategiaSimulacaoDispositivo.java
    EstrategiasSimulacaoPadrao.java
    HistoricoConsumo.java
    LeituraSimulada.java
    RegistroConsumoDiario.java
    SimuladorEnergia.java
  sistema/
    SistemaEnergetico.java
  testes/
    TestesSistemaEnergia.java
  util/
    ConstantesEnergia.java
    FormatadorEnergia.java
    TratadorExcecoes.java
    ValidadorEntrada.java
```

## Arquitetura

### `App`

Ponto de entrada da aplicacao. Inicializa a interface grafica na thread de eventos do Swing.

### `gui`

Concentra a experiencia do usuario na classe `SistemaEnergiaGUI`, responsavel por:

- cadastro e remocao de dispositivos;
- configuracao de tarifa;
- monitoramento em tempo real;
- exibicao de graficos;
- calendario mensal;
- analise diaria;
- geracao e exportacao de relatorios.

### `dispositivos`, `sensores` e `sistema`

- `DispositivoEletrico` representa cada equipamento monitorado e acumula tempo de uso, energia coletada e projecoes.
- `SensorEnergia` calcula potencia e energia a partir de tensao, corrente e duracao da leitura.
- `SistemaEnergetico` gerencia os dispositivos cadastrados e centraliza os calculos de consumo e custo.

### `simulacao`

- `SimuladorEnergia` gera leituras simuladas conforme o horario e o perfil do dispositivo.
- `EstrategiaSimulacaoDispositivo` define o contrato para novos comportamentos de simulacao.
- `EstrategiasSimulacaoPadrao` fornece perfis prontos para equipamentos como geladeira, chuveiro, iluminacao, televisao e ar-condicionado.
- `HistoricoConsumo` organiza o consumo por dia e divide corretamente leituras que atravessam a virada de data.
- `RegistroConsumoDiario` e `LeituraSimulada` modelam os dados historicos usados nos relatorios e graficos.

### `util`

Pacote de apoio com responsabilidades transversais:

- `ConstantesEnergia`: constantes de dominio e fatores de simulacao.
- `FormatadorEnergia`: padronizacao da exibicao de energia, potencia, moeda e tempo.
- `ValidadorEntrada`: validacoes de dados informados pelo usuario.
- `TratadorExcecoes`: tratamento consistente de erros e mensagens amigaveis.

## Regras e Calculos

Potencia eletrica:

```text
P = V * I
```

Energia em kWh:

```text
E = P * t / 3.600.000
```

Custo:

```text
Custo = energia_kWh * tarifa
```

Onde:

- `P` representa a potencia em watts.
- `V` representa a tensao em volts.
- `I` representa a corrente em amperes.
- `t` representa o tempo em segundos.

## Testes Cobertos

A classe `testes.TestesSistemaEnergia` valida:

- calculo de potencia e energia do sensor;
- projecao de consumo com base no tempo monitorado;
- avancos sequenciais da data simulada;
- divisao de consumo quando uma leitura cruza a virada do dia;
- protecao da lista interna de dispositivos;
- validacao de entradas do sensor;
- extensibilidade do simulador por estrategia customizada.

## Conceitos de POO Aplicados

- Encapsulamento de estado e comportamento.
- Separacao de responsabilidades por classe e pacote.
- Composicao entre sistema, dispositivos, sensores e historico.
- Uso de interfaces para extensibilidade das estrategias de simulacao.
- Reaproveitamento de utilitarios comuns para reduzir duplicacao.

## Limitacoes Atuais

- Os dados nao sao persistidos em arquivo ou banco de dados.
- A aplicacao ainda nao se integra a sensores reais.
- Os testes sao executados por uma classe `main`, sem framework dedicado.

## Possiveis Evolucoes

- Persistencia local ou em banco de dados.
- Integracao com sensores IoT.
- Exportacao de graficos.
- Alertas configuraveis de consumo.
- Filtros mais avancados nos relatorios.
- Adocao de um framework de testes como JUnit.
- Separacao mais profunda da interface grafica em camadas como MVC.
