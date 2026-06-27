package home;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== PEDRA, PAPEL E TESOURA (Sistema de Revanche) ===");
        System.out.print("IP do Servidor (ex: localhost): ");
        String ipServidor = scanner.nextLine().trim();

        System.out.print("Digite seu Nome: ");
        String nomeJogador = scanner.nextLine().trim();

        try {
            Registry registry = LocateRegistry.getRegistry(ipServidor, 1099);
            JogoRMI jogo = (JogoRMI) registry.lookup("JogoPPT");
            
            System.out.println("\nConectando ao servidor... Aguardando um oponente entrar...");
            String msgConexao = jogo.conectar(nomeJogador);
            
            if (msgConexao.equals("SALA_CHEIA")) {
                System.out.println("A sala já tem 2 jogadores. Tente mais tarde.");
                System.exit(0);
            }
            
            System.out.println("\n*** " + msgConexao + " ***");

            // Loop de Rodadas do Jogo
            while (true) {
                System.out.println("\nSua vez! Digite PEDRA, PAPEL ou TESOURA (ou SAIR):");
                System.out.print("> ");
                String jogada = scanner.nextLine().toUpperCase().trim();

                if (jogada.equals("SAIR")) {
                    System.out.println("Você abandonou a partida.");
                    break;
                }

                if (!jogada.equals("PEDRA") && !jogada.equals("PAPEL") && !jogada.equals("TESOURA")) {
                    System.out.println("Jogada inválida!");
                    continue;
                }

                System.out.println("Você escolheu " + jogada + ". Aguardando o oponente jogar...");
                String resultado = jogo.enviarJogada(nomeJogador, jogada);
                
                System.out.println("\n" + resultado);
                System.out.println("-------------------------------------------------");

                // MENU DE REVANCHE
                String respostaRevanche = "";
                while (true) {
                    System.out.print("Deseja jogar novamente? (SIM/NAO): ");
                    String opcao = scanner.nextLine().toUpperCase().trim();
                    
                    if (opcao.equals("SIM")) {
                        System.out.println("Aguardando confirmação do oponente...");
                        respostaRevanche = jogo.registrarVotoRevanche(nomeJogador, true);
                        break;
                    } else if (opcao.equals("NAO")) {
                        respostaRevanche = jogo.registrarVotoRevanche(nomeJogador, false);
                        break;
                    } else {
                        System.out.println("Opção inválida! Digite apenas SIM ou NAO.");
                    }
                }

                // Avalia o veredito da barreira de revanche do Servidor
                if (respostaRevanche.equals("REVANCHE_ACEITA")) {
                    System.out.println("\nAmbos aceitaram! Iniciando nova rodada...");
                } else {
                    // Exibe a mensagem personalizada configurada pelo servidor e encerra o app
                    System.out.println("\n" + respostaRevanche);
                    break; 
                }
            }

        } catch (Exception e) {
            System.err.println("Erro de Conexão: " + e.getMessage());
        } finally {
            scanner.close();
            System.out.println("Jogo encerrado.");
        }
    }
}