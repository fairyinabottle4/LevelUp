package com.Jios.LevelUp.ui.jios;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Events.LevelUp.ui.events.EventPage;
import com.Events.LevelUp.ui.events.EventsItem;
import com.MainActivity;
import com.example.LevelUp.ui.events.EventsFragment;
import com.example.LevelUp.ui.jios.JiosFragment;
import com.example.LevelUp.ui.mylist.MylistFragment;
import com.example.tryone.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class JiosAdapter extends RecyclerView.Adapter<JiosAdapter.JiosViewHolder> implements Filterable {
    //ArrayList is passed in from JiosItem.java
    private ArrayList<JiosItem> mJiosList;
    private ArrayList<JiosItem> mJiosListFull;
    private OnItemClickListener mListener;
    private Context mContext;
    private DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    //the ViewHolder holds the content of the card
    public static class JiosViewHolder extends RecyclerView.ViewHolder {
        public ImageView mAddButton;
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;
        public TextView mTextView3;
        public TextView mTextView4;
        public TextView mTextView5;

        public JiosViewHolder(final Context context, View itemView, final OnItemClickListener listener) {
            super(itemView);
            final Context context1 = context;
            mImageView = itemView.findViewById(R.id.imageView);
            mAddButton = itemView.findViewById(R.id.image_add);
            mTextView1 = itemView.findViewById(R.id.title);
            mTextView2 = itemView.findViewById(R.id.event_description);
            mTextView3 = itemView.findViewById(R.id.date);
            mTextView4 = itemView.findViewById(R.id.location);
            mTextView5 = itemView.findViewById(R.id.time);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Button Clicked", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, EventPage.class);
                    intent.putExtra("title", mTextView1.getText().toString());
                    intent.putExtra("description", mTextView2.getText().toString());
                    intent.putExtra("date", mTextView3.getText().toString());
                    intent.putExtra("location", mTextView4.getText().toString());
                    intent.putExtra("time", mTextView5.getText().toString());
                    context.startActivity(intent);
                }
            });
        }
    }

    //Constructor for JiosAdapter class. This ArrayList contains the
    //complete list of items that we want to add to the View.
    public JiosAdapter(Context context, ArrayList<JiosItem> JiosList) {
        mContext = context;
        mJiosList = JiosList;
        mJiosListFull = new ArrayList<>(JiosList); // copy of JiosList for SearchView
    }

    //inflate the items in a JiosViewHolder
    @NonNull
    @Override
    public JiosAdapter.JiosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        JiosAdapter.JiosViewHolder evh = new JiosAdapter.JiosViewHolder(mContext, v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull JiosAdapter.JiosViewHolder holder, final int position) {
        JiosItem currentItem = mJiosList.get(position);
        holder.mImageView.setImageResource(currentItem.getProfilePicture());
        holder.mTextView1.setText(currentItem.getTitle());
        holder.mTextView2.setText(currentItem.getDescription());
        holder.mTextView3.setText(df.format(currentItem.getDateInfo()));
        holder.mTextView4.setText(currentItem.getLocationInfo());
        holder.mTextView5.setText(currentItem.getTimeInfo());
        holder.mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JiosItem ji = mJiosList.get(position);
                //Now I am getting the unsorted copy from MainActivity instead of JiosFragment.
                //This is because I need to have a sorted ArrayList in MainActivity to send to Mylist
                int index = MainActivity.getJiosListCopy().indexOf(ji);
                MylistFragment.setNumberJios(index);
                Toast.makeText(mContext, "Button Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return mJiosList.size();
    }

    @Override
    public Filter getFilter() { // for the 'implements Filterable'
        return jiosFilter;
    }

    private Filter jiosFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<JiosItem> filteredList = new ArrayList<>(); // initially empty list

            if (constraint == null || constraint.length() == 0) { // search input field empty
                filteredList.addAll(mJiosListFull); // to show everything
            } else {
                String userSearchInput = constraint.toString().toLowerCase().trim();

                for (JiosItem item : mJiosListFull) {
                    // contains can be changed to StartsWith
                    if (item.getTitle().toLowerCase().contains(userSearchInput)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mJiosList.clear();
            mJiosList.addAll((List) results.values); // data list contains filtered items
            notifyDataSetChanged(); // tell adapter list has changed

        }
    };
}