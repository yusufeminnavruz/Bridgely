package com.yusuf.bridgely

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.yusuf.bridgely.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    var balance:Long = 0
    var newBalance :Long = 0

    //firebase init.
    var firebaseAuth=Firebase.auth
    var firebaseFirestore=Firebase.firestore
    var firebaseUser=firebaseAuth.currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //getting the current values of account
        var userEmail=firebaseUser!!.email.toString()
        firebaseFirestore.collection("Users").document(userEmail).addSnapshotListener { value, error ->
            if(error !=null){
                error.printStackTrace()
            }else{
                if(value!=null){
                    var balanceMap=value.data
                    balance = balanceMap!!.get("balance") as Long
                    binding.profileInfoText.text="Hello : $userEmail, your balance is : $balance"

                }
            }
        }

        binding.addMoneyButton.setOnClickListener {

            //codes to be executed when addMoney pressed
            if(binding.moneyInput.text.toString().toLong()>0){
                newBalance=balance+binding.moneyInput.text.toString().toLong()
                var data= hashMapOf<String,Long>()
                data.put("balance",newBalance)
                firebaseFirestore.collection("Users").document(userEmail).set(data).addOnSuccessListener {
                    Toast.makeText(requireContext(),"Balance increased.",Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    it.printStackTrace()
                }
            }

        }

        binding.logoffButton.setOnClickListener {
            //codes to be executed when logoff pressed
            firebaseAuth.signOut()
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToLoginFragment())

        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}