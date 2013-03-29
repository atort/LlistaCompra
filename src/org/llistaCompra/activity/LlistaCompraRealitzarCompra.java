package org.llistaCompra.activity;

import java.util.ArrayList;
import java.util.List;

import org.llistaCompra.R;
import org.llistaCompra.activity.cursor.ProducteCursorAdapter;
import org.llistaCompra.activity.helper.LlistaCompraFormatHelper;
import org.llistaCompra.adapter.LlistaCompraDbAdapter;
import org.llistaCompra.adapter.LlistaCompraProducteDbAdapter;

import com.flurry.android.FlurryAgent;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.SimpleCursorAdapter;

/**
 * Funció: Mostra els productes d'una llista, permetent marcar-los com a
 * comprats.<br>
 * Cursor: ProducteCursorAdapter.<br>
 * Funcions cridades:<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;Menú: Crear producte, Crear producte per veu,
 * Finalitzar Compra, Reobrir compra.<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;Press: Canviar el preu d'un producte.<br>
 * Layouts: llista_compra_compra_list, llista_compra_compra_row.
 */
public class LlistaCompraRealitzarCompra extends ListActivity {

	public static final int ENDBUY_ID = Menu.FIRST;
	public static final int ADDPRODUCT_ID = Menu.FIRST + 1;
	public static final int OPENBUY_ID = Menu.FIRST + 2;

	public static final int EDITPRICE_ID = Menu.FIRST + 3;
	public static final int ADDPRODUCTVOICE_ID = Menu.FIRST + 4;
	private static final int ADDPRODUCTVOICE_CODE = Menu.FIRST + 5;

	private LlistaCompraProducteDbAdapter llistaCompraProducteDbAdapter;
	private LlistaCompraDbAdapter llistaCompraDbAdapter;

	private Long listRowId;

	private Integer estatCompra;

	/**
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LlistaCompraFormatHelper.loadLocale(this);

		LlistaCompraFormatHelper.createTitle(this,
				R.layout.llista_compra_compra_list, getText(R.string.app_name)
						+ ": " + getText(R.string.buying_title));

		llistaCompraProducteDbAdapter = new LlistaCompraProducteDbAdapter(this);
		llistaCompraProducteDbAdapter.open();
		llistaCompraDbAdapter = new LlistaCompraDbAdapter(this);
		llistaCompraDbAdapter.open();

		listRowId = (savedInstanceState == null) ? null
				: (Long) savedInstanceState
						.getSerializable(LlistaCompraDbAdapter.LLISTA_ROWID);
		if (listRowId == null) {
			Bundle extras = getIntent().getExtras();
			listRowId = extras != null ? extras
					.getLong(LlistaCompraDbAdapter.LLISTA_ROWID) : null;
		}

		// guardar estat de la compra
		estatCompra = llistaCompraDbAdapter.getEstatLlista(listRowId);

		fillData();
		registerForContextMenu(getListView());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case ENDBUY_ID:
			// S'ha de mirar si la compra està en estat comprant
			if (llistaCompraProducteDbAdapter.productesNoComprats(listRowId)) {
				// final LlistaCompraRealitzarCompra llistaCompraRealitzarCompra
				// = this;
				new AlertDialog.Builder(this)
						.setTitle(R.string.endBuy_title)
						.setMessage(R.string.endBuyWithProducts_text)
						.setPositiveButton(R.string.yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										llistaCompraDbAdapter
												.endBuyLlista(listRowId);

										// estatCompra =
										// LlistaCompraDbAdapter.ESTAT_FINALITZADA;
										finish();
									}
								})
						.setNegativeButton(R.string.no,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// do nothing
									}
								}).show();
			} else if (LlistaCompraDbAdapter.ESTAT_COMPRANT != estatCompra) {
				new AlertDialog.Builder(this)
						.setTitle(R.string.warning_title)
						.setMessage(R.string.compraNoIniciada_text)
						.setPositiveButton(R.string.confirm,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).show();
			} else {
				// final LlistaCompraRealitzarCompra llistaCompraRealitzarCompra
				// = this;
				new AlertDialog.Builder(this)
						.setTitle(R.string.endBuy_title)
						.setMessage(R.string.endBuy_text)
						.setPositiveButton(R.string.yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										llistaCompraDbAdapter
												.endBuyLlista(listRowId);

										estatCompra = LlistaCompraDbAdapter.ESTAT_FINALITZADA;
										finish();
									}
								})
						.setNegativeButton(R.string.no,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// do nothing
									}
								}).show();
			}
			return true;
		case ADDPRODUCT_ID:
			// Afegir producte
			Intent i = new Intent(this, LlistaCompraProductEdit.class);
			i.putExtra(LlistaCompraProducteDbAdapter.PRODUCTE_LLISTA, listRowId);
			i.putExtra(LlistaCompraProducteDbAdapter.PRODUCTE_LLISTA_INICIAL,
					Integer.valueOf(0));
			// afegir estat en que esta la compra
			i.putExtra(LlistaCompraDbAdapter.LLISTA_ESTAT, estatCompra);
			startActivityForResult(i, ADDPRODUCT_ID);
			return true;
		case OPENBUY_ID:
			// demanar confirmació
			new AlertDialog.Builder(this)
					.setTitle(R.string.openBuy_title)
					.setMessage(R.string.openBuy_text)
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// actualitzar els productes a no comprats
									Cursor c = llistaCompraProducteDbAdapter
											.fetchAllProductes(listRowId);
									c.moveToFirst();
									while (c.isAfterLast() == false) {
										llistaCompraProducteDbAdapter.updateProducteCompratLlista(
												listRowId,
												c.getLong(c
														.getColumnIndex(LlistaCompraProducteDbAdapter.PRODUCTE_ROWID)),
												LlistaCompraProducteDbAdapter.ESTAT_NO_COMPRAT);
										c.moveToNext();
									}
									c.close();
									// canviar estat
									llistaCompraDbAdapter
											.openBuyLlista(listRowId);
									// anar a llista de compres. finish()?
									finish();
								}
							})
					.setNegativeButton(R.string.no,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// do nothing
								}
							}).show();

			return true;
		case ADDPRODUCTVOICE_ID:
			return addProductVoice();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, ENDBUY_ID, 1, R.string.end_buying);
		menu.add(0, ADDPRODUCT_ID, 1, R.string.insert_product);
		// Si te reconeixement de veu posa en el menu el reconeixement
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() == 0) {
			Log.d(this.getClass().getName(), "No tï¿½ reconeixement de veu");
		} else {
			menu.add(0, ADDPRODUCTVOICE_ID, 1, R.string.insert_product_voice);
		}
		menu.add(0, OPENBUY_ID, 1, R.string.open_buying);
		return result;
	}

	private void fillData() {
		// Get all of the notes from the database and create the item list
		Cursor c = llistaCompraProducteDbAdapter
				.fetchAllProductesComprant(listRowId);
		startManagingCursor(c);

		String[] from = new String[] {
				LlistaCompraProducteDbAdapter.PRODUCTE_NOM,
				LlistaCompraProducteDbAdapter.PRODUCTE_COMPRAT };
		int[] to = new int[] { R.id.text1, R.id.checkBox1 };

		// Now create an array adapter and set it to display using our row
		SimpleCursorAdapter llistaCompra = new ProducteCursorAdapter(listRowId,
				this, R.layout.llista_compra_compra_row, c, from, to);

		setListAdapter(llistaCompra);

	}

	private boolean addProductVoice() {
		// Afegir producte per veu
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				getText(R.string.producteVeu_text));
		startActivityForResult(intent, ADDPRODUCTVOICE_CODE);

		return true;
	}

	/**
	 * Handle the results from the voice recognition activity.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ADDPRODUCTVOICE_CODE && resultCode == RESULT_OK) {
			// Populate the wordsList with the String values the recognition
			// engine thought it heard
			ArrayList<String> matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			Log.d(this.getClass().getName(), "Nou producte: " + matches);

			final CharSequence[] items = { getText(R.string.add_and_next),
					getText(R.string.add_and_exit), getText(R.string.repeat),
					getText(R.string.cancel) };

			final String nameProduct = matches.get(0);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(nameProduct);
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					switch (item) {
					case 0:
						// Afegir i Continuar: Afegim i tornem a posar per
						// gravar
						// afegir porducte a bbdd
						llistaCompraProducteDbAdapter.createProducte(listRowId,
								nameProduct,1,1);
						// refrescar llistat
						fillData();
						addProductVoice();
						break;
					case 1:
						// Afegir i Sortir: Afegim i refresquem llistat
						// afegir porducte a bbdd
						llistaCompraProducteDbAdapter.createProducte(listRowId,
								nameProduct, 1, 1);
						// refrescar llistat
						fillData();
						break;
					case 2:
						// Repetir: Tornem a posar per gravar i no afegim
						addProductVoice();
						break;
					case 3:
						// Cancel.lar:no fem res
						break;
					}
				}
			});
			AlertDialog alert = builder.create();
			alert.show();

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		LlistaCompraFormatHelper.setContextMenuTitle(menu, this);

		menu.add(0, EDITPRICE_ID, 0, R.string.editPrice);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info;
		Intent i;
		switch (item.getItemId()) {
		case EDITPRICE_ID:
			i = new Intent(this, LlistaCompraProductPriceEdit.class);
			info = (AdapterContextMenuInfo) item.getMenuInfo();
			i.putExtra(LlistaCompraDbAdapter.LLISTA_ROWID, info.id);
			startActivityForResult(i, EDITPRICE_ID);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		finish();
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "PGW794DFBZKRBC9XBVGF");
		
		Cursor c = llistaCompraDbAdapter.fetchLlista(listRowId);
		startManagingCursor(c);
		LlistaCompraFormatHelper.setTitle(
				this,
				c.getString(c.getColumnIndex(LlistaCompraDbAdapter.LLISTA_NOM))
						+ ": "
						+ getText(R.string.buying_title)
						+ " "
						+ llistaCompraProducteDbAdapter.preuAcumulatLlista(
								listRowId).toString());
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}

}
