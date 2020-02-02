package com.example.contacts.app;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contacts.R;
import com.example.contacts.database.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private List<Contact> allContacts = new ArrayList<>();
    private List<Contact> selectedContacts = new ArrayList<>();
    private List<CheckBox> checkBoxes = new ArrayList<>();
    private ContactClickListener clickListener;
    private ActionMode actionMode;
    private boolean multiselect = false;

    public ContactAdapter(ContactClickListener clickListener) {
        this.clickListener = clickListener;
    }

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
            showCheckBoxes();
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            if (item.getItemId() == R.id.action_delete) {
                clickListener.onContactsDelete(selectedContacts);
                mode.finish();
                return true;
            }

            if (item.getItemId() == R.id.action_select_all) {

                if (selectedContacts.size() == allContacts.size()) {
                    selectedContacts.clear();

                    for (CheckBox checkBox : checkBoxes) {
                        checkBox.setChecked(false);
                    }

                    return true;
                }

                selectedContacts.clear();
                selectedContacts.addAll(allContacts);

                for (CheckBox checkBox : checkBoxes) {
                    checkBox.setChecked(true);
                }

                return true;
            }

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            multiselect = false;
            selectedContacts.clear();
            hideCheckBoxes();
        }
    };

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_view_holder, parent, false);

        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.contactName.setText(allContacts.get(position).getName());
        holder.contactNumber.setText(allContacts.get(position).getTelephoneNumber());

        String gender = allContacts.get(position).getGender();

        if (gender != null) {
            if (gender.equals("Male")) {
                holder.avatar.setImageResource(R.drawable.ic_avatar_male);
            } else if (gender.equals("Female")) {
                holder.avatar.setImageResource(R.drawable.ic_avatar_female);
            }
        }

        checkBoxes.add(holder.checkBox);
    }

    @Override
    public int getItemCount() {
        return allContacts.size();
    }

    private void startActionMode(View view) {
        if (actionMode != null) {
            return;
        }

        MainActivity activityContext = (MainActivity) view.getContext();
        actionMode = activityContext.startSupportActionMode(actionModeCallback);
        multiselect = true;
    }

    private void selectItem(int position, CheckBox checkBox) {
        Contact item = allContacts.get(position);

        if (!selectedContacts.contains(item)) {
            selectedContacts.add(item);
            checkBox.setChecked(true);
        } else {
            selectedContacts.remove(item);
            checkBox.setChecked(false);
        }
    }

    private void showCheckBoxes() {
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setChecked(false);
            checkBox.setVisibility(View.VISIBLE);
        }
    }

    private void hideCheckBoxes() {
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setVisibility(View.INVISIBLE);
        }
    }

    public void setAllContacts(List<Contact> allContacts) {
        this.allContacts = allContacts;
        notifyDataSetChanged();
    }


    public interface ContactClickListener {
        void onContactClick(Contact contact);

        void onContactsDelete(List<Contact> contacts);
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        public ImageView avatar;
        public TextView contactName;
        public TextView contactNumber;
        public CheckBox checkBox;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            contactName = itemView.findViewById(R.id.contact_name);
            contactNumber = itemView.findViewById(R.id.contact_number);
            checkBox = itemView.findViewById(R.id.checkbox);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!multiselect) {
                        clickListener.onContactClick(allContacts.get(getAdapterPosition()));
                    } else {
                        selectItem(getAdapterPosition(), checkBox);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    startActionMode(v);
                    selectItem(getAdapterPosition(), checkBox);
                    return true;
                }
            });
        }
    }
}
