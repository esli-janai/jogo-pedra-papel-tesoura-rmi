package home;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface JogoRMI extends Remote {
    String conectar(String nomeJogador) throws RemoteException;
    String enviarJogada(String nomeJogador, String jogada) throws RemoteException;
    
    // Método para registrar se o jogador quer revanche (true) ou não (false).
    String registrarVotoRevanche(String nomeJogador, boolean querRevanche) throws RemoteException;
}