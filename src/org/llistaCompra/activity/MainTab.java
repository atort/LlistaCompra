package org.llistaCompra.activity;

import org.llistaCompra.R;
import org.llistaCompra.activity.helper.LlistaCompraFormatHelper;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

/**
 * Pantalla inicial: Disposa de tabs per les diferents opcions:
 * Llistat de Llistes compra
 * Preferencies de sistema
 * 
 *
 */
public class MainTab extends TabActivity {
	
	public static String NAME_INDEX="IndexTab";
	
	public static Integer INDEX_TAB_LLISTES=0;
	public static Integer INDEX_TAB_PREFERENCIES=1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    LlistaCompraFormatHelper.loadLocale(this);
	    		
	    LlistaCompraFormatHelper.createTitle(this,
				R.layout.main, (String)getText(R.string.app_name), false);
	    
	    //setContentView(R.layout.main);

	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, LlistaCompraList.class);

	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("Llistes").setIndicator(getText(R.string.tab_llistes),
	                      res.getDrawable(R.drawable.ic_tab_llistes))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, PreferencesLlistaCompra.class);
	    spec = tabHost.newTabSpec("Preferencies").setIndicator(getText(R.string.tab_preferencies),
	                      res.getDrawable(R.drawable.ic_tab_preferences))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    Integer currentTab = (savedInstanceState == null) ? null
				: (Integer) savedInstanceState
						.getSerializable(MainTab.NAME_INDEX);
		if (currentTab == null) {
			Bundle extras = getIntent().getExtras();
			currentTab = extras != null ? extras
					.getInt(MainTab.NAME_INDEX) : MainTab.INDEX_TAB_LLISTES;
		}
		
	    tabHost.setCurrentTab(currentTab);
	}


}
