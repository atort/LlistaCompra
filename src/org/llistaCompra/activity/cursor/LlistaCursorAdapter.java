package org.llistaCompra.activity.cursor;

import org.llistaCompra.R;
import org.llistaCompra.adapter.LlistaCompraDbAdapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Cursor utilitzat per LlistaCompraList.
 */
public class LlistaCursorAdapter extends SimpleCursorAdapter {

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
	public LlistaCursorAdapter(Context context, int layout, Cursor c,
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
			v = inflater.inflate(R.layout.llista_compra_llista_row, null);
		}
		this.c.moveToPosition(pos);

		/*
		 * Populate the list
		 */
		TextView txt1 = (TextView) v.findViewById(R.id.text1);
		txt1.setText(this.c.getString(this.c
				.getColumnIndex(LlistaCompraDbAdapter.LLISTA_NOM)));

		ImageView imageView = (ImageView) v.findViewById(R.id.icon);

		int estat = this.c.getInt(this.c
				.getColumnIndex(LlistaCompraDbAdapter.LLISTA_ESTAT));
		switch (estat) {
		case LlistaCompraDbAdapter.ESTAT_PREPARACIO:
			imageView.setImageResource(R.drawable.basket_pendent);
			break;
		case LlistaCompraDbAdapter.ESTAT_COMPRANT:
			imageView.setImageResource(R.drawable.basket_iniciada);
			break;
		case LlistaCompraDbAdapter.ESTAT_FINALITZADA:
			imageView.setImageResource(R.drawable.basket_finalitzada);
			break;
		}
		return (v);
	}

}
