# ✌️ Pedra, Papel e Tesoura (Multiplayer via Java RMI)

Um sistema distribuído baseado na clássica mecânica de Pedra, Papel e Tesoura. Este projeto foi desenvolvido para aplicar conceitos avançados de comunicação de rede, concorrência e arquitetura Cliente-Servidor utilizando a linguagem Java.

## 🚀 O Projeto e seus Desafios
Embora a lógica do jogo seja simples, o desafio arquitetônico reside em orquestrar uma partida em rede local garantindo a sincronia perfeita entre duas máquinas diferentes. 

Para isso, o sistema implementa:
* **Comunicação Cliente-Servidor:** O servidor atua como um nó central, aguardando conexões e gerenciando o estado da partida em tempo real através do **Java RMI (Remote Method Invocation)**.
* **Sincronização de Threads:** Uso ativo de métodos como `wait()` e `notifyAll()` para criar barreiras de execução. O servidor apenas processa e devolve o resultado final quando os dois nós da rede enviam seus pacotes de jogada.
* **Sistema de Revanche Simultânea:** Uma lógica de barreira de votação. A próxima rodada só é iniciada se ambos os clientes concordarem, caso contrário, o servidor encerra a conexão de forma segura e limpa a sala para novos jogadores.

## 🛠️ Tecnologias Utilizadas
* **Java (JDK)**
* **Java RMI** (Remote Method Invocation)
* **Programação Concorrente** (Controle de Threads e Sincronização)

## 📄 Documentação Técnica
O detalhamento completo da arquitetura de comunicação, bem como as decisões de design de software, encontram-se no **Dossiê Técnico** anexado na pasta raiz deste repositório.