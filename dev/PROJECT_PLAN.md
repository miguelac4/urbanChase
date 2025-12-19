# PROJECT_PLAN.md — MSSN Final Project (Atualizado)

## Fase 1 — Fractal da Cidade
- [X] Definir L-System da cidade
  - Axioma: `F`
  - Regra: `F → F[+F]F[-F]F`
  - Ângulo: `90°`
- [X] Criar sistema de desenho da cidade com Turtle + L-System
- [X] Desenhar múltiplas redes de estradas (duas turtles com posições/orientações distintas)

## Fase 2 — Estruturas das Estradas
- [X] Criar classe `RoadSegment` (start, end)
- [X] Modificar Turtle para armazenar segmentos desenhados
- [X] Construir lista global de segmentos de estrada

## Fase 3 — Rede Viária (Grafo)
- [X] Criar classe `RoadNetwork`
- [X] Criar nós e arestas a partir dos segmentos desenhados
- [X] Agrupar pontos próximos através de *snap grid*
- [X] Implementar funções de vizinhança (grafo)
- [X] Validar navegabilidade da rede com um agente simples

## Fase 4 — Agentes Autónomos (Base)
- [X] Criar agente base com navegação em grafo
- [X] Evoluir o agente para um **Boid real**, baseado em física
- [X] Implementar movimento com *steering behaviors* (Reynolds)
  - arrive
  - limitação de velocidade
- [X] Restrição do movimento às estradas através de força de correção ao segmento

## Fase 5 — Comportamentos em Grupo (Predador–Presa)
- [X] Criar classe base `RoadAgent` (extende `Boid`)
- [X] Criar `CivilCar` (presas)
- [X] Criar `PoliceCar` (predadores)

### Civis
- [X] Civis deslocam-se aleatoriamente pela rede
- [X] Preferência por segmentos mais longos
- [X] Estados do civil:
  - Legal (verde)
  - Ilegal (amarelo)
  - Em fuga (laranja)
- [X] Transição probabilística para estado ilegal
- [X] Transição apenas quando não há polícia no raio de visão
- [X] Civis legais param temporariamente quando detetam polícia em perseguição
- [X] Civis ilegais fogem da polícia maximizando a distância ao alvo

### Polícia
- [X] Patrulhamento aleatório da rede
- [X] Deteção de civis ilegais dentro de um raio de visão
- [X] Perseguição baseada na minimização da distância ao alvo
- [X] Sirene visual (alternância azul/vermelho a cada 0.4s)
- [X] Captura de civis ilegais:
  - ambos param durante alguns segundos
  - civil regressa ao estado legal
  - polícia regressa ao patrulhamento
- [X] Polícia ignora civis legais

## Fase 6 — Steering e Física (Integrado)
- [X] Movimento contínuo baseado em forças
- [X] Velocidade máxima limitada
- [X] Suavização de movimento (abrandamento ao chegar aos nós)
- [X] Força de atração à estrada (*road snapping*)

## Fase 7 — Visualização e Interação
- [X] Visualização simultânea de múltiplos agentes
- [X] Visualização do raio de visão da polícia
- [X] Zoom e navegação com o rato
- [X] Diferenciação visual clara entre tipos e estados de agentes

## Fase 8 — Relatório
- [X] Descrição do modelo urbano
- [X] Descrição da navegação no grafo
- [X] Justificação do uso de Boids e steering behaviors
- [X] Descrição detalhada dos comportamentos de grupo
- [ ] Discussão de limitações
- [ ] Conclusão final

__________

### Pretendo adicionar ao project structure:

  > Passado 60 - 90 segundos (randomly) do carro estar [ILEGAL] caso não esteja no alcance da policia (cor amarela) pretendo que o carro volte ao seu estado [LEGAL], caso esse randomly passe e o carro encontra-se em fuga (laranja) proximo instante que não estiver em fuga fica [LEGAL].

  > Adicionar sistema de particulas aos carros civis [ILEGAL] quando detetam a policia e alterão a sua cor para laranja.

