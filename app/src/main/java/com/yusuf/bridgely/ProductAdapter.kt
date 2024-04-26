package com.yusuf.bridgely

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.yusuf.bridgely.databinding.RecyclerviewRowBinding

class ProductAdapter(val productArrayList: ArrayList<Product>): RecyclerView.Adapter<ProductAdapter.ProductHolder>() {


    class ProductHolder(val binding:RecyclerviewRowBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        var binding=RecyclerviewRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ProductHolder(binding)
    }

    override fun getItemCount(): Int {
       return productArrayList.size
    }

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        holder.binding.recyclerviewProductName.text=productArrayList.get(position).productName
        holder.binding.recyclerviewProductPrice.text=productArrayList.get(position).productPrice.toString()
        Picasso.get().load(productArrayList[position].productImageUrl).into(holder.binding.recyclerviewImageView)
        //how to navigate from adapter to fragment!
        holder.itemView.setOnClickListener {
            val action=StoreFragmentDirections.actionStoreFragmentToProductFragment(productArrayList.get(position).productName,productArrayList.get(position).productPrice.toLong(),productArrayList.get(position).productDescription,productArrayList.get(position).productImageUrl,productArrayList.get(position).productSeller)
            holder.itemView.findNavController().navigate(action)
        }



    }
}