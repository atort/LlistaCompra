package org.llistaCompra.to;

import java.util.Date;
import java.util.List;

/**
 * 
 * Llista de la compra
 *
 */
public class Llista {
	private int id;
	private String nomLlista;
	private Date dataLlista;
	private Date dataCompra;
	private List<ProducteLlista> productes;

	/**
	 * Crea una llista amb l'id i nom especificats.
	 * @param id Identificador de la llista
	 * @param nomLlista Nom de la llista
	 */
	public Llista(int id, String nomLlista) {
		super();
		this.id = id;
		this.nomLlista = nomLlista;
		this.dataLlista = null;
	}

	/**
	 * Crea una llista amb l'id, nom, data de creació i data de compra especificats.
	 * @param id Identificador de la llista
	 * @param nomLlista Nom de la llista
	 * @param dataLlista Data de creació de la llista
	 * @param dataCompra Data de compra de la llista
	 */
	public Llista(int id, String nomLlista, Date dataLlista, Date dataCompra) {
		super();
		this.id = id;
		this.nomLlista = nomLlista;
		this.dataLlista = dataLlista;
		this.dataCompra = dataCompra;
	}

	/**
	 * Retorna l'identificador de la llista.
	 * @return Identificador de la llista
	 */
	public int getId() {
		return id;
	}

	/**
	 * Modifica l'identificador de la llista.
	 * @param id Identificador de la llista
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Retorna el nom de la llista.
	 * @return Nom de la llista
	 */
	public String getNomLlista() {
		return nomLlista;
	}

	/**
	 * Modifica el nom de la llista
	 * @param nomLlista Nom de la llista
	 */
	public void setNomLlista(String nomLlista) {
		this.nomLlista = nomLlista;
	}

	/**
	 * Retorna la data de creació de la llista.
	 * @return Data de creació de la llista
	 */
	public Date getDataLlista() {
		return dataLlista;
	}

	/**
	 * Modifica la data de creació de la llista
	 * @param dataLlista Data de creació de la llista
	 */
	public void setDataLlista(Date dataLlista) {
		this.dataLlista = dataLlista;
	}

	/**
	 * Retorna la data de compra de la llista.
	 * @return Data de compra de la llista
	 */
	public Date getDataCompra() {
		return dataCompra;
	}

	/**
	 * Modifica la data de compra de la llista.
	 * @param dataCompra Data de compra de la llista
	 */
	public void setDataCompra(Date dataCompra) {
		this.dataCompra = dataCompra;
	}

	/**
	 * Retorna la llista de productes de la llista.
	 * @return Productes de la llista
	 */
	public List<ProducteLlista> getProductes() {
		return productes;
	}

	/**
	 * Modifica la llista de productes de la llisa.
	 * @param productes Productes de la llista
	 */
	public void setProductes(List<ProducteLlista> productes) {
		this.productes = productes;
	}

}
