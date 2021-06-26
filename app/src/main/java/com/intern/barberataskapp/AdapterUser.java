package com.intern.barberataskapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.HolderUser>{
    private Context context;
    public ArrayList<ModalUser> userList;

    public AdapterUser(Context context, ArrayList<ModalUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public HolderUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate  layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_user_details,parent,false);
        return new HolderUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderUser holder, int position) {
        //getData
        ModalUser modalUser = userList.get(position);
        String id = modalUser.getUid();
        String name = modalUser.getName();
        String phone = modalUser.getPhone();
        String mail = modalUser.getMail();
        String profileImage = modalUser.getProfileImage();
        String timestamp = modalUser.getTimestamp();

        //setData
        holder.nameTv.setText(name);
        holder.phoneTv.setText(phone);
        holder.mailTv.setText(mail);
        try {
            Glide.with(context).load(profileImage).placeholder(R.drawable.ic_person_grey).into(holder.profile_Iv);

        }
        catch (Exception e){
            holder.profile_Iv.setImageResource(R.drawable.ic_person_grey);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //handle item clicks show item details (in bottom sheet)
                detailBottomUser(modalUser);

            }
        });


    }

    private void detailBottomUser(ModalUser modalUser) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.bs_user_details,null);

        //init View

        ImageButton backBtn = view.findViewById(R.id.backBtn);
        ImageButton deleteBtn = view.findViewById(R.id.deleteBtn);

        ImageView iconIv =  view.findViewById(R.id.iconIv);

        TextView name_tv =  view.findViewById(R.id.name_tv);
        TextView phone_tv =  view.findViewById(R.id.phone_tv);
        TextView email_tv =  view.findViewById(R.id.email_tv);

        //getData
        final String id = modalUser.getUid();
        final String name = modalUser.getName();
        String phone = modalUser.getPhone();
        String email = modalUser.getMail();
        String profileImage = modalUser.getProfileImage();

        //setData
        name_tv.setText(name);
        phone_tv.setText(phone);
        email_tv.setText(email);
        try {
            Glide.with(context).load(profileImage).placeholder(R.drawable.ic_person_grey).into(iconIv);
        }
        catch (Exception e){
            iconIv.setImageResource(R.drawable.ic_person_grey);
        }
        //set view
        bottomSheetDialog.setContentView(view);

        //show dialog
        bottomSheetDialog.show();

        //delete Btn
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                //show delete confirm dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are  you sure you want to delete User "+name+"?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //delete
                                deleteProduct(id);

                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //cancel dialog dismiss
                        dialog.dismiss();

                    }
                })
                        .show();

            }
        });
        //back  btn
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dismiss bottom sheet
                bottomSheetDialog.dismiss();
            }
        });
    }
    private void deleteProduct(String id) {
        //delete user with string id
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "User Deleted...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed  deleting product
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public int getItemCount() {
        return userList.size();
    }

    class HolderUser extends RecyclerView.ViewHolder{
        private CircleImageView profile_Iv;
        private ImageView nextIv;
        private TextView nameTv, phoneTv,mailTv;
        public HolderUser(@NonNull View itemView) {
            super(itemView);
            profile_Iv = itemView.findViewById(R.id.profile_Iv);
            nextIv = itemView.findViewById(R.id.nextIv);
            nameTv = itemView.findViewById(R.id.NameTv);
            phoneTv = itemView.findViewById(R.id.phoneTv);
            mailTv = itemView.findViewById(R.id.mailTv);
        }
    }
}
