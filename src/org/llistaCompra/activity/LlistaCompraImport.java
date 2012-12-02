package org.llistaCompra.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

import org.llistaCompra.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Importar una llista de la compra a partir d'un fitxer .llista amb l'acció VIEW
 * @author Administrador
 *
 */
public class LlistaCompraImport extends Activity {

	private TextView mLlistaText;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llista_compra_import);
        
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
            while ((str = in.readLine()) != null) {
                // str is one line of text; readLine() strips the newline character(s)
            	llista.append(str);
            }
            mLlistaText.setText(llista);
            in.close();
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
    }

}
