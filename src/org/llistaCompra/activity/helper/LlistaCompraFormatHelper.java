package org.llistaCompra.activity.helper;

import java.util.Locale;

import org.llistaCompra.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * 
 * Helper per administrar l'idioma, el t�tol de les pantalles
 * 
 */
public class LlistaCompraFormatHelper {

	/**
	 * @return the languageToLoad
	 */
	public static String getLanguageToLoad(final Activity activity) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
		String languageToLoad = prefs.getString("Locale", "ca");
		return languageToLoad;
	}


	/**
	 * @param languageToLoad the languageToLoad to set
	 */
	public static void setLanguageToLoad(final Activity activity, String languageToLoad) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
		SharedPreferences.Editor prefEditor = prefs.edit();
		prefEditor.putString("Locale", languageToLoad);
		prefEditor.commit();
	}


	/**
	 * Posa el locale de l'aplicaci� a l'activity
	 * @param activity
	 */
	public static void loadLocale(final Activity activity){		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());

		String languageToLoad = prefs.getString("Locale", "ca");
	    Locale locale = new Locale(languageToLoad);   
	    Locale.setDefault(locale);  
	    Configuration config = activity.getBaseContext().getResources().getConfiguration();  
	    config.locale = locale;  
	    activity.getBaseContext().getResources().updateConfiguration(config,   
	    activity.getBaseContext().getResources().getDisplayMetrics());
	}
	
	/**
	 * Crea la zona de t�tol de l'activity i layouts respectius amb el text (title)
	 * assignat i marca si s'ha de mostrar el bot� enrere (visibility).
	 * 
	 * @param activity 
	 * @param layout
	 * @param title 
	 * @param visibility
	 */
	public static void createTitle(final Activity activity, int layout,
			String title, boolean visibility) {
		  
		
		activity.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		activity.setContentView(layout);
		activity.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.llista_compra_titol);
		((TextView) activity.findViewById(R.id.title)).setText(title);

		ImageButton backButton = (ImageButton) activity.findViewById(R.id.back);
		
		
		
		if (!visibility)
			backButton.setImageResource(R.drawable.exit_button);
		
		
		backButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				activity.onBackPressed();
			}
		});
	}

	/**
	 * Crea la zona de t�tol de l'activity i layouts respectius amb el title
	 * assignat. Es mostra el bot� enrere.
	 * 
	 * @param activity
	 * @param layout
	 * @param title
	 */
	public static void createTitle(final Activity activity, int layout,
			String title) {
		createTitle(activity, layout, title, true);
	}

	/**
	 * Modifica el text mostrat a la zona de t�tol.
	 * 
	 * @param activity
	 * @param title
	 */
	public static void setTitle(final Activity activity, String title) {
		((TextView) activity.findViewById(R.id.title)).setText(title);
	}

	
	/**
	 * Modifica el t�tol del context men�.
	 * @param menu
	 * @param activity
	 */
	public static void setContextMenuTitle(ContextMenu menu, Activity activity){
		
		LayoutInflater factory = LayoutInflater.from(activity);
		final View titleView = factory.inflate(R.layout.llista_compra_contextmenu_titol, null);
		
		ImageButton ib = (ImageButton)titleView.findViewById(R.id.close);
		
		final ContextMenu menuf = menu;
		ib.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				menuf.close();
			}
		});

		menu.setHeaderView(titleView);

	}
}
