# PROJECT_PLAN.md — MSSN Final Project

## Fase 1 — Fractal da Cidade
- [X] Definir L-System da cidade
    - Axioma: `F`
    - Regra: `F → F[+F]F[-F]F`
    - Ângulo: `90°`
- [X] Criar classe `CityMap` para desenhar a cidade (usar L-System + Turtle)
- [X] Desenhar duas redes de estradas (duas turtles com diferentes posições/orientações)

## Fase 2 — Estruturas das Estradas
- [X] Criar classe `RoadSegment` (start, end)
- [X] Modificar Turtle para guardar cada segmento desenhado
- [X] Criar lista `roads` com todos os segmentos da cidade

## Fase 3 — Rede Viária (Grafo)
- [X] Criar classe `RoadNetwork`
- [X] Detetar interseções entre `RoadSegment`
- [X] Criar nós e arestas (grafo de estradas)
- [X] Funções úteis:
    - obter estradas ligadas a um nó
    - obter direção ao longo de uma estrada
    - Teste da Deslocação no Grafo `RoadAgent`

## Fase 4 — Agentes
- [X] Criar classe base `RoadCar`
- [X] Criar `CivilCar` (presas)
    - seguir estrada
    - fugir da polícia
- [X] Criar `PoliceCar` (predadores)
    - patrulha
    - perseguir civis
- [X] Melhorar aparencia dos policias (alternar a cor com periodo de 0.4s azul/vermelho, quando entra em modo preseguição)

## Fase 5 — Comportamentos Inteligentes
- [ ] Criar Civis com velocidade pequena e randomly ir metendo eles em modo ilegal (aumentar velocidade/mudar cor)
- [ ] Quando civis [legal] encontram a policia, param.
- [ ] Quando civis [ilegal] encontram a policia, fogem.
- [ ] Policia só presegue veiculos ilegais.
- [ ] Quando um policia apanha um ilegal, ambos param por 4 seg e o ilegal muda para a cor legal e ambos voltam a andar normalmente
- [ ] Implementar steering behaviors:
    - `seek`, `flee`, `pursuit`
    - `separation` (civis)
- [ ] Campo de visão (civis e polícia)
- [ ] Comportamento de fuga com atraso (coragem)

## Fase 6 — Evolução (Seleção Natural)
- [ ] Definir genes dos civis:
    - velocidade máxima
    - alcance de visão
    - coragem
- [ ] Avaliar fitness (tempo de sobrevivência)
- [ ] Criar nova geração (seleção + mutação)

## Fase 7 — Resultados
- [ ] Gráfico de capturas por geração
- [ ] Evolução dos genes médios
- [ ] Heatmap das zonas mais perigosas
- [ ] Imagens ou vídeo da simulação

## Fase 8 — Relatório
- [ ] Narrativa / história
- [ ] Explicação técnica do modelo
- [ ] Resultados + discussão
- [ ] UML (classes principais)
- [ ] Conclusão
