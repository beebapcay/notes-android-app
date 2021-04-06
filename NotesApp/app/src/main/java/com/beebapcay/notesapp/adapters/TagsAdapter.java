package com.beebapcay.notesapp.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beebapcay.notesapp.R;
import com.beebapcay.notesapp.entities.Tag;
import com.beebapcay.notesapp.listeners.TagsListener;

import java.util.List;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.TagViewHolder> {

    private List<Tag> mTags;
    private TagsListener mTagsListener;

    public TagsAdapter(List<Tag> tags, TagsListener tagsListener) {
        mTags = tags;
        mTagsListener = tagsListener;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TagViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_tag,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        holder.setTag(mTags.get(position));
        holder.mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTagsListener.onTagClicked(mTags.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTags.size();
    }

    static class TagViewHolder extends RecyclerView.ViewHolder {
        TextView mName;

        TagViewHolder(@NonNull View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.text_name);
        }

        void setTag(Tag tag) {
            mName.setText(tag.getName());
        }
    }
}
