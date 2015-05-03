package org.iesjoandaustria.animals.control;
import org.iesjoandaustria.animals.model.Modelo;
import org.iesjoandaustria.animals.vista.Vista;
public class Controlador 
{
    public void run(){
    	Modelo model = null;
    	try{
	    	model = new Modelo();
	        boolean bool = true;
	        while(bool){
	        	System.out.print("[animals]:");
		        String str = IOUtils.llegeixStr();
		        String[] split = str.split(" ");
		        if(split[0].equals("help")){
		        	System.out.println("list");
		        	System.out.println("list «categoria»");
		        	System.out.println("add «nom animal» «categoria»");
		        	System.out.println("del «nom animal» [ «categoria» ]");
		        	System.out.println("assign «nom animal» [ «categoria1» ] to «categoria2»");
		        	System.out.println("quit");
		        }else if (split[0].equals("list") && split.length==1) {
		        	model.list();
		        }else if(split[0].equals("list") && split.length ==2) {
		        	model.list_cat(split[1]);
		    	}else if (split[0].equals("add") && split.length==3) {
		        	model.add_animal(split[1],split[2]);
		        }else if (split[0].equals("del") && split.length==3) {
		        	model.del_animal(split[1],split[2]);
		        }else if (split[0].equals("del") && split.length==2) {
		        	model.del_animal(split[1]);
		        }else if (split[0].equals("assign") && split.length==5) {
		        	model.mod_animal(split[1],split[2],split[4]);
		       	}else if (split[0].equals("assign") && split.length==4) {
		       		model.mod_animal(split[1],split[3]);
		        }else if (split[0].equals("quit")) {
		        	bool = false;
		        }else{
		        	System.out.println("Introdueix una comanda correcta");
		        }
	        }
	    }catch(Exception e){
	    	e.printStackTrace();
	    }finally{
	    	if(model != null){
	    		
	    	}
	    }
    }
}
/*help

mostra la llista de comandes vàlides

list

Mostra la llista d'animals a la base de dades en el següent format:

* primera categoria (per ordre alfabètic)

    - nom primer animal de la primera categoria (per ordre
      alfabètic)

    - nom segon animal de la primera categoria

* segona categoria

    - nom primer animal de la segona categoria

    - ..
Si no es troba cap animal a la bd, s'indicarà amb el missatge:

Cap animal
list «categoria»

Mostra la llista d'animals ordenats alfabèticament de la categoria indicada.

El format serà idèntic que per l'opció list, excepte que només mostrarà una categoria.

Si no es troba cap animal de la categoria indicada, s'indicarà amb el missatge:

Cap animal
add «nom animal» «categoria»

Afegeix un nou animal a la categoria indicada.

Si ja existeix un animal amb aquest nom, per a aquesta categoria, mostrarà el missatge d'error:

Ja existeix un animal amb aquest nom per a aquesta categoria
Si tot ha anat correctament, es mostrarà el missatge:

D'acord
del «nom animal» [ «categoria» ]

Aquesta comanda permet eliminar un animal pel seu nom. Opcionalment es pot indicar la categoria a la que pertany.

En cas que no existeixi cap animal amb aquest nom, o no existeixi amb el nom per la categoria indicada, es mostrarà el missatge:

No es troba l'animal
En cas que no s'especifiqui la categoria i hi hagi més d'un animal amb el mateix nom en diferents categories, es mostrarà el missatge i es tornarà mostrar el prompt :

S'ha trobat aquest nom d'animal en les següents categories:

* primera categoria (per ordre alfabètic)

* segona categoria

* ...

Per favor, expliciteu la categoria
Finalment, en cas que s'hagi identificat un únic animal, es demanarà confirmació segons:

Es procedirà a eliminar l'animal «nom animal» de la categoria «categoria»
Segur? (S|n):
En cas que l'usuari respongui amb 'S' o 's', s'eliminarà l'animal de la base de dades i s'informarà amb el missatge:

D'acord
Si es respon amb qualsevol altre caràcter, es mostrarà el missatge:

Cap modificació
assign «nom animal» [ «categoria1» ] to «categoria2»

Permet canviar la categoria d'un animal.

En cas que no quedi clarament especificat un sol animal (com a l'opció del), es mostrarà els mateixos missatges.

En cas de que l'animal estigui identificat, es procedirà a demanar confirmació amb el missatge:

Es procedirà a assignar l'animal «nom animal» de la categoria «categoria1» a la categoria «categoria2»
Segur? (S|n):
L'usuari rebrà el missatge corresponent com en el cas de del.

quit

Finalitzarà l'aplicació*/
