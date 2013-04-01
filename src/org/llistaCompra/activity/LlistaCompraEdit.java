package org.llistaCompra.activity;

import org.llistaCompra.R;
import org.llistaCompra.activity.helper.LlistaCompraFormatHelper;
import org.llistaCompra.adapter.LlistaCompraDbAdapter;

import com.flurry.android.FlurryAgent;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Funció: Edita les dades d'una llista de compra.<br>
 * Layouts: llista_compra_llista_edit.
 */
public class LlistaCompraEdit extends Activity {
	private EditText mLlistaText;
	private Long mRowId;
	private LlistaCompraDbAdapter llistaCompraDbAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		llistaCompraDbAdapter = new LlistaCompraDbAdapter(this);
		llistaCompraDbAdapter.open();
		

		LlistaCompraFormatHelper.loadLocale(this);
		
		LlistaCompraFormatHelper.createTitle(this,
				R.layout.llista_compra_llista_edit, getText(R.string.app_name)
						+ ": " + getText(R.string.edit_list_title));

		setTitle(R.string.edit_list);

		mLlistaText = (EditText) findViewById(R.id.nomLlista);

		mRowId = (savedInstanceState == null) ? null
				: (Long) savedInstanceState
						.getSerializable(LlistaCompraDbAdapter.LLISTA_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras
					.getLong(LlistaCompraDbAdapter.LLISTA_ROWID) : null;
		}

		populateFields();

		Button confirmButton = (Button) findViewById(R.id.button_confirm);
		confirmButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				saveState();
				setResult(RESULT_OK);
				finish();
			}

		});

		Button cancelButton = (Button) findViewById(R.id.button_cancel);
		cancelButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				setResult(RESULT_OK);
				finish();
			}

		});
	}

	private void populateFields() {
		if (mRowId != null && mRowId.longValue() != 0) {
			Cursor note = llistaCompraDbAdapter.fetchLlista(mRowId);
			startManagingCursor(note);
			mLlistaText.setText(note.getString(note
					.getColumnIndexOrThrow(LlistaCompraDbAdapter.LLISTA_NOM)));
			note.close();

		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// TODO Guardar en estat i no en base de dades
		saveState();
		outState.putSerializable(LlistaCompraDbAdapter.LLISTA_ROWID, mRowId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// saveState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		populateFields();
	}

	private void saveState() {
		String listName = mLlistaText.getText().toString();

		if (mRowId == null || mRowId.longValue() == 0) {
			long id = llistaCompraDbAdapter.createLlista(listName);
			if (id > 0) {
				mRowId = id;
			}
		} else {
			llistaCompraDbAdapter.updateLlista(mRowId, listName);
		}
	}

	@Override
	public void onBackPressed() {
		finish();
		//Intent i = new Intent(this, LlistaCompraList.class);
		//startActivity(i);
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
