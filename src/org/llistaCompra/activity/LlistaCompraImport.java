package org.llistaCompra.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

import org.llistaCompra.R;
import org.llistaCompra.activity.helper.LlistaCompraFormatHelper;
import org.llistaCompra.adapter.LlistaCompraDbAdapter;
import org.llistaCompra.adapter.LlistaCompraProducteDbAdapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * Importar una llista de la compra a partir d'un fitxer .llista amb l'acció VIEW
 * @author Administrador
 *
 */
public class LlistaCompraImport extends Activity {

	private TextView mLlistaText;
	
	private LlistaCompraDbAdapter llistaCompraDbAdapter;
	private LlistaCompraProducteDbAdapter llistaCompraProducteDbAdapter;

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LlistaCompraFormatHelper.loadLocale(this);
        
        LlistaCompraFormatHelper.createTitle(this,
				R.layout.llista_compra_import,
				(String)getText(R.string.app_name), false, false, true);
        

        //initialize the adapters to database
        llistaCompraDbAdapter = new LlistaCompraDbAdapter(this);
		llistaCompraDbAdapter.open();
		llistaCompraProducteDbAdapter = new LlistaCompraProducteDbAdapter(this);
		llistaCompraProducteDbAdapter.open();
        
        // Get the intent that started this activity
        Intent intent = getIntent();
        Uri data = intent.getData();
        
        mLlistaText = (TextView) findViewById(R.id.Llista);
        //llegir fitxer de llista
        try {
            InputStream input = getContentResolver().openInputStream(data);

            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            String str;
            StringBuilder llista = new StringBuilder();
            //get list name and add list in database
            String nomLlista = "";
            long idLlista=0;
            if((str = in.readLine()) != null){
            	nomLlista = str;
            	idLlista = llistaCompraDbAdapter.createLlista(nomLlista);
            	llista.append(str);
            	llista.append("\n");
            }
            //get products and add in list (idLlista)
            while ((str = in.readLine()) != null) {
                String[] prodQuantitat = str.split("\\(");
                if(prodQuantitat.length==2){
                	String productName = prodQuantitat[0].substring(2, prodQuantitat[0].length()-1); //per treure el guio inicial i el espai final
                	String productAmount = prodQuantitat[1].substring(0, prodQuantitat[1].length()-2); // per treure el parentesi i espai final
                	llistaCompraProducteDbAdapter.createProducte(idLlista,
        					productName, Integer.valueOf(productAmount), LlistaCompraDbAdapter.ESTAT_PREPARACIO);                	
                	llista.append(str);
                	llista.append("\n");
                }
            }
            mLlistaText.setText(llista);
            in.close();
        } catch (MalformedURLException e) {
        	Log.e("ERROR IMPORT LIST", e.getMessage());
        } catch (IOException e) {
        	Log.e("ERROR IMPORT LIST", e.getMessage());
        } catch (Exception e) {
        	Log.e("ERROR IMPORT LIST", e.getMessage());
        }
        	
    }

}
