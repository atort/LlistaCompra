package org.llistaCompra.activity;

import java.util.ArrayList;
import java.util.List;

import org.llistaCompra.R;
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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * 
 * Funció:Mostra els productes d'una llista. <br>
 * Cursor: SimpleCursorAdapter. <br>
 * Funcions cridades: <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;Clic:Editar Producte.<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;Press: Editar producte, Esborrar producte.<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;Men�: Crear producte, Comen�ar compra.<br>
 * Layouts: llista_compra_producte_list, llista_compra_llista_row.
 */
public class LlistaCompraProductList extends ListActivity {
	public static final int ADDPRODUCT_ID = Menu.FIRST;
	public static final int ADDPRODUCTVOICE_ID = Menu.FIRST + 1;
	public static final int BEGINBUY_ID = Menu.FIRST + 2;

	private static final int EDITPRODUCT_ID = 0;
	private static final int DELETEPRODUCT_ID = 1;
	private static final int ADDPRODUCTVOICE_CODE = 2;

	private LlistaCompraProducteDbAdapter llistaCompraProducteDbAdapter;
	private LlistaCompraDbAdapter llistaCompraDbAdapter;

	private Long listRowId;

	private int estatCompra;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LlistaCompraFormatHelper.loadLocale(this);
		
		LlistaCompraFormatHelper.createTitle(this,
				R.layout.llista_compra_producte_list,
				getText(R.string.app_name) + ": "
						+ getText(R.string.products_title));

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

		Cursor c = llistaCompraDbAdapter.fetchLlista(listRowId);
		startManagingCursor(c);
		LlistaCompraFormatHelper.setTitle(this,
				c.getString(c.getColumnIndex(LlistaCompraDbAdapter.LLISTA_NOM))
						+ ": " + getText(R.string.products_title));

		// guardar estat de la compra
		estatCompra = llistaCompraDbAdapter.getEstatLlista(listRowId);

		fillData();
		registerForContextMenu(getListView());
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case ADDPRODUCT_ID:
			// Afegir producte
			// S'ha de mirar si la compra est� en preparacio
			if (LlistaCompraDbAdapter.ESTAT_PREPARACIO != estatCompra) {
				new AlertDialog.Builder(this)
						.setTitle(R.string.warning_title)
						.setMessage(R.string.compraIniciada_text)
						.setPositiveButton(R.string.confirm,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).show();
			} else {
				Intent i = new Intent(this, LlistaCompraProductEdit.class);
				i.putExtra(LlistaCompraProducteDbAdapter.PRODUCTE_LLISTA,
						listRowId);
				i.putExtra(
						LlistaCompraProducteDbAdapter.PRODUCTE_LLISTA_INICIAL,1);
				// Afegir estat en que esta la compra
				i.putExtra(LlistaCompraDbAdapter.LLISTA_ESTAT, estatCompra);
				startActivityForResult(i, ADDPRODUCT_ID);
			}

			return true;
		case BEGINBUY_ID:
			return beginBuy();
		case ADDPRODUCTVOICE_ID:
			return addProductVoice();

		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Funci� encarregada de comen�ada de comen�ar la compra de la llista que se
	 * est� editant (afegint productes)
	 * 
	 * @return Retorna true si tot correcte, cap excepci�.
	 */
	private boolean beginBuy() {
		// S'ha de mirar si la compra est� en preparacio
		if (LlistaCompraDbAdapter.ESTAT_PREPARACIO != estatCompra) {
			new AlertDialog.Builder(this)
					.setTitle(R.string.warning_title)
					.setMessage(R.string.compraIniciada_text)
					.setPositiveButton(R.string.confirm,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).show();
		} else {
			final LlistaCompraProductList llistaCompraProductList = this;
			new AlertDialog.Builder(this)
					.setTitle(R.string.beginBuy_title)
					.setMessage(R.string.beginBuy_text)
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									llistaCompraDbAdapter
											.startBuyingLlista(listRowId);
									estatCompra = LlistaCompraDbAdapter.ESTAT_COMPRANT;
									Intent i = new Intent(
											llistaCompraProductList,
											LlistaCompraRealitzarCompra.class);
									i.putExtra(
											LlistaCompraDbAdapter.LLISTA_ROWID,
											listRowId);

									finish();
									startActivity(i);
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
	}

	private boolean addProductVoice() {
		// Afegir producte per veu
		// S'ha de mirar si la compra est� en preparacio
		if (LlistaCompraDbAdapter.ESTAT_PREPARACIO != estatCompra) {
			new AlertDialog.Builder(this)
					.setTitle(R.string.warning_title)
					.setMessage(R.string.compraIniciada_text)
					.setPositiveButton(R.string.confirm,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).show();
		} else {
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
	                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getText(R.string.producteVeu_text));
	        startActivityForResult(intent, ADDPRODUCTVOICE_CODE);
		}
		return true;
	}
	
	/**
     * Handle the results from the voice recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == ADDPRODUCTVOICE_CODE && resultCode == RESULT_OK)
        {
            // Populate the wordsList with the String values the recognition engine thought it heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            Log.d(this.getClass().getName(),"Nou producte: "+matches);
            
            final CharSequence[] items = {getText(R.string.add_and_next),
            		getText(R.string.add_and_exit), getText(R.string.repeat), getText(R.string.cancel)};

            final String nameProduct=matches.get(0);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(nameProduct);
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    switch(item){
                    	case 0:
                    		//Afegir i Continuar: Afegim i tornem a posar per gravar
                    		//afegir porducte a bbdd
                            llistaCompraProducteDbAdapter.createProducte(listRowId,
                            		nameProduct, 1, 1);
                          //refrescar llistat
                            fillData();
                            addProductVoice();
                    		break;
                    	case 1:
                    		//Afegir i Sortir: Afegim i refresquem llistat
                    		//afegir porducte a bbdd
                            llistaCompraProducteDbAdapter.createProducte(listRowId,
                            		nameProduct, 1, 1);
                            //refrescar llistat
                            fillData();
                    		break;
                    	case 2:
                    		//Repetir: Tornem a posar per gravar i no afegim
                    		addProductVoice();
                    		break;
                    	case 3:
                    		//Cancel.lar:no fem res                    		
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
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, ADDPRODUCT_ID, 0, R.string.insert_product);
		// Si te reconeixement de veu posa en el menu el reconeixement
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() == 0) {
			Log.d(this.getClass().getName(), "No t� reconeixement de veu");
		} else {
			menu.add(0, ADDPRODUCTVOICE_ID, 1, R.string.insert_product_voice);
		}

		menu.add(0, BEGINBUY_ID, 2, R.string.start_buying);

		return result;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		LlistaCompraFormatHelper.setContextMenuTitle(menu, this);
		menu.add(0, EDITPRODUCT_ID, 0, R.string.edit_product);
		menu.add(0, DELETEPRODUCT_ID, 1, R.string.delete_product);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info;
		Intent i;
		switch (item.getItemId()) {
		case DELETEPRODUCT_ID:
			// S'ha de mirar si la compra est� en preparacio
			if (LlistaCompraDbAdapter.ESTAT_PREPARACIO != estatCompra) {
				new AlertDialog.Builder(this)
						.setTitle(R.string.warning_title)
						.setMessage(R.string.compraIniciada_text)
						.setPositiveButton(R.string.confirm,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).show();
			} else {
				info = (AdapterContextMenuInfo) item.getMenuInfo();
				final long idProduct = info.id;

				new AlertDialog.Builder(this)
						.setTitle(R.string.delete_product_title)
						.setMessage(R.string.delete_product_text)
						.setPositiveButton(R.string.yes,
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										llistaCompraProducteDbAdapter
												.deleteProducte(listRowId,
														idProduct);
										fillData();
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
		case EDITPRODUCT_ID:
			// S'ha de mirar si la compra est� en preparacio
			if (LlistaCompraDbAdapter.ESTAT_PREPARACIO != estatCompra) {
				new AlertDialog.Builder(this)
						.setTitle(R.string.warning_title)
						.setMessage(R.string.compraIniciada_text)
						.setPositiveButton(R.string.confirm,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).show();
			} else {
				info = (AdapterContextMenuInfo) item.getMenuInfo();
				long idProduct = info.id;

				i = new Intent(this, LlistaCompraProductEdit.class);
				i.putExtra(LlistaCompraProducteDbAdapter.PRODUCTE_ROWID,
						idProduct);
				i.putExtra(LlistaCompraProducteDbAdapter.PRODUCTE_LLISTA,
						listRowId);
				i.putExtra(LlistaCompraDbAdapter.LLISTA_ESTAT, estatCompra);
				startActivityForResult(i, EDITPRODUCT_ID);
			}
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, LlistaCompraProductEdit.class);
		i.putExtra(LlistaCompraProducteDbAdapter.PRODUCTE_ROWID, id);
		i.putExtra(LlistaCompraProducteDbAdapter.PRODUCTE_LLISTA, listRowId);
		startActivityForResult(i, EDITPRODUCT_ID);
	}

	private void fillData() {
		// Get all of the notes from the database and create the item list
		Cursor c = llistaCompraProducteDbAdapter.fetchAllProductesFormat(listRowId);
		startManagingCursor(c);

		String[] from = new String[] {
				"PRODUCTE_QUANTITAT"};
		int[] to = new int[] { R.id.text1 };

		// Now create an array adapter and set it to display using our row
		SimpleCursorAdapter llistaCompra = new SimpleCursorAdapter(this,
				R.layout.llista_compra_producte_row, c, from, to);
		setListAdapter(llistaCompra);
		
	}

	/*
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) { if
	 * ((keyCode == KeyEvent.KEYCODE_BACK)) { Log.d(this.getClass().getName(),
	 * "back button pressed"); Intent i = new Intent(this,
	 * LlistaCompraList.class); startActivityForResult(i, 0); } return
	 * super.onKeyDown(keyCode, event); }
	 */

	@Override
	public void onBackPressed() {
		// Intent i = new Intent(this, LlistaCompraList.class);
		// startActivity(i);
		finish();
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "PGW794DFBZKRBC9XBVGF");
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}

}
