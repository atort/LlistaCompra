package org.llistaCompra.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.llistaCompra.helper.LlistaCompraHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * Cont� l'administraci� de Llistes a la base de dades.
 * 
 */
public class LlistaCompraDbAdapter {
	private static final String LLISTA_TABLE = "Llista";
	public static final String LLISTA_NOM = "nom";
	public static final String LLISTA_DATA = "data";
	public static final String LLISTA_DATACOMPRA = "dataCompra";
	public static final String LLISTA_ESTAT = "estat";
	public static final String LLISTA_ROWID = "_id";

	public static final int ESTAT_PREPARACIO = 1;
	public static final int ESTAT_COMPRANT = 0;
	public static final int ESTAT_FINALITZADA = 2;

	private final Context mCtx;

	private LlistaCompraHelper mDbHelper;
	private SQLiteDatabase mDb;

	/**
	 * Crea 'adapter.
	 * @param ctx Context
	 */
	public LlistaCompraDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Obre la connexi� a la base de dades.
	 * @return L'objecte LlistaCompraDbAdapter amb la connexi� oberta
	 * @throws SQLException
	 */
	public LlistaCompraDbAdapter open() throws SQLException {
		mDbHelper = new LlistaCompraHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	/**
	 * Tanca la connexi� a la base de dades
	 */
	public void close() {
		mDbHelper.close();
	}

	/**
	 * Crea una llista amb el nom assignat.
	 * @param nomLlista Nom de la llista
	 * @return Resultat de la creaci� de la llista
	 */
	public long createLlista(String nomLlista) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(LLISTA_NOM, nomLlista);
		DateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		String s = formatter.format(new Date());
		initialValues.put(LLISTA_DATA, s);
		initialValues.put(LLISTA_ESTAT, ESTAT_PREPARACIO);
		return mDb.insert(LLISTA_TABLE, null, initialValues);

	}

	/**
	 * Esborra la llista amb el rowid assignat.
	 * @param rowId Identificador de la llista
	 * @return <i>true </i> Si la llista s'ha esborrat correctament
	 */
	public boolean deleteLlista(Long rowId) {
		return mDb.delete(LLISTA_TABLE, LLISTA_ROWID + "=" + rowId, null) > 0;
	}

	/**
	 * Retorna totes els llistes ordenades per estat de compra i nom.
	 * @return Totes les llistes de la compra
	 */
	public Cursor fetchAllLlista() {
		return mDb.query(LLISTA_TABLE, new String[] { LLISTA_ROWID, LLISTA_NOM,
				LLISTA_ESTAT }, null, null, null, null, LLISTA_ESTAT+", "+LLISTA_NOM);
	}

	/**
	 * Retorna la llista amb l'id assignat.
	 * @param rowId Identificador de la llista
	 * @return Llista seleccionada
	 * @throws SQLException
	 */
	public Cursor fetchLlista(Long rowId) throws SQLException {
		Cursor mCursor =

		mDb.query(true, LLISTA_TABLE, new String[] { LLISTA_ROWID, LLISTA_NOM,
				LLISTA_ESTAT }, LLISTA_ROWID + "=" + rowId, null, null, null,
				null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	/**
	 * Retorna l'estat de la llista amb l'id assignat.
	 * @param rowId Identificador de la llista
	 * @return Estat de la llista
	 */
	public Integer getEstatLlista(Long rowId) {
		Integer estat = null;
		Cursor mCursor = mDb.query(true, LLISTA_TABLE, new String[] {
				LLISTA_ROWID, LLISTA_ESTAT }, LLISTA_ROWID + "=" + rowId, null,
				null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
			estat = mCursor.getInt(mCursor
					.getColumnIndexOrThrow(LlistaCompraDbAdapter.LLISTA_ESTAT));
			mCursor.close();
		}
		return estat;
	}

	/**
	 * Modifica el nom de la llista amb l'id assignat.
	 * @param rowId Identificador de la llista
	 * @param nomLlista Nom de la llista
	 * @return <i> true</i> Si la llista s'ha actualitzat correctament
	 */
	public boolean updateLlista(Long rowId, String nomLlista) {
		ContentValues args = new ContentValues();
		args.put(LLISTA_NOM, nomLlista);

		return mDb.update(LLISTA_TABLE, args, LLISTA_ROWID + "=" + rowId, null) > 0;
	}

	/**
	 * Inicia el proc�s de compra de l'id assignat.
	 * @param rowId Identificador de la llista
	 * @return <i> true</i> Si la llista s'ha iniciat correctament
	 */
	public boolean startBuyingLlista(Long rowId) {
		ContentValues args = new ContentValues();
		DateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		String s = formatter.format(new Date());
		args.put(LLISTA_DATACOMPRA, s);
		args.put(LLISTA_ESTAT, ESTAT_COMPRANT);
		return mDb.update(LLISTA_TABLE, args, LLISTA_ROWID + "=" + rowId, null) > 0;
	}

	/**
	 * Finalitza el proc�s de compra de l'id assignat.
	 * @param rowId Identificador de la llista
	 * @return <i> true</i> Si la llista s'ha finalitzat correctament
	 */
	public boolean endBuyLlista(Long rowId) {
		ContentValues args = new ContentValues();
		args.put(LLISTA_ESTAT, ESTAT_FINALITZADA);
		return mDb.update(LLISTA_TABLE, args, LLISTA_ROWID + "=" + rowId, null) > 0;
	}
	
	/**
	 * Torna a posar la compra de l'id assignat a l'estat editant.
	 * @param rowId Identificador de la llista
	 * @return <i> true</i> Si la llista s'ha obert correctament
	 */
	public boolean openBuyLlista(Long rowId) {
		ContentValues args = new ContentValues();
		args.put(LLISTA_ESTAT, ESTAT_PREPARACIO);
		return mDb.update(LLISTA_TABLE, args, LLISTA_ROWID + "=" + rowId, null) > 0;
	}

	/**
	 * Crear una nova llista amb el productes no comprats de la llista amb 
	 * identificador idLlista
	 * @param nomNovaLLista Nom de la llista a crear
	 * @param idLlista Identificador de la llista a partir de la cual es crear� la nova
	 */
	public void transferLlista(String nomNovaLLista, Long idLlista){
		//Crear nova llista
		ContentValues initialValues = new ContentValues();
		initialValues.put(LLISTA_NOM, nomNovaLLista);
		DateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		String s = formatter.format(new Date());
		initialValues.put(LLISTA_DATA, s);
		initialValues.put(LLISTA_ESTAT, ESTAT_PREPARACIO);
		Long idNovaLlista=mDb.insert(LLISTA_TABLE, null, initialValues);
		
		//agafar productes no comprats de idLlista
		//i afegirlos a idNovaLlista
		Cursor c = mDb.query(LlistaCompraProducteDbAdapter.PRODUCTE_TABLE,
						new String[] { LlistaCompraProducteDbAdapter.PRODUCTE_ROWID,
						LlistaCompraProducteDbAdapter.PRODUCTE_NOM,
						LlistaCompraProducteDbAdapter.PRODUCTE_QUANTITAT},
						LlistaCompraProducteDbAdapter.PRODUCTE_LLISTA + "=" + idLlista + 
						" and " + LlistaCompraProducteDbAdapter.PRODUCTE_COMPRAT
						+ "=" + LlistaCompraProducteDbAdapter.ESTAT_NO_COMPRAT,
						null, null, null, null);
		if (c != null) {
			c.moveToFirst();
	        while (!c.isAfterLast()) {
	        	//crear nou producte
	        	ContentValues initialValuesProd = new ContentValues();
	        	initialValuesProd.put(LlistaCompraProducteDbAdapter.PRODUCTE_NOM, c.getString(1));
	        	initialValuesProd.put(LlistaCompraProducteDbAdapter.PRODUCTE_QUANTITAT, c.getString(2));
	        	initialValuesProd.put(LlistaCompraProducteDbAdapter.PRODUCTE_LLISTA, idNovaLlista);
	        	initialValuesProd.put(LlistaCompraProducteDbAdapter.PRODUCTE_COMPRAT, Integer.valueOf(0));
	        	initialValuesProd.put(LlistaCompraProducteDbAdapter.PRODUCTE_LLISTA_INICIAL, true);
	        	initialValuesProd.put(LlistaCompraProducteDbAdapter.PRODUCTE_PREU, Integer.valueOf(0));
	    		mDb.insert(LlistaCompraProducteDbAdapter.PRODUCTE_TABLE, null, initialValuesProd);
	    		
	       	    c.moveToNext();
	        }
	        c.close();
		}
		
	}
	
	
	/**
	 * Retorna la llista (amb l'id assignat) en format text, nom i productes.
	 * @param rowId Identificador de la llista
	 * @return Llista seleccionada en format text
	 * @throws SQLException
	 */
	public String getTextList(Long idLlista) throws SQLException {
		StringBuilder builderList= new StringBuilder();
		
		//buscar nom de la llista
		Cursor mCursor = mDb.query(true, LLISTA_TABLE, new String[] { LLISTA_ROWID, LLISTA_NOM,
				LLISTA_ESTAT }, LLISTA_ROWID + "=" + idLlista, null, null, null,
				null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
			
			String nomLlista = mCursor.getString(1);
			builderList.append(nomLlista.toUpperCase(Locale.getDefault()) +"\n");
		}
		mCursor.close();
		
		//buscar productes de la llista
		Cursor c = mDb.query(LlistaCompraProducteDbAdapter.PRODUCTE_TABLE,
				new String[] { LlistaCompraProducteDbAdapter.PRODUCTE_ROWID,
				LlistaCompraProducteDbAdapter.PRODUCTE_NOM,
				LlistaCompraProducteDbAdapter.PRODUCTE_QUANTITAT},
				LlistaCompraProducteDbAdapter.PRODUCTE_LLISTA + "=" + idLlista
				,
				null, null, null, LlistaCompraProducteDbAdapter.PRODUCTE_NOM);
		if (c != null) {
			c.moveToFirst();
		    while (!c.isAfterLast()) {
		    	
		    	String nomProducte = c.getString(1);
		    	String quantitatProducte = c.getString(2);
				builderList.append("- " + nomProducte.toUpperCase(Locale.getDefault()) +
						" (" + quantitatProducte + ") "+
						"\n");
		   	    c.moveToNext();
		    }
		    c.close();
		}
		
		return builderList.toString();
	}
	
}
