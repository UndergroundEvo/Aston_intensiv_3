package ru.aston.recyclercontacts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.aston.recyclercontacts.databinding.ItemContactBinding

class ContactAdapter(
    private val onClick: (Contact) -> Unit,
    private val onLongClick: (Contact) -> Unit,
    private val onDragStart: (RecyclerView.ViewHolder) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private var contacts = listOf<Contact>()

    fun submitList(newContacts: List<Contact>) {
        val diffCallback = ContactDiffCallback(contacts, newContacts)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        contacts = newContacts
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        //Log.d("onCreateViewHolder","created $viewType")
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contacts[position])
        holder.itemView.setOnLongClickListener {
            onDragStart(holder)
            true
        }
        //Log.d("onBindViewHolder","bind $position")
    }

    override fun getItemCount() = contacts.size

    inner class ContactViewHolder(private val binding: ItemContactBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: Contact) {
            binding.apply {
                textName.text = "[${contact.id}] ${contact.firstName} ${contact.lastName}"
                textPhone.text = contact.phoneNumber

                root.setOnClickListener {
                    onClick(contact)
                }

                root.setBackgroundColor(
                    if (contact.isSelected) android.graphics.Color.LTGRAY
                    else android.graphics.Color.WHITE
                )

                root.setOnLongClickListener {
                    //Log.d("setOnLongClickListener","contact ${root.id}")
                    onClick(contact)
                    true
                }
            }
        }
    }

    class ContactDiffCallback(
        private val oldList: List<Contact>,
        private val newList: List<Contact>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}