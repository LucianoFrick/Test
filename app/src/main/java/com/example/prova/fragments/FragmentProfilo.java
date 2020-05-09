package com.example.prova.fragments;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.prova.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;


public class FragmentProfilo extends Fragment {
    //per rendere il codice un po' piu generale e riutilizzarlo definiamo tre liste
    private ArrayList permissionToRequest;
    private ArrayList<String> permissionRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    private final static  int ALL_PERMISSION_RESULT = 107;
    private final static  int PICK_IMAGE = 200;

    private ImageView propic;
    public TextView textNome;

    FirebaseAuth nAuth = FirebaseAuth.getInstance();
    final FirebaseUser currentUser = nAuth.getCurrentUser();

    FirebaseStorage storage = FirebaseStorage.getInstance ();
    StorageReference storageRef = storage.getReferenceFromUrl ("gs://noqueue-847af.appspot.com/images") .child (currentUser.getUid() + "propic.png");

    @Override
    public void onCreate( Bundle savedInstanceState) { super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profilo, container, false);

        propic=view.findViewById(R.id.proPic);
        propic.setClipToOutline(true);
        textNome=view.findViewById(R.id.proName);
        textNome.setText(currentUser.getDisplayName());

        if (currentUser.getPhotoUrl() != null) {
            Glide.with(this).load(currentUser.getPhotoUrl()).centerCrop().into(propic);
        } else {
            Glide.with(this)
                    .load(getActivity().getDrawable(R.drawable.placeholde))
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
                    requestPermissions((String[]) permissionToRequest.toArray(new String[permissionToRequest.size()]), ALL_PERMISSION_RESULT);
                }else{
                    startActivityForResult(getPickImageChooserIntent(), PICK_IMAGE);
                }
            }
        });

        return view;
    }

    //solo se ho usato la fotocamera
    private Uri getPickImageResultUri(Intent data){
        boolean isCamera = true;
        if(data != null){
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUrl() : data.getData(); // se true prime, se false secondo
    }

    private ArrayList findUnaskedPermissions(ArrayList<String> wanted){ //troviamo quali permessi non abbiamo chiesto
        ArrayList<String> result = new ArrayList<>();

        for(String perm : wanted){
            if(!(getActivity().checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED)){
                result.add(perm); //per ogni permesso necessario non è stato dato allora lo dobbiamo richiedere
            }
        }
        return result;
    }

    //funzione per far scegliere all'utente l'intent da dove vuole prendere la foto
    public Intent getPickImageChooserIntent(){

        Uri outputFileUri = getCaptureImageOutputUrl();
        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getActivity().getPackageManager();

        //PRIMO INTENT - fotocamera
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List <ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0); //lista delle app per fare foto
        for(ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri); //se ho catturato qualcosa metto negli extra dell'intent l'uri del file salvato
            }
            allIntents.add(intent);
        }

        //SECONDO INTENT - galleria
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        List <ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0); //lista delle app come galleria
        for(ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        //vogliamo togliere l'intent dei documenti, for che rimuove se lo trova l'intent dell'applicazione dei documenti
        Intent mainIntent = allIntents.get(allIntents.size()-1);
        for(Intent intent : allIntents){
            if(intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);


        Intent chooserIntent = Intent.createChooser(mainIntent, getString(R.string.selsorgente));

        //ora gli aggiungiamo tutti gli altri intent, abbiamo preso il principale che abbiamo scelto (se esiste è l'intent dei documenti altrimenti è l'ultimo) ci creiamo
        // un intent da questo e andiamo ad inserire tutti gli altri intent dopo abbiamo tolto tutti gli altri intent dalla lista degli intent
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    public Uri getCaptureImageOutputUrl(){
        Uri outputFileUri = null;
        File getImage =getActivity().getExternalCacheDir();
        if(getImage != null)
            outputFileUri = Uri.fromFile(new File(getImage.getPath(),  currentUser.getUid() + "propic.png"));
        return outputFileUri;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode==PICK_IMAGE){

            Bitmap bitmap = null;
            if(resultCode == RESULT_OK){

                if (getPickImageResultUri(intent) != null){ //abbiamo caricato la nostra immagine come bitmap

                    Uri picUri = getPickImageResultUri(intent);

                    try{
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),picUri);
                        uploadImage();
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

    //metodo chiamato quando l'utente ha finito di dare i permessi
    public void onRequestPermissionToResult(int requestCode, String[] permission, int []grantResults) {

        if (requestCode == ALL_PERMISSION_RESULT) {

            for (Object perms : permissionToRequest) {
                if (!(Objects.requireNonNull(getActivity()).checkSelfPermission((String) perms) == PackageManager.PERMISSION_GRANTED)) {
                    permissionRejected.add((String) perms);
                }
            }

            if(permissionRejected.size() > 0){

                if(shouldShowRequestPermissionRationale(permissionRejected.get(0))){
                    Toast.makeText(getContext(), "Dovresti Approvare tutto", Toast.LENGTH_LONG).show();
                }
            } else {
                startActivityForResult(getPickImageChooserIntent(), PICK_IMAGE);
            }
        }
    }

    public void downloadImage() throws IOException {

        final File localFile = File.createTempFile(currentUser.getUid() + "propic", "png");
        storageRef.getFile(localFile);

        Bitmap bmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
        propic.setImageBitmap(bmap);


    }

    public void uploadImage(){

        propic.setDrawingCacheEnabled (true);
        propic.measure (View.MeasureSpec.makeMeasureSpec (0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec (0, View.MeasureSpec.UNSPECIFIED));
        propic.layout (0, 0, propic.getMeasuredWidth (), propic.getMeasuredHeight ());
        propic.buildDrawingCache ();
        Bitmap bitmap = Bitmap.createBitmap (propic.getDrawingCache ()); ByteArrayOutputStream outputStream = new ByteArrayOutputStream ();
        bitmap.compress (Bitmap.CompressFormat.JPEG, 100, outputStream); byte [] data = outputStream.toByteArray ();

        UploadTask uploadTask = storageRef.putBytes (data);


    }

}
