package org.llistaCompra.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * 
 * Administraci� de l'esquema de la base de dades.
 *
 */
public class LlistaCompraHelper extends SQLiteOpenHelper {
/**
 * DATABASE_VERSION = 1
 * Primera versi�
 * 
 * DATABASE_VERSION = 2 
 * PRODUCTE_CREATE afegit: on delete cascade
 * 
 * DATABASE_VERSION = 3
 * PRODUCTE_CREATE afegida columna preu.
 *
 * **/
	
	public static final String DATABASE_NAME = "DbLlistaCompra.db";
	private static final int DATABASE_VERSION = 4;

	private static final String LLISTA_CREATE = "Create table Llista ("
			+ "_id INTEGER primary key autoincrement, " + "nom TEXT, "
			+ "data TEXT, " + "dataCompra TEXT, " + " estat INTEGER);";

	private static final String PRODUCTE_CREATE = "Create table Producte ("
			+ "_id INTEGER primary key autoincrement, "
			+ "idLlista INTEGER, "
			+ "nom TEXT,"
			+ "EstaComprat INTEGER, EnLlistaInicial INTEGER, "
			+ "preu NUMERIC, " 
			+ "quantitat NUMERIC, "
			+ "FOREIGN KEY (idLlista) references Llista(_id) on delete cascade) ";

	private static final String LLISTA_DROP = "drop table Llista";
	private static final String PRODUCTE_DROP = "drop table Producte";
	
	private static final String PRODUCTE_ADD_PREU = "alter table Producte add preu NUMERIC;";
	
	private static final String PRODUCTE_ADD_QUANTITAT = "alter table Producte add quantitat NUMERIC;";
	
	private static final String UPDATE_QUANTITAT_1="update Producte set quantitat=1;";

	/**
	* Crea el Helper.
	*/
	public LlistaCompraHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(LLISTA_CREATE);
		db.execSQL(PRODUCTE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch (oldVersion){
			case 1:
				db.execSQL(PRODUCTE_DROP);
				db.execSQL(LLISTA_DROP);
				db.execSQL(LLISTA_CREATE);
				db.execSQL(PRODUCTE_CREATE);
			case 2:
				db.execSQL(PRODUCTE_ADD_PREU);
			case 3:
				db.execSQL(PRODUCTE_ADD_QUANTITAT);
				db.execSQL(UPDATE_QUANTITAT_1);
		}
		

	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly()) {
			// Enable foreign key constraints
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}

}
