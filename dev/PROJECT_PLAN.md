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
- [ ] Funções úteis:
    - obter estradas ligadas a um nó
    - obter direção ao longo de uma estrada

## Fase 4 — Agentes
- [ ] Criar classe base `Agent` (pos, vel, acc)
- [ ] Criar `CivilCar` (presas)
    - seguir estrada
    - evitar colisões
    - fugir da polícia
- [ ] Criar `PoliceCar` (predadores)
    - patrulha
    - perseguir civis

## Fase 5 — Comportamentos Inteligentes
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
