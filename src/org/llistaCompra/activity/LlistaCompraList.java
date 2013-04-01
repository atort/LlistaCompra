package org.llistaCompra.activity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.llistaCompra.R;
import org.llistaCompra.activity.cursor.LlistaCursorAdapter;
import org.llistaCompra.activity.helper.LlistaCompraFormatHelper;
import org.llistaCompra.adapter.LlistaCompraDbAdapter;
import org.llistaCompra.constants.FlurryEvents;
import org.llistaCompra.provider.FileProvider;

import com.flurry.android.FlurryAgent;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
 * Funci�: Mostra les llistes de compra. <br>
 * Cursor: LlistaCursorAdapter.<br>
 * Funcions cridades: <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;Clic: Mostrar productes.<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;Press: Editar llista, Esborrar llista, Mostrar
 * productes.<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;Men�: Crear llista. <br>
 * Layouts: llista_compra_llista_list, llista_compra_llista_row.
 */
public class LlistaCompraList extends ListActivity {

	public static final int ADDLIST_ID = Menu.FIRST;

	public static final int PRODUCTLIST_ID = 0;

	public static final int CREATELIST_ID = 1;

	public static final int EDITLIST_ID = 2;
	public static final int DELETELIST_ID = 3;
	public static final int SHARELIST_ID = 4;

	private LlistaCompraDbAdapter llistaCompraDbAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LlistaCompraFormatHelper.loadLocale(this);
		
		setContentView(R.layout.llista_compra_llista_list);
		
		llistaCompraDbAdapter = new LlistaCompraDbAdapter(this);
		llistaCompraDbAdapter.open();
		fillData();
		registerForContextMenu(getListView());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case ADDLIST_ID:
			createList();
			return true;
			}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, ADDLIST_ID, 0, R.string.insert_list);
		return result;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i;
		// final long idList2 = info.id;
		Cursor c = llistaCompraDbAdapter.fetchLlista(id);
		startManagingCursor(c);
		int estat = (Integer) c.getInt(c
				.getColumnIndex(LlistaCompraDbAdapter.LLISTA_ESTAT));

		if (estat == LlistaCompraDbAdapter.ESTAT_PREPARACIO) {
			i = new Intent(this, LlistaCompraProductList.class);
		} else if (estat == LlistaCompraDbAdapter.ESTAT_COMPRANT) {
			i = new Intent(this, LlistaCompraRealitzarCompra.class);
		} else {
			i = new Intent(this, LlistaCompraFiCompra.class);
		}

		// i = new Intent(this, LlistaCompraProductList.class);
		i.putExtra(LlistaCompraDbAdapter.LLISTA_ROWID, id);
		// startActivityForResult(i, PRODUCTLIST_ID);
		startActivity(i);
		
	}

	private void fillData() {
		// Get all of the notes from the database and create the item list
		Cursor c = llistaCompraDbAdapter.fetchAllLlista();
		startManagingCursor(c);

		String[] from = new String[] { LlistaCompraDbAdapter.LLISTA_NOM };
		int[] to = new int[] { R.id.text1 };

		// Now create an array adapter and set it to display using our row
		SimpleCursorAdapter llistaCompra = new LlistaCursorAdapter(this,
				R.layout.llista_compra_llista_row, c, from, to);
		setListAdapter(llistaCompra);
		
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		LlistaCompraFormatHelper.setContextMenuTitle(menu, this);

		menu.add(0, EDITLIST_ID, 0, R.string.edit_list);
		menu.add(0, DELETELIST_ID, 1, R.string.delete_list);
		menu.add(0, PRODUCTLIST_ID, 2, R.string.product_list);
		menu.add(0, SHARELIST_ID, 2, R.string.share_list);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info;
		Intent i;
		switch (item.getItemId()) {
		case DELETELIST_ID:
			//FLURRY
			FlurryAgent.logEvent(FlurryEvents.DELETE_LIST);
			
			info = (AdapterContextMenuInfo) item.getMenuInfo();
			final long idList = info.id;

			new AlertDialog.Builder(this)
					.setTitle(R.string.delete_list_title)
					.setMessage(R.string.delete_list_text)
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									llistaCompraDbAdapter.deleteLlista(idList);
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

			return true;
		case EDITLIST_ID:
			i = new Intent(this, LlistaCompraEdit.class);
			info = (AdapterContextMenuInfo) item.getMenuInfo();
			i.putExtra(LlistaCompraDbAdapter.LLISTA_ROWID, info.id);
			startActivityForResult(i, EDITLIST_ID);
			return true;
		case PRODUCTLIST_ID:
			info = (AdapterContextMenuInfo) item.getMenuInfo();
			final long idList2 = info.id;
			Cursor c = llistaCompraDbAdapter.fetchLlista(idList2);
			startManagingCursor(c);
			int estat = (Integer) c.getInt(c
					.getColumnIndex(LlistaCompraDbAdapter.LLISTA_ESTAT));

			if (estat == LlistaCompraDbAdapter.ESTAT_PREPARACIO) {
				i = new Intent(this, LlistaCompraProductList.class);
			} else if (estat == LlistaCompraDbAdapter.ESTAT_COMPRANT) {
				i = new Intent(this, LlistaCompraRealitzarCompra.class);
			} else {
				i = new Intent(this, LlistaCompraFiCompra.class);
			}

			info = (AdapterContextMenuInfo) item.getMenuInfo();
			i.putExtra(LlistaCompraDbAdapter.LLISTA_ROWID, info.id);
			startActivityForResult(i, PRODUCTLIST_ID);
	
			return true;
			
		case SHARELIST_ID: //compartir lista (via gmail, whatsapp, etc)
			//FLURRY
			FlurryAgent.logEvent(FlurryEvents.SHARE_LIST);
			
			i = new Intent(this, LlistaCompraEdit.class);
			info = (AdapterContextMenuInfo) item.getMenuInfo();
			//obtener lista en formato texto
			String textList = llistaCompraDbAdapter.getTextList(info.id);
			String subject = getString(R.string.app_name);
			share(subject, textList);
			return true;
		}
		
		return super.onContextItemSelected(item);
	}

	private void createList() {
		//FLURRY
		FlurryAgent.logEvent(FlurryEvents.NEW_LIST);
		
		Intent i = new Intent(this, LlistaCompraEdit.class);
		startActivityForResult(i, CREATELIST_ID);
	}
	


	@Override
	public void onBackPressed() {
		// Sortir de l'aplicaci�
		// Log.d(this.getClass().getName(), "Entra per aqu�");
		this.finish();
	}
	
	private void share(String subject,String text) {
		
		
	    final Intent intent = new Intent(Intent.ACTION_SEND);

	    intent.setType("text/plain");
	    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
	    intent.putExtra(Intent.EXTRA_TEXT, text);
	    String nameFile;
		try {
			nameFile = writeFileList(subject,text);
			intent.putExtra(Intent.EXTRA_STREAM, 
		    		Uri.parse(FileProvider.CONTENT_URI +nameFile));
		} catch (IOException e) {
			Log.e("ERROR FILE", e.getMessage());
		}
	    
	    startActivity(Intent.createChooser(intent, getString(R.string.share)));
	}

	public String writeFileList(String nameList, String bodyList)
			throws IOException {
		
		String nameFile = nameList + ".llista.txt";
		File f = new File(getFilesDir(), nameFile);
		BufferedWriter out = new BufferedWriter(new FileWriter(f));
		out.write(bodyList);
		out.close();

		return nameFile;
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "PGW794DFBZKRBC9XBVGF");
		FlurryAgent.onPageView();
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}
}