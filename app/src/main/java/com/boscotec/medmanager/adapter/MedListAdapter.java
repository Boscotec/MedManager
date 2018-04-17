package com.boscotec.medmanager.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.boscotec.medmanager.R;
import com.boscotec.medmanager.interfaces.RecyclerItem;
import com.boscotec.medmanager.model.MedicineInfo;
import com.boscotec.medmanager.model.Month;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Johnbosco on 24-Mar-18.
 */

public class MedListAdapter extends RecyclerView.Adapter<MedListAdapter.ViewHolder> implements Filterable {

    private ListItemClickListener mOnClickListener;
    public interface ListItemClickListener {
        void onItemClick(RecyclerItem item);
    }

    @Override
    public Filter getFilter() {
        if (searchFilter == null) {
            searchFilter = new SearchFilter();
        }

        return searchFilter;
    }

    private SearchFilter searchFilter;
    private List<RecyclerItem> items;
    private List<RecyclerItem> filteredItems;
    private Context context;

    public MedListAdapter(Context context, ListItemClickListener mOnClickListener){
        this.context = context;
        this.mOnClickListener = mOnClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return filteredItems.get(position).getRecyclerItemType();
    }

    @Override
    public int getItemCount() {
        return filteredItems == null ? 0 : filteredItems.size();
    }

    public void swapItems(List<RecyclerItem> newItems) {
        if(newItems == null) return;

        if(items != null) items.clear();
        if(filteredItems != null) filteredItems.clear();

        items = newItems;
        filteredItems = newItems;

        //if(newItems != null){
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
       // }
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case RecyclerItem.TYPE_MONTH:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.month, parent, false);
                return new ViewHolderMonth(view);
            case RecyclerItem.TYPE_MED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.med_info, parent, false);
                return new ViewHolderMedical(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecyclerItem item = filteredItems.get(position);
        if(item == null) return;

        holder.bindType(item);
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {super(itemView);}
        public abstract void bindType(RecyclerItem item);
    }

    public class ViewHolderMonth extends ViewHolder implements View.OnClickListener {
        TextView mMonth;

        ViewHolderMonth (View view){
            super(view);
            mMonth = view.findViewById(R.id.month);
            mMonth.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            RecyclerItem item = filteredItems.get(getAdapterPosition());
            if(item == null) return;

            mOnClickListener.onItemClick(item);
        }

        @Override
        public void bindType(RecyclerItem item) {
            Month adapter = (Month) item;
            mMonth.setText(adapter.getName());
        }
    }

    public class ViewHolderMedical extends ViewHolder implements View.OnClickListener {
        ImageView mThumbnailImage;
        TextView mName, mDescription, mInterval, mDuration;
        ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;
        TextDrawable mDrawableBuilder;

        ViewHolderMedical(View view) {
            super(view);
            itemView.setOnClickListener(this);
            mThumbnailImage = view.findViewById(R.id.thumbnail);
            mName = view.findViewById(R.id.name);
            mDescription = view.findViewById(R.id.description);
            mInterval = view.findViewById(R.id.interval);
            mDuration = view.findViewById(R.id.duration);
        }

        @Override
        public void onClick(View view) {
            RecyclerItem item = filteredItems.get(getAdapterPosition());
            if (item == null) return;

            mOnClickListener.onItemClick(item);
        }

        @Override
        public void bindType(RecyclerItem item) {
            MedicineInfo adapter = (MedicineInfo) item;

            itemView.setTag(adapter.getId());
            loadImage(adapter.getName());
            mName.setText(adapter.getName());
            mDescription.setText(adapter.getDescription());
            mInterval.setText(String.format(Locale.getDefault(), "Taken every %d hours", adapter.getInterval()));
            mDuration.setText(String.format(Locale.getDefault(), "%s to %s", adapter.getStartDate(), adapter.getEndDate()));
        }

        private void loadImage(String title) {
            String letter = "A";
            if (!TextUtils.isEmpty(title))
                letter = title.substring(0, 1);
            int color = mColorGenerator.getRandomColor();

            // Create a circular icon consisting of  a random background colour and first letter of title
            mDrawableBuilder = TextDrawable.builder().buildRound(letter, color);
            mThumbnailImage.setImageDrawable(mDrawableBuilder);
        }
    }

    /**
     * Filter for item list
     * Filter content in item list according to the search text
     */
    private class SearchFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint!=null && constraint.length()>0) {
                List<RecyclerItem> tempItems = new ArrayList<>();

                // search content in items list
                for(RecyclerItem item: items){
                    if(item instanceof MedicineInfo){
                        String name = ((MedicineInfo) item).getName();
                        if(name.toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault()))){
                            tempItems.add(item);
                        }
                   }
                }

                filterResults.count = tempItems.size();
                filterResults.values = tempItems;
            } else {
                filterResults.count = items.size();
                filterResults.values = items;
            }

            return filterResults;
        }

        /**
         * Notify about filtered list to ui
         * @param constraint text
         * @param results filtered result
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredItems = (List<RecyclerItem>) results.values;
            notifyDataSetChanged();
        }
    }
}