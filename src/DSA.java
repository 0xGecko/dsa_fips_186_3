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

    /**
     * Vérifie une signature DSA selon FIPS 186-3 (section 4.7)
     * paramètres : - messageHash, le condensé (hash) du message reçu
     *              - r, la 1ère composante de la signature
     *              - s, la 2ème composante de la signautre
     * return : true si la signature est valide, false sinon
     */
    public boolean verify(byte[] messageHash, BigInteger r, BigInteger s) {
        // Vérification des bornes : 0 < r < q et 0 < s < q
        if (r.compareTo(BigInteger.ZERO) <= 0 || r.compareTo(q) >= 0 ||
            s.compareTo(BigInteger.ZERO) <= 0 || s.compareTo(q) >= 0) {
                return false;
            }

        // Conversion du hash en entier z
        BigInteger z = new BigInteger(1, messageHash);

        // Calcul de w = s^-1 mod q
        BigInteger w = s.modInverse(q);

        // Calcul de u1 = (z * w) mod q
        BigInteger u1 = z.multiply(w).mod(q);

        // Calcul de u2 = (r * w) mod q
        BigInteger u2 = r.multiply(w).mod(q);

        // Calcul de v = (((g^u1) * (y^u2)) mod p) mod q
        BigInteger gu1 = g.modPow(u1, p);
        BigInteger yu2 = y.modPow(u2, p);
        BigInteger v = gu1.multiply(yu2).mod(p).mod(q);

        return v.equals(r);
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
        System.out.println("Valeur de q : " + dsa.q);
        System.out.println("\nValeur de p : " + dsa.p);
        System.out.println("\nValeur de g : " + dsa.g);

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
        System.out.println("\nClé publique y : " + dsa.y);

        //=========================================================
        System.out.println("\n--- Préparation du message ---\n");
        //=========================================================

        String message = "Vive la Cryptomonnaie";
        System.out.println("Message brut : " + message);

        // On instancie notre moteur de hachage SHA-3
        SHA3 moteurSHA3 = new SHA3();

        // On appelle la méthode hash() sur cet objet 
        byte[] messageHash = moteurSHA3.hash(message.getBytes());

        //=========================================================
        System.out.println("\n--- Etape 3 : Signature ---\n");
        //=========================================================

        // Appel de la fonction signature
        BigInteger[] signature = dsa.sign(messageHash);

        System.out.println("Composante r : " + signature[0]);
        System.out.println("Composante s : " + signature[1]);

        //=========================================================
        System.out.println("\n--- Etape 4 : Vérification ---\n");
        //=========================================================

        boolean isValid = dsa.verify(messageHash, signature[0], signature[1]);
        System.out.println("La signature est-elle valide ?\n" + isValid);

        // Test avec un faux message pour vérifier que l'algorithme rejette les falsifications
        String fauxMessage = "La Cryptographie = La Crpytomonnaie";
        byte[] fauxMessageHash = fauxMessage.getBytes();
        boolean isFakeValid = dsa.verify(fauxMessageHash, signature[0], signature[1]);
        System.out.println("\nLa signature sur un message altéré est-elle valide ?\n" + isFakeValid);

        //=========================================================================
        System.out.println("\n--- Etape 5 : Benchmark (10k intérations) ---\n");
        //=========================================================================

        int iterations = 10000;
        String messageBenchmark = "Message de test pour le benchmark DSA";
        byte[] hashBenchmark = moteurSHA3.hash(messageBenchmark.getBytes());

        // 1. Phase de Warm-up pour chauffer la JVM (Java Virtual Machine)
        System.out.println("Chauffe de la JVM...");
        for (int i = 0; i < 1000; i++) {
            BigInteger[] sigWarmup = dsa.sign(hashBenchmark);
            dsa.verify(hashBenchmark, sigWarmup[0], sigWarmup[1]);
        }

        // 2. Le vrai Benchmark
        System.out.println("Lancement du benchmark sur " + iterations + " itérations...");
        
        long startTime = System.nanoTime();

        for (int i = 0; i < iterations; i++) {
            BigInteger[] sig = dsa.sign(hashBenchmark);
            boolean valide = dsa.verify(hashBenchmark, sig[0], sig[1]);

            // Sécurité : on s'assure que tout reste valide pendant le test
            if (!valide) {
                System.out.println("Erreur de vérification à l'itération : " + i);
                break;
            }
        }

        long endTime = System.nanoTime();

        // Calculs et affichage
        long dureeTotaleNanos = endTime - startTime;
        long dureeTotaleMillis = dureeTotaleNanos / 1_000_000;
        double moyenneParOperation = (double) dureeTotaleMillis / iterations;

        System.out.println("Temps total écoulé : " + dureeTotaleMillis + " ms");
        System.out.println("Temps moyen par itération (sign + verify) : " + moyenneParOperation + " ms");
    }
}