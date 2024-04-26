package com.yusuf.bridgely

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.squareup.picasso.Picasso
import com.yusuf.bridgely.databinding.CartRecyclerviewBinding

class CartAdapter(var cartProductArrayList: ArrayList<Product>): RecyclerView.Adapter<CartAdapter.CartHolder>() {

    class CartHolder(var binding:CartRecyclerviewBinding): RecyclerView.ViewHolder(binding.root) {

    }

    //firebase init.
    var firebaseFirestore= Firebase.firestore
    var firebaseAuth=Firebase.auth
    var firebaseUser=firebaseAuth.currentUser

    var balance: Long =0
    var newSellerBalance:Long =0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartHolder {
        var binding=CartRecyclerviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CartHolder(binding)
    }

    override fun getItemCount(): Int {
        return cartProductArrayList.size
    }

    override fun onBindViewHolder(holder: CartHolder, position: Int) {
        holder.binding.cartName.text=cartProductArrayList[position].productName
        holder.binding.cartPrice.text=cartProductArrayList[position].productPrice.toString()
        Picasso.get().load(cartProductArrayList[position].productImageUrl).into(holder.binding.cartImage)

        holder.binding.buyButton.setOnClickListener {
            //codes to be executed when buy button pressed
            var email=firebaseUser!!.email.toString()
            firebaseFirestore.collection("Users").document(email).get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val documentMap = documentSnapshot.data
                    balance = documentMap?.get("balance") as Long
                    if (cartProductArrayList[position].productPrice <= balance) {
                        //balance is higher than the product value. buy it.
                        var dataForReceiver= hashMapOf<String,Long>()
                        dataForReceiver.put("balance",balance-cartProductArrayList[position].productPrice.toLong())
                        firebaseFirestore.collection("Users").document(email).set(dataForReceiver).addOnSuccessListener {
                            Toast.makeText(holder.itemView.context,"Item bought successfully.",Toast.LENGTH_SHORT).show()
                            //item bought.
                            //delete it from cart and delete it from products.
                            //from cart :
                            firebaseFirestore.collection("Users").document(email).collection("Details").addSnapshotListener { value, error ->
                                if(error!=null){
                                    error.printStackTrace()
                                }else{
                                    if(value!=null){
                                        var documentMap1=value.documents
                                        for(document12 in documentMap1){
                                            var deletingProductName=document12.get("productName") as String
                                            var deletingProductDescription=document12.get("productDescription") as String
                                            var deletingProductPrice=document12.get("productPrice") as Long
                                            if(cartProductArrayList.get(position).productName==deletingProductName&&cartProductArrayList.get(position).productDescription==deletingProductDescription&&cartProductArrayList.get(position).productPrice==deletingProductPrice){
                                                //we bought this item. delete it
                                                firebaseFirestore.collection("Users").document(email).collection("Details").document(document12.id).delete().addOnSuccessListener {
                                                    //deleted from details. (Cart)
                                                    Toast.makeText(holder.itemView.context,"Product has been bought. Thanks!",Toast.LENGTH_SHORT).show()
                                                    holder.itemView.findNavController().navigate(CartFragmentDirections.actionCartFragmentToProfileFragment())
                                                }.addOnFailureListener {
                                                    it.printStackTrace()
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                            //from products
                            firebaseFirestore.collection("Products").addSnapshotListener { value, error ->
                                if(error!=null){
                                    error.printStackTrace()
                                }else{
                                    if(value!=null){
                                        var docsCheck=value.documents
                                        for(docCheck in docsCheck){
                                            var docDeleteName=docCheck.get("productName") as String
                                            var docDeleteDescription=docCheck.get("productDescription") as String
                                            var docDeletePrice=docCheck.get("productPrice") as Long
                                            if(cartProductArrayList.get(position).productName==docDeleteName&&cartProductArrayList.get(position).productDescription==docDeleteDescription&&cartProductArrayList.get(position).productPrice==docDeletePrice){
                                                //delete from products. main store screen
                                                firebaseFirestore.collection("Products").document(docCheck.id).delete().addOnSuccessListener {
                                                    //deleted successfully from products

                                                }.addOnFailureListener {
                                                    it.printStackTrace()
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        }.addOnFailureListener {
                            it.printStackTrace()
                        }
                        //increase the balance of seller
                        firebaseFirestore.collection("Users").document(cartProductArrayList[position].productSeller.toString()).get().addOnSuccessListener { documentSnapshot ->
                            if (documentSnapshot.exists()) {
                                var data2=documentSnapshot.data
                                var sellerBalance=data2!!.get("balance") as Long
                                newSellerBalance=sellerBalance+cartProductArrayList[position].productPrice.toLong()

                                var dataForSeller= hashMapOf<String,Long>()
                                dataForSeller.put("balance",newSellerBalance)
                                firebaseFirestore.collection("Users").document(cartProductArrayList[position].productSeller.toString()).set(dataForSeller).addOnSuccessListener {
                                    //seller has increased money
                                }.addOnFailureListener {
                                    it.printStackTrace()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(holder.itemView.context,"Insufficient balance.",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        holder.binding.deleteButton.setOnClickListener {
            //codes to be executed when delete button is clicked
            var email=firebaseUser!!.email
            firebaseFirestore.collection("Users").document(email!!).collection("Details").addSnapshotListener { value, error ->
                if(error!=null){
                    error.printStackTrace()
                }else{
                    if(value!=null){
                        var documentMap1=value.documents
                        for(document12 in documentMap1){
                            var deletingProductName=document12.get("productName") as String
                            var deletingProductDescription=document12.get("productDescription") as String
                            var deletingProductPrice=document12.get("productPrice") as Long
                            if(cartProductArrayList.get(position).productName==deletingProductName&&cartProductArrayList.get(position).productDescription==deletingProductDescription&&cartProductArrayList.get(position).productPrice==deletingProductPrice){
                                //we bought this item. delete it
                                firebaseFirestore.collection("Users").document(email!!).collection("Details").document(document12.id).delete().addOnSuccessListener {
                                    //deleted from details. (Cart)
                                    Toast.makeText(holder.itemView.context,"Product has been deleted",Toast.LENGTH_SHORT).show()
                                    holder.itemView.findNavController().navigate(CartFragmentDirections.actionCartFragmentToProfileFragment())
                                }.addOnFailureListener {
                                    it.printStackTrace()
                                }

                            }
                        }
                    }
                }
            }

        }
    }
}
