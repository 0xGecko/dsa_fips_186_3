import java.math.BigInteger;
import java.security.SecureRandom;


public class DSA {

    public BigInteger p;
    public BigInteger q;
    public BigInteger g;
    public BigInteger x; // Clé privée
    public BigInteger y; // Clé publique
    public SecureRandom random = new SecureRandom();

    /**
     * Signe un message haché selon FIPS 186-3 (section 4.6)
     * paramètre : messageHash, le hash du message
     * return    : {r,s}, un tableau de BigInteger
     */
    public BigInteger[] sign(byte[] messageHash) {  
        // Conversion du hash (tableau d'octets) en un entier positif 'z'
        BigInteger z = new BigInteger(1, messageHash);

        BigInteger k = null;
        BigInteger r = null;
        BigInteger s = null;

        // Boucle de sécurité dans les cas r = 0 ou s = 0
        do {
            // Génération du secret éphémère k (0 < k < q)
            do {
                k = new BigInteger(160, random);
            } while (k.compareTo(BigInteger.ZERO) <= 0 || k.compareTo(q) >= 0);

            // Calcul de r = (g^k mod p) mod q
            r = g.modPow(k, p).mod(q);

            // Calcul de s = (k^-1 * (z + x * r)) mod q
            BigInteger kInverse = k.modInverse(q);
            BigInteger xr = x.multiply(r);
            BigInteger zPlusxr = z.add(xr);
            s = kInverse.multiply(zPlusxr).mod(q);
        } while (r.equals(BigInteger.ZERO) || s.equals(BigInteger.ZERO));

        return new BigInteger[]{r, s};
    }
    public static void main(String[] args) {
        //=========================================================
        System.out.println("\n--- Etape 1 : Paramètres DSA ---\n");
        //=========================================================
        
        // On crée un "instance" de notre classe DSA pour utiliser ses attributs et méthodes
        DSA dsa = new DSA();

        // Calcul de q : q = 2^160 + 7
        dsa.q = BigInteger.TWO.pow(160).add(BigInteger.valueOf(7));

        // Calcul de p : p = 1 + (2^864 + 218)
        BigInteger term2 = BigInteger.TWO.pow(864).add(BigInteger.valueOf(218));
        dsa.p = BigInteger.ONE.add(dsa.q.multiply(term2));

        // Calcul de g : g = 2^((p-1)/l) mod p
        BigInteger exposant = dsa.p.subtract(BigInteger.ONE).divide(dsa.q);
        dsa.g = BigInteger.TWO.modPow(exposant, dsa.p);

        // Affichage des valeurs
        System.out.println("Valeur de q " + dsa.q);
        System.out.println("Valeur de p " + dsa.p);
        System.out.println("Valeur de g " + dsa.g);

        //=========================================================
        System.out.println("\n--- Etape 2 : Génération des clés ---\n");
        //=========================================================

        // Génération de la clé privée x tel que 0 < x < q
        do {
            // On demande un nombre aléatoire de 160 bits
            dsa.x = new BigInteger(160, dsa.random);
            
            // On boucle pour les cas où x <= 0 et >= l
        } while (dsa.x.compareTo(BigInteger.ZERO) <= 0 || dsa.x.compareTo(dsa.q) >= 0);

        // Calcul de clé publique y = g^x mod p
        dsa.y = dsa.g.modPow(dsa.x, dsa.p);

        System.out.println("Clé privée x   : " + dsa.x);
        System.out.println("Clé publique y : " + dsa.y);

        //=========================================================
        System.out.println("\n--- Etape 3 : Signature ---\n");
        //=========================================================

        String message = "Vive la Cryptomonnaie";
        byte[] messageHash = message.getBytes(); // On simule un message haché pour le moment

        // Appel de la fonction signature
        BigInteger[] signature = dsa.sign(messageHash);

        System.out.println("Composante r : " + signature[0]);
        System.out.println("Composante s : " + signature[1]);
    }
}