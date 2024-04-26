package com.yusuf.bridgely

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.squareup.picasso.Picasso
import com.yusuf.bridgely.databinding.FragmentProductBinding


class ProductFragment : Fragment() {
    var _binding:FragmentProductBinding?=null
    val binding get()=_binding!!

    //firebase init.
    var firebaseFirestore = Firebase.firestore
    var firebaseAuth =Firebase.auth
    var firebaseUser=firebaseAuth.currentUser


    //to retrieve the product info
    val args:ProductFragmentArgs by navArgs()

    var productName=""
    var productDescription=""
    var productPrice:Long =0
    var productImageUrl=""
    var productSeller=""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding=FragmentProductBinding.inflate(inflater,container,false)
        val view=binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //retrieve the product info
        productName=args.productName
        productDescription=args.productDescription
        productPrice=args.productPrice.toLong()
        productImageUrl=args.productImageUrl
        productSeller=args.seller


        //set the values
        binding.productNameDetails.text=productName
        binding.productDescriptionDetails.text=productDescription
        binding.productPriceDetails.text=productPrice.toString()
        Picasso.get().load(productImageUrl).into(binding.productImageDetails)

        binding.addToCartButton.setOnClickListener {
            //code that will be executed when add to cart button clicked.
            var userEmail=firebaseUser!!.email.toString()

            //Create a new collection to store the shopping cart products.
            var cartProducts= hashMapOf<String,Any>()
            cartProducts.put("productName",productName)
            cartProducts.put("productDescription",productDescription)
            cartProducts.put("productPrice",productPrice)
            cartProducts.put("productImageUrl",productImageUrl)
            cartProducts.put("seller",productSeller)

            firebaseFirestore.collection("Users").document(userEmail).collection("Details").add(cartProducts).addOnSuccessListener {
                //product added to shopping cart successfully
                Toast.makeText(requireContext(),"Product Added to Shopping Cart!",Toast.LENGTH_SHORT).show()

            }.addOnFailureListener {
                Toast.makeText(requireContext(),it.localizedMessage,Toast.LENGTH_SHORT).show()
            }

        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }


}