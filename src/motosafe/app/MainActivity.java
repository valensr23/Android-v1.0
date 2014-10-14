package motosafe.app;

/*
 * @author: Valentín Sánchez Ramírez
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;


	
import java.util.Set;



public class MainActivity extends Activity  {

	private static final String TAG = "InicioActivity";
	private static final int REQUEST_ENABLE_BT = 1;
	private static final String NOMBRE_DISPOSITIVO_BT = "HC-06";//Nombre de nuestro dispositivo bluetooth.
	private TextView tvInformacion;
	

	
@Override
	protected void onCreate(Bundle savedInstanceState) {
	/*Inicializamos la activity e inflamos el layout*/
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
	tvInformacion = (TextView) findViewById(R.id.textView_estado_BT);
	//Comprobamos si el GPS esta encendido o apagado
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
	        Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
	    }else{
	        showGPSDisabledAlertToUser();
	    }
    
    
	}



@Override
	protected void onResume() {
	/* El metodo on resume es el adecuado para inicialzar todos aquellos procesos que actualicen la interfaz de usuario
	Por lo tanto invocamos aqui al método que activa el BT y GPS*/
	super.onResume();
	descubrirDispositivosBT();

	}


///////////////////////////////////////////////////////////////////////////////////////////////


private void showGPSDisabledAlertToUser(){
	new AlertDialog.Builder(this)
	.setIcon(android.R.drawable.ic_dialog_alert)	
	.setTitle("Activar GPS")	
	.setMessage("GPS esta desactivado. ¿Desea activarlo?")
    .setPositiveButton("Menu opciones GPS",
            new DialogInterface.OnClickListener(){
        public void onClick(DialogInterface dialog, int id){
            Intent callGPSSettingIntent = new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(callGPSSettingIntent);
        }
    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
		//Salimos de la app
		finish();
		}

    }).show();
}




////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	private void descubrirDispositivosBT() {

	/*
	Este método comprueba si nuestro dispositivo dispone de conectividad bluetooh.
	En caso afirmativo, si estuviera desctivada, intenta activarla.
	En caso negativo presenta un mensaje al usuario y sale de la aplicación.
	*/
	
	//Comprobamos que el dispositivo tiene adaptador bluetooth
	BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	tvInformacion.setText("Comprobando bluetooth");
	if (mBluetoothAdapter != null) {
		//El dispositivo tiene adapatador BT. Ahora comprobamos que bt esta activado.
		if (mBluetoothAdapter.isEnabled()) {
				//Esta activado. Obtenemos la lista de dispositivos BT emparejados con nuestro dispositivo android.
				tvInformacion.setText("Obteniendo dispositivos emparejados, espere...");
				Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
				//Si hay dispositivos emparejados
			if (pairedDevices.size() > 0) {
						/*
						Recorremos los dispositivos emparejados hasta encontrar el
						adaptador BT del arduino, en este caso se llama HC-06
						*/
					
					
					BluetoothDevice arduino = null;
					for (BluetoothDevice device : pairedDevices) {
						if (device.getName().equalsIgnoreCase(NOMBRE_DISPOSITIVO_BT)) {
						arduino = device;
						}
					}
					if (arduino != null) {
						//TODO las operasciones que queramos al estar ya conectado
					} else {
					//No hemos encontrado nuestro dispositivo BT, es necesario emparejarlo antes de poder usarlo.
					//No hay ningun dispositivo emparejado. Salimos de la app.
					Toast.makeText(this, "No hay dispositivos emparejados, por favor, empareje el arduino", Toast.LENGTH_LONG).show();
					finish();
					}
				} else {
				
				//No hay ningun dispositivo emparejado. Salimos de la app.
				Toast.makeText(this, "No hay dispositivos emparejados, por favor, empareje el arduino", Toast.LENGTH_LONG).show();
				finish();
				}
			} else {
			muestraDialogoConfirmacionActivacion();
			}
		} else {
	// El dispositivo no soporta bluetooth. Mensaje al usuario y salimos de la app
	Toast.makeText(this, "El dispositivo no soporta comunicación por Bluetooth", Toast.LENGTH_LONG).show();
	}
}



	

@Override
	protected void onStop() {
	
	/*Cuando la actividad es destruida, se ejecuta este método.
	*/
	super.onStop();
	
		
	
	}


private void muestraDialogoConfirmacionActivacion() {
	new AlertDialog.Builder(this)
	.setIcon(android.R.drawable.ic_dialog_alert)	
	.setTitle("Activar Bluetooth")	
	.setMessage("BT esta desactivado. ¿Desea activarlo?")	
	.setPositiveButton("Si", new DialogInterface.OnClickListener() {
	
		@Override
		public void onClick(DialogInterface dialog, int which) {
			//Intentamos activarlo con el siguiente intent.
			tvInformacion.setText("Activando BT");
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
			
			}).setNegativeButton("No", new DialogInterface.OnClickListener() {
			
				@Override
				public void onClick(DialogInterface dialog, int which) {
				//Salimos de la app
				finish();
				}
		
		}).show();
	
	}




}
