package com.example.prova;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    //per rendere il codice un po' piu generale e riutilizzarlo definiamo tre liste
    private ArrayList<String> permissionToRequest;
    private ArrayList<String> permissionRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    private final static  int ALL_PERMISSION_RESULT = 107;
    private final static  int PICK_IMAGE = 200;
    private ImageView propic;
    public TextView textNome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        FirebaseAuth nAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = nAuth.getCurrentUser();

        propic=findViewById(R.id.proPic);
        propic.setClipToOutline(true);
        textNome=findViewById(R.id.proName);
        textNome.setText(currentUser.getDisplayName());

        if (currentUser.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(currentUser.getPhotoUrl())
                    .centerCrop()
                    .into(propic);
        } else {
            Glide.with(this)
                    .load(getDrawable(R.drawable.placeholde))
                    .centerCrop()
                    .into(propic);
        }

        propic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissions.add(Manifest.permission.CAMERA);
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                permissionToRequest=findUnaskedPermissions(permissions);
                if(permissionToRequest.size() > 0){ //va a richiedere i permessi
                    requestPermissions(permissionToRequest.toArray(new String[permissionToRequest.size()]), ALL_PERMISSION_RESULT);
                }else{
                    startActivityForResult(getPickImageChooserIntent(), PICK_IMAGE);
                }
            }
        });
        getSupportActionBar().setTitle(getString(R.string.benvenuti));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode==PICK_IMAGE){
            Bitmap bitmap = null;
            if(resultCode == RESULT_OK){
                if (getPickImageResultUri(intent) != null){ //abbiamo caricato la nostra immagine come bitmap
                    Uri picUri = getPickImageResultUri(intent);
                    try{
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),picUri);
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                } else bitmap = (Bitmap) intent.getExtras().get("data");

            }
            Glide.with(this)
                    .load(bitmap)
                    .centerCrop()
                    .into(propic);
        }
    }

    private Uri getPickImageResultUri(Intent data){
        boolean isCamera = true;
        if(data != null){
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUrl() : data.getData(); // se true prime sefalse secondo
    }

    private ArrayList findUnaskedPermissions(ArrayList<String> wanted){ //troviamo quali permessi non abbiamo chiesto
        ArrayList<String> result = new ArrayList<>();

        for(String perm : wanted){
            if(!(checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED)){
                result.add(perm); //per ogni perme necessario esso non è stato dato allora lo dobbiamo richiedere
            }
        }
        return result;
    }

    //se fa cosi e non rompe i coglioni copia incolla e sta zitto
    public Intent getPickImageChooserIntent(){
        Uri outputFileUri = getCaptureImageOutputUrl();
        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List <ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri); //se ho catturato qualcosa metto negli extra dell'intent l'uri del file salvato
            }
            allIntents.add(intent);
        }
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//intent che ha ocme aionequelladi lselezionare dalle immagini
        List <ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for(ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        Intent mainIntent = allIntents.get(allIntents.size()-1);
        for(Intent intent : allIntents){
            if(intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break; //vogliamo togliere l'intent dei documenti, for che rimuove se lo trova l'intent dell'applicazione dei documenti
            }
        }
        allIntents.remove(mainIntent);

        Intent chooserIntent = Intent.createChooser(mainIntent, getString(R.string.selsorgente));
//ora gli aggiungiamo tutti gli altri intent, abbiamo preso il principale che abbiamo scelto (se esiste è l'intent dei documenti altrimenti è l'ultimo) ci creiamo
// un intent da questo e andiamo ad inserire tutti gli altri intent dopo abbiamo tolto tutti gli altri intent dalla lista degli intent
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));
        return chooserIntent;
    }//tutta sta roba per chiede all'utente come vuole prende l'immagine

    public Uri getCaptureImageOutputUrl(){
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if(getImage != null)
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "propic.png"));
        return outputFileUri;

    }

    public void onRequestPermissionToResult(int requestCode, String[] permission, int []grantResults) {
        if (requestCode == ALL_PERMISSION_RESULT) {
            for (String perms : permissionToRequest) {
                if (!(checkSelfPermission(perms) == PackageManager.PERMISSION_GRANTED)) {
                    permissionRejected.add(perms);
                }
            }
            if(permissionRejected.size() > 0){
                if(shouldShowRequestPermissionRationale(permissionRejected.get(0))){
                    Toast.makeText(this, "Approva tutto", Toast.LENGTH_LONG);
                }
            }
            else {
                startActivityForResult(getPickImageChooserIntent(), PICK_IMAGE);
            }
        }
    }
}

