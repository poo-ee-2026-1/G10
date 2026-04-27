# Sistema de Monitoramento de Consumo de Energia

Projeto em Java desenvolvido para aplicar conceitos de Programacao Orientada a Objetos em um problema da Engenharia Eletrica: monitoramento, simulacao e analise de consumo de energia em dispositivos residenciais.

O sistema usa uma interface grafica em Swing para cadastrar dispositivos, simular sensores, acompanhar consumo em tempo quase real, visualizar graficos, consultar analises mensais e abrir a analise diaria de cada mes.

Este projeto nao persiste dados em arquivo ou banco de dados. As leituras ficam em memoria durante a execucao.

## Guia de Execucao

Esta parte mostra apenas como compilar, executar e testar o projeto.

### 1. Compilar

Se o `javac` estiver no PATH:

```powershell
$files = Get-ChildItem src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac --release 8 -encoding UTF-8 -d bin $files
```

No Windows, caso esteja usando o JDK instalado pelo Eclipse Adoptium e ele ainda nao esteja no PATH:

```powershell
$files = Get-ChildItem src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
& 'C:\Program Files\Eclipse Adoptium\jdk-25.0.2.10-hotspot\bin\javac.exe' --release 8 -encoding UTF-8 -d bin $files
```

### 2. Executar a Interface

Se o `java` estiver no PATH:

```powershell
java -cp bin App
```

Com caminho completo do JDK:

```powershell
& 'C:\Program Files\Eclipse Adoptium\jdk-25.0.2.10-hotspot\bin\java.exe' -cp bin App
```

### 3. Rodar os Testes

Depois de compilar:

```powershell
java -cp bin testes.TestesSistemaEnergia
```

Com caminho completo do JDK:

```powershell
& 'C:\Program Files\Eclipse Adoptium\jdk-25.0.2.10-hotspot\bin\java.exe' -cp bin testes.TestesSistemaEnergia
```

Saida esperada:

```text
Todos os testes passaram.
```

### 4. Fluxo de Uso da Interface

1. Execute o projeto.
2. Cadastre ou use os dispositivos iniciais.
3. Ajuste a tarifa de energia se necessario.
4. Escolha a escala da simulacao no monitoramento.
5. Clique em iniciar monitoramento.
6. Abra a aba de graficos para acompanhar a potencia e o consumo.
7. Abra o calendario mensal para ver o consumo por mes.
8. De dois cliques em um mes para abrir a analise diaria.
9. Abra o relatorio mensal para consultar ou exportar o resumo.

## Objetivo do Projeto

Desenvolver um sistema capaz de:

- Cadastrar dispositivos eletricos.
- Simular sensores de energia.
- Medir potencia, energia e custo.
- Coletar automaticamente o tempo de uso pelo sensor.
- Monitorar consumo em escala de segundo, minuto, hora ou dia.
- Gerar graficos de consumo e potencia.
- Exibir calendario mensal considerando a quantidade real de dias de cada mes.
- Abrir uma analise diaria ao dar dois cliques em um mes.
- Gerar relatorio mensal detalhado.

## Funcionalidades Implementadas

- Interface grafica com abas.
- Cadastro de dispositivos.
- Configuracao da tarifa de energia.
- Registro de leitura manual de sensor.
- Monitoramento automatico em tempo real.
- Escala de simulacao:
  - 1 segundo
  - 1 minuto
  - 1 hora
  - 1 dia
- Coleta automatica do tempo de uso pelo sensor.
- Grafico de consumo mensal por dispositivo.
- Grafico de potencia em tempo real.
- Calendario mensal por ano.
- Meses com quantidade real de dias.
- Simulacao iniciada a partir da data atual do computador.
- Analise diaria ao dar dois cliques em um mes.
- Relatorio mensal detalhado.
- Exportacao de relatorio TXT.
- Testes manuais automatizados por classe `main`.

## Descricao do Codigo

Esta parte explica a organizacao interna do projeto, as classes principais e a responsabilidade de cada pacote.

### Estrutura Atual

```text
src/
  App.java
  dispositivos/
    DispositivoEletrico.java
  sensores/
    SensorEnergia.java
  sistema/
    SistemaEnergetico.java
  gui/
    SistemaEnergiaGUI.java
  simulacao/
    HistoricoConsumo.java
    LeituraSimulada.java
    RegistroConsumoDiario.java
    SimuladorEnergia.java
  testes/
    TestesSistemaEnergia.java
```

### App

Classe de entrada do sistema. Ela inicia a interface grafica:

```java
new SistemaEnergiaGUI().exibir();
```

### SistemaEnergiaGUI

Interface grafica feita com Swing. Ela concentra as telas do sistema:

- Cadastro e remocao de dispositivos.
- Registro manual de leitura de sensor.
- Monitoramento em tempo real.
- Escolha da escala de simulacao.
- Graficos.
- Calendario mensal.
- Analise diaria por mes.
- Relatorio mensal.
- Exportacao do relatorio em TXT.

### DispositivoEletrico

Representa um equipamento eletrico.

Campos principais:

- `nome`
- `potenciaWatts`
- `tempoUsoSegundos`
- `tempoMonitoradoSegundos`
- `energiaColetadaKWh`

O dispositivo nao depende mais de horas de uso digitadas manualmente. O tempo de uso e coletado quando o sensor registra uma potencia maior que o limite minimo de uso.

Metodos importantes:

- `registrarLeituraSensor(...)`
- `obterTempoUsoSegundos()`
- `obterTempoMonitoradoSegundos()`
- `obterEnergiaColetadaKWh()`
- `projetarConsumoMensalKWh()`
- `obterPotenciaMediaColetadaWatts()`

### SensorEnergia

Simula um sensor eletrico conectado a um dispositivo.

Ele calcula:

```text
P = V * I
```

onde:

- `P` = potencia em watts
- `V` = tensao em volts
- `I` = corrente em amperes

Tambem calcula energia por leitura:

```text
E(kWh) = P(W) * tempo(s) / 3.600.000
```

Metodos principais:

- `medirPotencia()`
- `detectarUso()`
- `medirEnergiaKWh()`
- `getDuracaoLeituraSegundos()`

### SistemaEnergetico

Gerencia a lista de dispositivos e a tarifa de energia.

Responsabilidades:

- Adicionar dispositivos.
- Remover dispositivos.
- Calcular energia total coletada.
- Calcular projecao mensal.
- Calcular custo pela tarifa configurada.

### HistoricoConsumo

Controla o historico simulado de consumo por dia.

Ele usa a data atual do computador como ponto inicial e avanca a simulacao em sequencia. Se uma leitura atravessa a virada do dia, o consumo e dividido corretamente entre os dias envolvidos.

Responsabilidades:

- Registrar periodos simulados.
- Calcular energia por mes.
- Calcular energia por ano.
- Calcular energia por dispositivo em cada mes.
- Recuperar registros diarios para a analise de duplo clique no calendario.

### RegistroConsumoDiario

Representa o consumo de um dia especifico.

Armazena:

- Data.
- Tempo monitorado.
- Energia total do dia.
- Energia por dispositivo.
- Maior consumidor do dia.

### SimuladorEnergia

Gera leituras simuladas de potencia considerando o tipo de dispositivo e o horario simulado.

Exemplos de comportamento:

- Geladeira alterna ciclos de carga.
- Chuveiro tem maior chance de uso de manha e a noite.
- Lampada tende a ligar mais a noite.
- Televisao tende a ligar mais no periodo da noite.
- Ar-condicionado varia conforme horario.

### TestesSistemaEnergia

Classe simples de testes sem bibliotecas externas.

Ela valida:

- Calculo de potencia e energia do sensor.
- Projecao de consumo a partir do tempo monitorado.
- Avanco sequencial da data simulada.
- Divisao de consumo quando a leitura cruza a virada do dia.

## Formulas Utilizadas

Potencia eletrica:

```text
P = V * I
```

Energia em kWh:

```text
E = P * t / 3.600.000
```

onde:

- `P` = potencia em watts.
- `t` = tempo em segundos.
- `E` = energia em kWh.

Custo:

```text
Custo = energia_kWh * tarifa
```

## Conceitos de POO Aplicados

- Encapsulamento dos atributos principais.
- Separacao de responsabilidades por classe.
- Composicao entre sistema, dispositivos, sensores e historico.
- Organizacao por pacotes.
- Classes especificas para simulacao e registro historico.

## Melhorias Futuras

- Persistir leituras em arquivo ou banco de dados.
- Adicionar filtros por dispositivo no relatorio.
- Exportar graficos.
- Criar alertas configuraveis por usuario.
- Integrar com sensores reais ou IoT.
- Adicionar mais testes de interface e cenarios de simulacao.
