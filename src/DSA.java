import java.math.BigInteger;
import java.security.SecureRandom;


public class DSA {
    public static void main(String[] args) {
        //=========================================================
        System.out.println("--- Etape 1 : Paramètres DSA ---");
        //=========================================================
        

        // Calcul de q : q = 2^160 + 7
        BigInteger q = BigInteger.TWO.pow(160).add(BigInteger.valueOf(7));

        // Calcul de p : p = 1 + (2^864 + 218)
        BigInteger term2 = BigInteger.TWO.pow(864).add(BigInteger.valueOf(218));
        BigInteger p = BigInteger.ONE.add(q.multiply(term2));

        // Calcul de g : g = 2^((p-1)/l) mod p
        BigInteger exposant = p.subtract(BigInteger.ONE).divide(q);
        BigInteger g = BigInteger.TWO.modPow(exposant, p);

        // Affichage des valeurs
        System.out.println("Valeur de q " + q);
        System.out.println("Valeur de p " + p);
        System.out.println("Valeur de g " + g);

        //=========================================================
        System.out.println("--- Etape 2 : Génération des clés ---");
        //=========================================================

        // Initialisation du générateur de nombre aléatoire sécurisé
        SecureRandom random = new SecureRandom();
        BigInteger x;

        // Génération de la clé privée x tel que 0 < x < q
        do {
            // On demande un nombre aléatoire de 160 bits
            x = new BigInteger(160, random);

            // On boucle pour les cas où x <= 0 et >= l
        } while (x.compareTo(BigInteger.ZERO) <= 0 || x.compareTo(q) >= 0);

        // Calcul de clé publique y = g^x mod p
        BigInteger y = g.modPow(x, p);

        System.out.println("Clé privée x   : " + x);
        System.out.println("Clé publique y : " + y);

        String message = "Vive la Cryptomonnaie";


    }
}   