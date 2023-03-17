# Projet Scala

## Lancement de l'application

#### Dans un Terminal au dossier de l'application :
* Lancez la commande ``` scalac Main.scala ``` <br>
( afin de compiler le programme principale )
* Ensuite, lancez la commande ```scalac Main.scala```<br>
(ceci lance le programme principale)

## Utilisation de l'application
L'application fonctionne comme un terminal et plusieurs commandes sont à votre disposition :

> ### Commande ```exit``` <br>
> Cette Commande permet simplement de sortir de l'application

> ### Commande ```dummy``` <br> 
> Cette Commande créée un canvas de 3 par 4

> ### Commande ```dummy2``` <br> 
> Cette Commande créée un canvas de 3 par 1

> ### Commande ```new_canvas [Longueur] [Hauteur] [Caractère de Dessin]``` <br> 
> Cette Commande créée un canvas de la taille que vous souhaitez avec le caractère de votre choix. Par exemple '#'

> ### Commande ```load_image [Nom du fichier]``` <br> 
> Cette Commande créée un canvas depuis une image importée. <br> <br>
> **Attention :** Chaque ligne de votre fichier d'import doit mesurer la même taille. Si votre ligne ne possède pas de caractère à dessiner compléter avec des espaces.

> ### Commande ```update_pixel [Coordonées] [Caractère de Dessin]``` <br> 
> Cette commande permet de mofidier un Pixel en particulier en passant ces coordonnées sous la forme **x,y** ainsi que le nouveau caractère souhaité.<br> 

> ### Commande ```drawline [Coordonées A] [Coordonées B][Caractère de Dessin]``` <br> 
> Cette commande trace une ligne de la coordonnée A vers la coordonée B avec le caractère passé en paramètre.<br> 
Ici les coordonées sont toujours données sous la forme **x,y**.<br> <br>
**Attention :** Le programme ne sais pas tracer la ligne dans le sens "inverse". <br>
(Temporaire) : Le programme ne gère que les lignes verticales et horizontales

> ### Commande ```drawRectangle [Coordonées A] [Coordonées B][Caractère de Dessin]``` <br> 
> Cette commande trace un rectangle de la coordonnée A qui est le coin haut gauche du rectangle vers la coordonée B qui est le point bas droite du rectangle avec le caractère passé en paramètre.<br>
Ici les coordonées sont toujours données sous la forme **x,y**.


## Ajout

> ### Fonction ```checkInput(Seq[String], Canvas)``` <br> 
> Cette Fonction permet de vérifier si le nombre d'arguments est respecté mais aussi si les coordonées des points passées en paramètres appartiennent bien au canva.<br>
Cette fonction n'est appelée que pour les commandes de dessin.