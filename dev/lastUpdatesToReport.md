Quando `t ≥ 1`, o agente atinge o nó destino e seleciona um novo nó adjacente.

---

### Veículos Civis (`CivilCar`)

Os veículos civis representam carros comuns que circulam pela cidade. Estes agentes podem assumir diferentes estados e reagir dinamicamente à presença policial.

#### Estados do Civil
- **LEGAL** – circulação normal (cor verde);
- **ILEGAL** – circulação em excesso de velocidade (cor amarela);
- **EM FUGA** – quando foge da polícia (cor laranja).

#### Regras de Comportamento dos Civis

- **Movimento base**  
  Quando não existe perigo, os civis deslocam-se aleatoriamente pela rede, privilegiando segmentos mais longos para trajetos mais naturais.

- **Transição para estado ilegal**  
  Um civil legal pode tornar-se ilegal de forma probabilística, com uma probabilidade definida por segundo.  
  Esta transição só ocorre se não existir um veículo da polícia dentro do seu raio de visão.

- **Reação à polícia (civil legal)**  
  Se um civil legal detetar um carro da polícia em modo de perseguição dentro do seu raio de visão, o veículo para temporariamente.

- **Reação à polícia (civil ilegal)**  
  Um civil ilegal tenta fugir da polícia:
    - Escolhe o nó adjacente que maximiza a distância ao veículo policial;
    - Altera a sua cor para laranja durante a fuga;
    - Penaliza nós próximos de outros civis, evitando congestionamento.

---

### Veículos da Polícia (`PoliceCar`)

Os veículos da polícia representam agentes predadores responsáveis por patrulhar a cidade e intervir sobre civis ilegais.

#### Regras de Comportamento da Polícia

- **Patrulhamento**  
  Na ausência de alvos ilegais, a polícia patrulha a rede de forma aleatória, privilegiando segmentos mais longos.

- **Deteção de alvos**  
  A polícia apenas considera como alvo civis em estado ilegal que se encontrem dentro de um determinado raio de visão.

- **Perseguição**  
  Quando um civil ilegal é detetado:
    - A polícia entra em modo de perseguição;
    - A sirene visual é ativada, alternando entre azul e vermelho com um período de 0.4 segundos;
    - O próximo nó é escolhido de forma a minimizar a distância ao alvo.

- **Captura**  
  Quando a distância entre a polícia e o civil ilegal é inferior a um raio de captura:
    - Ambos os veículos param durante alguns segundos;
    - O civil é convertido novamente para o estado legal;
    - A polícia regressa ao modo de patrulhamento.

---

### Comunicação Implícita entre Agentes

Embora não exista comunicação direta entre agentes, os comportamentos emergem através de regras locais:

- Os civis reagem ao estado da polícia (perseguição ativa);
- A polícia ignora civis legais;
- Não existem colisões físicas diretas — todas as interações são baseadas em distâncias, estados internos e decisões locais.

---

### Considerações Finais

Este conjunto de regras permite observar comportamentos emergentes complexos, tais como perseguições dinâmicas, interrupções temporárias do tráfego civil e padrões de fuga condicionados pela topologia urbana.

A estrutura modular adotada facilita a extensão futura do sistema, permitindo a introdução de novos tipos de agentes, regras adicionais ou mecanismos evolutivos.
