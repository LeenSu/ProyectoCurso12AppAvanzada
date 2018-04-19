package com.example.leen.proyectocurso12appavanzada;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;


public class MainActivity extends AppCompatActivity {

    //objetos para el longin de facebook
    private CallbackManager cm;
    private LoginButton lb;
    private TextView info;
    private TextView infoPre;

    //variable para tomar el perfil de usuario y traer datos de perfil
    private ProfileTracker profileTracker;
    private Profile profile;
    //variable para la vista de la foto de perfil del usuario
    private ProfilePictureView profilePictureView;

    //variable para la vista
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //iniciamos el sdk de facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        //Crea un administrador de devolución de llamadas
        cm = CallbackManager.Factory.create();
        //linkiamos el loginbutton
        lb = (LoginButton)findViewById(R.id.login_button);
        //concedemos los permisos
        lb.setReadPermissions("email");
        //damos el permiso para datos publicos del usuario
        lb.setReadPermissions("public_profile");
        //linkiamos la variable de imagen
        profilePictureView = (ProfilePictureView)findViewById(R.id.fbimg);
        //unimos las variable sde texto con textos prederminados
        info = (TextView)findViewById(R.id.infoFace);
        info.setText("Unete a nuestra Comunidad");
        infoPre = (TextView)findViewById(R.id.infoPre);
        infoPre.setText("Loguin con Facebook");
        //fotoF = (ImageView)findViewById(R.id.imgf);

        if (profile==null){
            profilePictureView.setVisibility(View.GONE);
        }

        //validador del keyhash
        getFbKeyHash("HIE4Sgn9NusdNvHx47oWr3Bgzdw=");

        // registrar una devolución de llamada con LoginManager o LoginButton.
        lb.registerCallback(cm, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //creamos un nuevo profileTracker para los cambios de usuario
                profileTracker = new ProfileTracker() {
                    //el método que se llamará cuando el perfil cambie.
                    @Override
                    protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                        //llamamos al metodo q mostrara el nombre y foto de perfil del usuario logueado
                            Datos(currentProfile);
                    }
                };
                //iniciamos el cambio de perfil
                profileTracker.startTracking();
            }

            @Override
            public void onCancel() {
                //mensaje para confirmar la sesion cancelada
                Toast.makeText(MainActivity.this, "Inicio de sesion cancelado",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                //mensaje para confirmar la sesion iniciada
                Toast.makeText(MainActivity.this, "Inicio de sesion sin exito",Toast.LENGTH_SHORT).show();
            }

        });

        //-------- banner -----------
        //iniciamos el ad con el id
        MobileAds.initialize(this,"ca-app-pub-1417865905492941~3975418931");
        adView = (AdView)findViewById(R.id.ad_view);//linkiamos la vista
        //request par aq el servidor nos envie el banner a mostrar
        AdRequest adRequest = new AdRequest.Builder().build();
        //cargamos la publicidad en el backgruound
        adView.loadAd(adRequest);
    }
    //metodo que toma el perfil iniciado y muestra datos de usuario de facebook
    private void Datos(Profile perfil){
        if (perfil!=null){
            String nombre = perfil.getName();//traemos el nombre del usuario
            info.setText("Bienvenido "+nombre);//asignamos el texo con el nombre
            infoPre.setText("Disfruta ser parte de una gran comunidad de seguidores");//cambiamos el texto de login
            profilePictureView.setVisibility(View.VISIBLE);//habilitamos la vista de la foto de perfil
            profilePictureView.setProfileId(perfil.getId());//enviamos la foto del perfil
        }else{//caso contrario, sin inicio muestralos texto para el login
            info.setText("Unete a nuestra Comunidad");
            infoPre.setText("Loguin con Facebook");
            profilePictureView.setVisibility(View.GONE);//esconde la vista de la imagen del perfil
        }
    }

    //valida si el keyhash es el mismo o correcto
    public void getFbKeyHash(String packageName){
        try{
            PackageInfo info = getPackageManager().getPackageInfo(
                    packageName, PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash :", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                System.out.println("keyhash:" +Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }

        }catch(PackageManager.NameNotFoundException e){

        }catch(NoSuchAlgorithmException e){

        }
    }

    //pasamos el resultado del inicio de sesión a LoginManager mediante callbackManager.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        cm.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    //----baner---
    @Override
    protected void onDestroy() {
        //si esta mostrando el baneer entonces lo destruimos
        if (adView != null){
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        //si se esta mostrando el banner se pone pausa
        if (adView != null){
            adView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (adView != null){
            adView.resume();
        }
        super.onResume();
    }
}
