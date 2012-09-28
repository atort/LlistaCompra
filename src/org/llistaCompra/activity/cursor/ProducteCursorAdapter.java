package org.llistaCompra.activity.cursor;

import org.llistaCompra.R;
import org.llistaCompra.activity.LlistaCompraProductPriceEdit;
import org.llistaCompra.activity.LlistaCompraRealitzarCompra;
import org.llistaCompra.activity.helper.LlistaCompraFormatHelper;
import org.llistaCompra.adapter.LlistaCompraDbAdapter;
import org.llistaCompra.adapter.LlistaCompraProducteDbAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * 
 * Cursor utilitzat per LlistaCompraRealitzarCompra.
 * 
 */
public class ProducteCursorAdapter extends SimpleCursorAdapter {

	private Context context;
	private Cursor c;
	private Long idLlista;

	/**
	 * 
	 * @param idLlista Identificador de la llista
	 * @param context 
	 * @param layout 
	 * @param c 
	 * @param from
	 * @param to
	 */
	public ProducteCursorAdapter(Long idLlista, Context context, int layout,
			Cursor c, String[] from, int[] to) {
		super(context, layout, c, from, to);
		this.context = context;
		this.c = c;
		this.idLlista = idLlista;
	}

	@Override
	public View getView(int pos, View inView, ViewGroup parent) {
		View v = inView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.llista_compra_compra_row, null);
		}
		this.c.moveToPosition(pos);
		CheckBox cBox = (CheckBox) v.findViewById(R.id.checkBox1);
		cBox.setTag(this.c.getInt(this.c.getColumnIndex("_id")));

		/*
		 * when reloading the list, check for chkd status, this is broken. Need
		 * to query db directly.
		 */
		LlistaCompraProducteDbAdapter mDbHelper = new LlistaCompraProducteDbAdapter(
				context);
		mDbHelper.open();
		int idTag = (Integer) cBox.getTag();
		Cursor c = mDbHelper.fetchProducte(idLlista, new Long(idTag));
		
		int checked = c
				.getInt(c
						.getColumnIndex(LlistaCompraProducteDbAdapter.PRODUCTE_COMPRAT));
		c.close();
		mDbHelper.close();
		if (checked == 1) {
			cBox.setChecked(true);
		} else {
			cBox.setChecked(false);
		}
		mDbHelper.close();

		/*
		 * Populate the list
		 */
		TextView txt1 = (TextView) v.findViewById(R.id.text1);
		txt1.setText(this.c.getString(this.c
				.getColumnIndex(LlistaCompraProducteDbAdapter.PRODUCTE_NOM)));
		
		TextView quantitat = (TextView) v.findViewById(R.id.textQuantitat);
		quantitat.setText(this.c.getString(this.c
				.getColumnIndex(LlistaCompraProducteDbAdapter.PRODUCTE_QUANTITAT)));

		TextView preu = (TextView) v.findViewById(R.id.price);
		preu.setText(this.c.getString(this.c
				.getColumnIndex(LlistaCompraProducteDbAdapter.PRODUCTE_PREU)));
		
		ImageView imatge = (ImageView) v.findViewById(R.id.icon);

		final long idProducte = new Long(cBox.getTag().toString());
		txt1.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v) {
				Intent i = new Intent(context,
						LlistaCompraProductPriceEdit.class);

				i.putExtra(LlistaCompraProducteDbAdapter.PRODUCTE_LLISTA,
						idLlista);
				i.putExtra(LlistaCompraProducteDbAdapter.PRODUCTE_ROWID,
						idProducte);
				((Activity) context).startActivityForResult(i,
						LlistaCompraRealitzarCompra.EDITPRICE_ID);
						
				return true;
			}
		});
		
		preu.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v) {
				Intent i = new Intent(context,
						LlistaCompraProductPriceEdit.class);

				i.putExtra(LlistaCompraProducteDbAdapter.PRODUCTE_LLISTA,
						idLlista);
				i.putExtra(LlistaCompraProducteDbAdapter.PRODUCTE_ROWID,
						idProducte);
				((Activity) context).startActivityForResult(i,
						LlistaCompraRealitzarCompra.EDITPRICE_ID);
				
				return true;
			}
		});
		
		imatge.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v) {
				Intent i = new Intent(context,
						LlistaCompraProductPriceEdit.class);

				i.putExtra(LlistaCompraProducteDbAdapter.PRODUCTE_LLISTA,
						idLlista);
				i.putExtra(LlistaCompraProducteDbAdapter.PRODUCTE_ROWID,
						idProducte);
				((Activity) context).startActivityForResult(i,
						LlistaCompraRealitzarCompra.EDITPRICE_ID);
						

				return true;
			}
		});
		
		/*
		 * Controls action based on clicked checkbox
		 */
		cBox.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				LlistaCompraProducteDbAdapter mDbHelper = new LlistaCompraProducteDbAdapter(
						context);
				mDbHelper.open();
				
				LlistaCompraDbAdapter llistaCompraDbAdapter = new LlistaCompraDbAdapter(
						context);
				llistaCompraDbAdapter.open();


				CheckBox cBox = (CheckBox) v.findViewById(R.id.checkBox1);

				if (cBox.isChecked()) {
					mDbHelper.updateProducteCompratLlista(idLlista, new Long(
							cBox.getTag().toString()), new Integer(1));
				} else if (!cBox.isChecked()) {
					mDbHelper.updateProducteCompratLlista(idLlista, new Long(
							cBox.getTag().toString()), new Integer(0));
				}
				
				Cursor c = llistaCompraDbAdapter.fetchLlista(idLlista);
				LlistaCompraFormatHelper.setTitle(((Activity) context),
						c.getString(c.getColumnIndex(LlistaCompraDbAdapter.LLISTA_NOM))
								+ ": " + 	((Activity) context).getText(R.string.buying_title)
								+" "+mDbHelper.preuAcumulatLlista(idLlista).toString());
				c.close();
				mDbHelper.close();
			}
		});
		return (v);
	}

}
