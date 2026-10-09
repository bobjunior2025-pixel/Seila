# ⚔️ ReinoFoot - Gerenciador Tático Medieval 🛡️

**ReinoFoot** é um jogo de gerenciamento e estratégia tática inspirado na consagrada mecânica do **Brasfoot**, ambientado em um universo medieval com ordens de cavaleiros, feudos, magos e monstros.

---

## 🏰 Principais Mecânicas

- **16 Reinos e Ordens Divididos em 2 Ligas:**
  - **Primeira Divisão (Liga dos Campeões):** Reinos lendários como *Ordem dos Paladinos*, *Império Valíria*, *Clã dos Lobos*, *Guarda da Tempestade* e outros.
  - **Segunda Divisão (Liga dos Barões):** Disputa acirrada pelo acesso à elite.
  - **Grande Torneio Real (Copa da Coroa):** Torneio eliminatório estilo mata-mata envolvendo todos os 16 reinos!
- **Guerreiros e Posições Medievais:**
  - **Guardião (GDR):** Absorção de dano e defesas milagrosas (Goleiro).
  - **Vanguarda (VAN):** Paladinos e defensores pesados (Zagueiros e Laterais).
  - **Tático (TAC):** Bardos, magos e estrategistas que controlam o ritmo e criam chances (Meio-campo).
  - **Atirador / Algoz (ATQ):** Arqueiros letais e campeões velozes para finalizações e abates de honra (Atacantes).
- **Simulação de Batalhas ao Vivo (Estilo Brasfoot):**
  - Narrativa lance a lance minuto a minuto: golpes decisivos, defesas heroicas, magias, faltas violentas, cartões de desonra e substituições táticas.
  - Velocidade ajustável (1x, 2x, 5x ou resultado instantâneo).
  - Possibilidade de mudar formações e posturas (Ofensiva, Equilibrada, Defensiva/Muralha, Contra-ataque) no calor da peleja.
- **Mercado de Mercenários & Taverna:**
  - Contratação de guerreiros de outros reinos e mercenários livres.
  - Negociação de valores de passe e salários.
  - Venda e demissão de combatentes.
- **Economia e Gestão do Feudo:**
  - Tesouro Real em Moedas de Ouro.
  - Expansão do Coliseu/Arena (aumenta renda de ingressos nas batalhas em casa).
  - Melhorias na Enfermaria (recuperação mais rápida de guerreiros feridos).
  - Gestão de folha salarial por rodada.
- **Salvar e Carregar Campanha:**
  - Progresso persistido localmente no dispositivo para continuar a jornada a qualquer momento.

---

## 🤖 Como Gerar o APK com GitHub Actions

Este projeto inclui uma integração completa com o **GitHub Actions** (`.github/workflows/build-apk.yml`).

### Como baixar o APK diretamente no GitHub:
1. Envie o projeto para o seu repositório GitHub (`git push`).
2. No GitHub, clique na aba **Actions**.
3. Selecione o workflow **"Build Android APK"**.
4. O build será executado automaticamente a cada `push`. Você também pode acionar manualmente clicando em **Run workflow**.
5. Quando o workflow finalizar (ícone verde ✅), role até a seção **Artifacts** no final da página.
6. Baixe o arquivo zip contendo o APK (`ReinoFoot-debug-apk` ou `ReinoFoot-release-apk`), descompacte e instale no seu celular Android!
