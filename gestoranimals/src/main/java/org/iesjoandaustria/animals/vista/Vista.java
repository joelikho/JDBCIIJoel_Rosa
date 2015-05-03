package org.iesjoandaustria.animals.vista;

public class Vista {
	public static String animalNomRepetit = "Ja existeix un animal amb aquest nom per a aquesta categoria";
	public static String totCorrecte = "D'acord";
	public static String categoriaNoTrobada = "Cap categoria";
	public static String animalNoTrobat = "Cap animal";
	public static String animalNoTrobatEnCategoriaInoTrobatNom = "No es troba l'animal";
	public static String categoriaNoEspecificadaNomRepetit = "S'ha trobat aquest nom d'animal en les següents categories:";
	public static String respostaSiIdentificatUnicAnimali = "D'acord"; 
	public static String respostaAltraIdentificatUnicAnimali = "Cap modificació"; 
	public static String respostaSiNoClarIdentificatUnicAnimali = "D'acord"; 
	public static String respostaAltraNoClarIdentificatUnicAnimali = "Cap modificació"; 
	public static String respostaSiAnimalIdentificat = "D'acord"; 
	public static String respostaAltraAnimalIdentificat = "Cap modificació"; 
	

	public static String retornaAnimalIdentificat(String n, String c, String c2){
		return("Es procedirà a assignar l'animal "+n+" de la categoria "+c+" a la categoria "+c2+" Segur? (S|n):");
	}
	public static String retornaAnimalIdentificat(String n, String c){
		return("Es procedirà a eliminar l'animal "+n+" de la categoria "+c+" Segur? (S|n):");
	}
}
 
