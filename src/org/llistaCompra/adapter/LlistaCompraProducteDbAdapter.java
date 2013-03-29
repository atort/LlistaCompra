package org.llistaCompra.adapter;

import org.llistaCompra.helper.LlistaCompraHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * Conté l'administració de Prodcutes a la base de dades.
 * 
 */
public class LlistaCompraProducteDbAdapter {
	public static final String PRODUCTE_TABLE = "Producte";
	public static final String PRODUCTE_NOM = "nom";
	public static final String PRODUCTE_LLISTA = "idLlista";
	public static final String PRODUCTE_ROWID = "_id";
	public static final String PRODUCTE_COMPRAT = "EstaComprat";
	public static final String PRODUCTE_LLISTA_INICIAL = "EnLlistaInicial";
	public static final String PRODUCTE_PREU = "preu";
	public static final String PRODUCTE_QUANTITAT = "quantitat";

	public static final int ESTAT_COMPRAT = 1;
	public static final int ESTAT_NO_COMPRAT = 0;

	public static final int ESTAT_INICIAL = 1;
	public static final int ESTAT_NO_INICIAL = 0;

	private final Context mCtx;

	private LlistaCompraHelper mDbHelper;
	private SQLiteDatabase mDb;

	/**
	 * Crea l'adapter.
	 * 
	 * @param ctx
	 *            Context
	 */
	public LlistaCompraProducteDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Obre la connexió a la base de dades.
	 * 
	 * @return L'objecte LlistaCompraProducteDbAdapter amb la connexió oberta
	 * @throws SQLException
	 */
	public LlistaCompraProducteDbAdapter open() throws SQLException {
		mDbHelper = new LlistaCompraHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	/**
	 * Tanca la connexió a la base de dades.
	 */
	public void close() {
		mDbHelper.close();
	}

	/**
	 * Crea un producte a la llista assignada, amb el nom i estat assignat.
	 * 
	 * @param idLlista
	 *            Identificador de la llista
	 * @param nomProducte
	 *            Nom del producte
 	 * @param quantitat
	 * 			 Quantitat a comprar del producte
	 * @param enLlistaInicial
	 *            Indicador si estava a la llista abans de començar la compra
	 * @return Resultat de la creació del producte
	 */
	public long createProducte(Long idLlista, String nomProducte,
			Integer quantitat, Integer enLlistaInicial) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(PRODUCTE_NOM, nomProducte);
		initialValues.put(PRODUCTE_QUANTITAT, quantitat);
		initialValues.put(PRODUCTE_LLISTA, idLlista);
		initialValues.put(PRODUCTE_COMPRAT, new Integer(0));
		initialValues.put(PRODUCTE_LLISTA_INICIAL, enLlistaInicial);
		initialValues.put(PRODUCTE_PREU, new Integer(0));
		return mDb.insert(PRODUCTE_TABLE, null, initialValues);
	}

	/**
	 * Esborra el producte de la llista abm els ids assignats.
	 * 
	 * @param idLlista
	 *            Identificador de la llista
	 * @param rowId
	 *            Identificador del producte
	 * @return Resultat de l'esborrat del producte
	 */
	public boolean deleteProducte(Long idLlista, Long rowId) {
		return mDb.delete(PRODUCTE_TABLE, PRODUCTE_ROWID + "=" + rowId, null) > 0;
	}

	/**
	 * Retorna tots els productes de la llista assignada ordenats per nom
	 * 
	 * @param idLlista
	 *            Identificador de la llista
	 * @return Tots els productes de la llista
	 */
	public Cursor fetchAllProductes(Long idLlista) {
		return mDb.query(PRODUCTE_TABLE, new String[] { PRODUCTE_ROWID,
				PRODUCTE_NOM, PRODUCTE_COMPRAT, PRODUCTE_LLISTA_INICIAL,
				PRODUCTE_PREU, PRODUCTE_QUANTITAT }, PRODUCTE_LLISTA + "=" + idLlista, null, null,
				null, PRODUCTE_NOM);
	}
	
	/**
	 * Retorna tots els productes de la llista assignada ordenats per nom
	 * 
	 * @param idLlista
	 *            Identificador de la llista
	 * @return Tots els productes de la llista
	 */
	public Cursor fetchAllProductesFormat(Long idLlista) {
		return mDb.rawQuery("SELECT *, "+PRODUCTE_NOM+ "|| \" (\" || "+PRODUCTE_QUANTITAT +" || \")\" AS PRODUCTE_QUANTITAT "
				+" FROM "+PRODUCTE_TABLE
				+ " WHERE "+ PRODUCTE_LLISTA + "=" + idLlista
				+ " ORDER BY "+PRODUCTE_NOM , null);
	}
	

	/**
	 * Retorna tots els productes de la llista assignada ordenats per estat de
	 * compra i nom
	 * 
	 * @param idLlista
	 *            Identificador de la llista
	 * @return Tots els productes de la llista
	 */
	public Cursor fetchAllProductesComprant(Long idLlista) {
		return mDb.query(PRODUCTE_TABLE, new String[] { PRODUCTE_ROWID,
				PRODUCTE_NOM, PRODUCTE_COMPRAT, PRODUCTE_LLISTA_INICIAL,
				PRODUCTE_PREU, PRODUCTE_QUANTITAT}, PRODUCTE_LLISTA + "=" + idLlista, null, null,
				null, PRODUCTE_COMPRAT + " , " + PRODUCTE_NOM);
	}

	/**
	 * Retorna el producte de la llista amb els ids assignats.
	 * 
	 * @param idLlista
	 *            Identificador de la llista
	 * @param rowId
	 *            Identificador del producte
	 * @return El producte indicat
	 * @throws SQLException
	 */
	public Cursor fetchProducte(Long idLlista, Long rowId) throws SQLException {
		Cursor mCursor = mDb.query(true, PRODUCTE_TABLE, new String[] {
				PRODUCTE_ROWID, PRODUCTE_NOM, PRODUCTE_COMPRAT,
				PRODUCTE_LLISTA_INICIAL, PRODUCTE_PREU, PRODUCTE_QUANTITAT}, PRODUCTE_ROWID + "="
				+ rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	/**
	 * Retorna si hi ha productes sense comprar a la llista.
	 * 
	 * @param idLlista
	 *            Identificador de la llista
	 * @return <i>true</i> Si hi ha productes no comprats
	 */
	public boolean productesNoComprats(Long idLlista) {
		Cursor c = mDb.query(PRODUCTE_TABLE, new String[] { PRODUCTE_ROWID },
				PRODUCTE_LLISTA + "=" + idLlista + " and " + PRODUCTE_COMPRAT
						+ "=" + ESTAT_NO_COMPRAT, null, null, null, null);
		return c.getCount() != 0;
	}

	/**
	 * Retorna el preu acumulat de la llista.
	 * 
	 * @param idLlista
	 *            Identificador de la llista
	 * @return Preu acumulat de la llista
	 */
	public Double preuAcumulatLlista(Long idLlista) {
		Cursor c = mDb.query(PRODUCTE_TABLE, new String[] { "sum("
				+ PRODUCTE_PREU + ")" }, PRODUCTE_LLISTA + "=" + idLlista
				+ " and " + PRODUCTE_COMPRAT + " = " + ESTAT_COMPRAT, null,
				null, null, null);
		if (c.getCount() > 0) {
			c.moveToFirst();
			Double a = Math.rint(c.getDouble(0) * 100) / 100;
			c.close();
			return a;
		} else{
			c.close();
			return new Double(0);
		}
	}

	/**
	 * Modifica el nom del producte de la llista assignats.
	 * 
	 * @param idLlista
	 *            Identificador de la llista
	 * @param rowId
	 *            Identificador del producte
	 * @param nomProducte
	 *            Nom nou del producte
	 * @param quantitat
	 * 			 Quantitat a comprar del producte
	 * @return <i> true</i> Si l'actualització ha anat bé
	 */
	public boolean updateProducteLlista(Long idLlista, Long rowId,
			String nomProducte, Integer quantitat) {
		ContentValues args = new ContentValues();
		args.put(PRODUCTE_NOM, nomProducte);
		args.put(PRODUCTE_QUANTITAT, quantitat);
		args.put(PRODUCTE_LLISTA, idLlista);
		return mDb.update(PRODUCTE_TABLE, args, PRODUCTE_ROWID + "=" + rowId,
				null) > 0;
	}

	/**
	 * Modifica l'estat del producte de la llista assignats.
	 * 
	 * @param idLlista
	 *            Identificador de la llista
	 * @param rowId
	 *            Identificador del producte
	 * @param estaComprat
	 *            Estat de la compra
	 * @return <i> true</i> Si l'actualització ha anat bé
	 */
	public boolean updateProducteCompratLlista(Long idLlista, Long rowId,
			Integer estaComprat) {
		ContentValues args = new ContentValues();
		args.put(PRODUCTE_COMPRAT, estaComprat);
		args.put(PRODUCTE_LLISTA, idLlista);
		return mDb.update(PRODUCTE_TABLE, args, PRODUCTE_ROWID + "=" + rowId,
				null) > 0;
	}

	/**
	 * Modifica el preu del producte de la llista assignats.
	 * 
	 * @param idLlista
	 *            Identificador de la llista
	 * @param rowId
	 *            Identificador del producte
	 * @param preu
	 *            Nom preu del producte
	 * @return <i> true</i> Si l'actualització ha anat bé
	 */
	public boolean updateProductePreuLlista(Long idLlista, Long rowId,
			Double preu) {
		ContentValues args = new ContentValues();
		args.put(PRODUCTE_PREU, preu);
		args.put(PRODUCTE_LLISTA, idLlista);
		return mDb.update(PRODUCTE_TABLE, args, PRODUCTE_ROWID + "=" + rowId,
				null) > 0;
	}

}
