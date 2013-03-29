package org.llistaCompra.activity;

import org.llistaCompra.R;
import org.llistaCompra.activity.cursor.LlistaFiCompraCursorAdapter;
import org.llistaCompra.activity.helper.LlistaCompraFormatHelper;
import org.llistaCompra.adapter.LlistaCompraDbAdapter;
import org.llistaCompra.adapter.LlistaCompraProducteDbAdapter;

import com.flurry.android.FlurryAgent;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.SimpleCursorAdapter;

/**
 * 
 * Funció: Mostra els productes d'una llista de compra ja tancada. <br>
 * Cursor: LlistaFiCompraCursorAdapter. <br>
 * Funcions cridades: <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;Menú: Reobrir compra.<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;Press: Editar producte.<br> 
 * Layouts: llista_compra_producte_list,llista_compra_fi_compra_row.
 *  
 */
public class LlistaCompraFiCompra extends ListActivity {

	private LlistaCompraProducteDbAdapter llistaCompraProducteDbAdapter;
	private LlistaCompraDbAdapter llistaCompraDbAdapter;

	private Long listRowId;
	private String nomLlista;
	
	private static final int EDITPRODUCT_ID = 0;
	
	private static final int OPENLIST_ID = 1;
	private static final int TRANSFERLIST_ID = 2;	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LlistaCompraFormatHelper.loadLocale(this);
		
		LlistaCompraFormatHelper.createTitle(this,
				R.layout.llista_compra_producte_list,
				getText(R.string.app_name) + ": "
						+ getText(R.string.end_buying_title));

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
		
		nomLlista = c.getString(c.getColumnIndex(LlistaCompraDbAdapter.LLISTA_NOM));
		LlistaCompraFormatHelper.setTitle(this,
				nomLlista
						+ ": " + getText(R.string.end_buying_title)
						+" "+llistaCompraProducteDbAdapter.preuAcumulatLlista(listRowId).toString()
						);
		fillData();
		registerForContextMenu(getListView());
		
	}

	private void fillData() {
		// Get all of the notes from the database and create the item list
		Cursor c = llistaCompraProducteDbAdapter.fetchAllProductes(listRowId);
		startManagingCursor(c);

		String[] from = new String[] { 
				LlistaCompraProducteDbAdapter.PRODUCTE_NOM};
		int[] to = new int[] { R.id.text1};

		// Now create an array adapter and set it to display using our row
		SimpleCursorAdapter llistaCompra = new LlistaFiCompraCursorAdapter(
				this, R.layout.llista_compra_fi_compra_row, c, from, to);
		setListAdapter(llistaCompra);
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case OPENLIST_ID:
			final LlistaCompraFiCompra llistaCompraFiCompra = this;
			new AlertDialog.Builder(this)
			.setTitle(R.string.openBuy_title)
			.setMessage(R.string.openBuyEnd_text)
			.setPositiveButton(R.string.yes,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog,
								int which) {
							llistaCompraDbAdapter.startBuyingLlista(listRowId);
							
							Intent i = new Intent(
									llistaCompraFiCompra,
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
			break;
		case TRANSFERLIST_ID:
			Intent i = new Intent(this, LlistaCompraTransfer.class);
			i.putExtra(LlistaCompraDbAdapter.LLISTA_ROWID, listRowId);
			i.putExtra(LlistaCompraDbAdapter.LLISTA_NOM, nomLlista);
			finish();
			startActivity(i);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, OPENLIST_ID, 0, R.string.openBuy_title);
		menu.add(0, TRANSFERLIST_ID, 0, R.string.transferBuy_title);
		return result;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		LlistaCompraFormatHelper.setContextMenuTitle(menu, this);
		menu.add(0, EDITPRODUCT_ID, 0, R.string.edit_product);
		
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info;
		Intent i;
		switch (item.getItemId()) {
		case EDITPRODUCT_ID:
			info = (AdapterContextMenuInfo) item.getMenuInfo();
			long idProduct = info.id;
			i = new Intent(this, LlistaCompraProductEdit.class);
			i.putExtra(LlistaCompraProducteDbAdapter.PRODUCTE_ROWID,
					idProduct);
			i.putExtra(LlistaCompraProducteDbAdapter.PRODUCTE_LLISTA,
					listRowId);
			startActivityForResult(i, EDITPRODUCT_ID);
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
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}
}
