package com.codepath.fbu_newsfeed.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.R;

import org.w3c.dom.Text;

import java.util.List;

public class ArticleTitleSpinnerAdapter extends ArrayAdapter<Article> {

    public ArticleTitleSpinnerAdapter(Context context, List<Article> items) {
        super(context, R.layout.spinner_item, items);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (position == 0)
            return initialSelection(true);
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == 0)
            return initialSelection(false);
        return getCustomView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return super.getCount() + 1;
    }

    private View initialSelection(boolean dropdown) {
        TextView view = new TextView(getContext());
        view.setText("Choose an existing article");
        view.setPadding(8, 8, 8, 8);

        if (dropdown)
            view.setHeight(0);

        return view;
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        View row = convertView != null && !(convertView instanceof TextView) ?
                convertView :
                LayoutInflater.from(getContext()).inflate(R.layout.spinner_item, parent, false);

        position = position - 1;
        Article article = getItem(position);

        TextView tvItem = row.findViewById(R.id.tvItem);
        tvItem.setText(article.getTitle());

        return row;
    }

}
