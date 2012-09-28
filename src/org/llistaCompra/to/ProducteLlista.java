package org.llistaCompra.to;

/**
 * 
 * Producte d'una llista de la compra
 *
 */
public class ProducteLlista {
	private Llista llista;
	private int idProducte;
	private String nomProducte;
	private boolean estaComprat;
	private double preu;
	
	/**
	 * Crea un producte a la llista especificada amb l'id, nom i estat de compra especificats.
	 * @param llista Llista de la compra
	 * @param idProducte Identificador del producte
	 * @param nomProducte Nom del producte
	 * @param estaComprat Indicador si està comprat o no
	 * @param preu Preu del producte
	 */
	public ProducteLlista(Llista llista, int idProducte, String nomProducte,
			boolean estaComprat, double preu) {
		super();
		this.llista = llista;
		this.idProducte = idProducte;
		this.nomProducte = nomProducte;
		this.estaComprat = estaComprat;
		this.preu = preu;
	}
	
	/**
	 * Retorna la llista del producte.
	 * @return Llista del producte
	 */
	public Llista getLlista() {
		return llista;
	}
	/**
	 * Modifica la llista del producte.
	 * @param llista Llista del producte
	 */
	public void setLlista(Llista llista) {
		this.llista = llista;
	}
	/**
	 * Retorna l'identificador del producte.
	 * @return Identificador del producte
	 */
	public int getIdProducte() {
		return idProducte;
	}
	/**
	 * Modifica l'identificador del producte.
	 * @param idProducte Identificador del producte
	 */
	public void setIdProducte(int idProducte) {
		this.idProducte = idProducte;
	}
	/**
	 * Retorna el nom del producte.
	 * @return Nom del producte
	 */
	public String getNomProducte() {
		return nomProducte;
	}
	/**
	 * Modifica el nom del produtcte.
	 * @param nomProducte Nom del producte
	 */
	public void setNomProducte(String nomProducte) {
		this.nomProducte = nomProducte;
	}
	/**
	 * Retorna si el producte està comprat o no.
	 * @return El producte està comprat
	 */
	public boolean isEstaComprat() {
		return estaComprat;
	}
	/**
	 * Modifica l'estat de compra del producte.
	 * @param estaComprat El producte està comprat
	 */
	public void setEstaComprat(boolean estaComprat) {
		this.estaComprat = estaComprat;
	}
	
	
	/**
	 * Retorna el preu del producte.
	 * @return Preu del producte
	 */
	public double getPreu() {
		return preu;
	}
	/**
	 * Modifica el preu del producte.
	 * @param preu Preu del producte
	 */
	public void setPreu(double preu) {
		this.preu = preu;
	}
	
}
