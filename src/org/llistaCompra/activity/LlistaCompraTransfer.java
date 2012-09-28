package org.llistaCompra.activity;

import org.llistaCompra.R;
import org.llistaCompra.activity.helper.LlistaCompraFormatHelper;
import org.llistaCompra.adapter.LlistaCompraDbAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Funció: Crea llista a partir de productes no comprats d'una llista tancada.<br>
 * Layouts: llista_compra_llista_edit.
 */
public class LlistaCompraTransfer extends Activity {
	private EditText mLlistaNewText;
	private Long mLlistaId;
	private String mLlistaNom;
	private LlistaCompraDbAdapter llistaCompraDbAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		llistaCompraDbAdapter = new LlistaCompraDbAdapter(this);
		llistaCompraDbAdapter.open();
		

		LlistaCompraFormatHelper.loadLocale(this);
		
		mLlistaId = (savedInstanceState == null) ? null
				: (Long) savedInstanceState
						.getSerializable(LlistaCompraDbAdapter.LLISTA_ROWID);
		if (mLlistaId == null) {
			Bundle extras = getIntent().getExtras();
			mLlistaId = extras != null ? extras
					.getLong(LlistaCompraDbAdapter.LLISTA_ROWID) : null;
		}
		
		mLlistaNom = (savedInstanceState == null) ? null
				: (String) savedInstanceState
						.getSerializable(LlistaCompraDbAdapter.LLISTA_NOM);
		if (mLlistaNom == null) {
			Bundle extras = getIntent().getExtras();
			mLlistaNom = extras != null ? extras
					.getString(LlistaCompraDbAdapter.LLISTA_NOM) : null;
		}
		
		LlistaCompraFormatHelper.createTitle(this,
				R.layout.llista_compra_llista_edit, getText(R.string.app_name)
						+ ": " + getText(R.string.transfer_list_title) +" "+ mLlistaNom);

		setTitle(R.string.edit_list);

		mLlistaNewText = (EditText) findViewById(R.id.nomLlista);

		mLlistaId = (savedInstanceState == null) ? null
				: (Long) savedInstanceState
						.getSerializable(LlistaCompraDbAdapter.LLISTA_ROWID);
		if (mLlistaId == null) {
			Bundle extras = getIntent().getExtras();
			mLlistaId = extras != null ? extras
					.getLong(LlistaCompraDbAdapter.LLISTA_ROWID) : null;
		}

		Button confirmButton = (Button) findViewById(R.id.button_confirm);
		confirmButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				saveState();
				setResult(RESULT_OK);
				finish();
			}

		});

		Button cancelButton = (Button) findViewById(R.id.button_cancel);
		final Activity activity=this;
		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				setResult(RESULT_OK);
				finish(); 
				//reobrim la pantalla de fi de compra
				Intent i = new Intent(activity, LlistaCompraFiCompra.class);
				i.putExtra(LlistaCompraDbAdapter.LLISTA_ROWID, mLlistaId);
				startActivity(i);
			}

		});
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// TODO Guardar en estat i no en base de dades
		saveState();
		outState.putSerializable(LlistaCompraDbAdapter.LLISTA_ROWID, mLlistaId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// saveState();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void saveState() {
		String listName = mLlistaNewText.getText().toString();

		llistaCompraDbAdapter.transferLlista(listName, mLlistaId);
	}

	@Override
	public void onBackPressed() {
		finish();
	}

}
