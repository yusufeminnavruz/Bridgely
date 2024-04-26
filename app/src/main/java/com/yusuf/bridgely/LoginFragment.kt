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
import com.yusuf.bridgely.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {
    var _binding :FragmentLoginBinding?=null
    val binding get()=_binding!!

    var firebaseAuth=Firebase.auth
    var firebaseUser=firebaseAuth.currentUser
    var firebaseFirestore=Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //check if user is already signed in
        if(firebaseUser!=null){
            //redirect to main page because user already signed in
            val action=LoginFragmentDirections.actionLoginFragmentToStoreFragment()
            findNavController().navigate(action)

        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding= FragmentLoginBinding.inflate(inflater,container,false)
        val view=binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            //actions when loginButton is clicked
            var email=binding.emailInput.text.toString()
            var password=binding.passwordInput.text.toString()
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
                //user signed in correctly
                val action=LoginFragmentDirections.actionLoginFragmentToStoreFragment()
                findNavController().navigate(action)


            }.addOnFailureListener {
                //failure when signin in
                Toast.makeText(requireContext(),it.localizedMessage,Toast.LENGTH_SHORT).show()
            }
        }
        binding.signupButton.setOnClickListener {
            //actions when signupButton is clicked
            var email=binding.emailInput.text.toString()
            var password=binding.passwordInput.text.toString()
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
                //user signed in correctly
                //create the details data
                var data = hashMapOf<String,Int>()
                data.put("balance",0)
                firebaseFirestore.collection("Users").document(email).set(data).addOnSuccessListener {
                    val action=LoginFragmentDirections.actionLoginFragmentToStoreFragment()
                    findNavController().navigate(action)
                }.addOnFailureListener {

                }



            }.addOnFailureListener {
                //failure when signin in
                Toast.makeText(requireContext(),it.localizedMessage,Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

}