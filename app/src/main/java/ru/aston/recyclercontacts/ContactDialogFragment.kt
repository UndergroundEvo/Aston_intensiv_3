package ru.aston.recyclercontacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ru.aston.recyclercontacts.databinding.DialogContactBinding
import kotlin.random.Random

class ContactDialogFragment (
    private val contact: Contact? = null,
    private val onSave: (Contact) -> Unit
) : DialogFragment() {

    private var _binding: DialogContactBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contact?.let {
            binding.editFirstName.setText(it.firstName)
            binding.editLastName.setText(it.lastName)
            binding.editPhoneNumber.setText(it.phoneNumber)
        }

        binding.btnSave.setOnClickListener {
            val firstName = binding.editFirstName.text.toString()
            val lastName = binding.editLastName.text.toString()
            val phoneNumber = binding.editPhoneNumber.text.toString()

            if (firstName.isNotEmpty() && lastName.isNotEmpty() && phoneNumber.isNotEmpty()) {
                val newContact = contact?.copy(
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber
                ) ?: Contact(
                    id = Random.nextInt(100,999),
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber
                )
                onSave(newContact)
                dismiss()
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}