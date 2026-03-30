# ⚡ Sistema de Monitoramento de Consumo de Energia

Projeto desenvolvido para aplicar conceitos de **Programação Orientada a Objetos (POO)** em um problema real da **Engenharia Elétrica**: o monitoramento e análise do consumo de energia elétrica em dispositivos residenciais.

O sistema simula sensores elétricos conectados a equipamentos, calcula **potência**, **consumo energético** e **custo da energia**, permitindo analisar o comportamento de cargas elétricas dentro de uma residência ou sistema energético.

---

# 🎯 Objetivo do Projeto

Desenvolver um sistema capaz de:

- Monitorar dispositivos elétricos
- Calcular **consumo de energia**
- Estimar **custo da energia elétrica**
- Simular medições de sensores elétricos
- Identificar **possíveis sobrecargas no sistema**

O projeto integra conceitos fundamentais da engenharia elétrica como:

- Potência elétrica
- Consumo de energia (Wh / kWh)
- Monitoramento de cargas
- Eficiência energética

Além disso, aplica conceitos essenciais de **POO**, como encapsulamento, herança e polimorfismo.

---

# 🧱 Estrutura do Projeto

O sistema é composto por três classes principais.

## 1️⃣ DispositivoEletrico

Representa um equipamento elétrico presente no sistema.

### Atributos

- `nome`
- `potenciaWatts`
- `horasUsoPorDia`

### Métodos

- `calcularConsumoDiario()`
- `calcularConsumoMensal()`
- `calcularCustoEnergia(tarifaEnergia)`

---

## 2️⃣ SensorEnergia

Simula um sensor responsável por medir grandezas elétricas de um dispositivo.

### Atributos

- `idSensor`
- `dispositivo`
- `tensao`
- `corrente`

### Métodos

- `medirPotencia()`
- `registrarLeitura()`

A potência é calculada utilizando a fórmula:


P = V × I


onde:

- `P` = potência (Watts)
- `V` = tensão (Volts)
- `I` = corrente (Ampères)

---

## 3️⃣ SistemaEnergetico (ou Residencia)

Responsável por gerenciar todos os dispositivos do sistema.

### Atributos

- `listaDispositivos`
- `tarifaEnergia`

### Métodos

- `adicionarDispositivo()`
- `calcularConsumoTotal()`
- `calcularCustoTotal()`

---

# 🔗 Relacionamento entre Classes


SistemaEnergetico
|
| possui
v
DispositivoEletrico
|
| monitorado por
v
SensorEnergia


O **SistemaEnergetico** gerencia diversos **DispositivosEletricos**, que podem ser monitorados por **Sensores de Energia**.

---

# 🧠 Conceitos de POO Aplicados

## Encapsulamento

Os atributos das classes são privados e acessados através de **getters e setters**, garantindo controle sobre os dados.

---

## Herança

A classe `DispositivoEletrico` pode servir como base para dispositivos específicos:


DispositivoEletrico
├── MotorEletrico
├── Lampada
└── ArCondicionado


Cada tipo de dispositivo pode possuir comportamentos diferentes.

---

## Polimorfismo

O método `calcularConsumo()` pode ter implementações distintas dependendo do tipo de dispositivo.

Exemplo:

- **Motor elétrico** → consumo depende do fator de carga
- **Lâmpada** → consumo constante
- **Ar-condicionado** → consumo depende do ciclo de funcionamento

---

# ⚙️ Funcionalidades do Sistema

- Cadastro de dispositivos elétricos
- Simulação de sensores de energia
- Cálculo de potência elétrica
- Cálculo de consumo diário
- Cálculo de consumo mensal
- Estimativa de custo da energia
- Relatório de consumo energético

---

# 📊 Fórmulas Utilizadas

### Potência Elétrica


P = V × I


### Energia Consumida


E = P × t


onde:

- `P` = potência (W)
- `t` = tempo de funcionamento (h)
- `E` = energia consumida (Wh ou kWh)

---

# ⚠️ Simulação de Sobrecarga

O sistema pode incluir uma verificação de **sobrecarga elétrica**.

Se a potência total ultrapassar um limite pré-definido:


⚠️ ALERTA: SOBRECARGA NA REDE ELÉTRICA


Isso simula um cenário real de **dimensionamento de carga elétrica** em sistemas residenciais.

---

# 🛠️ Possíveis Melhorias Futuras

- Interface gráfica para visualização do consumo
- Integração com **IoT ou sensores reais**
- Análise de **eficiência energética**
- Simulação de **picos de carga**
- Exportação de relatórios de consumo

---

# 🎓 Contexto Acadêmico

Este projeto foi desenvolvido como aplicação prática de:

- **Programação Orientada a Objetos**
- **Modelagem de sistemas**
- **Aplicação de conceitos da Engenharia Elétrica**

O objetivo é demonstrar a integração entre **software e sistemas elétricos**, aproximando programação de problemas reais da área.

📅 Cronograma de Desenvolvimento do Projeto
🔹 Fase 1 — Planejamento e Base (23/03 → 06/04)

Objetivo: estruturar corretamente o projeto (evitar refatoração caótica depois)

Revisar modelagem atual das classes
Definir linguagem e estrutura de pastas
Implementar:
DispositivoEletrico
SensorEnergia
SistemaEnergetico
Criar primeiros testes simples (mesmo que básicos)
Versionamento no Git (commit organizado desde o início)

🔹 Fase 2 — Funcionalidades Core (07/04 → 27/04)

Objetivo: sistema funcionando de ponta a ponta

Implementar:
Cálculo de consumo diário e mensal
Cálculo de custo total
Integração entre classes
Criar fluxo principal (main)
Simulação de múltiplos dispositivos
Testes mais completos

🔹 Fase 3 — POO de Verdade (28/04 → 18/05)

Objetivo: sair do básico e mostrar domínio real de POO

Implementar herança:
MotorEletrico
Lampada
ArCondicionado
Aplicar polimorfismo nos cálculos
Melhorar encapsulamento (validações, proteção de atributos)
Refatoração geral do código

🔹 Fase 4 — Funcionalidades Avançadas (19/05 → 08/06)

Objetivo: adicionar “peso” ao projeto

Sistema de alerta de sobrecarga (bem implementado, não só print)
Histórico de consumo (lista ou registro de leituras)
Relatórios de consumo (resumo por dispositivo)
Tratamento de erros

🔹 Fase 5 — Diferencial (09/06 → 22/06)

Objetivo: fugir do comum (isso impacta muito na avaliação)

Escolher 1 ou 2:

Interface simples (CLI organizada ou GUI básica)
Exportação de relatório (CSV ou TXT)
Simulação de cenários (pico de carga, uso variável)
Estrutura preparada para IoT (mesmo que mock)

🔹 Fase 6 — Finalização e Apresentação (23/06 → 08/07)

Objetivo: lapidar (fase mais subestimada)

Revisão completa do código
Melhorar README (explicação clara + exemplos)
Preparar apresentação:
Problema → solução → arquitetura → demonstração
Ensaiar apresentação (isso muda completamente a nota)
Corrigir bugs finais

