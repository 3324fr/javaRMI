# INF4410
Ce 2e travail pratique de INF4410 a pour but de donner à l'étudiant une expérience de première
main avec les appels de procédure à distance.


#PARTIE 1


##serveur 
1. Compiler avec ant
1. Aller dans bin (cd bin)
1. Executer /opt/java/jdk8.x86_64/bin/rmiregistry 5002 &
1. Revenir au root (cd ..)
1. Dans la ligne suivante (dans le fichier server au root):
  -Djava.rmi.server.hostname=L4712-07 \
  Assurez vous que le numero de poste soit la bonne 
1. Partir le serveur en faisant ./server -r qi 
qi etant le nombre de ressource (ex 2 ou 3 ou 5)
1. Le message server ready  devrait afficher 

##client mode securise
1. Entrer les hostnames et le nombre de ressource de chaque serveur dans le fichier host.csv \
ex:
```  
L4712-10 2
L4712-07 3
L4712-05 2
L4712-11 3
```

1. Compiler avec ant 
1. Avoir les fichiers d'operations au niveau root 
ex: operations-588 au niveau du root (./operations-588)
1. Executer le client avec:
./client liste
liste etant le nom de la liste 
ex: ./client operations-588
1. L'execution se fait ici. Vous pouvez voir le temps dexecution et le resultat obtenu.


#PARTIE 2


##serveur malicieux
1. Idem que serveur, mais a l'etape 6, utiliser ./MaliciousServer -r qi -d di
qi etant le nombre de ressource (5)
di etant le taux de defect (50 ou 80)

##client mode non securise
1. Meme chose que client mode securise,
mais utilisez ./clientNonSecurise liste 
ex:
```  
./clientNonSecurise liste
liste etant le nom de la liste 
ex: ./client operations-588
```  
