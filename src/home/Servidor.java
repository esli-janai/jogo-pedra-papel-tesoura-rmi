package home;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Servidor extends UnicastRemoteObject implements JogoRMI {

    private String jogador1 = null;
    private String jogador2 = null;
    
    private String jogada1 = null;
    private String jogada2 = null;
    
    private String resultadoFinal = null;
    private int jogadoresQueReceberamResultado = 0;

    // Variáveis de controle para a Revanche
    private Boolean revanche1 = null;
    private Boolean revanche2 = null;
    private int jogadoresQueReceberamVoto = 0;

    protected Servidor() throws RemoteException {
        super();
    }

    @Override
    public synchronized String conectar(String nome) throws RemoteException {
        if (jogador1 == null) {
            jogador1 = nome;
            System.out.println(nome + " conectou. Aguardando oponente...");
            try {
                while (jogador2 == null) {
                    wait(); 
                }
            } catch (InterruptedException e) { e.printStackTrace(); }
            
            return "Oponente (" + jogador2 + ") encontrado! O jogo começou!";
            
        } else if (jogador2 == null) {
            jogador2 = nome;
            System.out.println(nome + " conectou. A partida vai começar!");
            notifyAll(); 
            return "Você entrou contra " + jogador1 + "! O jogo começou!";
        } else {
            return "SALA_CHEIA";
        }
    }

    @Override
    public synchronized String enviarJogada(String nome, String jogada) throws RemoteException {
        if (nome.equals(jogador1)) {
            jogada1 = jogada;
        } else if (nome.equals(jogador2)) {
            jogada2 = jogada;
        }

        System.out.println(nome + " fez a jogada.");

        if (jogada1 != null && jogada2 != null) {
            if (resultadoFinal == null) {
                resultadoFinal = calcularVencedor(jogador1, jogada1, jogador2, jogada2);
                System.out.println("Ambos jogaram. Enviando resultados!");
            }
            notifyAll(); 
        } else {
            try {
                while (resultadoFinal == null) {
                    wait(); 
                }
            } catch (InterruptedException e) { e.printStackTrace(); }
        }

        String resposta = resultadoFinal;
        
        jogadoresQueReceberamResultado++;
        if (jogadoresQueReceberamResultado == 2) {
            // Limpa as jogadas antigas preparando o terreno para uma possível revanche
            jogada1 = null;
            jogada2 = null;
            resultadoFinal = null;
            jogadoresQueReceberamResultado = 0;
        }

        return resposta;
    }

    // LÓGICA DA REVANCHE SIMULTÂNEA
    @Override
    public synchronized String registrarVotoRevanche(String nome, boolean querRevanche) throws RemoteException {
        // Registra o voto do respectivo jogador
        if (nome.equals(jogador1)) {
            revanche1 = querRevanche;
        } else if (nome.equals(jogador2)) {
            revanche2 = querRevanche;
        }

        // Se os dois já votaram, libera a barreira
        if (revanche1 != null && revanche2 != null) {
            notifyAll();
        } else {
            // Se foi o primeiro a votar, fica aguardando o voto do oponente
            try {
                while (revanche1 == null || revanche2 == null) {
                    wait();
                }
            } catch (InterruptedException e) { e.printStackTrace(); }
        }

        String resposta;
        boolean ambosQuerem = revanche1 && revanche2;

        if (ambosQuerem) {
            resposta = "REVANCHE_ACEITA";
        } else {
            // Se a revanche foi negada por alguém, filtra quem quis e quem não quis jogar
            if (nome.equals(jogador1)) {
                resposta = revanche1 ? "Que pena, seu oponente não deseja mais jogar. GAME OVER!" : "GAME OVER!";
            } else {
                resposta = revanche2 ? "Que pena, seu oponente não deseja mais jogar. GAME OVER!" : "GAME OVER!";
            }
        }

        // Controle de limpeza coordenada do servidor
        jogadoresQueReceberamVoto++;
        if (jogadoresQueReceberamVoto == 2) {
            if (!ambosQuerem) {
                // Se o jogo acabou, limpa a sala inteira para permitir novos jogadores no futuro
                limparSalaCompleta();
            } else {
                System.out.println("Revanche confirmada entre " + jogador1 + " e " + jogador2);
            }
            // Reseta as variáveis de votação para a próxima rodada
            revanche1 = null;
            revanche2 = null;
            jogadoresQueReceberamVoto = 0;
        }

        return resposta;
    }

    private String calcularVencedor(String j1, String jog1, String j2, String jog2) {
        String baseStr = ">> " + j1 + " jogou " + jog1 + " | " + j2 + " jogou " + jog2 + "\nResultado: ";
        
        if (jog1.equals(jog2)) {
            return baseStr + "EMPATE!";
        } else if ((jog1.equals("PEDRA") && jog2.equals("TESOURA")) ||
                   (jog1.equals("PAPEL") && jog2.equals("PEDRA")) ||
                   (jog1.equals("TESOURA") && jog2.equals("PAPEL"))) {
            return baseStr + "Vencedor " + j1.toUpperCase() + "!";
        } else {
            return baseStr + "Vencedor " + j2.toUpperCase() + "!";
        }
    }

    private void limparSalaCompleta() {
        jogador1 = null;
        jogador2 = null;
        jogada1 = null;
        jogada2 = null;
        resultadoFinal = null;
        jogadoresQueReceberamResultado = 0;
        System.out.println("--- Jogo finalizado. Sala resetada e aberta para novas conexões ---");
    }

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            Servidor jogoServidor = new Servidor();
            registry.rebind("JogoPPT", jogoServidor);
            System.out.println("Servidor ONLINE. Aguardando a conexão de 2 jogadores...");
        } catch (Exception e) {
            System.err.println("Erro no Servidor: " + e.getMessage());
        }
    }
}