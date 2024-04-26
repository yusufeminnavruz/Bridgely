package com.yusuf.bridgely

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.yusuf.bridgely.databinding.ActivityUploadBinding
import java.util.UUID

class UploadActivity : AppCompatActivity() {
    lateinit var binding:ActivityUploadBinding

    //things that we should do to take permission and take photos from gallery
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    lateinit var permissionLauncher: ActivityResultLauncher<String>

    //firebase init.
    lateinit var firebaseAuth:FirebaseAuth
    lateinit var firebaseFirestore: FirebaseFirestore
    lateinit var firebaseStorage:FirebaseStorage
    lateinit var firebaseUser:FirebaseUser
    //init. the selectedImage for firebase
    lateinit var imageData:Uri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUploadBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        //firebase init.
        firebaseAuth=Firebase.auth
        firebaseFirestore=Firebase.firestore
        firebaseStorage=Firebase.storage
        firebaseUser=firebaseAuth.currentUser!!



        //and of course we should call the function
        registerLauncher()

        binding.imageView.setOnClickListener {
            //actions to be executed when imageView clicked
            //first, check if the permission is granted or not.
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_MEDIA_IMAGES)!=PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_MEDIA_IMAGES)){
                    //show rationale
                    Snackbar.make(view,"Permission needed.",Snackbar.LENGTH_INDEFINITE).setAction("Give",
                        View.OnClickListener {
                            //request permission
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        }).show()
                }else{
                    //request permission
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }else{
                //permission granted. go to gallery
                val action= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(action)

            }
        }
        binding.uploadProductButton.setOnClickListener {
            //codes that will be executed when uploadButton clicked
            //first, upload the image to database
            var productName=binding.productNameInput.text.toString()
            var productDescription=binding.productDescriptionInput.text.toString()
            var productPrice = binding.productPriceInput.text.toString().toLong()
            var userEmail=firebaseUser.email.toString()
            if(productName!=null&&productDescription!=null&&productPrice!=null&&imageData!=null){
                val uuid = UUID.randomUUID()
                val imageName="$uuid.jpg"
                var storageReference=firebaseStorage.reference

                storageReference.child("images").child(imageName).putFile(imageData).addOnSuccessListener {
                    //image uploaded successfully
                    //continue with uploading the fields and retrieve the download url
                    var imageLocation=storageReference.child("images").child(imageName)
                    imageLocation.downloadUrl.addOnSuccessListener {
                        //retrieved correctly, now combine with firestore
                        var productMap= hashMapOf<String,Any>()
                        productMap.put("productName",productName)
                        productMap.put("productDescription",productDescription)
                        productMap.put("productPrice",productPrice)
                        productMap.put("imageUrl",it.toString())
                        productMap.put("seller",userEmail)
                        firebaseFirestore.collection("Products").add(productMap).addOnSuccessListener {
                            //all of the fields and photos uploaded correctly, return to the main screen which is our main activity
                            var intent=Intent(this,MainActivity::class.java)
                            startActivity(intent)
                        }.addOnFailureListener {
                            Toast.makeText(this,it.localizedMessage,Toast.LENGTH_SHORT).show()
                        }

                    }


                }.addOnFailureListener {
                    //there is an error while uploading the image
                    Toast.makeText(this,it.localizedMessage,Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"All of the fields should be filled correctly.",Toast.LENGTH_SHORT).show()
            }





        }
    }

    // to prevent from mind confusion, I declared the registration in this function
    private fun registerLauncher(){
        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
            if(result.resultCode== RESULT_OK){
                val intentFromResult=result.data
                if(intentFromResult!=null&&intentFromResult.data!=null){
                    imageData= intentFromResult.data!!
                    binding.imageView.setImageURI(imageData)
                }
            }
        }
        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){result ->
            if(result){
                //permission granted. go to gallery
                val action= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(action)
            }else{
                //permission denied
                Toast.makeText(this@UploadActivity,"Permission required.",Toast.LENGTH_SHORT).show()

            }
        }
    }
}