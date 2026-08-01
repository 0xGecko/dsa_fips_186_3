# Feuille de Route (Roadmap) - Projet DSA

Cette feuille de route détaille les étapes de développement pour l'implémentation de l'algorithme DSA (FIPS 186-3).

## Étape 1 : Compréhension & Paramétrage
- [x] Initialiser un dépôt Git local et sur GitHub.
- [x] Configurer le fichier `.gitignore` pour le développement Java.
- [x] Écrire la classe principale `DSA.java`.
- [x] Déclarer et instancier les paramètres publics $p$ et $l$ (équivalent à $q$) en utilisant `BigInteger`.
- [x] Implémenter le calcul du générateur $g$ via la méthode d'exponentiation modulaire (`modPow`).

## Étape 2 : Génération des clés
- [x] Implémenter une méthode pour générer la clé privée $x$ telle que $0 < x < l$.
- [x] Implémenter le calcul de la clé publique $y = g^x \pmod p$.

## Étape 3 : Algorithme de Signature
- [x] Créer la fonction de hachage $H(M)$ de SHA-3.
    - [x] Structure de l'état interne : Représenter l'état de 1600 bits sous la forme d'un tableau tridimensionnel 5x5 de mots de 64 bits (long[5][5]).
    - [x] Padding : Implémenter la règle de bourrage pad10*1 pour aligner les messages selon le taux propre à la fonction.
    - [x] Permutation Keccak-f[1600] : Coder les 5 étapes successives d'un round :
        * Theta ($\theta$) : Diffusion et calcul de parité des colonnes.  
        * Rho ($\rho$) : Rotation circulaire interne des bits au sein de chaque mot.
        * Pi ($\pi$) : Permutation géométrique des mots sur la grille 5x5.  
        * Chi ($\chi$) : Opération non-linéaire (S-box) combinant les lignes.  
        * Iota ($\iota$) : Injection des constantes de rounds pour briser la symétrie.
    - [x] Mécanisme Éponge : Assembler les phases d'absorption des blocs de messages et de compression/extraction (squeezing) pour délivrer un condensé (digest) de taille fixe (à décidé, ex : SHA3-256).
- [x] Générer le nombre secret $k$ (per-message) tel que $0 < k < l$.
- [x] Calculer la composante $r = (g^k \pmod p) \pmod l$.
- [x] Gérer l'inversion modulaire de $k$ ($k^{-1} \pmod l$).
- [x] Calculer la composante $s = (k^{-1}(H(M) + x \cdot r)) \pmod l$.
- [x] Gérer les cas exceptionnels (recommencer si $r=0$ ou $s=0$).

## Étape 4 : Algorithme de Vérification
- [x] Implémenter le calcul de $w = s^{-1} \pmod l$.
- [x] Implémenter le calcul de $u_1 = (H(M) \cdot w) \pmod l$.
- [x] Implémenter le calcul de $u_2 = (r \cdot w) \pmod l$.
- [x] Implémenter la vérification finale : vérifier que $r == ((g^{u_1} \cdot y^{u_2}) \pmod p) \pmod l$.

## Étape 5 : Benchmarking
- [x] Développer une boucle réalisant 10 000 opérations de signature et de vérification.
- [x] Développer un outil pour mesurer les performances.
- [x] Enregistrer les résultats et les spécifications de la machine de test.

## Étape 6 : Finalisation et Rapport
- [ ] Nettoyer, commenter et structurer le code.
- [ ] Rédiger le rapport d'implémentation justifiant les choix techniques.
- [ ] Mettre à jour le fichier `MANUAL.md` avec les instructions d'utilisation.