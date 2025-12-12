# REPORT UPDATE TODO:

Nesta sessão, o foco passou da geração visual da cidade para a sua estruturação lógica, de modo a permitir a futura simulação de agentes (carros civis e polícia).

### 1. Extração explícita das estradas

O comportamento da turtle foi estendido através da classe RoadTurtle, permitindo que cada segmento desenhado fosse armazenado como um objeto RoadSegment (com ponto inicial e final no espaço do mundo).
Desta forma, a cidade deixou de ser apenas um desenho e passou a ser representada como um conjunto explícito de segmentos de estrada.

### 2. Construção da Road Network (grafo)

A principal evolução desta sessão foi a criação da Road Network, que converte o conjunto de segmentos num grafo topológico:

Cada extremidade de estrada é convertida num nó (Node).

Pontos espacialmente próximos são unificados através de um mecanismo de snap com tolerância (eps).

Cada estrada gera uma aresta não-direcionada entre dois nós.

O grafo é armazenado como uma lista de adjacências, permitindo consultas eficientes aos vizinhos de cada nó.

> Estado Atual do Projeto
> No final desta sessão, o projeto possui:
- Uma cidade procedimental baseada em fractais (L-Systems).
- Uma representação explícita das estradas.
- Uma Road Network funcional, pronta para navegação.
- Uma base sólida para a introdução de agentes móveis.

### Próximo Passo

O próximo passo natural do projeto será a introdução de agentes (carros) que se movimentem ao longo da RoadNetwork, começando por um comportamento simples de random walk, que será posteriormente evoluído para comportamentos de fuga (presas) e perseguição (predadores).