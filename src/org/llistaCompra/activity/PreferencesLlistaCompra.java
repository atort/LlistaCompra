package org.llistaCompra.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.llistaCompra.R;
import org.llistaCompra.activity.helper.LlistaCompraFormatHelper;
import org.llistaCompra.helper.LlistaCompraHelper;
import org.llistaCompra.to.Idioma;

import com.flurry.android.FlurryAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Tab de prefer锟絥cies
 * 
 */
public class PreferencesLlistaCompra extends Activity {

	private boolean firstSelection;

	public static final int EXPORT_BACKUPDB_ID = Menu.FIRST;
	public static final int RESTORE_BACKUPDB_ID = Menu.FIRST + 1;

	// Variables per fer el restore de la db
	private String[] mFileList;
	private String mChosenFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LlistaCompraFormatHelper.loadLocale(this);

		setContentView(R.layout.preferences_llista_compra);

		Spinner spinner = (Spinner) findViewById(R.id.spinnerIdioma);
		ArrayAdapter<Idioma> adapter = new ArrayAdapter<Idioma>(this,
				android.R.layout.simple_spinner_item);
		adapter.add(new Idioma("Catal酄", "ca"));
		adapter.add(new Idioma("English", "en"));
		adapter.add(new Idioma("Espa駉l", "es"));

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		Idioma selectedIdioma = new Idioma(
				LlistaCompraFormatHelper.getLanguageToLoad(this));
		int i = adapter.getPosition(selectedIdioma);
		spinner.setSelection(i);
		final Activity preferencesActivity = this;

		firstSelection = true;
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View v, int pos,
					long id) {
				if (!firstSelection) {
					Idioma selectedIdioma = (Idioma) parent
							.getItemAtPosition(pos);
					LlistaCompraFormatHelper.setLanguageToLoad(
							preferencesActivity, selectedIdioma.getCode());
					// tornar a la llista, refrescar llista
					MainTab parentActivity = (MainTab) preferencesActivity
							.getParent();
					parentActivity.finish();
					Intent intent = new Intent(preferencesActivity,
							MainTab.class);
					intent.putExtra(MainTab.NAME_INDEX,
							MainTab.INDEX_TAB_PREFERENCIES);
					startActivity(intent);
				} else {
					firstSelection = false;
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case EXPORT_BACKUPDB_ID:
			exportBackupDB();
			return true;
		case RESTORE_BACKUPDB_ID:
			restoreBackupDB();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, EXPORT_BACKUPDB_ID, 0, R.string.send_backup_db);
		menu.add(0, RESTORE_BACKUPDB_ID, 0, R.string.restore_backup_db);
		return result;
	}

	/**
	 * Crea un backup de la base de dades. Es crea a la sd, dintre de la carpeta
	 * LlistaCompra i amb el nom: dia i hora i nom de la base de dades. Ex:
	 * LlistaCompra/22052012_102325_DbLlistaCompra.db
	 */
	private void exportBackupDB() {
		try {
			// obtenir la ubicaci贸 de la sd
			File sd = Environment.getExternalStorageDirectory();

			// mirar si t茅 permis d'escriptura
			if (sd.canWrite()) {

				// mirar si hi ha el directori LlistaCompra, sino crearlo
				File exportDir = new File(sd, "LlistaCompra");
				if (!exportDir.exists()) {
					exportDir.mkdirs();
				}

				// nom fitxer backup
				SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault());
				String currentDateTimeString = sdf.format(new Date());
				String backupDBPath = currentDateTimeString + "_"
						+ LlistaCompraHelper.DATABASE_NAME;

				// localitzaci贸 base de dades actual
				File currentDB = getDatabasePath(LlistaCompraHelper.DATABASE_NAME);
				// localitzaci贸 del backup a realitzar
				File backupDB = new File(exportDir, backupDBPath);

				if (currentDB.exists()) {
					// fer copia base de dades
					FileInputStream srcIn = new FileInputStream(currentDB);
					FileChannel src = srcIn.getChannel();
					FileOutputStream dstOut = new FileOutputStream(backupDB);
					FileChannel dst = dstOut.getChannel();
					dst.transferFrom(src, 0, src.size());
					srcIn.close();
					src.close();
					dstOut.close();
					dst.close();

					Toast.makeText(this, R.string.backup_ok, Toast.LENGTH_SHORT)
							.show();
				}
			} else {
				Toast.makeText(this, R.string.backup_not_write_access,
						Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Toast.makeText(
					this,
					getText(R.string.backup_error) + "(" + e.getMessage() + ")",
					Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * Obrir dialog per escollir backup a restaurar
	 */
	private void restoreBackupDB() {

		loadFileList();

		AlertDialog.Builder builder = new Builder(this);

		builder.setTitle("Choose your file");
		if (mFileList == null) {
			Log.e(this.getClass().getName(),
					"Showing file picker before loading the file list");
		}
		final Activity currentActivity = this;
		builder.setItems(mFileList, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				mChosenFile = mFileList[which];
				// you can do stuff with the file here too
				Log.d(this.getClass().getName(), mChosenFile);
				// restaurar bbdd
				new AlertDialog.Builder(currentActivity)
						.setTitle(R.string.restore_backup_db)
						.setMessage(
								R.string.restore_backup_confirm_1
										+ " " + mChosenFile
										+ R.string.restore_backup_confirm_2)
						.setPositiveButton(R.string.yes,
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										// restaurar...
										restoreBackupDB(mChosenFile);

									}
								})
						.setNegativeButton(R.string.no,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// do nothing
									}
								}).show();

			}
		});

		builder.show();

	}

	private void loadFileList() {

		final String FTYPE = ".db";

		// obtenir la ubicaci贸 de la sd
		File sd = Environment.getExternalStorageDirectory();

		// mirar si t茅 permis d'escriptura
		if (sd.canWrite()) {
			// mirar si hi ha el directori LlistaCompra
			File exportDir = new File(sd, "LlistaCompra");
			if (exportDir.exists()) {
				FilenameFilter filter = new FilenameFilter() {
					public boolean accept(File dir, String filename) {
						File sel = new File(dir, filename);
						return filename.contains(FTYPE) || sel.isDirectory();
					}
				};
				mFileList = exportDir.list(filter);
			} else {
				mFileList = new String[0];
			}
		}
	}

	private void restoreBackupDB(String fileRestore) {
		try {
			// obtenir la ubicaci贸 de la sd
			File sd = Environment.getExternalStorageDirectory();

			// mirar si hi ha el directori LlistaCompra, sino crearlo
			File exportDir = new File(sd, "LlistaCompra");

			// localitzaci贸 base de dades actual
			File currentDB = getDatabasePath(LlistaCompraHelper.DATABASE_NAME);
			// localitzaci贸 del backup a restaurar
			File backupDB = new File(exportDir, fileRestore);

			if (backupDB.exists()) {
				// fer copia base de dades
				FileInputStream srcIn = new FileInputStream(currentDB);
				FileChannel src = srcIn.getChannel();
				FileOutputStream dstOut = new FileOutputStream(backupDB);
				FileChannel dst = dstOut.getChannel();
				dst.transferFrom(src, 0, src.size());
				srcIn.close();
				src.close();
				dstOut.close();
				dst.close();

				Toast.makeText(this, R.string.restore_backup_ok,
						Toast.LENGTH_SHORT).show();

				// tornar a la llista, refrescar llista
				MainTab parentActivity = (MainTab) this.getParent();
				parentActivity.finish();
				Intent intent = new Intent(this, MainTab.class);
				intent.putExtra(MainTab.NAME_INDEX, MainTab.INDEX_TAB_LLISTES);
				startActivity(intent);
			} else {
				Toast.makeText(this,
						R.string.restore_backup_not_exist,
						Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Toast.makeText(this,
					getText(R.string.restore_backup_error)+" (" + e.getMessage() + ")",
					Toast.LENGTH_SHORT).show();
		}

	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, "PGW794DFBZKRBC9XBVGF");
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}
}
