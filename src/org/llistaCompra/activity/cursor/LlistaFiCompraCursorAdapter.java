package org.llistaCompra.activity.cursor;

import org.llistaCompra.R;
import org.llistaCompra.adapter.LlistaCompraProducteDbAdapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Cursor utilitzat per LlistaCompraFiCompra.
 */
public class LlistaFiCompraCursorAdapter extends SimpleCursorAdapter {

	private Context context;
	private Cursor c;

	/**
	 * 
	 * @param context
	 * @param layout
	 * @param c
	 * @param from
	 * @param to
	 */
	public LlistaFiCompraCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		this.context = context;
		this.c = c;
	}

	@Override
	public View getView(int pos, View inView, ViewGroup parent) {

		View v = inView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.llista_compra_fi_compra_row, null);
		}
		this.c.moveToPosition(pos);

		/*
		 * Populate the list
		 */
		TextView txt1 = (TextView) v.findViewById(R.id.text1);
		String productName = this.c.getString(this.c
				.getColumnIndex(LlistaCompraProducteDbAdapter.PRODUCTE_NOM));
		String quantitat= this.c.getString(this.c
				.getColumnIndex(LlistaCompraProducteDbAdapter.PRODUCTE_QUANTITAT));
		txt1.setText(productName + " ("+quantitat+")");
		
		TextView preu = (TextView) v.findViewById(R.id.price);
		preu.setText(this.c.getString(this.c
				.getColumnIndex(LlistaCompraProducteDbAdapter.PRODUCTE_PREU)));		

		ImageView result = (ImageView) v.findViewById(R.id.result);

		int estat = this.c
				.getInt(this.c
						.getColumnIndex(LlistaCompraProducteDbAdapter.PRODUCTE_COMPRAT));
		int inicial = this.c
				.getInt(this.c
						.getColumnIndex(LlistaCompraProducteDbAdapter.PRODUCTE_LLISTA_INICIAL));
		if (estat == LlistaCompraProducteDbAdapter.ESTAT_COMPRAT
				&& inicial == LlistaCompraProducteDbAdapter.ESTAT_INICIAL)
			result.setImageResource(R.drawable.ok);
		else if (estat == LlistaCompraProducteDbAdapter.ESTAT_COMPRAT
				&& inicial != LlistaCompraProducteDbAdapter.ESTAT_INICIAL)
			result.setImageResource(R.drawable.alert);
		else if (estat == LlistaCompraProducteDbAdapter.ESTAT_NO_COMPRAT) {
			result.setImageResource(R.drawable.ko);
		}
		return (v);
	}

}
