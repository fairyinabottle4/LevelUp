package com.Events.LevelUp.ui.events;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.MainActivity;
import com.example.LevelUp.ui.events.EventsFragment;
import com.example.LevelUp.ui.mylist.MylistFragment;
import com.example.tryone.R;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventsViewHolder> implements Filterable {
    //ArrayList is passed in from EventsItem.java
    private ArrayList<EventsItem> mEventsList;
    private ArrayList<EventsItem> mEventsListFull;

    private EventsAdapter.OnItemClickListener mListener;

    private FragmentActivity mContext;
    private DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);

    // dont know what this is for at the moment but it was already here -Yi En
    public interface OnItemClickListener {
        void onItemClick(int position);

    }

    public void setOnItemClickListener(EventsAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    // ViewHolder holds the content of the card
    public static class EventsViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;
        public ImageView mAddButton;
        public TextView mTextView1;
        public TextView mTextView2;
        public TextView mTextView3;
        public TextView mTextView4;
        public TextView mTextView5;

        public View itemView;

        public EventsViewHolder(final Context context, View itemView, final EventsAdapter.OnItemClickListener listener) {
            super(itemView);
            this.itemView = itemView;
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

    //Constructor for EventsAdapter class. This ArrayList contains the
    //complete list of items that we want to add to the View.
    public EventsAdapter(FragmentActivity context, ArrayList<EventsItem> EventsList) {
        mEventsList = EventsList;
        mContext = context;
        mEventsListFull = new ArrayList<>(EventsList); // copy of EventsList for SearchView
    }

    //inflate the items in a EventsViewHolder
    @NonNull
    @Override
    public EventsAdapter.EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        EventsAdapter.EventsViewHolder evh = new EventsAdapter.EventsViewHolder(mContext, v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull EventsAdapter.EventsViewHolder holder, final int position) {
        final EventsItem currentItem = mEventsList.get(position);
        holder.mImageView.setImageResource(currentItem.getProfilePicture());
        holder.mTextView1.setText(currentItem.getTitle());
        holder.mTextView2.setText(currentItem.getDescription());
        holder.mTextView3.setText(df.format(currentItem.getDateInfo()));
        holder.mTextView4.setText(currentItem.getLocationInfo());
        holder.mTextView5.setText(currentItem.getTimeInfo());

        holder.mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventsItem ei = mEventsList.get(position);
                int index = EventsFragment.getEventsItemList().indexOf(ei);
                MylistFragment.setNumberEvents(index);
                // Toast.makeText(mContext, "Button Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEventsList.size();
    }

    @Override
    public Filter getFilter() { // for the 'implements Filterable'
        return eventsFilter;
    }

    private Filter eventsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<EventsItem> filteredList = new ArrayList<>(); // initially empty list

            if (constraint == null || constraint.length() == 0) { // search input field empty
                filteredList.addAll(mEventsListFull); // to show everything
            } else {
                String userSearchInput = constraint.toString().toLowerCase().trim();

                for (EventsItem item : mEventsListFull) {
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

        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mEventsList.clear();
            mEventsList.addAll((List) results.values); // data list contains filtered items
            notifyDataSetChanged(); // tell adapter list has changed

        }
    };
}
