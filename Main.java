
import java.io.*;
import java.security.*;
import java.util.*;
import java.nio.file.Files;


/**
 * Autora: Daniela Amaral
 * Trabalho 2 da cadeira de Segurança de Sistemas, dado um video passado por parametro, este é dividido em arrays de 1024 bytes,
 * é calculado o Hash em SHA-256 do ultimo bloco, este hash é concatenado ao final do penultimo bloco, é gerado hash deste novo bloco e concatenado no antepenultimo bloco
 * este processo é repetido até chegar ao primeiro bloco do array de bytes do video.
 * <p>
 * Para execução: compilar esta classe com Java 8+ e executar, seguir orientações no console
 * Video com demonstração: https://youtu.be/6CpmcyzVxwY
 */
public class Main {

    private static MessageDigest md;

    static {try {md = MessageDigest.getInstance("SHA-256");}
    catch (NoSuchAlgorithmException e) { }}

    //separa o video em uma matriz de bytes, cada bloco tem 1024 bytes, o ultimo bloco pode ser menor se o tamanho do video nao for multiplo de 1024
    private static byte[][] preencherBlocos(byte[] video) {
        int ultimoIndex = 0;
        int qtdBlocos = video.length % 1024 != 0 ?  (video.length / 1024 + 1) :  video.length / 1024;
        byte[][] blocos = new byte[qtdBlocos][1024];

        for (int i = 0; i < qtdBlocos; i++) {
            for (int j = 0; j < 1024 && ultimoIndex < video.length; j++) {
                blocos[i][j] = video[ultimoIndex];
                ultimoIndex++;
            }
        }
        if(blocos==null || md == null){
            return null;
        }
        return blocos;
    }

        //metodo que printa o H0, parte do ultimo bloco do video, gera o hash, concatena esse hash no final do bloco anterior,
    // gera o hash, concatena.. até chegar no primeiro bloco de bytes
    private static void buscarH0(byte[][] blocos, int length) throws IOException {
        byte[][] blocosHash = new byte[blocos.length][];
        byte[] ultimoBloco = Arrays.copyOfRange(blocos[blocos.length - 1], 0, length % 1024);
        blocosHash[blocos.length - 1] = md.digest(ultimoBloco); //gera hash do ultimo bloco

        //parte do penultimo bloco ate o primeiro, gerando hash e concatenando no bloco anterior
        for (int i = blocos.length - 2; i >= 0; i--) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(blocos[i]);
            outputStream.write(blocosHash[i + 1]);
            blocosHash[i] = md.digest(outputStream.toByteArray());
        }
        StringBuilder sb = new StringBuilder();

        //transforma o hash de array de bytes para hexadecimal
        for (int i = 0; i < blocosHash[0].length; i++) {
            sb.append(Integer.toString((blocosHash[0][i] & 0xff) + 0x100, 16).substring(1));

        }
        System.out.println("Digest(in hex format):: " + sb.toString());


    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n Digite o nome do arquivo e sua extensão");
        String entrada = scanner.nextLine();      
        buscarH0(preencherBlocos(Files.readAllBytes(new File(entrada).toPath())), Files.readAllBytes(new File(entrada).toPath()).length);
    }



    

    /*metodo para gerar hash de um array de bytes utilizando a biblioteca MessageDigest
    private static byte[] gerarHash(byte[] bloco) {
        return md.digest(bloco);
    }
    */

    

    //metodo auxiliar para ler o arquivo e colocar em array de bytes
    
}
