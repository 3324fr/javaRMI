# INF4410
Ce premier travail pratique de INF4410 a pour but de donner à l'étudiant une expérience de première
main avec les appels de procédure à distance.

#serveur 
1) Compilez avec ant
2) Aller dans bin (cd bin)
3) Executer /opt/java/jdk8.x86_64/bin/rmiregistry 5002 &
4) Revenir au root (cd ..)
5) Dans la ligne suivante (dans le fichier server au root):
  -Djava.rmi.server.hostname=L4712-07 \
  Assurez vous que le numero de poste soit la bonne 
6) Partir le serveur en faisant ./server -r qi
qi etant le nombre de ressource (ex 2 ou 3 ou 5)
7) Le message server ready  devrait afficher 

#serveur malicieux
1) Idem que serveur, mais a letape 6, utiliser ./MaliciousServer -r qi

#client 
1) Entrez les hostname et le nombre de ressource dans le fichier host.csv \
ex:  
L4712-10 2
L4712-07 3
L4712-05 2
L4712-11 3

2) Compilez avec ant 
3) Avoir les fichiers doperations au niveau root 
ex: operations-588 au niveau du root (./operations-588)
4) Executer le client avec:
./client liste
liste etant le nom de la liste 
ex: ./client operations-588
5) Lexecution se fait ici. Vous pouvez voir le temps dexecution et le resultat obtenu.
