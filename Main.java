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



    //separa o video em uma matriz de bytes, cada bloco tem 1024 bytes, o ultimo bloco pode ser menor se o tamanho do video nao for multiplo de 1024
    private static byte[][] init(byte[] video) {
        int numBlocks = video.length % 1024 != 0 ?  (video.length / 1024 + 1) :  video.length / 1024;
        int contPos = 0;
        byte[][] blocks = new byte[numBlocks][1024];

        for (int i = 0; i < numBlocks; i++) {
            for (int j = 0; j < 1024 && contPos < video.length; j++) {
                blocks[i][j] = video[contPos];
                contPos++;
            }
        }
        if(blocks==null || contPos <= -1){
            return null;
        }
        return blocks;
    }

    static MessageDigest algorithm;

    static {try {algorithm = MessageDigest.getInstance("SHA-256");}
    catch (NoSuchAlgorithmException nsae) { }}

        //metodo que printa o H0, parte do ultimo bloco do video, gera o hash, concatena esse hash no final do bloco anterior,
    // gera o hash, concatena.. até chegar no primeiro bloco de bytes
    private static void findBlock(byte[][] blocks, int length) throws IOException {
        byte[][] blocksResume = new byte[blocks.length][];
        byte[] ultimoBloco = Arrays.copyOfRange(blocks[blocks.length - 1], 0, length % 1024);
        blocksResume[blocks.length - 1] = algorithm.digest(ultimoBloco); //gera hash do ultimo bloco

        //parte do penultimo bloco ate o primeiro, gerando hash e concatenando no bloco anterior
        for (int i = blocks.length - 2; i >= 0; i--) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(blocks[i]);
            outputStream.write(blocksResume[i + 1]);
            blocksResume[i] = algorithm.digest(outputStream.toByteArray());
        }
        StringBuilder sb = new StringBuilder();

        //transforma o hash de array de bytes para hexadecimal
        for (int i = 0; i < blocksResume[0].length; i++) {
            sb.append(Integer.toString((blocksResume[0][i] & 0xff) + 0x100, 16).substring(1));

        }
        System.out.println("Digest(in hex format):: " + sb.toString());


    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n Digite o nome do arquivo e sua extensão");
        String entrada = scanner.nextLine();      
        findBlock(init(Files.readAllBytes(new File(entrada).toPath())), Files.readAllBytes(new File(entrada).toPath()).length);
    }



    

    /*metodo para gerar hash de um array de bytes utilizando a biblioteca MessageDigest
    private static byte[] gerarHash(byte[] bloco) {
        return algorithm.digest(bloco);
    }
    */

    

    //metodo auxiliar para ler o arquivo e colocar em array de bytes
    
}
