package com.yusuf.bridgely

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.yusuf.bridgely.databinding.FragmentStoreBinding


class StoreFragment : Fragment() {
    var _binding:FragmentStoreBinding?=null
    val binding get()=_binding!!

    //firebase init.
    var firebaseAuth=Firebase.auth
    var firebaseFirestore=Firebase.firestore

    //Product Array for storing the products and passing thru the recyclerview
    var productArrayList= arrayListOf<Product>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentStoreBinding.inflate(inflater,container,false)
        val view=binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productArrayList.clear()

        //adapter init.
        binding.recyclerView.layoutManager=LinearLayoutManager(requireContext())
        var adapter=ProductAdapter(productArrayList)
        binding.recyclerView.adapter=adapter

        //View created, retrieve all of the products from database
        firebaseFirestore.collection("Products").addSnapshotListener { value, error ->
            if(error!=null){
                //there is an error while getting the products
            }else{
                if(value!=null){
                    //there is NO error and value is OK
                    var documents = value.documents
                    for(document in documents){
                        var productName=document.get("productName") as String
                        var productDescription=document.get("productDescription") as String
                        var productPrice=document.get("productPrice") as Long
                        var productImageUrl=document.get("imageUrl") as String
                        var productSeller=document.get("seller") as String
                        var product =Product(productName,productDescription,productPrice,productImageUrl,productSeller)

                        // all of the products are retrieving one by one. one of them is ready.
                        //now, add the product to the productArrayList
                        productArrayList.add(product)

                    }
                    mergeSort(productArrayList, 0, productArrayList.size - 1)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }



    fun mergeSort(arr: MutableList<Product>, l: Int, r: Int) {
        if (l < r) {
            val m = (l + r) / 2
            mergeSort(arr, l, m)
            mergeSort(arr, m + 1, r)
            merge(arr, l, m, r)
        }
    }

    fun merge(arr: MutableList<Product>, l: Int, m: Int, r: Int) {
        val n1 = m - l + 1
        val n2 = r - m

        val L = mutableListOf<Product>()
        val R = mutableListOf<Product>()

        for (i in 0 until n1)
            L.add(arr[l + i])
        for (j in 0 until n2)
            R.add(arr[m + 1 + j])

        var i = 0
        var j = 0
        var k = l

        while (i < n1 && j < n2) {
            if (L[i].productPrice <= R[j].productPrice) {
                arr[k] = L[i]
                i++
            } else {
                arr[k] = R[j]
                j++
            }
            k++
        }

        while (i < n1) {
            arr[k] = L[i]
            i++
            k++
        }

        while (j < n2) {
            arr[k] = R[j]
            j++
            k++
        }
    }
}


