package com.yusuf.bridgely

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import com.yusuf.bridgely.databinding.FragmentCartBinding


class CartFragment : Fragment() {

    private lateinit var binding : FragmentCartBinding

    //firebase init.
    var firebaseAuth= Firebase.auth
    var firebaseFirestore=Firebase.firestore
    var firebaseUser=firebaseAuth.currentUser
    var firebaseStorage=Firebase.storage

    //Cart Products ArrayList for showing it on recyclerview
    var cartProductsArrayList= arrayListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= FragmentCartBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //set the balance text.

        var emaill=firebaseUser!!.email.toString()
        firebaseFirestore.collection("Users").document(emaill).addSnapshotListener { value, error ->
            if(error !=null){
                error.printStackTrace()
            }else{
                if(value!=null){
                    var balanceMap1=value.data
                    var balance1 = balanceMap1!!.get("balance") as Long
                    binding.balanceText.text="Balance : ${balance1.toString()}"


                }
            }
        }



        //clear arraylist to duplicate elements everytime
        cartProductsArrayList.clear()

        //adapter init.
        var adapter=CartAdapter(cartProductsArrayList)
        binding.recyclerView3.adapter=adapter
        binding.recyclerView3.layoutManager=LinearLayoutManager(requireContext())

        //retrieve the cart items

        firebaseFirestore.collection("Users").document(firebaseUser!!.email.toString()).collection("Details").addSnapshotListener { value, error ->
            if(error!=null){
                Toast.makeText(requireContext(),error.localizedMessage,Toast.LENGTH_SHORT).show()
            }else{
                if(value!=null){
                    var documents=value.documents
                    for(document in documents){
                        var productName=document.get("productName") as String
                        var productDescription=document.get("productDescription") as String
                        var productPrice=document.get("productPrice") as Long
                        var productImageUrl=document.get("productImageUrl") as String
                        var productSeller=document.get("seller") as String

                        var cartProduct=Product(productName,productDescription,productPrice,productImageUrl,productSeller)
                        cartProductsArrayList.add(cartProduct)

                    }
                    adapter.notifyDataSetChanged()
                }
            }
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
    }


}