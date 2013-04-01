package org.llistaCompra.activity;

import java.util.HashMap;
import java.util.Map;

import org.llistaCompra.R;
import org.llistaCompra.activity.helper.LlistaCompraFormatHelper;
import org.llistaCompra.constants.FlurryEvents;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

import com.flurry.android.FlurryAgent;

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
				R.layout.main, (String)getText(R.string.app_name), false, true, false);
	    
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
	
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "PGW794DFBZKRBC9XBVGF");
		FlurryAgent.onPageView();
		
		Map<String, String> appParams = new HashMap<String, String>();
		appParams.put("Locale",LlistaCompraFormatHelper.getLanguageToLoad(this));
		FlurryAgent.logEvent(FlurryEvents.START_APP,appParams);
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();
		FlurryAgent.logEvent(FlurryEvents.STOP_APP);
		FlurryAgent.onEndSession(this);
	}


}
