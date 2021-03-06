package org.llistaCompra.activity;

import org.llistaCompra.R;
import org.llistaCompra.activity.helper.LlistaCompraFormatHelper;
import org.llistaCompra.adapter.LlistaCompraDbAdapter;
import org.llistaCompra.adapter.LlistaCompraProducteDbAdapter;

import com.flurry.android.FlurryAgent;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Funci�: Edita el preu d'un producte.<br>
 * Layouts: llista_compra_producte_preu_edit.
 */
public class LlistaCompraProductPriceEdit extends Activity {

	private EditText mProductePreuText;
	private Long mRowId; // id del producte
	private Long mLlistaId;
	private Integer mLlistaInicial;
	private Integer mEstatLlista;

	private LlistaCompraProducteDbAdapter llistaCompraProducteDbAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		llistaCompraProducteDbAdapter = new LlistaCompraProducteDbAdapter(this);
		llistaCompraProducteDbAdapter.open();

		LlistaCompraFormatHelper.loadLocale(this);

		LlistaCompraFormatHelper.createTitle(this,
				R.layout.llista_compra_producte_preu_edit,
				getText(R.string.app_name) + ": "
						+ getText(R.string.insertPrice_title));
		setTitle(R.string.edit_product);

		mProductePreuText = (EditText) findViewById(R.id.preuProducte);

		// agafar id del producte
		mRowId = (savedInstanceState == null) ? null
				: (Long) savedInstanceState
						.getSerializable(LlistaCompraProducteDbAdapter.PRODUCTE_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras
					.getLong(LlistaCompraProducteDbAdapter.PRODUCTE_ROWID)
					: null;
		}

		// agafar id de la lista
		mLlistaId = (savedInstanceState == null) ? null
				: (Long) savedInstanceState
						.getSerializable(LlistaCompraProducteDbAdapter.PRODUCTE_LLISTA);
		if (mLlistaId == null) {
			Bundle extras = getIntent().getExtras();
			mLlistaId = extras != null ? extras
					.getLong(LlistaCompraProducteDbAdapter.PRODUCTE_LLISTA)
					: null;
		}

		if (mLlistaInicial == null) {
			Bundle extras = getIntent().getExtras();
			mLlistaInicial = extras != null ? extras
					.getInt(LlistaCompraProducteDbAdapter.PRODUCTE_LLISTA_INICIAL)
					: null;
		}

		if (mEstatLlista == null) {
			Bundle extras = getIntent().getExtras();
			mEstatLlista = extras != null ? extras
					.getInt(LlistaCompraDbAdapter.LLISTA_ESTAT) : null;
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
			Cursor producte = llistaCompraProducteDbAdapter.fetchProducte(
					mLlistaId, mRowId);
			startManagingCursor(producte);
			mProductePreuText
					.setText(producte.getString(producte
							.getColumnIndexOrThrow(LlistaCompraProducteDbAdapter.PRODUCTE_PREU)));
			

		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// TODO Guardar en estat i no en base de dades
		saveState();
		outState.putSerializable(LlistaCompraProducteDbAdapter.PRODUCTE_ROWID,
				mRowId);
		outState.putSerializable(LlistaCompraProducteDbAdapter.PRODUCTE_LLISTA,
				mLlistaId);
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
		String productPrice = mProductePreuText.getText().toString();
		try {
			Double d = Double.valueOf(productPrice);
			llistaCompraProducteDbAdapter.updateProductePreuLlista(mLlistaId,
					mRowId, d);
		} catch (Exception e) {
			Log.d(LlistaCompraProductPriceEdit.class.toString(), e.getMessage());
		}
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
		FlurryAgent.onPageView();
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}
}
