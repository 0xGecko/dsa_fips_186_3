public class SHA3 {

    /**
     * =====================================================================
     * LEXIQUE DES PERMUTATIONS KECCAK (FIPS 202)
     * La fonction de permutation keccakF applique 5 opérations (round) :
     * 
     * 1. Theta (θ) : Diffusion. Applique un XOR sur chaque bit avec la 
     *                parité de deux colonnes adjacentes.
     * 2. Rho (ρ)   : Dispersion interne. Rotation circulaire des bits au 
     *                sein de chaque mot de 64 bits.
     * 3. Pi (π)    : Dispersion externe. Permutation géométrique 
     *                déplaçant les mots entiers sur la grille 5x5.
     * 4. Chi (χ)   : Non-linéarité. Opération logique AND/XOR entre colonnes.
     *                Le seul traitement non linéaire de l'algorithme.
     * 5. Iota (ι)  : Asymétrie. XOR du mot (0,0) avec une constante de round (RC)
     *                pour briser les symétries de nos opérations.
     * =====================================================================
     */

    // L'état interne de 1600 bits (tableau de 5x5 mots de 64 bits)
    private long[][] state = new long[5][5];

    // Les 24 constantes de round (Iota) définies dans le standard FIPS 202
    private static final long[] RC = {
        0x0000000000000001L, 0x0000000000008082L, 0x800000000000808aL,
        0x8000000080008000L, 0x000000000000808bL, 0x0000000080000001L,
        0x8000000080008081L, 0x8000000000008009L, 0x000000000000008aL,
        0x0000000000000088L, 0x0000000080008009L, 0x000000008000000aL,
        0x000000008000808bL, 0x800000000000008bL, 0x8000000000008089L,
        0x8000000000008003L, 0x8000000000008002L, 0x8000000000000080L,
        0x000000000000800aL, 0x800000008000000aL, 0x8000000080008081L,
        0x8000000000008080L, 0x0000000080000001L, 0x8000000080008008L
    };

    private static final int[][] RHO_OFFSETS = {
        {0,  36,  3, 41, 18},
        {1,  44, 10, 45,  2},
        {62,  6, 43, 15, 61},
        {28, 55, 25, 21, 56},
        {27, 20, 39,  8, 14}
    };

    private void theta() {
        long[] C = new long[5];
        long[] D = new long[5];

        // 1. Calcul de la parité (C) pour chaque colonne x
        for (int x = 0; x < 5; x++) {
            C[x] = state[x][0] ^ state[x][1] ^ state[x][2] ^ state[x][3] ^ state[x][4];
        }

        // 2. Calcul de l'effet de diffusion D[x]
        for (int x = 0; x < 5; x++) {

            long colonneGauche = C[(x + 4) % 5];
            
            // Rotation circulaire d'1 bit vers la gauche sur la colonne de droite
            long colonneDroite = Long.rotateLeft(C[(x + 1) % 5],1);
            
            D[x] = colonneGauche ^ colonneDroite;
        }

        // 3. Mise à jour de l'état interne (Xor final)
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                state[x][y] ^= D[x];
            }
        }
    }

    private void rho() {
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                state[x][y] = Long.rotateLeft(state[x][y], RHO_OFFSETS[x][y]);
            }
        }
    }

    private void pi() {
        // Création d'un tableau temporaire 5x5 init à 0
        long[][] tempState = new long[5][5];

        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                // Calcul de la nouvelle position du mot selon FIPS 202
                int newX = y;
                int newY = (2 * x + 3 * y) % 5;

                // Placement dans le tableau temporaire
                tempState[newX][newY] = state[x][y];
            }
        }

        // Remplacement de l'état interne par le nouvel état
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                state[x][y] = tempState[x][y];
            }
        }
    }

    private void chi() {
        for (int y = 0; y < 5; y++) {
            // Tableau temporaire pour sauvegarder la ligne en cours
            long[] tempRow = new long[5];

            // Calcul des nouvelles valeurs pour la ligne y
            for (int x = 0; x < 5; x++) {
                long voisinDroite = state[(x + 1) % 5][y];
                long voisinSuivant = state[(x + 2) % 5][y];

                // Formule de Chi : a' = a ^ ((~b) & c)
                tempRow[x] = state[x][y] ^ ((~ voisinDroite) & voisinSuivant);
            }

            // Remplacement de la ligne dans l'état principal
            for (int x = 0; x < 5; x++) {
                state[x][y] = tempRow[x];
            }
        }
    }

    private void iota(int round) {
        // Modification de la variable globale state[0][0]
        state[0][0] ^= RC[round];
    }

    private void keppacF() {
        // Pour SHA-3 (b = 1600), le nombre de round est 24.
        // L'indexation commence à 12 + 2l - nr, ce qui donne 0 à 23 pour 24 rounds.
        int l = 6; // car 1600 = 25 * 2^6 (donc w = 64, l = 6)
        int nr = 24;
        int startIndex = 12 + 2 * l - nr;
        
        for (int ir =  startIndex; ir < 12 + 2 * l; ir++) {
            theta();
            rho();
            pi();
            chi();
            iota(ir);
        }
    }
}