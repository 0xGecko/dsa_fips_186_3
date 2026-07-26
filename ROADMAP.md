# Feuille de Route (Roadmap) - Projet DSA

Cette feuille de route détaille les étapes de développement pour l'implémentation de l'algorithme DSA (FIPS 186-3).

## Étape 1 : Compréhension & Paramétrage
- [x] Initialiser un dépôt Git local et sur GitHub.
- [x] Configurer le fichier `.gitignore` pour le développement Java.
- [x] Écrire la classe principale `DSA.java`.
- [x] Déclarer et instancier les paramètres publics $p$ et $l$ (équivalent à $q$) en utilisant `BigInteger`.
- [x] Implémenter le calcul du générateur $g$ via la méthode d'exponentiation modulaire (`modPow`).

## Étape 2 : Génération des clés
- [ ] Implémenter une méthode pour générer la clé privée $x$ telle que $0 < x < l$.
- [ ] Implémenter le calcul de la clé publique $y = g^x \pmod p$.

## Étape 3 : Algorithme de Signature
- [ ] Créer une fonction de hachage $H(M)$ (SHA-3 ou factice pour commencer).
- [ ] Générer le nombre secret $k$ (per-message) tel que $0 < k < l$.
- [ ] Calculer la composante $r = (g^k \pmod p) \pmod l$.
- [ ] Gérer l'inversion modulaire de $k$ ($k^{-1} \pmod l$).
- [ ] Calculer la composante $s = (k^{-1}(H(M) + x \cdot r)) \pmod l$.
- [ ] Gérer les cas exceptionnels (recommencer si $r=0$ ou $s=0$).

## Étape 4 : Algorithme de Vérification
- [ ] Implémenter le calcul de $w = s^{-1} \pmod l$.
- [ ] Implémenter le calcul de $u_1 = (H(M) \cdot w) \pmod l$.
- [ ] Implémenter le calcul de $u_2 = (r \cdot w) \pmod l$.
- [ ] Implémenter la vérification finale : vérifier que $r == ((g^{u_1} \cdot y^{u_2}) \pmod p) \pmod l$.

## Étape 5 : Benchmarking
- [ ] Développer une boucle réalisant 10 000 opérations de signature et de vérification [cite: 5].
- [ ] Développer un outil pour mesurer les performances.
- [ ] Identifier et contourner les biais d'optimisation du compilateur JIT de Java.
- [ ] Enregistrer les résultats et les spécifications de la machine de test.

## Étape 6 : Finalisation et Rapport
- [ ] Nettoyer, commenter et structurer le code.
- [ ] Rédiger le rapport d'implémentation justifiant les choix techniques.
- [ ] Mettre à jour le fichier `MANUAL.md` avec les instructions d'utilisation.